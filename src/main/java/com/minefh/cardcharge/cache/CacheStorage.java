package com.minefh.cardcharge.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.utils.PluginUtils;

import java.io.*;
import java.util.List;

public class CacheStorage {

    //JUST SAVE WHEN THE PLUGIN IS DOWN OR ON SERVER SHUTDOWN

    private final Gson gson;
    private final CardCharge plugin;
    private final CardCache cardCache;

    private CacheStorage() {
        this.plugin = CardCharge.getInstance();
        this.cardCache = CardCache.getInstance();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }


    public void savePendingTransactions() {
        List<Transaction> transactions = cardCache.getTransactionList();
        File file = new File(plugin.getDataFolder(), "cache.json");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            gson.toJson(transactions, writer);
            plugin.getLogger().warning(transactions.size() + " " +
                    "pending cards has been saved to cache.json");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PluginUtils.cleanUpFileIO(writer, null);
        }

    }

    public void loadTransactions() {
        File file = new File(plugin.getDataFolder(), "cache.json");
        try {
            boolean created = file.createNewFile();
            if(created) {
                plugin.getLogger().info("File cache.json has been created!");
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Transaction[] transactions = gson.fromJson(reader, Transaction[].class);
            if(transactions == null) {
                return;
            }
            for (Transaction transaction : transactions) {
                CardCache.getInstance().addTransaction(transaction);
                plugin.getLogger().info("Transaction " + transaction.getCard() + " " +
                        "has been added to the plugin's cache");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CacheStorage getInstance() {
        return InstanceHelper.INSTANCE;
    }

    private static class InstanceHelper {
        private static final CacheStorage INSTANCE = new CacheStorage();
    }
}
