package org.atcgroup.starlight.bundle.security.scan;

public interface ScanCallback {
    void onFound(
            ScannerInstance scanner, String className, int line, String owner, String methodName, String methodDesc, MethodPattern target
    );
}
