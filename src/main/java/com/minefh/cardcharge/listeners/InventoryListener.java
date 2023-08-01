package com.minefh.cardcharge.listeners;

import com.minefh.cardcharge.enums.CardAmount;
import com.minefh.cardcharge.gui.AmountSelector;
import com.minefh.cardcharge.gui.CardSelector;
import com.minefh.cardcharge.gui.SerialInput;
import com.minefh.cardcharge.objects.Card;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory inv = e.getInventory();
        if(inv.getHolder() instanceof CardSelector) {
            CardSelector holder = (CardSelector) inv.getHolder();
            String selectedTelco = holder.getCardBySlot(e.getSlot());
            if(selectedTelco != null) {
                Card card = new Card();
                card.setType(selectedTelco);
                AmountSelector amountSelectorUI = new AmountSelector(selectedTelco, card);
                player.openInventory(amountSelectorUI.getInventory());
            }
            e.setCancelled(true);
            player.playSound(player, Sound.UI_BUTTON_CLICK, 9999999, 1);
            return;
        }
        if(inv.getHolder() instanceof AmountSelector) {
            AmountSelector holder = (AmountSelector) inv.getHolder();
            int selectedAmount = holder.getAmountBySlot(e.getSlot());
            if(selectedAmount != 0) {
                Card card = holder.getCard();
                card.setAmount(CardAmount.getAmount(selectedAmount));
                new SerialInput(card).open(player);
            }
            e.setCancelled(true);
            player.playSound(player, Sound.UI_BUTTON_CLICK, 9999999, 1);
        }
    }

}
