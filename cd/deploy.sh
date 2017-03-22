#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'impl' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn deploy -P sign --settings cd/mvnsettings.xml
fi

if [ -n "$TRAVIS_TAG" ]; then
    mvn org.codehaus.mojo:versions-maven-plugin:2.3:set -DnewVersion=$TRAVIS_TAG
    mvn deploy -P sign --settings cd/mvnsettings.xml
fi