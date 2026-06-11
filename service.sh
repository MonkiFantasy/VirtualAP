#!/system/bin/sh
# VirtualAP boot service
# Starts the AP at boot when the user enabled "Run at boot" in the WebUI.

MODDIR=${0%/*}
VAP_DIR="/data/local/virtualap"
BOOT_FLAG="$VAP_DIR/boot-ap"
LOG_FILE="$VAP_DIR/logs/boot-service.log"

mkdir -p "$VAP_DIR/logs"

# Wait for boot to complete
while [ "$(getprop sys.boot_completed)" != "1" ]; do
    sleep 1
done

# Give WiFi/radio stack time to settle
sleep 25

if [ -f "$BOOT_FLAG" ] && [ "$(cat "$BOOT_FLAG" 2>/dev/null)" = "1" ]; then
    > "$LOG_FILE"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Boot service started" >> "$LOG_FILE"

    # start-ap reuses the saved ap.conf when no flags are given
    if su -c "sh $VAP_DIR/start-ap start" >> "$LOG_FILE" 2>&1; then
        echo "[$(date '+%Y-%m-%d %H:%M:%S')] AP started successfully" >> "$LOG_FILE"
        su -lp 2000 -c "cmd notification post -S bigtext -t 'VirtualAP Started' 'virtualap' 'Access point is up.'" 2>/dev/null
    else
        echo "[$(date '+%Y-%m-%d %H:%M:%S')] AP startup failed" >> "$LOG_FILE"
        su -lp 2000 -c "cmd notification post -S bigtext -t 'VirtualAP Failed' 'virtualap' 'AP startup failed. Check logs in the WebUI.'" 2>/dev/null
    fi
fi
