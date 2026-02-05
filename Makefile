.PHONY: changeset version changelog changeset-status prepare-release

changeset:
	./changeset.sh add

version:
	./gradlew changesetsVersion --no-daemon

changelog:
	./gradlew changesetsChangelog --no-daemon

prepare-release: version changelog

changeset-status:
	./gradlew changesetsStatus

