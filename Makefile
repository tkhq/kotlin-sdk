GROUP_PATH      = com/turnkey
ARTIFACT_ID     ?= sdk-kotlin
VERSION         ?= latest
GPG_KEY_FPR     ?= FEAB724C02DF9FDD03DAA418019F7030A511FF6F
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
