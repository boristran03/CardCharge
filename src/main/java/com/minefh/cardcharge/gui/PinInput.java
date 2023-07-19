package com.minefh.cardcharge.gui;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.utils.PluginUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public record PinInput(Card card) {

    private static final CardCharge plugin = CardCharge.getInstance();

    public void open(Player player) {
        ItemStack leftItem = plugin.getLeftInputPin();
        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        /*        builder.onClose((this::closeHandle));*/
        builder.onClick(this::clickHandler);
        builder.plugin(plugin);
        builder.title(plugin.getPinInputTitle());
        builder.text(plugin.getPinInputText());
        if (leftItem != null) {
            builder.itemLeft(leftItem);
        }
        builder.open(player);
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 9999999, 1);
    }

/*    public void closeHandle(AnvilGUI.StateSnapshot stateSnapshot) {
        Player player = stateSnapshot.getPlayer();
    }*/

    public List<AnvilGUI.ResponseAction> clickHandler(int slot, AnvilGUI.StateSnapshot stateSnapshot) {
        Player player = stateSnapshot.getPlayer();
        if (slot != AnvilGUI.Slot.OUTPUT) {
            return Collections.emptyList();
        }
        String input = stateSnapshot.getText();
        if (PluginUtils.isFullOfNumber(input)) {
            card.setPin(input);
            Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
                boolean submitSuccess = Transaction.makeTransaction(player, card);
                if (submitSuccess) {
                    player.sendMessage("§aThẻ của bạn đã được gửi thành công lên hệ thống!");
                } else {
                    player.sendMessage("§cThẻ của bạn đã bị lỗi trong quá trình gửi đi, vui lòng liên hệ admin");
                }
            });
        } else {
            player.sendMessage("§cSố Pin bạn vừa nhập không hợp lệ");
        }
        return Collections.singletonList(AnvilGUI.ResponseAction.close());
    }
}
