package com.minefh.cardcharge.objects;

import com.google.gson.JsonObject;
import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.databases.InternalLogger;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class Transaction {

    private final long time;
    private final Card card;
    private final String playerName, submitPlayerUUID;
    private String id = "UNKNOWN";
    private String receivedMsg;
    private Result result;

    private Transaction(String playerName, String submitPlayerUUID, Card card) {
        this.card = card;
        this.playerName = playerName;
        this.time = System.currentTimeMillis();
        this.submitPlayerUUID = submitPlayerUUID;
        this.result = Result.PENDING; //DEFAULT RESULT
    }

    public static boolean makeTransaction(CardCharge plugin, Player player, Card card) {
        String submitterUUID = player.getUniqueId().toString();
        JsonObject response = plugin.getTheSieuTocAPI().sendCard(card);
        InternalLogger logger = InternalLogger.getInstance();

        plugin.debug("SUBMIT CARD", card.toString());

        Transaction transaction = new Transaction(player.getName(), submitterUUID, card);
        if (!response.get("status").getAsString().equals("00")) {
            String error = response.get("msg").getAsString();
            player.sendMessage("§cMã lỗi: " + error);

            //Logging player's transaction into an internal log file
            transaction.setReceivedMsg(error);
            logger.logTransaction(transaction, Result.FAIL);
            return false;
        }
        String transactionID = response.get("transaction_id").getAsString();
        transaction.setId(transactionID);
        plugin.getCardCache().addTransaction(transaction);
        return true;
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

    public enum Result {
        SUCCESS, FAIL, PENDING
    }
}
