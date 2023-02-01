package bot.service;

import bot.models.OpenWeather;
import bot.models.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    private final OpenWeather openWeather;

    public WeatherService() {

        this.openWeather = new OpenWeather("");
    }

    public String getWeatherApiKey() {
        return openWeather.getApiKey();
    }

    public String getCurrentWeather(String city) {
        String request = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",
                city, getWeatherApiKey());

        try {
            URL urlObject = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 404) {
                return "Я не нашел такого города в моем списке, попробуйте еще раз";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

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

            StringBuffer result = new StringBuffer();
            return result.append("Температура на улице: ")
                    .append(String.format("%.2f", temp) + " С\n")
                    .append("Чувствуется как: ")
                    .append(String.format("%.2f", feelsLike) + " С\n")
                    .append("Скорость ветра: ")
                    .append(speed + " м/с")
                    .toString();
        } catch (IOException e) {
            return "Ошибка при запросе к серверу";
        }
    }
}
