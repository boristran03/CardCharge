package com.minefh.cardcharge.objects;

import com.minefh.cardcharge.enums.CardAmount;
import org.bukkit.entity.Player;

public class Card {

    private String type, serial, pin;
    private CardAmount amount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String seri) {
        this.serial = seri;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public CardAmount getAmount() {
        return amount;
    }

    public void setAmount(CardAmount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Card{" +
                "type='" + type + '\'' +
                ", serial='" + serial + '\'' +
                ", pin='" + pin + '\'' +
                ", amount=" + amount.getAsInt() +
                '}';
    }
}
