package com.aydin.mindmirror;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    void goToChooseAvatar(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/ChoOse.fxml"));
            Scene chooseAvatarScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(chooseAvatarScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goToWhyBestLearn(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/WhyBestLearn.fxml"));
            Scene whyBestLearnScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(whyBestLearnScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToTeach(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/teach.fxml"));
            Scene teachScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(teachScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToMyNotes(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/MyNotes.fxml"));
            Scene teachScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(teachScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}