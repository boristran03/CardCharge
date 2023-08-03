package com.minefh.cardcharge.gui;

import com.minefh.cardcharge.enums.CardAmount;
import com.minefh.cardcharge.objects.Card;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AmountSelector implements InventoryHolder {

    private final String telco;
    private final Card card;
    private HashMap<Integer, Integer> values;

    public AmountSelector(String selectedTelco, Card card) {
        this.telco = selectedTelco;
        this.card = card;
    }

    public int getAmountBySlot(int clickedSlot) {
        if (!values.containsKey(clickedSlot)) {
            return 0;
        }
        return values.get(clickedSlot);
    }

    public Card getCard() {
        return this.card;
    }

    public String getTelco() {
        return telco;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27);
        this.values = new HashMap<>();
        int slot = 0;
        for (CardAmount card : CardAmount.values()) {
            int amount = card.getAsInt();
            if (amount > 0) {
                ItemStack item = new ItemStack(Material.BOOK);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(amount));
                item.setItemMeta(meta);
                values.put(slot, amount);
                inv.setItem(slot++, item);
            }
        }
        return inv;
    }

}
