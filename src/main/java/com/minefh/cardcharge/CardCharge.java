package com.minefh.cardcharge;

import com.minefh.cardcharge.cache.CacheStorage;
import com.minefh.cardcharge.cache.CacheTask;
import com.minefh.cardcharge.cache.CardCache;
import com.minefh.cardcharge.commands.NapTheCommand;
import com.minefh.cardcharge.config.MainConfig;
import com.minefh.cardcharge.databases.MySQL;
import com.minefh.cardcharge.listeners.InventoryListener;
import com.minefh.cardcharge.thesieutoc.TheSieuTocAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class CardCharge extends JavaPlugin {

    private static CardCharge __instance;

    private MainConfig mainConfig;
    private TheSieuTocAPI theSieuTocAPI;
    private CardCache cardCache;
    private CacheStorage cacheStorage;
    private CacheTask cacheTask;

    public static CardCharge getInstance() {
        return __instance;
    }

    @Override
    public void onEnable() {
        __instance = this;

        //Load config
        mainConfig = new MainConfig(this);
        mainConfig.load();

        //Registering stuffs
        registerCommands();
        registerListeners();

        initMysql();
        testConfig();

        theSieuTocAPI = new TheSieuTocAPI(mainConfig);
        cardCache = new CardCache(this);

        cacheStorage = new CacheStorage(this);
        cacheStorage.loadTransactions();

        this.cacheTask = new CacheTask(this);

        //Debug fields
        debug("SUCCESS HASHMAP", mainConfig.getSuccessCommands().toString());
    }

    public void debug(String functionName, String message) {
        getLogger().info("Debug Message - " + functionName + ": " + message);
    }

    private void initMysql() {
        if (mainConfig.isMySQLEnabled()) {
            MySQL mySQL = new MySQL(mainConfig.getHostname(), mainConfig.getDatabase(), mainConfig.getUsername(), mainConfig.getPassword());
            mySQL.connect();
            mySQL.createDonateSuccessTable();
        } else {
            getLogger().warning("MySQL is not enabled, some function maybe not work!");
        }
    }

    private void testConfig() {
        if (mainConfig.getApiKey() == null || mainConfig.getApiSecret() == null
                || mainConfig.getApiKey().isEmpty() || mainConfig.getApiSecret().isEmpty()) {
            getLogger().warning("Please put your api key and api secret into the config.yml" +
                    " file before turning on this plugin");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        cacheStorage.savePendingTransactions();
        MySQL.getInstance().close();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("napthe")).setExecutor(new NapTheCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    }
}
