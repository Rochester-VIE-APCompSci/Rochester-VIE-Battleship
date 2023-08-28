#!/bin/bash

STUDENT_JAR_DIR=${1:?"Please enter directory with student jars"};

SOURCE_DIR="student_java_source"

JAR_COUNT=0

rm -rf "$SOURCE_DIR"

# Loop through all jars in provided directory
find "$STUDENT_JAR_DIR" -type f -name "*.jar" -print0 | while IFS= read -r -d '' jar; 
do

  # Get name without spaces if students were evil enough to submit a jar with spaces
  new_name="$(echo "$jar" | sed -e "s/[[:space:]]/_/g ; s/.*\///g")"
  echo "Running $JAR_COUNT: $new_name"


  # Extract source code out for viewing later
  mkdir -p "$SOURCE_DIR/$new_name"
  unzip "$jar" "*.java" -d "$SOURCE_DIR/$new_name" || echo "No java source files found."
  
done

echo
echo "See $SOURCE_DIR for corresponding source code"
echo
