import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class WeatherService {
    private final String api_key;

    public WeatherService() throws FileNotFoundException {
        this.api_key = getAPIkey();
    }

    private String getAPIkey() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream("src/APIkey.txt"));
        return scanner.nextLine().trim();
    }

    private void responseToFile(String response) {
        int c = 0;
        try (FileWriter writer = new FileWriter("src/output.txt")) {
            while (response.charAt(c) != '!') {
                char currentChar = response.charAt(c);
                if (currentChar == '{') {
                    writer.write("\n" + currentChar + "\n");
                } else if (currentChar == '}') {
                    writer.write("\n" + currentChar);
                } else if (currentChar == ',') {
                    writer.write('\n');
                } else {
                    writer.write(currentChar);
                }
                c++;
            }
            writer.flush();
        } catch (Exception e) {
            System.out.println("Error writing file");
        }
    }

    public void printTemperature() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream("src/output.txt"));
        int temperatureSum = 0;
        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            if (currentLine.contains("temp")) {
                String[] strings = currentLine.split(":");
                System.out.println("Current temperature is " + strings[1] + " degrees celsius");
                break;
            }
        }
        System.out.print("\nForecast for this week: ");
        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            if (currentLine.contains("\"date\"")) {
                String[] strings = currentLine.split(":");
                System.out.print("\n" + strings[1].replaceAll("\"", "") + ": ");
                while (scanner.hasNextLine()) {
                    currentLine = scanner.nextLine();
                    if (currentLine.contains("\"temp_avg\"")) {
                        strings = currentLine.split(":");
                        System.out.print(strings[1] + " degrees");
                        temperatureSum += Integer.parseInt(strings[1]);
                        break;
                    }
                }
            }
        }
        System.out.println("\nAverage temperature this week: " + (temperatureSum / 7) + " degrees");
    }

    public void getData() {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62"))
                    .header("X-Yandex-Weather-Key", api_key)
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response Code: " + response.statusCode());
                System.out.println("Response Body: " + response.body());
                responseToFile(response.body() + "!");
            } catch (Exception e) {
                System.err.println("Error making HTTP request: " + e.getMessage());
            }
        }
    }

}
