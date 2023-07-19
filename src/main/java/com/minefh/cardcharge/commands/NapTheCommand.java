package com.minefh.cardcharge.commands;

import com.minefh.cardcharge.enums.CardAmount;
import com.minefh.cardcharge.gui.CardSelector;
import com.minefh.cardcharge.gui.PinInput;
import com.minefh.cardcharge.gui.SerialInput;
import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.utils.PluginUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NapTheCommand implements CommandExecutor {
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
        }
        return true;
    }
}
