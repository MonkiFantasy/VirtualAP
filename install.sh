export ZIPFILE="$ZIPFILE"
export TMPDIR="$TMPDIR"

# source our functions
unzip -o "$ZIPFILE" 'META-INF/*' -d $TMPDIR >&2
. "$TMPDIR/META-INF/com/google/android/util_functions.sh"

SKIPMOUNT=false
PROPFILE=false
POSTFSDATA=false
LATESTARTSERVICE=true

print_modname() {
    echo " __   ___     _             _   _   ___ "
    echo " \ \ / (_)_ _| |_ _  _ __ _| | /_\ | _ \\"
    echo "  \ V /| | '_|  _| || / _\` | |/ _ \|  _/"
    echo "   \_/ |_|_|  \__|\_,_\__,_|_/_/ \_\_|  "
    echo "                                        "
    echo "            by @ravindu644              "
    echo " "
}

on_install() {
    # Detect root method and show warnings
    detect_root

    # Extract web interface files
    unzip -o "$ZIPFILE" 'webroot/*' -d $MODPATH >&2
    unzip -oj "$ZIPFILE" 'service.sh' -d $MODPATH >&2

    # Extract tools + rootfs
    setup_tools
    extract_rootfs
    create_symlink

    # Clear package cache to avoid conflicts
    rm -rf /data/system/package_cache/*
}

set_permissions() {
    # Set permissions for module files
    set_perm_recursive $MODPATH 0 0 0755 0644

    # Set permissions for runtime scripts
    set_perm "/data/local/virtualap/vap.sh" 0 0 0755
    set_perm "/data/local/virtualap/start-ap" 0 0 0755

    # Set permissions for module service script
    set_perm "$MODPATH/service.sh" 0 0 0755
}
