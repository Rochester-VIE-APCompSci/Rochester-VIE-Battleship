#!/bin/bash

scriptdir="$( cd "$( dirname $BASH_SOURCE[0] )" && pwd )"

STUDENT_SOURCE_DIR=${1:?"Please enter directory with student java source files."};

battleship_jar="$scriptdir/../target/battleship.jar"

mkdir -p "$STUDENT_SOURCE_DIR/student/player"
cd "$STUDENT_SOURCE_DIR"
for entry in `ls *.java`
do
    # Assumes that TemplatePlayer.java files are prefixed with 'studentName-', e.g. 'rebeccad-TemplatePlayer.java
    IFS='-' read -ra separated <<< "$entry"
    echo ${separated[0]}
    
    # Replit files don't have the package in them, so add it in
    echo "package student.player;" > "student/player/TemplatePlayer.java"
    cat $entry >> "student/player/TemplatePlayer.java"

    javac -cp "$battleship_jar" "student/player/TemplatePlayer.java"
    jar cf ${separated[0]}-battleship.jar ./student/player/*.class

    rm -rf student/player/*
done
cd -

