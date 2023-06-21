package com.exchangeratebot.bot;

import com.exchangeratebot.exception.ServiceException;
import com.exchangeratebot.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
@Slf4j
public class ExchangeRateBot extends TelegramLongPollingBot {

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String CNY = "/cny";
    private static final String HELP = "/help";
    @Autowired
    private ExchangeRateService service;
    @Value("${bot.name}")
    private String botName;

    public ExchangeRateBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (message) {
            case START:
                String userName = update.getMessage().getChat().getFirstName();
                startCommand(chatId, userName);
                break;
            case USD:
                usdCommand(chatId);
                break;
            case EUR:
                eurCommand(chatId);
                break;
            case CNY:
                cnyCommand(chatId);
                break;
            case HELP:
                helpCommand(chatId);
                break;
            default:
                unknownCommand(chatId);
        }
    }

    private void startCommand(Long chatId, String userName) {
        String text = "Привет, " + userName + "!\n\n" +
                "Здесь можно узнать официальные курсы валют на сегодня, установленные ЦБ РФ \n\n" +
                "Команды для использования: \n" +
                "/usd - курс доллара \n" +
                "/eur - курс евро \n" +
                "/cny - курс юань \n" +
                "/help - получение справки";
        sendMessage(chatId, text);
    }

    private void usdCommand(Long chatId) {
        String resultText;
        try {
            resultText = "Курс доллара на " + LocalDate.now() + " составляет " + service.getUSDExchangeRate() + " рублей.";
        } catch (ServiceException e) {
            log.error("Ошибка получения курса доллара", e);
            resultText = "Не удалось получить текущий курс доллара. Попробуйте позже.";
        }
        sendMessage(chatId, resultText);
    }

    private void eurCommand(Long chatId) {
        String resultText;
        try {
            resultText = "Курс евро на " + LocalDate.now() + " составляет " + service.getEURExchangeRate() + " рублей.";
        } catch (ServiceException e) {
            log.error("Ошибка получения курса евро", e);
            resultText = "Не удалось получить текущий курс евро. Попробуйте позже.";
        }
        sendMessage(chatId, resultText);
    }

    private void cnyCommand(Long chatId) {
        String resultText;
        try {
            resultText = "Курс юаня на " + LocalDate.now() + " составляет " + service.getCNYExchangeRate() + " рублей.";
        } catch (ServiceException e) {
            log.error("Ошибка получения курса юаня", e);
            resultText = "Не удалось получить текущий курс юаня. Попробуйте позже.";
        }
        sendMessage(chatId, resultText);
    }

    private void helpCommand(Long chatId) {
        var text = "Справочная информация по боту\n\n" +
                "Для получения текущих курсов валют воспользуйтесь командами: \n" +
                "/usd - курс доллара \n" +
                "/eur - курс евро \n" +
                "/cny - курс юаня \n\n" +
                "Контакт разработчика: @n1k3rr";
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        String text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

}
