package com.aydin.mindmirror;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static javafx.scene.input.KeyCode.ESCAPE;

public class ChooseController {

    @FXML
    private AnchorPane chooseAnchor;

    private static String selectedAvatarVideo;
    public static String getSelectedAvatarVideo() {
        return selectedAvatarVideo;
    }

    @FXML
    public void initialize() {

        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) chooseAnchor.getScene().getWindow();
            stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == ESCAPE) {
                    returnToMainMenu(event);
                }
            });
        });
    }

    @FXML
    void pickJerry(ActionEvent event) {
        showSelectionMessage("Jerry has been selected!");
        selectedAvatarVideo = "/com/aydin/mindmirror/videos/monkey.mp4";
        returnToMainMenu(event);
    }

    @FXML
    void pickMax(ActionEvent event) {
        showSelectionMessage("Max has been selected!");
        selectedAvatarVideo = "/com/aydin/mindmirror/videos/man.mp4";
        returnToMainMenu(event);
    }

    @FXML
    void pickBuddy(ActionEvent event) {
        showSelectionMessage("Buddy has been selected!");
        selectedAvatarVideo = "/com/aydin/mindmirror/videos/lion.mp4";
        returnToMainMenu(event);
    }

    @FXML
    void pickSophia(ActionEvent event) {
        showSelectionMessage("Sophia has been selected!");
        selectedAvatarVideo = "/com/aydin/mindmirror/videos/woman.mp4";
        returnToMainMenu(event);
    }

    private void showSelectionMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Avatar Selected");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void returnToMainMenu(javafx.event.Event event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/MainMenu.fxml"));
            Scene mainMenuScene = new Scene(fxmlLoader.load(), 1073, 811);


            Stage stage = (Stage) chooseAnchor.getScene().getWindow();
            stage.setScene(mainMenuScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
