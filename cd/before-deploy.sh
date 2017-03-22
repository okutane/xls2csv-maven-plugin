#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'impl' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -K $encrypted_eec546499c77_key -iv $encrypted_eec546499c77_iv -in codesigning.asc.enc -out codesigning.asc -d
  gpg --fast-import cd/signingkey.asc
fi

if [ -n "$TRAVIS_TAG" ]; then
  openssl aes-256-cbc -K $encrypted_eec546499c77_key -iv $encrypted_eec546499c77_iv -in codesigning.asc.enc -out codesigning.asc -d
  gpg --fast-import cd/signingkey.asc
fi