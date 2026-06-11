# VirtualAP

Turn your phone's WiFi chip into a **real router**.

VirtualAP is a Magisk/KernelSU/APatch module that creates a virtual access point
(`ap0`) directly on the phone's WiFi hardware, with:

- **Static gateway IP** (`192.168.42.1`) — unlike Android's built-in hotspot, the
  gateway address never changes. Port forwards, bookmarks and SSH configs stay valid forever.
- **Selectable upstream** — Mobile Data, WiFi, Ethernet… or `wg0`: start a tunnel in the
  official WireGuard app (kernel backend), pick `wg0` as upstream, and `ap0` becomes a
  **WireGuard hotspot**. Every connected client is tunneled.
- **Auto upstream detection** — by default VirtualAP reads netd's default-network
  routing rule (the kernel's ground truth for "which network has internet").
- **DHCP + DNS** for clients via dnsmasq.
- **5GHz channel-following** — the AP follows the STA's current channel
  (same-channel concurrency), the main reason 5GHz "randomly failed" in older tools.
- **Modern WebUI** (KernelSU/APatch native; Magisk needs a WebUI launcher).
- **~15MB payload** — a minimal Alpine chroot carrying only `hostapd`, `dnsmasq` and `iw`
  (many phones ship no `iw` binary; routing/firewall use Android's own `ip`/`iptables`).

## Build

```bash
# 1. Build the Alpine rootfs (needs docker; cross-builds arm64 via binfmt)
./rootfs-builder/build_rootfs.sh -a aarch64

# 2. Build the flashable zip (bundles rootfs + static busybox extracted from it)
./build_zip.sh v1
```

## CLI

After install, the `virtualap` command is on PATH:

```bash
virtualap start -s "My AP" -p "password123"          # auto upstream
virtualap start -s "My AP" -p "password123" -o wg0   # WireGuard hotspot
virtualap start -s "My AP" -p "password123" -b 5     # 5GHz (follows STA channel)
virtualap status
virtualap stop
```

Saved settings (`/data/local/virtualap/ap.conf`) are reused when flags are omitted —
which is also how "Run at boot" works.

## How it routes

```
client → ap0 (192.168.42.1, hostapd) 
       → ip rule pref 7010: from all iif ap0 lookup <upstream table>
       → MASQUERADE (-s 192.168.42.0/24 ! -d 192.168.42.0/24)
       → internet (or WireGuard tunnel)
replies → ip rule pref 7000: to 192.168.42.0/24 lookup main → ap0
```

Pinned rule priorities sit above netd's entire range (10000+), so VPN catch-all
rules can never hijack AP client traffic — and above Android's `32000: from all
unreachable` guard that silently killed unpinned rules in older implementations.

## License

MIT — Copyright (c) 2026 ravindu644
