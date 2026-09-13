package org.atcraftmc.starlight.util.version;

import me.gb2022.commons.TriState;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionInfo {
    private static final Pattern PATTERN = Pattern.compile(
            "^(?:(?<product>[^-]+)-)?(?<version>\\d+\\.\\d+\\.\\d+)(?:-snapshot-(?<snapshot>\\d+))?$");
    public final String product;
    public final int major;
    public final int minor;
    public final int patch;
    public final boolean snapshot;
    public final int snapshotId;

    public VersionInfo(String product, int major, int minor, int patch, boolean snapshot, int snapshotId) {
        this.product = product;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.snapshot = snapshot;
        this.snapshotId = snapshotId;
    }

    public static VersionInfo parse(String input) {
        Matcher m = PATTERN.matcher(input);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid version: " + input);
        }

        String product = m.group("product"); // 可能为 null

        String[] parts = m.group("version").split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        String snap = m.group("snapshot");
        boolean isSnapshot = snap != null;
        int snapshotId = isSnapshot ? Integer.parseInt(snap) : -1;

        return new VersionInfo(product != null ? product : "", major, minor, patch, isSnapshot, snapshotId);
    }

    public TriState compareTo(VersionInfo o) {
        if (!Objects.equals(this.product, o.product)) {
            return TriState.UNKNOWN;
        }

        if (this.major > o.major) {
            return TriState.TRUE;
        }

        if (this.major < o.major) {
            return TriState.FALSE;
        }

        if (this.minor > o.minor) {
            return TriState.TRUE;
        }

        if (this.minor < o.minor) {
            return TriState.FALSE;
        }

        if (this.patch > o.patch) {
            return TriState.TRUE;
        }

        if (this.patch < o.patch) {
            return TriState.FALSE;
        }

        // version equals.

        if (!this.snapshot && !o.snapshot) {
            return TriState.TRUE;
        }

        if (!this.snapshot) {
            return TriState.TRUE;
        }

        if (!o.snapshot) {
            return TriState.FALSE;
        }

        if (o.snapshotId >= this.snapshotId) {
            return TriState.TRUE;
        }

        return TriState.FALSE;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();


        if (!this.product.isEmpty()) {
            sb.append(this.product);
            sb.append("-");
        }

        sb.append(this.major);
        sb.append(".");
        sb.append(this.minor);
        sb.append(".");
        sb.append(this.patch);

        if (this.snapshot) {
            sb.append("-snapshot-");
            sb.append(this.snapshotId);
        }

        return sb.toString();
    }
}