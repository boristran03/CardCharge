package com.minefh.cardcharge.thesieutoc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.minefh.cardcharge.CardCharge;
import com.minefh.cardcharge.objects.Card;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.text.MessageFormat;
import java.util.stream.Collectors;

public class TheSieuTocAPI {


    private final String API_SERVER = "https://thesieutoc.net";
    private final CardCharge plugin = CardCharge.getInstance();

    public static TheSieuTocAPI getInstance() {
        return INSTANCE_HELPER.theSieuTocAPI;
    }

    public JsonObject sendCard(Card card) {
        final String url = MessageFormat.format(
                "{0}/API/transaction?APIkey={1}&APIsecret={2}&mathe={3}&seri={4}&type={5}&menhgia={6}",
                API_SERVER, plugin.getApiKey(), plugin.getApiSecret(), card.getPin(), card.getSerial(),
                card.getType(), card.getAmount().getId());
        return sendRequest(url);
    }

    public JsonObject checkCard(String transactionID) {
        final String url = MessageFormat.format("{0}/card_charging_api/check-status.html?APIkey={1}&APIsecret={2}&transaction_id={3}",
                API_SERVER, plugin.getApiKey(), plugin.getApiSecret(), transactionID);
        return sendRequest(url);
    }

    private JsonObject sendRequest(String url) {
        try {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setDoInput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final String response = reader.lines().collect(Collectors.joining());
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class INSTANCE_HELPER {
        private static final TheSieuTocAPI theSieuTocAPI = new TheSieuTocAPI();
    }
}
