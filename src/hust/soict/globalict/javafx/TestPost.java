package hust.soict.globalict.javafx;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestPost{
    public static void postRequestJson(String jsonBody){
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response from server: \n" + response.body());
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void main(){
        postRequestJson("{\"name\": \"Charles\", \"major\": \"computer science\"}");
    }
}