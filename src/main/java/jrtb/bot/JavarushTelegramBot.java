package jrtb.bot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Telegram bot for Javarush Community from Javarush community.
 */
@Component
public class JavarushTelegramBot extends TelegramLongPollingBot {
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            String chatId = update.getMessage().getChatId().toString();

            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.setText(getMessage(message));
            sm.setReplyMarkup(replyKeyboardMarkup);

            try {
                execute(sm);
            } catch (TelegramApiException e) {
                //todo add logging to the project.
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public String getMessage(String msq){
        setKeyboard();

        String url;
        switch (msq){
            case "Днепр":
                url = "https://www.gismeteo.ua/weather-dnipro-5077/";
                break;
            case "Донецк":
                url = "https://www.gismeteo.ua/weather-donetsk-14406/";
            break;
            case "Запорожье":
                url = "https://www.gismeteo.ua/weather-zaporizhia-5093/";
            break;
            case "Львов":
                url = "https://www.gismeteo.ua/weather-lviv-4949/";
            break;
            case "Киев":
                url = "https://www.gismeteo.ua/weather-kyiv-4944/";
            break;
            default:
                return "Город неизвестен";
        }

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .referrer("http://www.google.com")
            .get();
        } catch (IOException e) {
            return "Ошибка при обработке сообщения";
        }

        return "Температура в "+ msq + " " + doc.select("div.js_meas_container.temperature.tab-weather__value span.unit.unit_temperature_c").text() + " по цельсию";
    }

    public void setKeyboard(){
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        keyboard.clear();
        keyboardFirstRow.clear();
        keyboardFirstRow.add("Днепр");
        keyboardFirstRow.add("Донецк");
        keyboardFirstRow.add("Запорожье");
        keyboardFirstRow.add("Львов");
        keyboardFirstRow.add("Киев");
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
