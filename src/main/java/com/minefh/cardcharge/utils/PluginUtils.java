package com.minefh.cardcharge.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PluginUtils {

    public static boolean isFullOfNumber(String text) {
        return text.matches("[0-9]+");
    }

    public static void runCommandAsPlayer(Player player, String command) {
        Bukkit.dispatchCommand(player, command);
    }

    public static void runCommandAsConsole(String name, String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%p", name));
    }

    public static ItemStack parseConfigItem(String path, FileConfiguration config) {
        if (!config.getBoolean(path + ".enabled")) {
            return null;
        }
        String rawMaterial = config.getString(path + ".material");
        ItemStack item = new ItemStack(Material.matchMaterial(rawMaterial));
        ItemMeta meta = item.getItemMeta();
        String displayName = config.getString(path + ".displayName");
        List<String> lore = config.getStringList(path + ".lore");
        if (displayName != null) {
            meta.displayName(convertStringToComponent(displayName));
        }
        if (!lore.isEmpty()) {
            meta.lore(convertStringListToComponent(lore));
        }
        meta.setCustomModelData(config.getInt(path + ".custom-model-data"));
        item.setItemMeta(meta);
        return item;
    }

    public static Component convertStringToComponent(String str) {
        String colorized = ChatColor.translateAlternateColorCodes('&', str);
        return MiniMessage.miniMessage().deserialize(colorized);
    }

    public static void cleanMySQL(PreparedStatement statement, ResultSet rs) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Component> convertStringListToComponent(List<String> strList) {
        List<Component> result = new ArrayList<>();
        strList.forEach((str) -> result.add(convertStringToComponent(str)));
        return result;
    }

    public static List<String> replaceCharInStrings(@NotNull List<String> input, String replaceStr, String replacedBy) {
        List<String> result = new ArrayList<>();
        input.forEach((line) -> {
            String replacedLine = line.replaceAll(replaceStr, replacedBy);
            result.add(replacedLine);
        });
        return result;
    }

    public static String parseColor(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static void cleanUpFileIO(Writer writer, Reader reader) {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
