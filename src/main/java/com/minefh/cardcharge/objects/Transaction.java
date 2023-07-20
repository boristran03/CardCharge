package com.minefh.cardcharge.objects;

import com.google.gson.JsonObject;
import com.minefh.cardcharge.cache.CardCache;
import com.minefh.cardcharge.thesieutoc.TheSieuTocAPI;
import com.minefh.cardcharge.databases.InternalLogger;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Transaction {

    private final long time;
    private String id = "UNKNOWN";
    private final Card card;
    private final String playerName, submitPlayerUUID;
    private String receivedMsg;
    private Result result;

    private Transaction(String playerName, String submitPlayerUUID, Card card) {
        this.card = card;
        this.playerName = playerName;
        this.time = System.currentTimeMillis();
        this.submitPlayerUUID = submitPlayerUUID;
        this.result = Result.PENDING; //DEFAULT RESULT
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public long getDate() {
        return time;
    }

    public String getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public UUID getSubmiterUUID() {
        return UUID.fromString(submitPlayerUUID);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReceivedMsg(String receivedMsg) {
        this.receivedMsg = receivedMsg;
    }

    public String getReceivedMsg() {
        return receivedMsg;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "time=" + time +
                ", id='" + id + '\'' +
                ", card=" + card +
                ", playerName='" + playerName + '\'' +
                ", submitPlayerUUID='" + submitPlayerUUID + '\'' +
                ", receivedMsg='" + receivedMsg + '\'' +
                ", result=" + result +
                '}';
    }

    public static boolean makeTransaction(Player player, Card card) {
        String submitterUUID = player.getUniqueId().toString();
        JsonObject response = TheSieuTocAPI.getInstance().sendCard(card);
        InternalLogger logger = InternalLogger.getInstance();

        Transaction transaction = new Transaction(player.getName(), submitterUUID, card);
        if (!response.get("status").getAsString().equals("00")) {
            String error = response.get("msg").getAsString();
            player.sendMessage("§cMã lỗi: " + error);

            //SOME LOGGING STUFF BEHIND THIS
            transaction.setReceivedMsg(error);
            logger.logTransaction(transaction, Result.FAIL);
            return false;
        }
        String transactionID = response.get("transaction_id").getAsString();
        transaction.setId(transactionID);
        CardCache.getInstance().addTransaction(transaction);
        return true;
    }

    public enum Result {
        SUCCESS, FAIL, PENDING
    }
}
