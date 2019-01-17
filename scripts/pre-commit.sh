#!/bin/sh

abort()
{
    echo >&2 '
***************
*** ABORTED ***
***************
'
    echo "An error occurred. Exiting..." >&2
    exit 1
}

trap 'abort' 0

set -e
set -o pipefail

# Add your script below....
# If an error occurs, the abort() function will be called.
#----------------------------------------------------------
# ===> Your script goes here

# pre-commit.sh
STASH_NAME="pre-commit-$(date +%s)"
git stash save -q --keep-index $STASH_NAME

# Run lintKotlin task
./gradlew lintKotlin

STASHES=$(git stash list)
if [[ $STASHES == "$STASH_NAME" ]]; then
  git stash pop -q
fi

# Done!
trap : 0

echo >&2 '
************
*** DONE *** 
************
'

