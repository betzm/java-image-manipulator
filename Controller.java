/*
 * Course: CSC1120A - 121
 * Spring 2024
 * Lab 3
 * Name: Madison Betz
 * Created: 2/1/2024
 */
package betzm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller for the GUI for Lab3 that displays an
 * output image from several input images
 */
public class Controller {
    private Image[] inputImages;
    private final List<Image> inputsArray = new ArrayList<>();

    @FXML
    private Button minButton;

    @FXML
    private Button maxButton;

    @FXML
    private Button randomButton;

    @FXML
    private Button meanButton;

    @FXML
    private Button medianButton;

    @FXML
    private Button addImageButton;

    @FXML
    private TextArea inputImageTextArea;

    @FXML
    private Button saveButton;

    @FXML
    private ImageView outputImageView;

    /**
     * Sets up buttons and methods
     */
    @FXML
    public void initialize() {
        addImageButton.setDisable(false);
        meanButton.setDisable(true);
        medianButton.setDisable(true);
        maxButton.setDisable(true);
        minButton.setDisable(true);
        randomButton.setDisable(true);
        saveButton.setDisable(true);

        addImageButton.setOnAction(this::addImageButtonClicked);
        meanButton.setOnAction(this::meanButtonClicked);
        medianButton.setOnAction(this::medianButtonClicked);
        maxButton.setOnAction(this::maxButtonClicked);
        minButton.setOnAction(this::minButtonClicked);
        randomButton.setOnAction(this::randomButtonClicked);
        saveButton.setOnAction(this::saveButtonClicked);
    }

    @FXML
    private void addImageButtonClicked(ActionEvent event) {
        //create new file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        fileChooser.setInitialDirectory(new File("images"));

        //open window to allow user to choose image file
        File selectedFile = fileChooser.showOpenDialog(null);

        //checks that image path is not null
        if (selectedFile != null) {
            try {
                Image image = MeanImageMedian.readImage(selectedFile.toPath());
                String filePath = selectedFile.getAbsolutePath();
                //allows user to keep adding images everytime they press the button
                inputImageTextArea.appendText(filePath + "\n");
                //add image to array list
                inputsArray.add(image);
                //print input image
                outputImageView.setImage(image);
            } catch (IOException exception) {
                alertMessage("Failed to read image(s).", exception.getMessage());
            }

        }
        //reset buttons for new inputs
        updateButtons();
    }

    @FXML
    private void meanButtonClicked(ActionEvent event) {
        // Convert ArrayList to array
        inputImages = inputsArray.toArray(new Image[0]);
        try {
            //checks more than two images and images are not null
            if (inputImages.length >= 2) {
                //calculate mean image
                Image meanImage = MeanImageMedian.generateImage(inputImages, "mean");
                //add image to image view
                outputImageView.setImage(meanImage);
            }
            //catch if image dimensions do not match
        } catch (IllegalArgumentException exception) {
            alertMessage("Image dimensions don't match.", exception.getMessage());
        }
        //updateButtons for new inputs
        updateButtons();
    }

    @FXML
    private void medianButtonClicked(ActionEvent event) {
        // Convert ArrayList to array
        inputImages = inputsArray.toArray(new Image[0]);
        //if image is not null and more than two images are entered
        if (inputImages.length >= 2) {
            try {
                //creates new image from median image
                Image medianImage = MeanImageMedian.generateImage(inputImages, "median");
                //add image to image view
                outputImageView.setImage(medianImage);
                //catch if dimensions do not match
            } catch (IllegalArgumentException exception) {
                alertMessage("Image dimensions don't match.", exception.getMessage());
            }
        }
        //update buttons for new inputs
        updateButtons();
    }

    @FXML
    private void minButtonClicked(ActionEvent event) {
        // Convert ArrayList to array
        inputImages = inputsArray.toArray(new Image[0]);
        //if image is not null and more than two images are entered
        if (inputImages.length >= 2) {
            try {
                //creates new image from median image
                Image medianImage = MeanImageMedian.generateImage(inputImages, "min");
                //add image to image view
                outputImageView.setImage(medianImage);
                //catch if dimensions do not match
            } catch (IllegalArgumentException exception) {
                alertMessage("Image dimensions don't match.", exception.getMessage());
            }
        }
        //update buttons for new inputs
        updateButtons();
    }

    @FXML
    private void maxButtonClicked(ActionEvent event) {
        // Convert ArrayList to array
        inputImages = inputsArray.toArray(new Image[0]);
        //if image is not null and more than two images are entered
        if (inputImages.length >= 2) {
            try {
                //creates new image from median image
                Image medianImage = MeanImageMedian.generateImage(inputImages, "max");
                //add image to image view
                outputImageView.setImage(medianImage);
                //catch if dimensions do not match
            } catch (IllegalArgumentException exception) {
                alertMessage("Image dimensions don't match.", exception.getMessage());
            }
        }
        //update buttons for new inputs
        updateButtons();
    }

    @FXML
    private void randomButtonClicked(ActionEvent event) {
        // Convert ArrayList to array
        inputImages = inputsArray.toArray(new Image[0]);
        //if image is not null and more than two images are entered
        if (inputImages.length >= 2) {
            try {
                //creates new image from median image
                Image medianImage = MeanImageMedian.generateImage(inputImages, "random");
                //add image to image view
                outputImageView.setImage(medianImage);
                //catch if dimensions do not match
            } catch (IllegalArgumentException exception) {
                alertMessage("Image dimensions don't match.", exception.getMessage());
            }
        }
        //update buttons for new inputs
        updateButtons();
    }

    @FXML
    private void saveButtonClicked(ActionEvent event) {
        //check image view is not empty
        if (outputImageView.getImage() != null) {
            //create new file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    //allow for certain file types
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("PPM Files", "*.ppm"),
                    new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
                    new FileChooser.ExtensionFilter("MSOE Files", "*.msoe"));
            File selectedFile = fileChooser.showSaveDialog(null);
            //send image file to write image
            if (selectedFile != null) {
                try {
                    Path imagePath = selectedFile.toPath();
                    MeanImageMedian.writeImage(imagePath, outputImageView.getImage());
                    //catch if image was not able to save
                } catch (IOException exception) {
                    alertMessage("An error occurred while saving the image.",
                            exception.getMessage());
                }
            }
        }
    }

    /**
     * Creates alert pop up
     * @param header of alert
     * @param content displayed in alert
     */
    private void alertMessage(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * updates buttons, so they reset before the user interacts
     * with other features
     */
    private void updateButtons() {
        inputImages = inputsArray.toArray(new Image[0]);
        meanButton.setDisable(inputImages.length < 2);
        medianButton.setDisable(inputImages.length < 2);
        minButton.setDisable(inputImages.length < 2);
        maxButton.setDisable(inputImages.length < 2);
        randomButton.setDisable(inputImages.length < 2);
        saveButton.setDisable(outputImageView.getImage() == null);
    }
}
