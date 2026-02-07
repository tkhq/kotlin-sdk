#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CHANGESET_DIR="$SCRIPT_DIR/.changeset"
VERSION_KT="$SCRIPT_DIR/packages/http/src/main/kotlin/com/turnkey/http/Version.kt"

# --- Module definitions ---
# These correspond to :packages:<name> Gradle paths
MODULES=(
    "crypto"
    "encoding"
    "http"
    "passkey"
    "sdk-kotlin"
    "stamper"
    "tools"
    "types"
)

# --- Word lists for filename generation ---

ADJECTIVES=(
    brave bright calm clever cool crisp
    daring deep eager fair fast fierce
    fluffy gentle giant golden grand green
    happy humble icy jolly keen kind
    light lively lucky magic mighty misty
    noble proud quick rapid red regal
    sharp silent silver sleek slim smooth
    soft solid spicy spring steady still
    strong super sweet swift tall thick
    tiny tough vivid warm wet wild
)

NOUNS=(
    badger bear bird breeze brook canyon
    cloud coral crane creek dawn deer
    dolphin dove dragon dusk eagle ember
    falcon flame forest fox frost galaxy
    garden glacier grove harbor hawk hill
    island jade lake leaf lion maple
    meadow moon oak ocean orchid otter
    panda pearl pine planet pond puma
    quartz rain raven ridge river rose
    sage seal shadow shore sky sparrow
    stone storm summit sun tiger trail
    valley wave willow wind wolf yarn
)

# --- Helpers ---

generate_filename() {
    local adj="${ADJECTIVES[$((RANDOM % ${#ADJECTIVES[@]}))]}"
    local noun="${NOUNS[$((RANDOM % ${#NOUNS[@]}))]}"
    local name="$adj-$noun"

    if [[ -f "$CHANGESET_DIR/$name.yml" ]]; then
        name="$name-$((RANDOM % 1000))"
    fi

    echo "$name"
}

# Get module directory path
module_dir() {
    local module="$1"
    echo "$SCRIPT_DIR/packages/$module"
}

# Normalize module identifier (handle :packages:crypto or just crypto)
normalize_module() {
    local id="$1"
    # Remove leading :packages: if present
    id="${id#:packages:}"
    id="${id#:}"
    id="${id#packages:}"
    echo "$id"
}

# --- Commands ---

cmd_add() {
    mkdir -p "$CHANGESET_DIR"

    echo ""
    echo "Select modules to include (comma/space separated indexes):"
    echo ""
    local i=1
    for mod in "${MODULES[@]}"; do
        printf "[%d] %s\n" "$i" "$mod"
        i=$((i + 1))
    done

    echo ""
    read -r -p "Your selection (e.g. 1,3 5): " sel_raw

    # Parse selection
    local idxs=()
    for token in $(echo "$sel_raw" | tr ',\t' ' '); do
        token=$(echo "$token" | tr -d ' ')
        if [[ "$token" =~ ^[0-9]+$ ]]; then
            local idx=$((token - 1))
            if [[ $idx -ge 0 && $idx -lt ${#MODULES[@]} ]]; then
                idxs+=("$idx")
            fi
        fi
    done

    # Remove duplicates
    local unique_idxs=($(printf '%s\n' "${idxs[@]}" | sort -nu))

    if [[ ${#unique_idxs[@]} -eq 0 ]]; then
        echo "No valid selection. Aborting."
        exit 1
    fi

    local chosen=()
    for idx in "${unique_idxs[@]}"; do
        chosen+=("${MODULES[$idx]}")
    done

    echo ""
    echo "Selected: ${chosen[*]}"

    # Bump type per module (using parallel arrays instead of associative array)
    echo ""
    echo "Bump type per module (default: patch). Accepts: major / minor / patch / beta"

    local bumps=()
    for mod in "${chosen[@]}"; do
        read -r -p "- $mod bump [major|minor|patch|beta]: " bump_input
        bump_input=$(echo "$bump_input" | tr '[:upper:]' '[:lower:]')
        case "$bump_input" in
            major|minor|patch|beta)
                bumps+=("$bump_input")
                ;;
            *)
                bumps+=("patch")
                ;;
        esac
    done

    # Changelog note
    echo ""
    echo "Write changelog note. End with a single '.' on its own line:"
    local note_lines=()
    while IFS= read -r line; do
        if [[ "$line" == "." ]]; then
            break
        fi
        note_lines+=("$line")
    done

    local note
    if [[ ${#note_lines[@]} -eq 0 ]]; then
        note="No additional notes."
    else
        note=$(printf '%s\n' "${note_lines[@]}")
    fi

    # Generate filename
    local filename
    filename=$(generate_filename)
    local filepath="$CHANGESET_DIR/$filename.yml"

    # Write YAML file
    {
        echo "packages:"
        local i=0
        for mod in "${chosen[@]}"; do
            echo "  $mod: ${bumps[$i]}"
            i=$((i + 1))
        done
        echo "changelog: |-"
        while IFS= read -r line; do
            echo "  $line"
        done <<< "$note"
    } > "$filepath"

    echo ""
    echo "Created changeset: .changeset/$filename.yml"
    echo ""
    echo "Preview:"
    echo "--------------------------------"
    cat "$filepath"
    echo "--------------------------------"
    echo ""
}

usage() {
    echo "Usage: changeset.sh <command>"
    echo ""
    echo "Commands:"
    echo "  add         Create a new changeset interactively"
    echo ""
    echo "Workflow:"
    echo "  1. Run 'add' to create changesets during development"
    echo "  2. Run './gradlew changesetsVersion' to bump versions (moves changesets to .changeset/applied/)"
    echo "  3. Run './gradlew changesetsChangelog' to generate CHANGELOG.md entries and clean up"
    echo ""
    exit 1
}

# --- Main ---

case "${1:-}" in
    add)       cmd_add ;;
    *)         usage ;;
esac
