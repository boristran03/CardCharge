package com.minefh.cardcharge.forms;

import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.enums.CardAmount;
import com.minefh.cardcharge.objects.Card;
import com.minefh.cardcharge.objects.Transaction;
import com.minefh.cardcharge.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;

import java.util.List;

public class NapTheForm {

    private final CardCharge plugin;
    private final Player player;

    public NapTheForm(Player player) {
        this.plugin = CardCharge.getInstance();
        this.player = player;
    }

    public CustomForm getForm() {
        CustomForm.Builder builder = CustomForm.builder();
        List<String> enabledCards = plugin.getMainConfig().getEnabledCards();
        List<String> enabledAmount = CardAmount.getEnabledAmountAsStr();

        builder.dropdown("Chọn nhà mạng", enabledCards);
        builder.dropdown("Chọn giá trị thẻ nạp", enabledAmount);
        builder.input("Nhập số serial", "Nhập vào đây");
        builder.input("Nhập mã pin", "Nhập vào đây");

        builder.validResultHandler((response) -> responseHandler(enabledCards, enabledAmount, response));
        return builder.build();
    }

    public void responseHandler(List<String> enabledCards, List<String> enabledAmount, CustomFormResponse response) {
        String telcoSelected = enabledCards.get(response.next());
        String amountSelected = enabledAmount.get(response.next());
        String serialNumber = response.next();
        String pinNumber = response.next();

        if (serialNumber == null || serialNumber.isEmpty() || pinNumber == null || pinNumber.isEmpty()) {
            player.sendMessage(Component.text("§cSố serial và mã thẻ khng được để trống!"));
            return;
        }
        if (!PluginUtils.isFullOfNumber(serialNumber) || !PluginUtils.isFullOfNumber(pinNumber)) {
            player.sendMessage(Component.text("§cSố serial hoặc mã thẻ không hợp lệ, vui lòng kiểm tra lại"));
            return;
        }
        Card card = new Card();
        card.setType(telcoSelected);
        card.setAmount(CardAmount.getAmount(Integer.parseInt(amountSelected)));
        card.setSerial(serialNumber);
        card.setPin(pinNumber);

        Bukkit.getScheduler().runTaskAsynchronously(CardCharge.getInstance(), () -> {
            boolean submitSuccess = Transaction.makeTransaction(plugin, player, card);
            if (submitSuccess) {
                player.sendMessage("§aThẻ của bạn đã được gửi thành công lên hệ thống!");
            } else {
                player.sendMessage("§cThẻ của bạn đã bị lỗi trong quá trình gửi đi, vui lòng liên hệ admin");
            }
        });
    }

}
