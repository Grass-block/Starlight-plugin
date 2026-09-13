package org.atcraftmc.starlight.data.record;

import me.gb2022.commons.nbt.NBTTagCompound;
import org.atcraftmc.starlight.data.record.registry.DataRenderer;
import org.bukkit.entity.Player;

public interface BukkitRecordRenderer {
    DataRenderer<Player> PLAYER = new DataRenderer<>() {

        @Override
        public String render(Player o) {
            return o.getName();
        }

        @Override
        public void render(NBTTagCompound tag, String name, Player o) {
            tag.setString(name, o.getName());
        }
    };
}
