module GUIProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires java.net.http;

    opens hust.soict.globalict.javafx to javafx.fxml;
    exports hust.soict.globalict.javafx;
}
