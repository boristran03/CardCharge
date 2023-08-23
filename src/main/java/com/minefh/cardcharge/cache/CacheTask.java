package com.minefh.cardcharge.cache;

import com.google.gson.JsonObject;
import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.config.MainConfig;
import com.minefh.cardcharge.databases.InternalLogger;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.thesieutoc.TheSieuTocAPI;
import com.minefh.cardcharge.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CacheTask extends BukkitRunnable {

    private final CardCharge plugin;
    private final CardCache cardCache;
    private final TheSieuTocAPI theSieuTocAPI;
    private final InternalLogger logger;

    public CacheTask(CardCharge plugin) {
        this.plugin = plugin;
        this.cardCache = plugin.getCardCache();
        this.theSieuTocAPI = plugin.getTheSieuTocAPI();

        this.logger = InternalLogger.getInstance();

        this.runTaskTimerAsynchronously(plugin, 20L, 60L);
        plugin.debug("CACHE TASK", "Started successfully!");
    }

    @Override
    public void run() {
        List<Transaction> transactionList = cardCache.getTransactionList();
        List<Transaction> successList = transactionList.stream().filter((transaction) -> {
            MainConfig config = plugin.getMainConfig();

            JsonObject response = theSieuTocAPI.checkCard(transaction.getId());
            Player player = Bukkit.getPlayer(transaction.getSubmitPlayerUUID());
            if (response == null) {
                return true;
            }
            String status = response.get("status").getAsString();
            String msg = response.get("msg").getAsString();
            switch (status) {
                case "00" -> {
                    if (player != null) {
                        player.sendMessage("Nạp thẻ thành công, đang xử lí giao dịch cho bạn!");
                    }
                    int amount = transaction.getCard().getAmount().getAsInt();
                    List<String> commands = config.getSuccessCommands().get(amount);
                    for (String command : commands) {
                        PluginUtils.runCommandAsConsole(transaction.getPlayerName(), command);
                    }
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
    }
}
