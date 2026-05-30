package org.atcraftmc.starlight.security.scan;

import java.util.regex.Pattern;

public record MethodPattern(String owner, String name, String desc) {
    public static MethodPattern parse(String input) {
        var pattern = Pattern.compile("^(.+)#([^(]+)\\((.*)\\)$");
        var matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal method style: " + input);
        }

        var owner = matcher.group(1).replace('.', '/');
        var methodName = matcher.group(2);
        var argsRaw = matcher.group(3);

        var desc = new StringBuilder("(");

        if (!argsRaw.isBlank()) {
            var args = argsRaw.split(",");

            for (var arg : args) {
                desc.append(BytecodeScanner.toDescriptor(arg.trim()));
            }
        }

        desc.append(")");

        return new MethodPattern(owner, methodName, desc.toString());
    }
}
