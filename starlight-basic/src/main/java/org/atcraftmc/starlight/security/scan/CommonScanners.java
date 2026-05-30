package org.atcraftmc.starlight.security.scan;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused") //provide api
public interface CommonScanners {
    Map<String, ScannerInstance> SCANNERS = new HashMap<>();

    String TYPE_BUKKIT = "bukkit";
    String TYPE_EXECUTE = "execute";
    String TYPE_REFLECT = "reflect";

    ScannerInstance BUKKIT_OP = register("bukkit-op", b -> {
        b.level(ScannerLevel.DANGEROUS);
        b.type(TYPE_BUKKIT);
        b.match("org.bukkit.entity.Player#setOp(boolean)");
    });
    ScannerInstance BUKKIT_COMMAND = register("bukkit-command", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_BUKKIT);
        b.match(
                "org.bukkit.Bukkit#dispatchCommand(org.bukkit.command.CommandSender,java.lang.String)"
        );
    });
    ScannerInstance RUNTIME_EXEC_STRING = register("runtime-exec-string", b -> {
        b.level(ScannerLevel.DANGEROUS);
        b.type(TYPE_EXECUTE);
        b.match("java.lang.Runtime#exec(java.lang.String)");
    });
    ScannerInstance RUNTIME_EXEC_ARRAY = register("runtime-exec-array", b -> {
        b.level(ScannerLevel.DANGEROUS);
        b.type(TYPE_EXECUTE);
        b.match("java.lang.Runtime#exec(java.lang.String[])");
    });
    ScannerInstance RUNTIME_EXEC_ENV = register("runtime-exec-env", b -> {
        b.level(ScannerLevel.DANGEROUS);
        b.type(TYPE_EXECUTE);
        b.match(
                "java.lang.Runtime#exec(java.lang.String,java.lang.String[])"
        );
    });
    ScannerInstance SCRIPT_EVAL = register("script-eval", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_EXECUTE);
        b.match(
                "javax.script.ScriptEngine#eval(java.lang.String)"
        );
    });
    ScannerInstance SCRIPT_ENGINE = register("script-engine", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_EXECUTE);
        b.match(
                "javax.script.ScriptEngineManager#getEngineByName(java.lang.String)"
        );
    });
    ScannerInstance SYSTEM_LOAD = register("system-load", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_EXECUTE);
        b.match("java.lang.System#load(java.lang.String)");
    });
    ScannerInstance SYSTEM_LOAD_LIBRARY = register("system-load-library", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_EXECUTE);
        b.match("java.lang.System#loadLibrary(java.lang.String)");
    });
    ScannerInstance PROCESS_BUILDER = register("process-builder", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_EXECUTE);
        b.match(
                "java.lang.ProcessBuilder#<init>(java.lang.String[])"
        );
    });
    ScannerInstance UNSAFE_GET = register("unsafe-get", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match("sun.misc.Unsafe#getUnsafe()");
    });
    ScannerInstance UNSAFE_DEFINE_CLASS = register("unsafe-define-class", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "sun.misc.Unsafe#defineClass(java.lang.String,byte[],int,int,java.lang.ClassLoader,java.security.ProtectionDomain)"
        );
    });
    ScannerInstance URL_CLASSLOADER = register("url-classloader", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match("java.net.URLClassLoader#<init>(java.net.URL[])");
    });
    ScannerInstance METHOD_INVOKE = register("method-invoke", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.reflect.Method#invoke(java.lang.Object,java.lang.Object[])"
        );
    });
    ScannerInstance CLASS_FORNAME = register("class-forname", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match("java.lang.Class#forName(java.lang.String)");
    });
    ScannerInstance LOOKUP_FIND_VIRTUAL = register("lookup-find-virtual", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.invoke.MethodHandles$Lookup#findVirtual(java.lang.Class,java.lang.String,java.lang.invoke.MethodType)"
        );
    });
    ScannerInstance CLASSLOADER_DEFINE_CLASS = register("classloader-define-class", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.ClassLoader#defineClass(byte[],int,int)"
        );
    });
    ScannerInstance CLASSLOADER_DEFINE_CLASS_NAMED = register("classloader-define-class-named", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.ClassLoader#defineClass(java.lang.String,byte[],int,int)"
        );
    });
    ScannerInstance LOOKUP_DEFINE_CLASS = register("lookup-define-class", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.invoke.MethodHandles$Lookup#defineClass(byte[])"
        );
    });
    ScannerInstance LOOKUP_FIND_STATIC = register("lookup-find-static", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.invoke.MethodHandles$Lookup#findStatic(java.lang.Class,java.lang.String,java.lang.invoke.MethodType)"
        );
    });
    ScannerInstance LOOKUP_FIND_SPECIAL = register("lookup-find-special", b -> {
        b.level(ScannerLevel.WARN);
        b.type(TYPE_REFLECT);
        b.match(
                "java.lang.invoke.MethodHandles$Lookup#findSpecial(java.lang.Class,java.lang.String,java.lang.invoke.MethodType,java.lang.Class)"
        );
    });

    static ScannerInstance register(String id, Consumer<ScannerInstance.Builder> builder) {
        var obj = ScannerInstance.create(id, builder);
        SCANNERS.put(id, obj);

        return obj;
    }
}
