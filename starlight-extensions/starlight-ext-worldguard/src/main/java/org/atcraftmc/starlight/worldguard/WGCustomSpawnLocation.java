package org.atcraftmc.starlight.worldguard;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentFieldCodec;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.joml.Vector3d;
import org.joml.Vector3f;

@ApplicationModule(id = "wg-custom-spawn")
public class WGCustomSpawnLocation extends BukkitAbstractModule {
    public static final double UNKNOWN_V = Double.NaN;
    public static final Vector3d UNKNOWN_POS = new Vector3d(Double.NaN, Double.NaN, Double.NaN);
    public static final DocumentField<Vector3d> SPAWN_LOCATION = DocumentField.custom(
            "custom-spawn",
            UNKNOWN_POS,
            DocumentFieldCodec.VECTOR_3D
    );

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-custom-spawn:" + id);
    }


    @Override
    public void enable() throws Exception {

    }

    @Override
    public void disable() throws Exception {

    }

    @BukkitCommand(name = "spawn", permission = "+starlight.worldguard.spawn")
    public static final class PlotRenameCommand extends StandaloneCommand {
        @Override
        public void execute(CommandExecution context) {
            var t = WGCommandService.getManageableRegion(context);

            if (t.isEmpty()) {
                return;
            }

            var player = context.requireSenderAsPlayer();
            var target = t.get();

            if (!target.getOwners().getUniqueIds().contains(player.getUniqueId())) {
                WGCommandService.lang("rg-not-self").send(QLib.audience(context.getSender()), target.getId());
                return;
            }

            var location = player.getLocation();

            //todo: out of range
            if(){
                lang("rg-spawn-invalid").send(QLib.audience(context.getSender()));
                return;
            }

            var data = WGExtraInfoServiceV2.instance().getData(target, player.getWorld());

            SPAWN_LOCATION.set(data, new Vector3d(location.getX(),location.getY(),location.getZ()));

            lang("rg-spawn-set").send(QLib.audience(context.getSender()));
        }
    }
}
