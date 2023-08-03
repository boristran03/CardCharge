package com.minefh.cardcharge.tasks;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DiscountAnnounceTask extends BukkitRunnable {

    private final CardCharge plugin;
    private List<String> messages = null;

    public DiscountAnnounceTask() {
        this.plugin = CardCharge.getInstance();
        this.runTaskTimer(plugin, 20, plugin.getTimer() * 20);
    }

    @Override
    public void run() {
        int pointsRate = plugin.getPointsRate();
        if (messages == null) {
            List<String> rawMessages = plugin.getAnnounceMessage();
            int rateToPercentage = (plugin.getPointsRate() * 100) - 100;
            messages = PluginUtils.replaceCharInStrings(rawMessages, "%rate%", String.valueOf(rateToPercentage));
        }
        if (pointsRate > 1) {
            messages.forEach((message) -> Bukkit.broadcast(Component.text(PluginUtils.parseColor(message))));
        }
    }
}
