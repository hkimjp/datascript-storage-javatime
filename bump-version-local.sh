#!/usr/bin/env bash

if [ -x "/opt/homebrew/bin/gsed" ]; then
  SED=/opt/homebrew/bin/gsed
else
  SED=/usr/bin/sed
fi

VER=$1

${SED} -i.bak "/(def version /c\
(def version \"${VER}\")" src/hkimjp/datascript.clj
