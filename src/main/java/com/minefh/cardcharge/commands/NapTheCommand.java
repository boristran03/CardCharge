package com.minefh.cardcharge.commands;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.config.MainConfig;
import com.minefh.cardcharge.databases.MySQL;
import com.minefh.cardcharge.enums.CardAmount;
import com.minefh.cardcharge.forms.NapTheForm;
import com.minefh.cardcharge.gui.SerialInput;
import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class NapTheCommand implements CommandExecutor {

    private final CardCharge plugin;
    private final MainConfig config;

    public NapTheCommand(CardCharge plugin) {
        this.plugin = plugin;
        this.config = plugin.getMainConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        if (strings.length == 0 && player.hasPermission("cardcharge.open")) {
            FloodgateApi floodgateApi = FloodgateApi.getInstance();
            if (!floodgateApi.isFloodgatePlayer(player.getUniqueId())) {
                PluginUtils.runCommandAsConsole(player.getName(), "dm open napthe %p");
                return true;
            }
            floodgateApi.getPlayer(player.getUniqueId()).sendForm(new NapTheForm(player).getForm());
            return true;
        }
        if (strings.length == 1 && strings[0].equalsIgnoreCase("help")) {
            player.sendMessage(Component.text("§c§lLỆNH PLUGIN NẠP THẺ"));
            player.sendMessage(Component.text(""));
            player.sendMessage(Component.text("§e/napthe choose <Loại thẻ> <Giá trị> §7để tiến hành nạp thẻ")); //CHOOSE
            player.sendMessage(Component.text("§e/napthe top §7để xem bảng xếp hạng 10 người nạp nhiều nhất")); //VIEW TOP
            player.sendMessage(Component.text("§e/napthe purge <tên người chơi> §7xóa dữ liệu của người chơi đó")); //PURGE
            player.sendMessage(Component.text("§e/napthe test <tên người chơi> <số lương> để kiểm tra database")); //TEST
            return true;
        }
        //COMMAND: /napthe choose Viettel 10000
        if (player.hasPermission("cardcharge.choose")
                && strings.length == 3
                && strings[0].equalsIgnoreCase("choose")) {
            String telco = strings[1];
            String priceStr = strings[2];
            if (!PluginUtils.isFullOfNumber(priceStr)) {
                player.sendMessage("§cGiá trị thẻ nạp bạn vừa nhâp không hợp lệ!");
                return true;
            }
            int amount = Integer.parseInt(priceStr);
            if (CardAmount.getAmount(amount) == CardAmount.UNKNOWN) {
                player.sendMessage("§cGiá trị thẻ nạp bạn vừa nhâp không hợp lệ!");
                return true;
            }
            Card card = new Card();
            card.setType(telco);
            card.setAmount(CardAmount.getAmount(amount));
            new SerialInput(card).open(player);
            return true;
        }
        //COMMAND: /napthe top
        if (player.hasPermission("cardcharge.viewtop")
                && strings.length == 1
                && strings[0].equalsIgnoreCase("top")) {
            if (!config.isMySQLEnabled()) {
                player.sendMessage(Component.text("§cTính năng này yêu cầu phải sử dụng mysql!"));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
                MySQL mysql = MySQL.getInstance();
                AtomicInteger start = new AtomicInteger(1);
                player.sendMessage(Component.text("§e§lBXH NGƯỜI NẠP NHIỀU NHẤT"));
                player.sendMessage(Component.text(""));
                mysql.getTopTen().forEach((username, amount) -> player.sendMessage(Component.text("§8" + start.getAndIncrement() + ") §c" + username
                        + " §fđã nạp §e" + amount + " VNĐ")));
            });
            return true;
        }
        //COMMAND: /napthe test %playerName% %amount%
        if (player.hasPermission("cardcharge.admin")
                && strings.length == 3
                && strings[0].equalsIgnoreCase("test")) {
            if (!config.isMySQLEnabled()) {
                player.sendMessage(Component.text("§cTính năng này yêu cầu phải sử dụng mysql!"));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
                MySQL mySQL = MySQL.getInstance();
                mySQL.debugDonate(strings[1], Integer.parseInt(strings[2]));
                player.sendMessage("§aĐã thực thi lệnh test lên người chơi!");
            });
            return true;
        }
        //COMMAND: /napthe purge <name>
        if (player.hasPermission("cardcharge.admin")
                && strings.length == 2
                && strings[0].equalsIgnoreCase("purge")) {
            if (!config.isMySQLEnabled()) {
                player.sendMessage(Component.text("§cTính năng này yêu cầu phải sử dụng mysql!"));
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
                MySQL mySQL = MySQL.getInstance();
                mySQL.purgeData(strings[1]);
                player.sendMessage(Component.text("§aĐã xóa thành công dữ liệu của người chơi " + strings[1]));
            });
        }
        return true;
    }
}
