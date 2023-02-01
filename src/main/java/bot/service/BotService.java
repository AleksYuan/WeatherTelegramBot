package bot.service;

import bot.models.Bot;
import bot.models.Client;
import bot.repository.ClientRepo;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class BotService {

    private final ClientRepo clientRepo;
    private final Bot bot;
    private final WeatherService weatherService;

    public BotService() {
        bot = new Bot("FoPiplbot", "");
        clientRepo = new ClientRepo();
        weatherService = new WeatherService();
    }

    public String getBotName() {
        return bot.getUserName();
    }

    public String getBotToken() {
        return bot.getBotToken();
    }

    public String getAnswer(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String msg = update.getMessage().getText().trim();


            switch (msg.trim()) {
                case "/start":
                    addNewClientToRepo(getAuthor(update), getChatId(update));
                    return getInstruction();

                case "/instruction":
                    return getInstruction();

                case "/setCity":
                    Client client = clientRepo.getClients().get(getChatId(update));
                    if (client == null) {
                        addNewClientToRepo(getAuthor(update), getChatId(update));
                        client = clientRepo.getClients().get(getChatId(update));
                    }
                    client.setSetCity(true);
                    return "Назовите город в соответствии с инструкцией";

                case "/setTime":
                    Client client1 = clientRepo.getClients().get(getChatId(update));
                    if (client1 == null) {
                        return "Вы не можете установить время так как не установлен город";
                    }
                    client1.setSetTime(true);
                    return "Введите время в формате ЧЧ:ММ \nФормат 24 ч.\nПример 13:30";

                default:
                    Client client2 = clientRepo.getClients().get(getChatId(update));

                    if (client2 == null) return weatherService.getCurrentWeather(msg.trim());
                    if (client2.isSetCity()) {
                        client2.setCity(msg.trim());
                        updateClientInRepo(client2);
                        client2.setSetCity(false);
                        return "Вы установили город - " + client2.getCity();
                    } else if (client2.isSetTime()) {
                        String ans = setTimeToClient(client2, msg);
                        updateClientInRepo(client2);
                        client2.setSetTime(false);
                        return ans;
                    } else {
                        return weatherService.getCurrentWeather(msg.trim());
                    }
            }
        }
        return null;
    }

    public String getChatId(Update update) {
        return update.getMessage().getChatId().toString();
    }

    public String getAuthor(Update update) {
        return update.getMessage().getChat().getUserName();
    }

//    public String getText(Update update) {
//        return update.getMessage().getText().trim();
//    }

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

    public void addNewClientToRepo(String name, String chatId) {
        Client client = new Client();
        client.setUserName(name);
        client.setChatId(chatId);

        if (clientRepo.getClients().containsKey(client.getChatId())) {
            System.out.println("клиент с таким идентификатором уже есть в бд");
        } else {
            clientRepo.addClient(client);
            printClientsData();
        }
    }

    public void updateClientInRepo(Client client) {
        clientRepo.addClient(client);
        printClientsData();
    }

    public void printClientsData() {
        for (Client value : clientRepo.getClients().values()) {
            System.out.println(value);
        }
    }

    public List<Client> getClientsTime() {
        Date current = new Date();
        List<Client> result = new ArrayList<>();
        for (Client client : clientRepo.getClients().values()) {
            if (client.getHours() == current.getHours()) {
                result.add(client);
            }
        }

        return result;
    }

    public String setTimeToClient(Client client, String time) {

        if (time == null
                || time.trim().length() > 5
                || time.trim().split(":").length != 2) return "Введенный формат не соответствует условию";

        client.setHours(Byte.parseByte(time.trim().split(":")[0]));
        client.setMinutes(Byte.parseByte(time.trim().split(":")[1]));
        return "Вы установили время " + client.getHours() + ":" + client.getMinutes();
    }

}

