#!/bin/bash
# We can try to run the app by compiling, building and packaging it, with this command:
#nohup java -jar /path/to/app/hello-world.jar > /path/to/log.txt 2>&1 &
#This option should be considered only if the following code does not work:
nohup ./gradlew bootRun > log.txt 2>&1 &
echo $! > ./pid.file
