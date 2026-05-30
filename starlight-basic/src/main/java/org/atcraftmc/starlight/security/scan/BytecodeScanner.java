package org.atcraftmc.starlight.security.scan;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.bytebuddy.jar.asm.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.jar.JarFile;

interface BytecodeScanner {
    Cache<String, ClassTreeNode> CACHED_FILES = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(3)).build();

    static String toDescriptor(String type) {
        return switch (type) {

            case "void" -> "V";
            case "boolean" -> "Z";
            case "byte" -> "B";
            case "char" -> "C";
            case "short" -> "S";
            case "int" -> "I";
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            default -> {
                if (type.endsWith("[]")) {
                    yield "[" + toDescriptor(type.substring(0, type.length() - 2));
                }

                yield "L" + type.replace('.', '/') + ";";
            }
        };
    }

    static void scan(Class<?> clazz, Collection<ScannerInstance> scanners, ScanCallback callback) {
        var resource = "/" + clazz.getName().replace('.', '/') + ".class";

        try (var in = clazz.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Failed to read class: " + clazz.getName());
            }

            var bytes = in.readAllBytes();

            scan(bytes, scanners, callback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void scan(byte[] bytes, Collection<ScannerInstance> scanners, ScanCallback callback) {
        var reader = new ClassReader(bytes);

        reader.accept(new ClassVisitor(Opcodes.ASM9) {

            String className;

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                className = name;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {

                    int line = -1;

                    @Override
                    public void visitLineNumber(int line, Label start) {
                        this.line = line;
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String methodName, String methodDescriptor, boolean isInterface) {
                        for (var scanner : scanners) {
                            var target = scanner.invocation();

                            var om = owner.equals(target.owner());
                            var nm = methodName.equals(target.name());
                            var dm = methodDescriptor.startsWith(target.desc());

                            if (om && nm && dm) {
                                callback.onFound(scanner, className, line, owner, methodName, methodDescriptor, target);
                            }

                            super.visitMethodInsn(opcode, owner, methodName, methodDescriptor, isInterface);
                        }
                    }
                };
            }

        }, ClassReader.SKIP_FRAMES);
    }

    static void scanFile(File f, Collection<ScannerInstance> scanners, ScanCallback callback) {
        try (var jar = new JarFile(f)) {

            var entries = jar.entries();

            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                var name = entry.getName();

                if (!name.endsWith(".class")) {
                    continue;
                }

                if (name.equals("module-info.class")) {
                    continue;
                }

                if (name.equals("package-info.class")) {
                    continue;
                }

                try (var in = jar.getInputStream(entry)) {
                    scan(in.readAllBytes(), scanners, callback);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
