GROUP_PATH      = com/turnkey
ARTIFACT_ID     ?= sdk-kotlin
VERSION         ?= latest
GPG_KEY_FPR     ?= B9AF0D750809B7A308142AE4C66031ABC6FF180B
ARTIFACT_IDS    = sdk-kotlin crypto encoding http passkey stamper types

VERIFY_KOTLIN_SCRIPT ?= ./scripts/verify-kotlin.sh

.PHONY: verify-kotlin verify-all-kotlin

verify-kotlin:
	@GROUP_PATH="$(GROUP_PATH)" \
	 GPG_KEY_FPR="$(GPG_KEY_FPR)" \
	 ARTIFACT_ID="$(ARTIFACT_ID)" \
	 VERSION="$(VERSION)" \
	 bash "$(VERIFY_KOTLIN_SCRIPT)"

verify-all-kotlin:
	@for a in $(ARTIFACT_IDS); do \
	  echo "=============================="; \
	  echo "Verifying $$a (VERSION=$(VERSION))"; \
	  ARTIFACT_ID=$$a $(MAKE) --no-print-directory verify-kotlin; \
	done
