package bot.controller;


import bot.models.Client;
import bot.service.BotService;
import bot.service.WeatherService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class BotController extends TelegramLongPollingBot {

    private final BotService botService;
//    private final WeatherService weatherService;
    public BotController(BotService botService, WeatherService weatherService) {
        this.botService = botService;
//        this.weatherService = weatherService;
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60*60*24*1000);
                    List<Client> observers = botService.getClientsTime();
                    if  (observers.size() > 0) {
                        for (Client observer : observers) {
                            sendMsg(observer.getChatId(), weatherService.getCurrentWeather(observer.getCity()));
                            System.out.println("отправил погоду " + observer.getUserName());
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public String getBotUsername() {
        return botService.getBotName();
    }

    @Override
    public String getBotToken() {
        return botService.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        sendMsg(botService.getChatId(update),botService.getAnswer(update));
    }

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
