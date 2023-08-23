package com.minefh.cardcharge.gui;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.config.MainConfig;
import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.utils.PluginUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public record SerialInput(Card card) {

    private static final CardCharge plugin = CardCharge.getInstance();

    public void open(Player player) {
        MainConfig config = plugin.getMainConfig();
        ItemStack leftItem = config.getLeftInputSerial();
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.onClick(this::clickHandler);
        builder.plugin(plugin);
        builder.title(config.getSerialInputTitle());
        builder.text(config.getSerialInputText());
        if (leftItem != null) {
            builder.itemLeft(leftItem);
        }
        builder.open(player);
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 9999999, 1);
    }


    public List<AnvilGUI.ResponseAction> clickHandler(int slot, AnvilGUI.StateSnapshot stateSnapshot) {
        Player player = stateSnapshot.getPlayer();
        if (slot != AnvilGUI.Slot.OUTPUT) {
            return Collections.emptyList();
        }
        String input = stateSnapshot.getText();
        if (PluginUtils.isFullOfNumber(input)) {
            card.setSerial(input);
            Bukkit.getScheduler().runTaskLater(CardCharge.getInstance(), () -> {
                new PinInput(card).open(player);
            }, 15);
        } else {
            player.sendMessage("§cSố Seri bạn vừa nhập không hợp lệ");
        }
        return Collections.singletonList(AnvilGUI.ResponseAction.close());
    }
}
