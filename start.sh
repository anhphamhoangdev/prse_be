#!/bin/bash

# Configuration
export APPPATH="/home/ute/prse/be"
export CONFIG_FILE="$APPPATH/conf/config.ini"
export JAVA_OPTS="-Xms512m -Xmx1024m -Dapppath=$APPPATH -Dfile.encoding=UTF-8"
export PID_FILE="$APPPATH/app.pid"

# Function to start the application
start() {
    if [ -f "$PID_FILE" ]; then
        echo "Application is already running (PID: $(cat $PID_FILE))"
    else
        echo "Starting application..."
        cd "$APPPATH"
        nohup java $JAVA_OPTS -Dconfig.file=$CONFIG_FILE -jar prse_be.jar > /dev/null 2>&1 &
        echo $! > "$PID_FILE"
        echo "Application started with PID $(cat $PID_FILE)"
    fi
}

# Function to stop the application
stop() {
    if [ -f "$PID_FILE" ]; then
        echo "Stopping application..."
        kill $(cat "$PID_FILE")
        rm "$PID_FILE"
        echo "Application stopped"
    else
        echo "Application is not running"
    fi
}

# Function to restart the application
restart() {
    stop
    sleep 2
    start
}

# Main script logic
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac

exit 0