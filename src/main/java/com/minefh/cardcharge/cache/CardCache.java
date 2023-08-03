package com.minefh.cardcharge.cache;

import com.google.gson.JsonObject;
import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.databases.InternalLogger;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.thesieutoc.TheSieuTocAPI;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardCache {

    private final CardCharge plugin;
    private final List<Transaction> transactionList;
    private final TheSieuTocAPI theSieuTocAPI;
    private final PlayerPointsAPI playerPointsAPI;
    private final InternalLogger logger;

    private CardCache() {
        this.plugin = CardCharge.getInstance();
        this.theSieuTocAPI = TheSieuTocAPI.getInstance();
        this.transactionList = Collections.synchronizedList(new ArrayList<>());
        this.playerPointsAPI = CardCharge.getInstance().getPlayerPointsAPI();
        this.logger = InternalLogger.getInstance();

        Bukkit.getScheduler().runTaskTimerAsynchronously(CardCharge.getInstance(), () -> {
            List<Transaction> successList = transactionList.stream().filter((transaction) -> {
                JsonObject response = theSieuTocAPI.checkCard(transaction.getId());
                Player player = Bukkit.getPlayer(transaction.getSubmiterUUID());
                if (response == null) {
                    return true;
                }
                String status = response.get("status").getAsString();
                String msg = response.get("msg").getAsString();
                switch (status) {
                    case "00" -> {
                        if (player != null) {
                            player.sendMessage("Nạp thẻ thành công, đang cộng points!");
                        }
                        int pointsRate = plugin.getPointsRate();
                        int pointsAmount = transaction.getCard().getAmount().getAsInt() / 1000;
                        playerPointsAPI.give(transaction.getSubmiterUUID(), pointsAmount * pointsRate);


                        //SOME LOGGING STUFF
                        transaction.setReceivedMsg(msg);
                        logger.logTransaction(transaction, Transaction.Result.SUCCESS);
                        return true;
                    }
                    case "-9" -> {
                        if (player != null) {
                            player.sendMessage("Vui lòng chờ trong giây lát, thẻ của bạn sẽ được xử lí");
                        }
                        return false;
                    }
                    default -> {
                        if (player != null) {
                            player.sendMessage("Nạp thẻ thất bại, vui lòng liên hệ admin để biết thêm thông tin");
                        }

                        //SOME LOGGING STUFF
                        transaction.setReceivedMsg(msg);
                        logger.logTransaction(transaction, Transaction.Result.FAIL);
                        return true;
                    }
                }
            }).toList();

            transactionList.removeAll(successList);
        }, 20L, 120);
    }

    public static CardCache getInstance() {
        return InstanceHelper.INSTANCE;
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

    public List<Transaction> getTransactionList() {
        return Collections.unmodifiableList(transactionList);
    }

    private static class InstanceHelper {
        private final static CardCache INSTANCE = new CardCache();
    }
}
