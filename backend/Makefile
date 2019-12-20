export $(shell if [ -f .env ]; then sed 's/=.*//' .env; fi)

.PHONY: lint build test run clean secret

lint:
	@./gradlew ktlint

build:
	@./gradlew build

test:
	@./gradlew test

run:
	@./gradlew bootRun

secret:
	@curl -s https://raw.githubusercontent.com/oursky/devsecops-secret/master/generate-secret.sh \
        | bash -s -- \
            -i .env.example .env