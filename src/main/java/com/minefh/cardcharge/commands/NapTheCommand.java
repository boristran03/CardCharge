package com.minefh.cardcharge.commands;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.databases.MySQL;
import com.minefh.cardcharge.enums.CardAmount;
import com.minefh.cardcharge.gui.CardSelector;
import com.minefh.cardcharge.gui.PinInput;
import com.minefh.cardcharge.gui.SerialInput;
import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NapTheCommand implements CommandExecutor {

    private final CardCharge plugin;

    public NapTheCommand() {
        this.plugin = CardCharge.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player player)) {
            return true;
        }
        if(strings.length == 0 && player.hasPermission("cardcharge.open")) {
            CardSelector cardSelector = new CardSelector();
            player.openInventory(cardSelector.getInventory());
            return true;
        }
        //COMMAND: /napthe choose Viettel 10000
        if(player.hasPermission("cardcharge.choose")
                && strings.length == 3
                && strings[0].equalsIgnoreCase("choose")) {
            String telco = strings[1];
            String priceStr = strings[2];
            if(!PluginUtils.isFullOfNumber(priceStr)) {
                player.sendMessage("Giá trị thẻ nạp bạn vừa nhâp không hợp lệ!");
                return true;
            }
            int amount = Integer.parseInt(priceStr);
            if(CardAmount.getAmount(amount) == CardAmount.UNKNOWN) {
                player.sendMessage("Giá trị thẻ nạp bạn vừa nhâp không hợp lệ!");
                return true;
            }
            Card card = new Card();
            card.setType(telco);
            card.setAmount(CardAmount.getAmount(amount));
            new SerialInput(card).open(player);
            return true;
        }
        //COMMAND: /napthe top
        if(player.hasPermission("cardcharge.viewtop")
                && strings.length == 1
                && strings[0].equalsIgnoreCase("top")) {
            if(!plugin.isMySQLEnabled()) {
                player.sendMessage(Component.text("§cTính năng này yêu cầu phải sử dụng mysql!"));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
                MySQL mysql = MySQL.getInstance();
                mysql.getTopTen().forEach((username, amount) -> {
                    player.sendMessage(Component.text(username + " - " + amount));
                });
            });
            return true;
        }
        //COMMAND: /napthe test %playerName% %amount%
        if(player.hasPermission("cardcharge.admin")
                && strings.length == 3
                && strings[0].equalsIgnoreCase("test")) {
            if(!plugin.isMySQLEnabled()) {
                player.sendMessage(Component.text("§cTính năng này yêu cầu phải sử dụng mysql!"));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
                MySQL mySQL = MySQL.getInstance();
                mySQL.debugDonate(strings[1], Integer.parseInt(strings[2]));
                player.sendMessage("Da thuc thi lenh test!");
            });
        }
        return true;
    }
}
