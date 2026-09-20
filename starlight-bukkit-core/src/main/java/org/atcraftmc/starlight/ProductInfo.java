package org.atcraftmc.starlight;

import me.gb2022.commons.TriState;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.core.TextSender;
import org.atcraftmc.starlight.core.placeholder.BukkitPlaceHolders;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.internal.ProductService;
import org.atcraftmc.starlight.util.version.VersionInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("deprecation")
public interface ProductInfo {
    Properties BUILD_CONSTANTS = loadBuildConstants();
    VersionInfo VERSION = VersionInfo.parse(buildConstant("version"));
    String BUILD_TIME = buildConstant("build-time");
    String CORE_ID = "starlight-core";
    int API_VERSION = 82;
    int BSTATS_ID = 22683;

    static String versionIdentifier() {
        return "starlight-bukkit-%s, gluon-1.6.3".formatted(VERSION);
    }

    static String version() {
        return VERSION.toString();
    }

    static Properties loadBuildConstants() {
        var prop = new Properties();
        try {
            prop.load(ProductInfo.class.getResourceAsStream("/product-info.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }

    static String buildConstant(String key) {
        return BUILD_CONSTANTS.getProperty(key);
    }

    static String textLogo() {
        return "{color(purple)}Starlight {color(gray)} - {color(white)}v%s".formatted(version());
    }

    static String logo() {
        return ChatColor.translateAlternateColorCodes('&', """
                &d&l 一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一
                &d&l       ____ __              __ _        __   __
                &d&l     / __// /_ ___ _ ____ / /(_)___ _ / /  / /_
                &d&l    _\\ \\ / __// _ `// __// // // _ `// _ \\/ __/
                &d&l   /___/ \\__/ \\_,_//_/  /_//_/ \\_, //_//_/\\__/
                &d&l                          /___/                    -&fv%s
                &7&l   Artifact by &fGrassBlock2022&7, Copyright &f[C]A.T.C Group 2025.
                &d&l 一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一
                """.formatted(version()));
    }

    static void sendStatsDisplay(CommandSender sender) {
        var dom = """
                 &e 一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一
                 &e Statistics:
                 &7 Version: &f%s
                 &7 BuildTime: &f%s
                 &7 Modules: &b%d&7/&f%d {click(command,/starlight module list);color(gold)}[view]{;}
                 &7 Packages: &b%d&7/&f%d {click(command,/starlight package list);color(gold)}[view]{;}
                 &7 Services: &b%d&7
                
                 &7 CoreUA: &f%s
                 &7 InstanceID: {click(copy,%s);color(gold)}[copy]{;}
                 &7 ProductID: {click(copy,%s);color(gold)}[copy]{;}
                 &e 一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一
                """;

        var mm = StarlightBukkitCore.instance().getGluonContext().getModuleManager();
        var sm = StarlightBukkitCore.instance().getGluonContext().getServiceManager();
        var pm = StarlightBukkitCore.instance().getGluonContext().getPackageManager();

        var text = ChatColor.translateAlternateColorCodes('&', dom.formatted(VERSION, BUILD_TIME, mm.getIdsByStatus(TriState.TRUE).size(), mm.getModules().size(), pm.getIdsByStatus(TriState.TRUE).size(), pm.getAllPackages().size(), sm.all().size(), versionIdentifier(), Starlight.instance().getInstanceUUID(), ProductService.getSystemIdentifier()));
        TextSender.sendBlock(sender, QLib.textBuilder().build(PlaceHolderService.format(text, BukkitPlaceHolders.quarkStats())));
    }

    static void sendInfoDisplay(CommandSender sender) {
        String s = """
                {logo}
                
                 A plugin containing everything you need. :D
                
                 Website & Docs: {#aqua}https://dev.atcraftmc.cn/starlight{#reset}
                 Official Release: {#aqua}https://modrinth.com/plugin/starlight-plugin{#reset}
                 Contact: {#aqua}grassblock2022@atcraftmc.cn{#reset}
                
                {#purple}> Credits & Special thanks:
                {#white} - GrassBlock2022: {#gray}Core developer.
                {#white} - DeepSeek-R1: {#gray}Documentation&translate.
                {#white} - IdealMC/Mipa: {#gray}Production environment test.
                {#white} - Modrinth: {#gray}Publishing and version check service.
                {#white} - OpenAI/ChatGPT: {#gray}Technical assistant.
                
                 {#white}Copyright @A.T.C Group[CN,MAINLAND]. All Right Reserved.
                {#purple}一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一
                """;
        if (ProductService.isActivated()) {
            s = s.replace("{activate}", "已激活");
        } else {
            s = s.replace("{activate}", "未激活");
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            String prefix = "{#purple}一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一\n";

            TextSender.sendBlock(sender, QLib.textBuilder().build(prefix + s.replace("{logo}", textLogo())));
        } else {
            for (var s2 : QLib.textEngine().renderString(s.replace("{logo}", logo())).split("\n")) {
                sender.sendMessage(s2);
            }
        }
    }
}
