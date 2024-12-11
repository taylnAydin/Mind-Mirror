package com.aydin.mindmirror;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;

import static javafx.scene.input.KeyCode.ESCAPE;

public class WhyBestLearnController {

    @FXML
    private AnchorPane whyBestAnchor;

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) whyBestAnchor.getScene().getWindow();
            stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == ESCAPE) {
                    goBackToMainMenu();
                }
            });
        });
    }

    public void goBackToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/MainMenu.fxml"));
            Scene mainMenuScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) whyBestAnchor.getScene().getWindow();
            stage.setScene(mainMenuScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}