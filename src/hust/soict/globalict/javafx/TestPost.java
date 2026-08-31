package hust.soict.globalict.javafx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestPost{
    public static void postRequestJson(String jsonBody){
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://httpbin.org/post"))
                .header("Content-Type", "image/png")
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

    public static void postRequestImg(String imagePath) throws FileNotFoundException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:5000/process-image"))
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofFile(Paths.get(imagePath)))
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status code: " + response.statusCode());

            if (response.statusCode() == 200){
                System.out.println("Response body:\n" + response.body());

            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        try{
            postRequestImg("image.png");
        }catch(FileNotFoundException fnf){
            System.out.println("File not found!");
        }
    }
}