cp --recursive /app/* /home/gradle
gradle :$TARGET:build :$TARGET:run --no-daemon
