#!/bin/bash

#set -x
set -e
STUDENT_JAR_DIR=${1:?"Please enter directory with student jars"};

MAIN="my.battleship.PlatformImpl"
STUDENT_CLASS="student.player.TemplatePlayer"
SOURCE_DIR="student_java_source"

MY_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

BATTLE_JAR="$MY_HOME/../target/battleship.jar"

JAR_COUNT=0

rm -rf "$SOURCE_DIR"

# Recompile function in case student didn't submit jar with class file
function recompile()
{
  rm -rf "$STUDENT_JAR_DIR/temp"*
  
  mkdir "$STUDENT_JAR_DIR/temp"
  unzip "$1" -d "$STUDENT_JAR_DIR/temp"
  source=$(find "$STUDENT_JAR_DIR/temp/student" -name "*.java")
  javac -cp "$BATTLE_JAR" -sourcepath "$STUDENT_JAR_DIR/temp" $source

  cd "$STUDENT_JAR_DIR/temp"
  zip -r "../temp.jar" .
  cd -
}


# Loop through all jars in provided directory
find "$STUDENT_JAR_DIR" -type f -name "*.jar" -print0 | while IFS= read -r -d '' jar; 
do
  # Get name without spaces if students were evil enough to submit a jar with spaces
  new_name="$(echo "$jar" | sed -e "s/[[:space:]]/_/g ; s/.*\///g")"
  echo "Running $JAR_COUNT: $new_name"

  # Always recompile source
  #recompile "$jar"
  #jar="$STUDENT_JAR_DIR/temp.jar"

  # Ensure jar contains class file, if not, recompile 
  num_classes="$(jar tf "$jar" | grep -c class | tr -d '\n')"

  if [[ "$num_classes" -eq 0 ]]; then
    recompile "$jar"
    jar="$STUDENT_JAR_DIR/temp.jar"
  fi

  cp "$jar" current-player.jar

  # Run player against boards
  #java -cp "$BATTLE_JAR:current-player.jar" "$MAIN" --boards "$MY_HOME/../testBoards" --player "$STUDENT_CLASS" --timeout 5000
  #java -cp "$BATTLE_JAR:current-player.jar" "$MAIN" --boards "$MY_HOME/../mayo_1_boards" --player "$STUDENT_CLASS" --timeout 5000 > player.log
  java -cp "$BATTLE_JAR:current-player.jar" "$MAIN" --boards "$MY_HOME/../mayo_1_boards" --player "$STUDENT_CLASS" --timeout 5000 > player.log

  cat player.log | grep -E "^[0-9]" | grep -E "Game|winner" | grep -v "Game is already over" > player.out

  player_name="$(cat player.out | grep winner | sed -e "s/.*\] //g ; s/ is the winner.*//g" | uniq)"
  
  # Extract boards and shot totals; save to file
  sed -e "s/.*board: //g ; s/[[:space:]]---.*//g ; s/.*Shots://g" player.out > board_and_shots.out

  # Create csv strings
  boards="Name,jar"
  shots="$player_name,$new_name"

  linecount=0

  while read -r line 
  do
    if [[ $((linecount % 2)) == 0 ]]; then
      boards="$boards,$line"
    else
      shots="$shots,$line"
    fi
    (( linecount += 1 ))
  done < board_and_shots.out
  
  # Store results in our csv file
  if [[ "$JAR_COUNT" -eq 0 ]]; then
    echo "$boards" > results.csv 
  fi
 
  echo "$shots" >> results.csv

  (( JAR_COUNT += 1))
done

echo
echo "Please see result.csv for player results."
echo

rm -rf "$STUDENT_JAR_DIR/temp"* current-player.jar player.out player.log board_and_shots.out
