/*
 * Course: CSC1120A - 121
 * Spring 2024
 * Lab 3
 * Name: Madison Betz
 * Created: 2/1/2024
 */
package betzm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Creates a UI for the user to input images and get a resulting
 * image from the mean or median of pixel characteristics
 */
public class Lab3 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Lab3.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Mean/Median Image Creator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
