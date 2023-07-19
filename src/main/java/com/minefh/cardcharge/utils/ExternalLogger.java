package com.minefh.cardcharge.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalLogger {

    private final CardCharge plugin;
    private final File file;
    private final Gson gson;

    private ExternalLogger() {
        this.plugin = CardCharge.getInstance();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.file = new File(plugin.getDataFolder(), "log.txt");

        this.createFile();
    }

    private void createFile() {
        try {
            boolean created = file.createNewFile();
            if(created) {
                plugin.getLogger().warning("log.txt file has been created!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logTransaction(Transaction transaction, Transaction.Result result) {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file));
            reader = new BufferedReader(new FileReader(this.file));
            Transaction[] rawArray = gson.fromJson(reader, Transaction[].class);
            List<Transaction> transactions;
            if(rawArray != null) {
                transactions = Arrays.asList(rawArray);
            } else {
                transactions = new ArrayList<>();
            }
            transaction.setResult(result);
            transactions.add(transaction);
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PluginUtils.cleanUpFileIO(writer, reader);
        }
    }

    public static ExternalLogger getInstance() {
        return InstanceHelper.INSTANCE;
    }

    private static class InstanceHelper {
        private static final ExternalLogger INSTANCE = new ExternalLogger();
    }

}
