package org.atcraftmc.starlight.display;

import me.gb2022.commons.TriState;
import me.gb2022.commons.math.SHA;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.*;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.api.event.ModuleEvent;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.config.Configurations;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationModule(id = "chat-announce", description = "Broadcasts periodic announcements to players")
@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider(ChatAnnounce.AnnounceCommand.class)
//todo: 公告系统
//todo: 轮播信息
public class ChatAnnounce extends BukkitAbstractModule {
    private final Map<String, LocalizedMessageSupplier> tips = new HashMap<>();
    private final List<String> sortedKeys = new ArrayList<>();
    private final AtomicBoolean modified = new AtomicBoolean(false);

    @Inject
    private Logger logger;

    @Override
    public void enable() throws Exception {
        grabTips();
        loadCustomTips();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config().value("send-tip-on-join").bool()) {
            showTip(event.getPlayer());
        }
    }

    @EventHandler
    public void onModuleEnable(ModuleEvent.Enable event) {
        var id = event.getMeta().getMetadata().key().fullId();
        var container = this.language().handle();
        var key = id + ":--tip";

        if (!container.hasAny(key)) {
            return;
        }

        this.tips.put(key, container.item(key));

        this.modified.set(true);
    }

    @EventHandler
    public void onModuleDisable(ModuleEvent.Disable event) {
        var id = event.getMeta().getMetadata().key().fullId();
        var container = this.language().handle();
        var key = id + ":--tip";

        this.tips.remove(key, container.item(key));
        this.sortedKeys.remove(key);

        this.modified.set(true);
    }

    //later
    public void grabTips() {
        var container = this.language().handle();

        for (var key : container.getItems().keySet()) {
            if (!key.endsWith("--tip")) {
                continue;
            }

            var keys = key.split(":");

            if (SLPluginEnvironment.getContext().getModuleManager().getStatus(keys[0] + ":" + keys[1]) != TriState.TRUE) {
                continue;
            }

            this.tips.put(key, container.item(key));
        }

        var size = this.tips.size();
        this.logger.info("Auto-loaded {} tips.", size);
    }


    public void loadCustomTips() {
        var dom = Configurations.standalone("tips");
        var keys = dom.getKeys(false);

        for (var cs : keys) {
            this.tips.put("__custom::" + cs, new VirtualLanguageItem(Objects.requireNonNull(dom.getConfigurationSection(cs))));
        }

        resortTips();

        this.logger.info("loaded {} custom tips.", keys.size());
    }

    public void resortTips() {
        var cache = new HashMap<String, String>();

        for (var s : this.tips.keySet()) {
            cache.put(s, SHA.getSHA1(s, false));
        }

        this.sortedKeys.clear();
        this.sortedKeys.addAll(cache.keySet());
        this.sortedKeys.sort(Comparator.comparing(cache::get));
    }

    public void showTip(CommandSender sender) {
        if (this.modified.get()) {
            this.resortTips();
            this.modified.set(false);
        }

        var loc = QLib.audience(sender).pointed().locale();
        var idx = LanguageItem.TEXT_RANDOM.nextInt(0, this.sortedKeys.size());
        var key = this.sortedKeys.get(idx);
        var msg = this.tips.get(key).message(loc).render();

        var temp = this.config().value("template-tips").list(String.class);
        var template = String.join("\n", temp);
        var ui = Language.format(this.language().inline(template, loc), msg).formatted(msg);

        QLib.audience(sender).sendMessage(QLib.textBuilder().buildComponent(ui));
    }


    public static final class VirtualLanguageItem implements LocalizedMessageSupplier {
        private final Map<String, String> messages = new HashMap<>();

        public VirtualLanguageItem(ConfigurationSection section) {
            for (var key : section.getKeys(false)) {
                this.messages.put(key, section.getString(key));
            }
        }

        @Override
        public RenderedMessage message(MinecraftLocale locale, Object... objects) {
            var k = locale.minecraft().replace("_", "-");

            if (this.messages.containsKey(k)) {
                return new RenderedMessage(QLib.context(), locale, this.messages.get(k));
            }

            if (this.messages.containsKey("en-us")) {
                return new RenderedMessage(QLib.context(), MinecraftLocale.EN_US, this.messages.get("en-us"));
            }

            return new RenderedMessage(QLib.context(), MinecraftLocale.ZH_CN, this.messages.get("zh-cn"));
        }
    }


    @BukkitCommand(name = "announce", permission = "+starlight.display.announce")
    public static final class AnnounceCommand extends ModuleCommand<ChatAnnounce> {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "create", "view", "tips");
        }

        @Override
        public void execute(CommandExecution context) {
            switch (context.requireEnum(0, "create", "view", "tips")) {
                case "tips" -> this.getModule().showTip(context.getSender());
                case "create" -> {
                }
                case "view" -> {
                }
            }
        }
    }
}
