module com.aydin.mindmirror {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires client.sdk;


    opens com.aydin.mindmirror to javafx.fxml;
    exports com.aydin.mindmirror;
}