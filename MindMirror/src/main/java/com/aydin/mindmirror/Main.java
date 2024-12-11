package com.aydin.mindmirror;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1073, 811);

        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/com/aydin/mindmirror/icon.png"))); // NOTE: This code may not work on macOS due to special permissions required for com.apple.eawt APIs.
        stage.setTitle("MindMirror");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}