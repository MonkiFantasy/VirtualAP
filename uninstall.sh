#!/system/bin/sh
# Stop the AP + chroot and remove all VirtualAP data.
VAP_DIR="/data/local/virtualap"
[ -f "$VAP_DIR/start-ap" ] && sh "$VAP_DIR/start-ap" stop >/dev/null 2>&1
[ -f "$VAP_DIR/vap.sh" ] && sh "$VAP_DIR/vap.sh" stop -s >/dev/null 2>&1
rm -rf "$VAP_DIR"
