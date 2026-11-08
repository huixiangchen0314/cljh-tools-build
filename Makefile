.PHONY: clean compile test jar uberjar repl

VERSION := 0.1.0
JAR_FILE := target/cljh-tools-build-$(VERSION).jar

all: clean install

clean:
	clj -T:build clean

jar:
	clj -T:build jar

install: jar
	clj -T:build install
