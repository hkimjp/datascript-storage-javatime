#!/usr/bin/env bash

if [ -x "/run/current-system/sw/bin/sed" ]; then
  SED=/run/current-system/sw/bin/sed
else
  SED=/usr/bin/sed
fi

VER=$1

${SED} -i.bak "/(def version /c\
(def version \"${VER}\")" src/hkimjp/datascript.clj
