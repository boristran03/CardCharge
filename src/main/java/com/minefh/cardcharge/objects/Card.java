package com.minefh.cardcharge.objects;

import com.minefh.cardcharge.enums.CardAmount;
import lombok.Data;

@Data
public class Card {

    private String type, serial, pin;
    private CardAmount amount;


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
