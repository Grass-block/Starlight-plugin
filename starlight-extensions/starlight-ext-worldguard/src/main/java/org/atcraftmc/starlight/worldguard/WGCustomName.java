package org.atcraftmc.starlight.worldguard;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.data.RegionKey;

import java.util.Objects;

@ApplicationModule(id = "wg-custom-name", description = "Allows custom display names for WorldGuard regions")
public final class WGCustomName extends BukkitAbstractModule {
    public static final String DEFAULT_VALUE = "__default__";
    public static final DocumentField<String> REGION_CUSTOM_NAME = DocumentField.string("custom-name", DEFAULT_VALUE);
    private final PlotRenameCommand cmd = new PlotRenameCommand();

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-custom-name:" + id);
    }

    @Override
    public void enable() {
        WGCommandService.COMMAND.registerSubCommand(this.cmd);

        WGRegionHUD.PIPELINE.addFirst("starlight:custom-name", (r, w, s) -> {
            var dom = WGExtraInfoServiceV2.instance().getData(r, w);

            if (!REGION_CUSTOM_NAME.exist(dom)) {
                var key = RegionKey.of(w, r);
                var h = WGExtraInfoService.getInstance().getDataHandle(key);

                if (h.has("custom-name")) {
                    REGION_CUSTOM_NAME.set(dom, h.getString("custom-name", DEFAULT_VALUE));
                }
            }

            var name = REGION_CUSTOM_NAME.get(dom);

            if (Objects.equals(name, DEFAULT_VALUE)) {
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

            if (!target.getOwners().getUniqueIds().contains(player.getUniqueId())) {
                WGCommandService.lang("rg-not-self").send(QLib.audience(context.getSender()), target.getId());
                return;
            }

            var data = WGExtraInfoServiceV2.instance().getData(target, player.getWorld());

            REGION_CUSTOM_NAME.set(data,line);

            lang("rg-rename").send(QLib.audience(context.getSender()), line);
        }
    }
}
