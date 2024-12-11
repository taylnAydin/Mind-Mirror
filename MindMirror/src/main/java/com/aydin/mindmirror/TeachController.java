package com.aydin.mindmirror;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class TeachController {

    @FXML
    private Label teachingLabel;

    @FXML
    private Label startLabel;

    @FXML
    private Label stopLabel;

    @FXML
    private Label finishLabel;

    @FXML
    private MediaView videoPlayer;

    @FXML
    private AnchorPane teachAnchor;

    @FXML
    private TextArea textBox;

    @FXML
    private ImageView micImage;

    private MediaPlayer mediaPlayer;
    private FadeTransition micFadeTransition;
    private boolean isStarted = false;
    private boolean isServiceAvailable = true;

    private static final String NOTES_FOLDER = "/Users/taylanaydin/Desktop/CV/MindMirror/MindMirror/MyNotes";
    private static final String AZURE_KEY = System.getenv("AZURE_KEY");
    private static final String AZURE_REGION = System.getenv("AZURE_REGION");

    @FXML
    public void initialize() {
        if (!testSpeechService()) {
            showErrorAlert("Speech Service Error", "Speech service is not available. Please check your configuration.");
            isServiceAvailable = false;
            return;
        }

        initializeMicAnimation();

        Platform.runLater(() -> {
            Stage stage = (Stage) teachAnchor.getScene().getWindow();
            stage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    returnToMainMenu();
                }
            });
        });

        String videoPath = ChooseController.getSelectedAvatarVideo();
        if (videoPath == null) {
            videoPath = "/com/aydin/mindmirror/videos/man.mp4";
        }

        Media media = new Media(getClass().getResource(videoPath).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        videoPlayer.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
    }

    private void initializeMicAnimation() {
        micFadeTransition = new FadeTransition(Duration.millis(500), micImage);
        micFadeTransition.setFromValue(1.0);
        micFadeTransition.setToValue(0.3);
        micFadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        micFadeTransition.setAutoReverse(true);
    }

    @FXML
    public void startTeaching() {
        if (!isServiceAvailable) {
            showErrorAlert("Speech Service Error", "Speech service is not available. Cannot start teaching.");
            micImage.setVisible(false);
            micFadeTransition.stop();
            return;
        }

        teachingLabel.setVisible(true);
        startLabel.setVisible(false);
        stopLabel.setVisible(false);
        finishLabel.setVisible(false);

        micImage.setVisible(true);
        micFadeTransition.play();

        isStarted = true;

        new Thread(this::startSpeechRecognition).start();
    }

    @FXML
    public void stopTeaching() {
        if (!isServiceAvailable) {
            return;
        }

        if (isStarted) {
            teachingLabel.setVisible(false);
            startLabel.setVisible(false);
            stopLabel.setVisible(true);
            finishLabel.setVisible(false);

            micFadeTransition.stop();
            micImage.setVisible(true);
        }
    }

    @FXML
    public void finishTeaching() {
        if (!isServiceAvailable) {
            return;
        }

        if (isStarted) {
            teachingLabel.setVisible(false);
            stopLabel.setVisible(false);
            startLabel.setVisible(false);
            finishLabel.setVisible(true);

            micFadeTransition.stop();
            micImage.setVisible(false);
            isStarted = false;
        }
    }

    private void startSpeechRecognition() {
        try {
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(AZURE_KEY, AZURE_REGION);

            // Manually set the endpoint
            speechConfig.setProperty(PropertyId.SpeechServiceConnection_Endpoint, "https://westeurope.api.cognitive.microsoft.com");
            AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            recognizer.recognizing.addEventListener((s, e) -> {
                Platform.runLater(() -> textBox.appendText(e.getResult().getText() + " "));
            });

            recognizer.recognized.addEventListener((s, e) -> {
                if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                    Platform.runLater(() -> textBox.appendText(e.getResult().getText() + " "));
                } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                    Platform.runLater(() -> textBox.appendText("[No speech recognized]\n"));
                }
            });

            recognizer.canceled.addEventListener((s, e) -> {
                Platform.runLater(() -> showErrorAlert("Recognition canceled", e.getErrorDetails()));
                recognizer.close();
            });

            recognizer.sessionStopped.addEventListener((s, e) -> recognizer.close());

            recognizer.startContinuousRecognitionAsync().get();

        } catch (Exception e) {
            Platform.runLater(() -> showErrorAlert("Error", "Speech recognition failed: " + e.getMessage()));
        }
    }

    private boolean testSpeechService() {
        try {
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(AZURE_KEY, AZURE_REGION);
            AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
            recognizer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    public void deleteContent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to delete the content?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            textBox.clear();
        }
    }

    @FXML
    public void saveNote() {
        File notesFolder = new File(NOTES_FOLDER);
        if (!notesFolder.exists()) {
            notesFolder.mkdirs();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(NOTES_FOLDER));
        fileChooser.setTitle("Save Note");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(teachAnchor.getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(textBox.getText());
                textBox.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Note Saved");
                alert.setHeaderText(null);
                alert.setContentText("Your note has been saved successfully.");
                alert.showAndWait();
            } catch (IOException e) {
                showErrorAlert("Error", "Could not save the note.");
            }
        }
    }

    private void returnToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/aydin/mindmirror/MainMenu.fxml"));
            Scene mainMenuScene = new Scene(fxmlLoader.load(), 1073, 811);
            Stage stage = (Stage) teachAnchor.getScene().getWindow();
            stage.setScene(mainMenuScene);
        } catch (Exception e) {
            showErrorAlert("Error", "Could not load the main menu.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}