/*
 * Course: CSC1120A - 121
 * Spring 2024
 * Lab 2
 * Name: Madison Betz
 * Created: 1/23/2024
 */
package betzm;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

import static betzm.MeanImageMedian.*;

/**
 * Allows for the user to print different image types
 * into a window to be displayed
 */
public class Lab2 extends Application {
    @Override
    public void start(Stage stage) {
        try {
            //Parse command line arguments
            Parameters params = getParameters();
            String operation = params.getUnnamed().get(0);
            String outputFilename = params.getUnnamed().get(1);
            String[] inputFilenames = params.getUnnamed().subList(2,
                    params.getUnnamed().size()).toArray(new String[0]);
            //Read input images
            Image[] inputImages = new Image[inputFilenames.length];
            for (int i = 0; i < inputFilenames.length; i++) {
                inputImages[i] = readImage(Path.of(inputFilenames[i]));
            }
            //perform the specified operation
            Image resultImage = null;
            if (operation.equalsIgnoreCase("median")) {
                resultImage = MeanImageMedian.calculateMedianImage(inputImages);
            } else if (operation.equalsIgnoreCase("mean")) {
                resultImage = MeanImageMedian.calculateMeanImage(inputImages);
            }
            if (resultImage == null) {
                System.out.println("Invalid operation preference. Use 'median' or 'mean'.");
            } else {
                //write the output image
                writeImage(Path.of(outputFilename), resultImage);
                //display the resulting image
                displayImage(stage, resultImage);
            }
        } catch (IOException invalidImage) {
            System.out.println("Image not valid.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Displays the image
     * @param stage to display
     * @param image being displayed
     */
    private void displayImage(Stage stage, Image image) {
        //create ImageView and set the image
        ImageView imageView = new ImageView(image);
        //create Group and add ImageView to it
        Group group = new Group(imageView);
        //find dimensions of image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        //create scene with group and set dimensions
        Scene scene = new Scene(group, width, height);
        //set title of graphical window to dimensions of first image
        stage.setTitle(width + " " + height);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
