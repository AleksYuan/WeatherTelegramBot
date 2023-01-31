package bot;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;


public class MyBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "FoPiplbot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String author = message.getChat().getUserName();
        String text = message.getText().trim();

        if (update.hasMessage() && update.getMessage().hasText()) {

            switch (text.trim()) {
                case "/start":
                case "/instruction":
                    sendMsg(chatId, getInstruction());
                    break;
                case "/setTime":
                    sendMsg(chatId, "bububu");
                    break;
                default:
                    try {
                        sendMsg(chatId, getCurrentWeather(text));
                    } catch (IOException e) {
                        sendMsg(chatId, "Ошибка, возможно там идет снег");
                    }
            }
        }
    }

    public String getInstruction() {
        try {
            String filePath = "src/main/resources/templates/instruction.txt";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            System.out.println("error getInstructions");
            return "ошибка в получении инструкции";
        }
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

    public String getCurrentWeather(String city) throws IOException {
        StringBuffer result = new StringBuffer();

        String apiKey = "";
        String apiCall = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", city, apiKey);

        URL urlObject = new URL(apiCall);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == 404) {
            return  "Я не нашел такого города в моем списке, попробуйте еще раз";
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        ObjectMapper objectMapper = new ObjectMapper();
        Weather weather = objectMapper.readValue(response.toString(), Weather.class);

        Double temp = Double.parseDouble(weather.getMain().get("temp")) - 273;
        Double feelsLike = Double.parseDouble(weather.getMain().get("feels_like")) - 273;
        String speed = weather.getWind().get("speed");

        return result.append("Температура на улице: ")
                .append(String.format("%.2f", temp) + " С\n")
                .append("Чувствуется как: ")
                .append(String.format("%.2f", feelsLike) + " С\n")
                .append("Скорость ветра: ")
                .append(speed + " м/с")
                .toString();
    }
}
