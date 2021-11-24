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

check-updates:
	./gradlew dependencyUpdates

read-json:
	./build/install/app/bin/app src/test/resources/file3.json src/test/resources/file4.json

read-json-plain:
	./build/install/app/bin/app -f plain src/test/resources/file3.json src/test/resources/file4.json

read-json-json:
	./build/install/app/bin/app -f json src/test/resources/file3.json src/test/resources/file4.json

read-yaml:
	./build/install/app/bin/app src/test/resources/file1.yml src/test/resources/file2.yml

act:
	act --secret-file my.secrets
