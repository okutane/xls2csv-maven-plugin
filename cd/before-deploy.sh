#!/usr/bin/env bash
if ([ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]) || [ -n "$TRAVIS_TAG" ]; then
  openssl aes-256-cbc -K $encrypted_eec546499c77_key -iv $encrypted_eec546499c77_iv -in cd/signingkey.asc.enc -out cd/signingkey.asc -d
  gpg --fast-import --batch cd/signingkey.asc
fi
