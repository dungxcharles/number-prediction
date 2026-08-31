package hust.soict.globalict.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

import javafx.scene.control.Alert;

public class PainterController {

    @FXML
    private Pane drawingAreaPane;

    @FXML
    private RadioButton penRadioButton;

    @FXML
    private RadioButton eraserRadioButton;

    @FXML
    private ToggleGroup toolToggleGroup;

    @FXML
    void clearButtonPressed(ActionEvent event) {
        drawingAreaPane.getChildren().clear();
    }

    @FXML
    void predictButtonPressed(ActionEvent event) {
        try {
            WritableImage writableImage = new WritableImage((int) drawingAreaPane.getWidth(), (int) drawingAreaPane.getHeight());
            drawingAreaPane.snapshot(null, writableImage);

            File file = new File("image.png");
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);

            // Request to server to proceed
            try {
                postRequestImg("image.png");
            }catch(FileNotFoundException fnfE){
                System.out.println("File not found:\nMore info:\n");
                fnfE.printStackTrace();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void drawingAreaMouseDragged(MouseEvent event) {
        Color drawColor = Color.BLACK;
        if (eraserRadioButton.isSelected()) {
            drawColor = Color.WHITE;
        }
        
        Circle newCircle = new Circle(event.getX(), event.getY(), 20, drawColor);
        drawingAreaPane.getChildren().add(newCircle);
    }

    private void postRequestImg(String imagePath) throws FileNotFoundException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:5000/process-image"))
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofFile(Paths.get(imagePath)))
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            if (response.statusCode() == 200){
                System.out.println("Image is received\n");

                String result = response.body().toString();
                popupWindow(result);
            }
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    private void popupWindow(String result){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("The AI prediction!");

        System.out.println(result);
        char predictedNb = trimJsonToOneDigitNb(result);
        System.out.println(predictedNb);
        if (Character.isDigit(predictedNb)){
            alert.setContentText("AI predicts number " + trimJsonToOneDigitNb(result));
        }else{
            System.out.println("AI can not predict this or something's wrong here!");
        }

        alert.showAndWait();
    }

    private char trimJsonToOneDigitNb(String result){
        return result.charAt(result.indexOf(':') + 1);
    }
}
