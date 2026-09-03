package org.atcraftmc.starlight.tweaks;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

@ApplicationModule(id = "extra-damage", defaultEnable = false)
@AutoRegister(Registrations.SERVER_EVENT)
public final class ExtraDamage extends BukkitAbstractModule {
    private final HashMap<String, Double> percentDamageWeapons = new HashMap<>();
    private final HashMap<String, Double> extraDamageWeapons = new HashMap<>();

    @Override
    public void enable() {
        this.percentDamageWeapons.clear();
        this.extraDamageWeapons.clear();

        var ps = this.config().value("percent-damage").section();
        var ts = this.config().value("extra-damage").section();

        for (var k : ps.getKeys(false)) {
            this.percentDamageWeapons.put(k, ps.getDouble(k));
        }

        for (var k : ts.getKeys(false)) {
            this.extraDamageWeapons.put(k, ts.getDouble(k));
        }
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

        if (this.extraDamageWeapons.containsKey(weapon)) {
            var amount = this.extraDamageWeapons.get(weapon);
            livingEntity.damage(amount, DamageSource.builder(DamageType.GENERIC_KILL).withDirectEntity(event.getDamager()).build());
            if (SLPluginEnvironment.isDebug()) {
                System.out.println("extra: " + amount);
            }
        }

        if (this.percentDamageWeapons.containsKey(weapon)) {
            var amount = this.percentDamageWeapons.get(weapon);
            livingEntity.damage(
                    livingEntity.getMaxHealth() * amount,
                    DamageSource.builder(DamageType.GENERIC_KILL).withDirectEntity(event.getDamager()).build()
            );
            if (SLPluginEnvironment.isDebug()) {
                System.out.println("percent: " + livingEntity.getMaxHealth() * amount);
            }
        }
    }
}
