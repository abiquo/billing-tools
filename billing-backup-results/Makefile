default: all
.PHONY: default

all: ci-build
.PHONY: all

ci-build:
	echo "Starting CI Build for the package abiquo-billing-backup-results"
	sudo chown -R jenkins:jenkins /opt/jenkins/workspace/
	mvn clean install -N
	mvn clean install
.PHONY: ci-build

bash:
.PHONY: bash

copy-artifacts:
ifndef TARGET_PATH
	$(error TARGET_PATH is undefined)
endif
	mkdir -p "${TARGET_PATH}"
	rm -rf "${TARGET_PATH}"/*
	mkdir -p "${TARGET_PATH}"/abiquo-billing-backup-results
	pwd
	cp -r target/billing-backup-results-*.jar src/main/resources/config/ src/main/resources/logback.xml src/main/resources/run.sh src/main/resources/schema.sql "${TARGET_PATH}"/abiquo-billing-backup-results
	pwd
	cd "${TARGET_PATH}" && tar cvfz abiquo-billing-backup-results.tar.gz abiquo-billing-backup-results
.PHONY: copy-artifacts