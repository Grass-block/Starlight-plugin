package org.atcraftmc.starlight.utilities.viewdistance;

import me.gb2022.commons.math.MathHelper;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.bukkit.entity.Player;

public final class CustomSettingStrategy implements ViewDistanceStrategy {
    public static final DocumentField<Number> CUSTOM = DocumentField.number("view_distance", -1);

    public static boolean has(Player player) {
        return CUSTOM.get(JDBCData.PLAYER_LOCAL, player.getUniqueId()).intValue() != -1;
    }

    public static int set(Player player, int value) {
        value = (int) MathHelper.clamp(value, 2, 32);

        CUSTOM.set(JDBCData.PLAYER_LOCAL, player.getUniqueId(), value);

        return value;
    }

    public static void clear(Player player) {
        set(player, -1);
    }

    public static int get(Player player) {
        if (!has(player)) {
            return -1;
        }

        return CUSTOM.get(JDBCData.PLAYER_LOCAL, player.getUniqueId()).intValue();
    }

    @Override
    public int determine(Player player, int currentValue) {
        if (has(player)) {
            return get(player);
        }
        return currentValue;
    }

    @Override
    public boolean remindPlayer(Player player, boolean originalRemind) {
        return true;
    }
}
