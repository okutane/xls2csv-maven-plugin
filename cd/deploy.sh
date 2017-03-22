#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'impl' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn deploy --settings cd/mvnsettings.xml
fi

if [ -n "$TRAVIS_TAG" ]; then
    mvn deploy -P sign --settings cd/mvnsettings.xml
fi