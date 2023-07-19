package com.minefh.cardcharge;

import com.minefh.cardcharge.cache.CacheStorage;
import com.minefh.cardcharge.commands.NapTheCommand;
import com.minefh.cardcharge.listeners.InventoryListener;
import com.minefh.cardcharge.utils.PluginUtils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class CardCharge extends JavaPlugin {

    private static CardCharge __instance;
    private PlayerPointsAPI playerPointsAPI;


    //CONFIG FIELD
    private String apiKey, apiSecret;
    private List<String> enabledCards;
    private String serialInputTitle, pinInputTitle;
    private ItemStack leftInputSerial, leftInputPin;
    private String serialInputText, pinInputText;


    @Override
    public void onEnable() {
        __instance = this;
        this.saveDefaultConfig();

        hookPlayerPoints();
        loadConfig();
        registerCommands();
        registerListeners();

        CacheStorage.getInstance().loadTransactions();

        if(apiKey == null || apiSecret == null || apiSecret.isEmpty() || apiKey.isEmpty()) {
            getLogger().warning("Please put your api key and api secret into the config.yml" +
                    " file before turning on this plugin");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        CacheStorage.getInstance().savePendingTransactions();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("napthe")).setExecutor(new NapTheCommand());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    }

    private void hookPlayerPoints() {
        PluginManager manager = Bukkit.getPluginManager();
        if(manager.getPlugin("PlayerPoints") == null) {
            getLogger().warning("Can't find PlayerPoints, turning off the plugin");
            manager.disablePlugin(this);
            return;
        }
        this.playerPointsAPI = PlayerPoints.getInstance().getAPI();
    }

    private void loadConfig() {
        this.apiKey = getConfig().getString("TheSieuToc.api-key");
        this.apiSecret = getConfig().getString("TheSieuToc.api-secret");
        this.enabledCards = getConfig().getStringList("Card-Enabled");
        this.serialInputTitle = getConfig().getString("PC-GUI.serialInput.title");
        this.serialInputText = getConfig().getString("PC-GUI.serialInput.input-text");
        this.pinInputTitle = getConfig().getString("PC-GUI.pinInput.title");
        this.pinInputText = getConfig().getString("PC-GUI.pinInput.input-text");
        this.leftInputSerial = PluginUtils.parseConfigItem("PC-GUI.serialInput.left-item", getConfig());
        this.leftInputPin = PluginUtils.parseConfigItem("PC-GUI.pinInput.left-item", getConfig());
    }


    public PlayerPointsAPI getPlayerPointsAPI() {
        return this.playerPointsAPI;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public List<String> getEnabledCards() {
        return enabledCards;
    }

    public String getSerialInputTitle() {
        return serialInputTitle;
    }

    public String getPinInputTitle() {
        return pinInputTitle;
    }

    public ItemStack getLeftInputSerial() {
        return leftInputSerial;
    }

    public ItemStack getLeftInputPin() {
        return leftInputPin;
    }

    public String getSerialInputText() {
        return serialInputText;
    }

    public String getPinInputText() {
        return pinInputText;
    }

    public static CardCharge getInstance() {
        return __instance;
    }
}
