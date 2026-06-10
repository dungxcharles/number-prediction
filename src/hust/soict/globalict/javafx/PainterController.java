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
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            // Lấy ảnh từ bảng vẽ
            WritableImage writableImage = new WritableImage((int) drawingAreaPane.getWidth(), (int) drawingAreaPane.getHeight());
            drawingAreaPane.snapshot(null, writableImage);
            
            // Lưu ra file image.png
            File file = new File("image.png");
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            
            // Gọi Python predict
            ProcessBuilder builder = new ProcessBuilder("python", "predict.py", "image.png");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            
            // Đọc kết quả
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            process.waitFor();
            
            // Hiển thị kết quả bằng Alert Box
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("AI Prediction");
            alert.setHeaderText(null);
            alert.setContentText("AI dự đoán đây là số: " + result);
            alert.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText("Lỗi khi dự đoán: " + e.getMessage());
            error.showAndWait();
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

}
