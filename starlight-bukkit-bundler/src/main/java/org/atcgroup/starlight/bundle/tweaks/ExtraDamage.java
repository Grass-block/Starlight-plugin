package org.atcgroup.starlight.bundle.tweaks;

import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationModule(id = "extra-damage", defaultEnable = false, description = "allow user to attach extra damage to certain weapon.")
@AutoRegister(Registrations.SERVER_EVENT)
@SuppressWarnings("UnstableApiUsage") //As we will detect existence and must use it.
public final class ExtraDamage extends BukkitAbstractModule {
    @Inject
    private Logger logger;

    private final DamageEffectAppender extraDamageAppender = new DamageEffectAppender.ExtraDamageAppender();
    private final DamageEffectAppender trueDamageAppender = new DamageEffectAppender.TrueDamageAppender();

    @Override
    public void enable() {
        this.extraDamageAppender.clear();
        this.trueDamageAppender.clear();

        var extraDamageDom = this.config().value("extra-damage").section();
        var trueDamageDom = this.config().value("true-damage").section();

        for (var k : extraDamageDom.getKeys(false)) {
            var list = extraDamageDom.getStringList(k);

            for (var item : list) {
                var provider = DataProvider.parse(item);
                this.extraDamageAppender.register(k, provider);
            }

            this.logger.info("{} -> {}", k, this.extraDamageAppender.providers.get(k));
        }

        for (var k : trueDamageDom.getKeys(false)) {
            var list = trueDamageDom.getStringList(k);

            for (var item : list) {
                var provider = DataProvider.parse(item);
                this.trueDamageAppender.register(k, provider);
            }

            this.logger.info("{} -> {}", k, this.trueDamageAppender.providers.get(k));
        }
    }

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireClass(() -> Class.forName("org.bukkit.damage.DamageSource"));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamageSource().getDamageType() == DamageType.GENERIC_KILL) {
            return;
        }

        var weapon = event.getDamager().getType().getKey().asString();

        if ((event.getDamager() instanceof Player player)) {
            weapon = player.getInventory().getItemInMainHand().getType().getKey().asString();
        }

        if (!(event.getEntity() instanceof LivingEntity livingEntity)) {
            return;
        }

        if (SLPluginEnvironment.isDebug()) {
            System.out.println(weapon);
        }

        this.extraDamageAppender.handle(livingEntity, event.getDamager(), weapon, event, event.getDamage());
        this.trueDamageAppender.handle(livingEntity, event.getDamager(), weapon, event, event.getDamage());
    }


    private static abstract class DamageEffectAppender {
        protected final Map<String, Set<DataProvider>> providers = new HashMap<>();

        public final void register(String weaponId, DataProvider provider) {
            if (provider == null) {
                return;
            }

            this.providers.computeIfAbsent(weaponId, (k) -> new HashSet<>()).add(provider);
        }

        public abstract void append(LivingEntity target, Entity attacker, EntityDamageByEntityEvent event, double amount);

        public final void handle(LivingEntity target, Entity attacker, String weapon, EntityDamageByEntityEvent event, double currentDamage) {
            if (!this.providers.containsKey(weapon)) {
                return;
            }

            var set = this.providers.get(weapon);

            for (var provider : set) {
                this.append(target, attacker, event, provider.eval(target, currentDamage));
            }
        }

        public void clear() {
            this.providers.clear();
        }

        private static class TrueDamageAppender extends DamageEffectAppender {
            @Override
            public void append(LivingEntity target, Entity attacker, EntityDamageByEntityEvent event, double amount) {
                target.setHealth(Math.max(target.getHealth() - amount, 5));
            }
        }

        private static class ExtraDamageAppender extends DamageEffectAppender {
            @Override
            public void append(LivingEntity target, Entity attacker, EntityDamageByEntityEvent event, double amount) {
                QLib.task().entity(attacker).delay(1, () -> {
                    if (attacker.isDead()) {
                        return;
                    }

                    var source = DamageSource.builder(DamageType.GENERIC_KILL).withDirectEntity(attacker).build();
                    target.damage(amount, source);
                });
            }
        }
    }

    interface DataProvider {
        double eval(LivingEntity target, double currentDamage);

        static DataProvider targetPercent(double p) {
            return (target, currentDamage) -> target.getMaxHealth() * (p / 100f);
        }

        static DataProvider damagePercent(double p) {
            return (target, currentDamage) -> currentDamage * (p / 100f);
        }

        static DataProvider fixed(double p) {
            return (target, currentDamage) -> p;
        }

        static DataProvider parse(String s) {
            var args = s.split(":");

            return switch (args[0]) {
                case "percent" -> targetPercent(Double.parseDouble(args[1]));
                case "append" -> damagePercent(Double.parseDouble(args[1]));
                case "fixed" -> fixed(Double.parseDouble(args[1]));
                default -> throw new IllegalStateException("Unexpected value: " + args[0]);
            };
        }
    }
}
