package org.atcraftmc.starlight.core.view;

import org.atcraftmc.qlib.bukkit.task.Task;
import org.bukkit.entity.Player;

public interface ViewRendererCallback {
    void render(Player player, Task context);
}