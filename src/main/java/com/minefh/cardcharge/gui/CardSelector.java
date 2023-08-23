package com.minefh.cardcharge.gui;

import com.minefh.cardcharge.CardCharge;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class CardSelector implements InventoryHolder {

    private final CardCharge plugin;
    private String[] cards;

    public CardSelector() {
        this.plugin = CardCharge.getInstance();
    }

    public String getCardBySlot(int clickedSlot) {
        return cards[clickedSlot];
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27);
        this.cards = new String[inv.getSize()];
        int slot = 0;
        for (String cardName : plugin.getMainConfig().getEnabledCards()) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(cardName));
            item.setItemMeta(meta);
            cards[slot] = cardName;
            inv.setItem(slot++, item);
        }
        return inv;
    }
}
