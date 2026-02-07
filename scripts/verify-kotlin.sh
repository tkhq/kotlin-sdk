#!/usr/bin/env bash
set -euo pipefail

# Inputs (can come from env / Makefile)
GROUP_PATH="${GROUP_PATH:-com/turnkey}"
ARTIFACT_ID="${ARTIFACT_ID:-}"
VERSION="${VERSION:-latest}"
GPG_KEY_FPR="${GPG_KEY_FPR:-}"

if [[ -z "$ARTIFACT_ID" ]]; then
  echo "ARTIFACT_ID is required (e.g. sdk-kotlin, http, types)" >&2
  exit 1
fi

if [[ -z "$GPG_KEY_FPR" ]]; then
  echo "Please set GPG_KEY_FPR to the kotlin-publishers@turnkey.io fingerprint" >&2
  exit 1
fi

echo "Ensuring GPG key $GPG_KEY_FPR is in keyring..."
if ! gpg --list-keys "$GPG_KEY_FPR" >/dev/null 2>&1; then
  echo "Key not found, importing from keys.openpgp.org..."
  gpg --keyserver hkps://keys.openpgp.org --recv-keys "$GPG_KEY_FPR"
else
  echo "Key already present."
fi

if [[ "$VERSION" == "latest" ]]; then
  echo "Resolving latest version for $ARTIFACT_ID from Maven Central..."
  META_URL="https://repo1.maven.org/maven2/${GROUP_PATH}/${ARTIFACT_ID}/maven-metadata.xml"
  META_CONTENT="$(curl -fsSL "$META_URL")"

  VERSION_RESOLVED="$(printf '%s\n' "$META_CONTENT" \
    | sed -n 's:.*<release>\(.*\)</release>.*:\1:p' \
    | head -n1)"

  if [[ -z "$VERSION_RESOLVED" ]]; then
    VERSION_RESOLVED="$(printf '%s\n' "$META_CONTENT" \
      | sed -n 's:.*<latest>\(.*\)</latest>.*:\1:p' \
      | head -n1)"
  fi

  if [[ -z "$VERSION_RESOLVED" ]]; then
    echo "Failed to resolve latest version from $META_URL" >&2
    exit 1
  fi
else
  VERSION_RESOLVED="$VERSION"
fi

echo "Using version $VERSION_RESOLVED"

BASE_URL="https://repo1.maven.org/maven2/${GROUP_PATH}/${ARTIFACT_ID}/${VERSION_RESOLVED}"

DL_DIR="$(mktemp -d ".verify-kotlin-${ARTIFACT_ID}-${VERSION_RESOLVED}-XXXX")"
echo "Using temp directory $DL_DIR"

cleanup() {
  echo "Cleaning up $DL_DIR"
  rm -rf "$DL_DIR"
}
trap cleanup EXIT

echo "Downloading artifacts from $BASE_URL"
curl -fsSL -o "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}-sources.jar"      "$BASE_URL/${ARTIFACT_ID}-${VERSION_RESOLVED}-sources.jar"
curl -fsSL -o "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}-sources.jar.asc"  "$BASE_URL/${ARTIFACT_ID}-${VERSION_RESOLVED}-sources.jar.asc"
curl -fsSL -o "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}.pom"              "$BASE_URL/${ARTIFACT_ID}-${VERSION_RESOLVED}.pom"
curl -fsSL -o "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}.pom.asc"          "$BASE_URL/${ARTIFACT_ID}-${VERSION_RESOLVED}.pom.asc"

echo "Verifying signatures..."
gpg --verify "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}-sources.jar.asc" "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}-sources.jar"
gpg --verify "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}.pom.asc"         "$DL_DIR/${ARTIFACT_ID}-${VERSION_RESOLVED}.pom"

echo "✅ All good for $ARTIFACT_ID $VERSION_RESOLVED"
