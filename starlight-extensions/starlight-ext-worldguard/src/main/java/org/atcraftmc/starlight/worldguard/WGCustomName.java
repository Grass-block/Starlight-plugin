package org.atcraftmc.starlight.worldguard;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.api.RegionKey;
import org.atcraftmc.starlight.worldguard.api.WGCustomNameAPI;
import org.atcraftmc.starlight.worldguard.data.RegionKey_L;

import java.util.Objects;

@ApplicationModule(id = "wg-custom-name", description = "Allows custom display names for WorldGuard regions")
public final class WGCustomName extends BukkitAbstractModule {
    private final PlotRenameCommand cmd = new PlotRenameCommand();

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-custom-name:" + id);
    }

    @Override
    public void enable() {
        WGCommandService.COMMAND.registerSubCommand(this.cmd);

        WGRegionHUD.PIPELINE.addFirst("starlight:custom-name", (r, w, s) -> {
            var name = WGCustomNameAPI.getRegionCustomName(RegionKey.fromRegion(w,r));

            if (Objects.equals(name, WGCustomNameAPI.DEFAULT_VALUE)) {
                return s;
            }

            return s.replace("{name}", name + "{;}");
        });
    }

    @Override
    public void disable() {
        WGCommandService.COMMAND.unregisterSubCommand(cmd);

        WGRegionHUD.PIPELINE.remove("starlight:custom-name");
    }

    @BukkitCommand(name = "rename", permission = "+starlight.worldguard.rename")
    public static final class PlotRenameCommand extends StandaloneCommand {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "[name...]");
        }

        @Override
        public void execute(CommandExecution context) {
            var t = WGCommandService.getManageableRegion(context);

            if (t.isEmpty()) {
                return;
            }

            var player = context.requireSenderAsPlayer();
            var target = t.get();
            var line = context.requireRemainAsParagraph(0, true);
            WGCustomNameAPI.setRegionCustomName(RegionKey.fromRegion(player,target),line);

            lang("rg-rename").send(QLib.audience(context.getSender()), line);
        }
    }
}
