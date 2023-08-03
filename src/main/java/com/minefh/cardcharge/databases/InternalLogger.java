package com.minefh.cardcharge.databases;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.utils.PluginUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class InternalLogger {

    //PROVIDES READABILITY

    private final CardCharge plugin;
    private final File file;

    private InternalLogger() {
        this.plugin = CardCharge.getInstance();
        this.file = new File(plugin.getDataFolder(), "log.txt");

        this.createFile();
    }

    public static InternalLogger getInstance() {
        return InstanceHelper.INSTANCE;
    }

    private void createFile() {
        try {
            boolean created = file.createNewFile();
            if (created) {
                plugin.getLogger().warning("log.txt file has been created!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logTransaction(Transaction transaction, Transaction.Result result) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(this.file);
            transaction.setResult(result);
            writer.println(transaction);

            //WILL REFACTOR IN THE FUTURE
            if (plugin.isMySQLEnabled() && result == Transaction.Result.SUCCESS) {
                MySQL mySQL = MySQL.getInstance();
                mySQL.insertDonateSuccess(transaction);
                plugin.getLogger().warning("A new record has been added to mysql," +
                        " this is debug message and will be removed in the future");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PluginUtils.cleanUpFileIO(writer, null);
        }
    }

    private static class InstanceHelper {
        private static final InternalLogger INSTANCE = new InternalLogger();
    }

}
