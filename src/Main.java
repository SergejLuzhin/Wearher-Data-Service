import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        WeatherService weatherService = new WeatherService();
        weatherService.getData();
        weatherService.printTemperature();
    }
}