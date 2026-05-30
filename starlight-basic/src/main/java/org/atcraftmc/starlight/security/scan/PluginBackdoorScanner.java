package org.atcraftmc.starlight.security.scan;

import me.gb2022.gluon.module.ApplicationModule;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@ApplicationModule(id = "plugin-backdoor-scanner", description = "Scans plugins for malicious backdoor code patterns")
@CommandProvider(PluginBackdoorScanner.ScanPluginsCommand.class)
public final class PluginBackdoorScanner extends BukkitAbstractModule {

    @Override
    public void enable() throws Exception {
    }

    @Override
    public void disable() throws Exception {
    }

    public static final class ConsoleLogger implements ScanCallback {
        Logger LOGGER = SLPluginEnvironment.createLogger("PluginScanner");

        @Override
        public void onFound(ScannerInstance scanner, String name, int line, String owner, String method, String md, MethodPattern target) {
            LOGGER.info("");
            LOGGER.info("========[Scanned]========");
            LOGGER.info("Type: {}({}) [{}]", scanner.type(), scanner.id(), scanner.level());
            LOGGER.info("Location: {}:{}", name, line);
            LOGGER.info("Operation: {}#{}", owner, method, md);
            LOGGER.info("Match: {}", scanner.invocation());
            LOGGER.info("=========================");
            LOGGER.info("");
        }
    }

    @BukkitCommand(name = "scan-plugins", op = true)
    public static final class ScanPluginsCommand extends ModuleCommand<PluginBackdoorScanner> {

        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "plugin-class");
            suggestion.matchArgument(0, "plugin-class", c -> {
                c.suggest(1, Arrays.stream(Objects.requireNonNull(Bukkit.getPluginsFolder().listFiles()))
                        .filter(File::isFile)
                        .map(File::getName)
                        .collect(Collectors.toSet()));

                if (c.getBuffer().size() < 2) {
                    return;
                }

                var file = c.getBuffer().get(1);

                if (!file.endsWith(".jar")) {
                    return;
                }

                var f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/" + file);

                if (!f.exists() || f.isDirectory()) {
                    return;
                }

                var prefix = c.getBuffer().size() > 2 ? c.getBuffer().get(2) : "";
                var pf = prefix.substring(0, prefix.lastIndexOf('/'));
                var node = ClassTreeNode.scanClasses(f);
                var tgt = ClassTreeNode.find(node, pf);

                if (tgt == null) {
                    return;
                }

                c.suggest(2, tgt.childrenNames().map(s -> pf + "/" + s).collect(Collectors.toSet()));
            });
            suggestion.matchArgument(0, "plugin", (c) -> {
                var v = Arrays.stream(Objects.requireNonNull(Bukkit.getPluginsFolder().listFiles()))
                        .filter(File::isFile)
                        .map(File::getName)
                        .collect(Collectors.toSet());

                c.suggest(1, v);
            });
        }

        @Override
        public void execute(CommandExecution context) {
            var lang = this.getLanguage();
            var audience = QLib.audience(context.getSender());

            if (!context.hasArgumentAt(0)) {
                audience.sendMessage(lang.item("hint").message());
                return;
            }

            switch (context.requireArgumentAt(0)) {
                case "plugin" -> {
                    var file = context.requireArgumentAt(1);

                    if (!file.endsWith(".jar")) {
                        return;
                    }

                    var f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/" + file);

                    if (!f.exists() || f.isDirectory()) {
                        return;
                    }

                    BytecodeScanner.scanFile(f, CommonScanners.SCANNERS.values(), new ConsoleLogger());
                }

                case "plugin-class" -> {
                    var file = context.requireArgumentAt(1);
                    var name = context.requireArgumentAt(2);

                    if (!file.endsWith(".jar")) {
                        return;
                    }

                    var f = new File(Bukkit.getPluginsFolder().getAbsolutePath() + "/" + file);

                    if (!f.exists() || f.isDirectory()) {
                        return;
                    }

                    try (var jf = new JarFile(f)) {
                        try (var i = jf.getInputStream(jf.getJarEntry(name))) {
                            BytecodeScanner.scan(i.readAllBytes(), CommonScanners.SCANNERS.values(), new ConsoleLogger());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}


