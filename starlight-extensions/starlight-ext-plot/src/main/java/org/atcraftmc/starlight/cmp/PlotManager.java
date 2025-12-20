package org.atcraftmc.starlight.cmp;

import me.gb2022.commons.reflect.Inject;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.bukkit.permissions.Permission;

@QuarkCommand(name = "plot", permission = "+starlight.plot")
public final class PlotManager extends SLCommandModule {
    public static final String[] options = new String[]{"create", "set-spawn", "allow", "disallow", "render", "delete", "list", "tp"};

    @Inject("-starlight.plot.admin")
    private Permission adminPermission;

    @Inject("-starlight.plot.create")
    private Permission createPermission;

    @Inject("+starlight.plot.render")
    private Permission renderPermission;


    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, options);
    }

    @Override
    public void execute(CommandExecution context) {
        switch (context.requireEnum(0,options)){
            case
        }
    }
}