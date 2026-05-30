package org.atcraftmc.starlight.security.scan;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class ClassTreeNode {
    private final Map<String, ClassTreeNode> children = new HashMap<>();

    private final String name;
    private boolean classNode;

    public ClassTreeNode(String name, boolean classNode) {
        this.name = name;
        this.classNode = classNode;
    }

    public String getName() {
        return name;
    }

    public Stream<String> childrenNames() {
        return children.values().stream().map((n)->{
            if(n.classNode){
                return n.getName()+".class";
            }
            return n.getName();
        });
    }

    public static ClassTreeNode find(ClassTreeNode root, String path) {
        if (path == null || path.isBlank()) {
            return root;
        }

        var parts = path.split("/");
        var current = root;

        for (var part : parts) {

            current = current.children.get(part);

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    public static ClassTreeNode scanClasses(File file) {

        try {
            return BytecodeScanner.CACHED_FILES.get(file.getAbsolutePath(), () -> {

                ClassTreeNode root = new ClassTreeNode("", true);

                try (var jar = new JarFile(file)) {

                    var entries = jar.entries();

                    while (entries.hasMoreElements()) {

                        JarEntry entry = entries.nextElement();

                        String name = entry.getName();

                        if (!name.endsWith(".class")) {
                            continue;
                        }

                        if (name.equals("module-info.class")) {
                            continue;
                        }

                        if (name.equals("package-info.class")) {
                            continue;
                        }

                        name = name.substring(0, name.length() - 6);

                        insert(root, name);
                    }
                }

                return root;
            });

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insert(ClassTreeNode root, String className) {
        var parts = className.split("/");

        var current = root;

        for (int i = 0; i < parts.length; i++) {
            var part = parts[i];

            current = current.children.computeIfAbsent(part, k -> new ClassTreeNode(k, false));

            if (i == parts.length - 1) {
                current.classNode = true;
            }
        }
    }
}
