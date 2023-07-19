package com.minefh.cardcharge.utils;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Transaction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PluginUtils {

    public static boolean isFullOfNumber(String text) {
        return text.matches("[0-9]+");
    }

    public static ItemStack parseConfigItem(String path, FileConfiguration config) {
        if(!config.getBoolean(path + ".enabled")) {
            return null;
        }
        String rawMaterial = config.getString(path + ".material");
        ItemStack item = new ItemStack(Material.matchMaterial(rawMaterial));
        ItemMeta meta = item.getItemMeta();
        String displayName = config.getString(path + ".displayName");
        List<String> lore = config.getStringList(path + ".lore");
        if(displayName != null) {
            meta.displayName(convertStringToComponent(displayName));
        }
        if(!lore.isEmpty()) {
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

    public static List<Component> convertStringListToComponent(List<String> strList) {
        List<Component> result = new ArrayList<>();
        strList.forEach((str) -> result.add(convertStringToComponent(str)));
        return result;
    }

    public static void cleanUpFileIO(Writer writer, Reader reader) {
        try {
            if(writer != null) {
                writer.close();
            }
            if(reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
