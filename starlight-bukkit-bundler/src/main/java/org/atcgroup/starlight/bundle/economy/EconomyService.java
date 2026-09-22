package org.atcgroup.starlight.bundle.economy;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import me.gb2022.gluon.service.ServiceProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

@ApplicationService(id = "economy")
public interface EconomyService extends Economy, BukkitService {
    Logger LOGGER = SLPluginEnvironment.createLogger("EconomyService");

    @ServiceInject
    ServiceHolder<EconomyService> INSTANCE = new ServiceHolder<>();

    static EconomyService instance(){
        return INSTANCE.get();
    }

    @ServiceProvider
    static EconomyService create() {
        var provider = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (provider != null) {
            var e = provider.getProvider();
            LOGGER.info("Using [wrapped -> {}] as economy service.", e);
            return new EconomyWrapper(e);
        }

        LOGGER.error("FAILED TO INIT ECONOMY SERVICE WITH NO ECONOMY CONFIGURED.");
        return null;
    }

    final class EconomyWrapper implements EconomyService {
        @Override
        public boolean isEnabled() {
            return economy.isEnabled();
        }

        @Override
        public String getName() {
            return economy.getName();
        }

        @Override
        public boolean hasBankSupport() {
            return economy.hasBankSupport();
        }

        @Override
        public int fractionalDigits() {
            return economy.fractionalDigits();
        }

        @Override
        public String format(double v) {
            return economy.format(v);
        }

        @Override
        public String currencyNamePlural() {
            return economy.currencyNamePlural();
        }

        @Override
        public String currencyNameSingular() {
            return economy.currencyNameSingular();
        }

        @Deprecated
        @Override
        public boolean hasAccount(String s) {
            return economy.hasAccount(s);
        }

        @Override
        public boolean hasAccount(OfflinePlayer offlinePlayer) {
            return economy.hasAccount(offlinePlayer);
        }

        @Deprecated
        @Override
        public boolean hasAccount(String s, String s1) {
            return economy.hasAccount(s, s1);
        }

        @Override
        public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
            return economy.hasAccount(offlinePlayer, s);
        }

        @Deprecated
        @Override
        public double getBalance(String s) {
            return economy.getBalance(s);
        }

        @Override
        public double getBalance(OfflinePlayer offlinePlayer) {
            return economy.getBalance(offlinePlayer);
        }

        @Deprecated
        @Override
        public double getBalance(String s, String s1) {
            return economy.getBalance(s, s1);
        }

        @Override
        public double getBalance(OfflinePlayer offlinePlayer, String s) {
            return economy.getBalance(offlinePlayer, s);
        }

        @Deprecated
        @Override
        public boolean has(String s, double v) {
            return economy.has(s, v);
        }

        @Override
        public boolean has(OfflinePlayer offlinePlayer, double v) {
            return economy.has(offlinePlayer, v);
        }

        @Deprecated
        @Override
        public boolean has(String s, String s1, double v) {
            return economy.has(s, s1, v);
        }

        @Override
        public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
            return economy.has(offlinePlayer, s, v);
        }

        @Deprecated
        @Override
        public EconomyResponse withdrawPlayer(String s, double v) {
            return economy.withdrawPlayer(s, v);
        }

        @Override
        public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
            return economy.withdrawPlayer(offlinePlayer, v);
        }

        @Deprecated
        @Override
        public EconomyResponse withdrawPlayer(String s, String s1, double v) {
            return economy.withdrawPlayer(s, s1, v);
        }

        @Override
        public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
            return economy.withdrawPlayer(offlinePlayer, s, v);
        }

        @Deprecated
        @Override
        public EconomyResponse depositPlayer(String s, double v) {
            return economy.depositPlayer(s, v);
        }

        @Override
        public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
            return economy.depositPlayer(offlinePlayer, v);
        }

        @Deprecated
        @Override
        public EconomyResponse depositPlayer(String s, String s1, double v) {
            return economy.depositPlayer(s, s1, v);
        }

        @Override
        public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
            return economy.depositPlayer(offlinePlayer, s, v);
        }

        @Deprecated
        @Override
        public EconomyResponse createBank(String s, String s1) {
            return economy.createBank(s, s1);
        }

        @Override
        public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
            return economy.createBank(s, offlinePlayer);
        }

        @Override
        public EconomyResponse deleteBank(String s) {
            return economy.deleteBank(s);
        }

        @Override
        public EconomyResponse bankBalance(String s) {
            return economy.bankBalance(s);
        }

        @Override
        public EconomyResponse bankHas(String s, double v) {
            return economy.bankHas(s, v);
        }

        @Override
        public EconomyResponse bankWithdraw(String s, double v) {
            return economy.bankWithdraw(s, v);
        }

        @Override
        public EconomyResponse bankDeposit(String s, double v) {
            return economy.bankDeposit(s, v);
        }

        @Deprecated
        @Override
        public EconomyResponse isBankOwner(String s, String s1) {
            return economy.isBankOwner(s, s1);
        }

        @Override
        public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
            return economy.isBankOwner(s, offlinePlayer);
        }

        @Deprecated
        @Override
        public EconomyResponse isBankMember(String s, String s1) {
            return economy.isBankMember(s, s1);
        }

        @Override
        public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
            return economy.isBankMember(s, offlinePlayer);
        }

        @Override
        public List<String> getBanks() {
            return economy.getBanks();
        }

        @Deprecated
        @Override
        public boolean createPlayerAccount(String s) {
            return economy.createPlayerAccount(s);
        }

        @Override
        public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
            return economy.createPlayerAccount(offlinePlayer);
        }

        @Deprecated
        @Override
        public boolean createPlayerAccount(String s, String s1) {
            return economy.createPlayerAccount(s, s1);
        }

        @Override
        public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
            return economy.createPlayerAccount(offlinePlayer, s);
        }

        private final Economy economy;

        public EconomyWrapper(Economy economy) {
            this.economy = economy;
        }
    }
}
