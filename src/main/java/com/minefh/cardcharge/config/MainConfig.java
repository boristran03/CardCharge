package com.minefh.cardcharge.config;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.utils.PluginUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MainConfig {


    //SUCCESS COMMANDS FIELDS
    private final Map<Integer, List<String>> successCommands;
    private final CardCharge plugin;
    //CONFIG FIELD
    private String apiKey, apiSecret;
    private List<String> enabledCards;
    private String serialInputTitle, pinInputTitle;
    private ItemStack leftInputSerial, leftInputPin;
    private String serialInputText, pinInputText;
    //MYSQL FIELDS
    private boolean isMySQLEnabled;
    private String hostname, database, username, password;

    public MainConfig(CardCharge plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();

        successCommands = new HashMap<>();
        load();
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();
        this.apiKey = config.getString("TheSieuToc.api-key");
        this.apiSecret = config.getString("TheSieuToc.api-secret");
        this.enabledCards = config.getStringList("Card-Enabled");
        this.serialInputTitle = config.getString("PC-GUI.serialInput.title");
        this.serialInputText = config.getString("PC-GUI.serialInput.input-text");
        this.pinInputTitle = config.getString("PC-GUI.pinInput.title");
        this.pinInputText = config.getString("PC-GUI.pinInput.input-text");
        this.leftInputSerial = PluginUtils.parseConfigItem("PC-GUI.serialInput.left-item", config);
        this.leftInputPin = PluginUtils.parseConfigItem("PC-GUI.pinInput.left-item", config);

        //MYSQL ZONE
        this.isMySQLEnabled = config.getBoolean("MySQL.enabled");
        this.hostname = config.getString("MySQL.hostname");
        this.database = config.getString("MySQL.database");
        this.username = config.getString("MySQL.username");
        this.password = config.getString("MySQL.password");

        //SUCCESS-COMMAND
        config.getConfigurationSection("Success-Commands").getKeys(false).forEach((price) -> {
            int priceAsInt = Integer.parseInt(price);
            List<String> commands = config.getStringList("Success-Commands." + price);
            successCommands.put(priceAsInt, commands);
        });
    }
}
