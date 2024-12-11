package com.aydin.mindmirror;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class MyNotesController {

    @FXML
    private ListView<String> fileListView;

    @FXML
    private AnchorPane notesAnchor;

    @FXML
    private TextArea areaRead;

    private static final String NOTES_FOLDER = "MyNotes";

    @FXML
    public void initialize() {
        loadFilesIntoListView();

        fileListView.setFocusTraversable(false);

        fileListView.setOnMouseClicked(event -> loadSelectedFile());

        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) notesAnchor.getScene().getWindow();
            stage.getScene().getRoot().requestFocus();

            stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    goBackToMainMenu();
                }
            });
        });
    }

    private void loadFilesIntoListView() {
        File folder = new File(NOTES_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        ObservableList<String> items = fileListView.getItems();
        items.clear();

        if (files != null) {
            for (File file : files) {
                items.add(file.getName());
            }
        }
    }

    private void loadSelectedFile() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            Alert noSelectionAlert = new Alert(Alert.AlertType.WARNING);
            noSelectionAlert.setTitle("No Selection");
            noSelectionAlert.setHeaderText("No File Selected");
            noSelectionAlert.setContentText("Please select a file to load.");
            noSelectionAlert.showAndWait();
            return;
        }

        File fileToLoad = new File(NOTES_FOLDER, selectedFile);

        try {
            String content = new String(Files.readAllBytes(fileToLoad.toPath()));
            areaRead.clear();
            areaRead.setText(content);
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Load Failed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not load the file: " + selectedFile);
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void saveNote() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            Alert noSelectionAlert = new Alert(Alert.AlertType.WARNING);
            noSelectionAlert.setTitle("No Selection");
            noSelectionAlert.setHeaderText("No File Selected");
            noSelectionAlert.setContentText("Please select a file to save changes.");
            noSelectionAlert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Save Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to save changes?");
        confirmationAlert.setContentText("File: " + selectedFile);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            File fileToSave = new File(NOTES_FOLDER, selectedFile);

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(areaRead.getText());
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Save Succeeded");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Changes saved to '" + selectedFile + "'.");
                successAlert.showAndWait();
            } catch (IOException e) {
                Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                failureAlert.setTitle("Save Failed");
                failureAlert.setHeaderText(null);
                failureAlert.setContentText("Could not save changes to the file.");
                failureAlert.showAndWait();
                e.printStackTrace();
            }
        }
        areaRead.clear();
    }

    @FXML
    private void deleteSelectedFile() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();

        if (selectedFile == null) {
            Alert noSelectionAlert = new Alert(Alert.AlertType.WARNING);
            noSelectionAlert.setTitle("No Selection");
            noSelectionAlert.setHeaderText("No File Selected");
            noSelectionAlert.setContentText("Please select a file to delete.");
            noSelectionAlert.showAndWait();
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this file?");
        confirmationAlert.setContentText("File: " + selectedFile);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            File fileToDelete = new File(NOTES_FOLDER, selectedFile);
            if (fileToDelete.delete()) {
                loadFilesIntoListView();
                areaRead.clear();
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("File Deleted");
                successAlert.setHeaderText(null);
                successAlert.setContentText("File '" + selectedFile + "' has been deleted.");
                successAlert.showAndWait();
            } else {
                Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                failureAlert.setTitle("Delete Failed");
                failureAlert.setHeaderText(null);
                failureAlert.setContentText("Could not delete the file: " + selectedFile);
                failureAlert.showAndWait();
            }
        }
    }

    @FXML
    private void newTxt() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("New Note");
        inputDialog.setHeaderText("Create a New Note");
        inputDialog.setContentText("Enter the name of the new note:");

        Optional<String> result = inputDialog.showAndWait();
        result.ifPresent(fileName -> {
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }
            File newFile = new File(NOTES_FOLDER, fileName);

            try {
                if (newFile.createNewFile()) {
                    loadFilesIntoListView();
                    areaRead.clear();
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("File Created");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("New file created: " + fileName);
                    successAlert.showAndWait();
                } else {
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                    failureAlert.setTitle("File Exists");
                    failureAlert.setHeaderText(null);
                    failureAlert.setContentText("A file with this name already exists.");
                    failureAlert.showAndWait();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void ClearArea() {
        areaRead.clear();
    }

    private void goBackToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/MainMenu.fxml"));
            Scene mainMenuScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) notesAnchor.getScene().getWindow();
            stage.setScene(mainMenuScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}