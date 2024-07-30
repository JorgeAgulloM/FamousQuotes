#!/bin/bash

#
# Copyright (c) 2024. File developed for FamousQuotes App by Jorge Agulló Martín for SoftYorch
#

# Decrypt the file
mkdir $HOME/secrets
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$FAMOUS_QUOTES_KEY" \
--output $HOME/secrets/google-services.json app/google-services.json.gpg
#