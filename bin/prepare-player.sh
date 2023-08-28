#!/bin/bash

scriptdir="$( cd "$(dirname $0)" && pwd )"

sed -e "s|\"\"||g" "$scriptdir/../src/main/java/student/player/TemplatePlayer.java" > "$scriptdir/../target/TemplatePlayer.java"
