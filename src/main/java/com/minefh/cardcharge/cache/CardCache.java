package com.minefh.cardcharge.cache;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Transaction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CardCache {

    private final List<Transaction> transactionList;

    public CardCache(CardCharge cardCharge) {
        this.transactionList = Collections.synchronizedList(new ArrayList<>());
        cardCharge.debug("CACHE", "Cache initialized successfully!");
    }

    public void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactionList.remove(transaction);
    }

    public boolean contains(Transaction transaction) {
        return transactionList.contains(transaction);
    }

}
