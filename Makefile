install:
	./gradlew clean install

run-dist:
	./build/install/app/bin/app

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

build:
	./gradlew build

.PHONY: build

make-migrations:
	./gradlew generateMigrations

check-updates:
	./gradlew dependencyUpdates

act:
	act --secret-file my.secrets
