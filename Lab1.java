/*
 * Course: CSC1120A - 121
 * Spring 2024
 * Lab 1 - Mean Image Median
 * Name: Madison Betz
 * Created: 1/16/2024
 */
package betzm;

import javafx.scene.image.Image;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Reads images in the plain PPM format to produce
 * an image that contains the median and average of
 * the input images.
 */
public class Lab1 {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        //variables used for user input
        String preference;
        String outputFile;
        String[] inputPaths;
        preference = preferenceMethod(in);
        outputFile = outputFileMethod(in);
        inputPaths = inputPathsMethod(in);
        //checks that the input images are the same size
        while (!checkSize(inputPaths)) {
            System.out.println("Input images must have the same dimensions. " +
                    "Please enter valid images.");
            inputPaths = inputPathsMethod(in);
        }
        //creates new array of image paths
        Image[] inputImages = new Image[inputPaths.length];
        //sends images to find the median
        for (int i = 0; i < inputPaths.length; i++) {
            inputImages[i] = MeanImageMedian.readImage(Path.of(inputPaths[i]));
        }
        Image outputImage;
        //sends images to find the median or mean depending on user input
        if (preference.equals("median")) {
            outputImage = MeanImageMedian.calculateMedianImage(inputImages);
        } else {
            outputImage = MeanImageMedian.calculateMeanImage(inputImages);
        }

        MeanImageMedian.writeImage(Path.of(outputFile), outputImage);
    }

    //gets the user input for preference of mean or median
    private static String preferenceMethod(Scanner in) {
        String choice;
        //allows for upper or lower case
        do {
            System.out.println("Enter operation preference (median/mean):");
            choice = in.nextLine().toLowerCase();
        } while (!choice.equals("median") && !choice.equals("mean"));
        return choice;
    }

    //gets the user input for the output file name
    private static String outputFileMethod(Scanner in) {
        //asks for correct format
        System.out.println("Enter output filename (Ex. images/output.ppm):");
        return in.nextLine();
    }

    //gets the user input for the input image paths
    private static String[] inputPathsMethod(Scanner in) {
        int numImages;
        do {
            //asks for correct format
            System.out.println("Enter the number of input images (at least 2):");
            while (!in.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number.");
                in.next();
            }
            numImages = in.nextInt();
            in.nextLine();
            //requires more than two images
        } while (numImages < 2);
        //creates array of image paths
        String[] inputImagePaths = new String[numImages];
        for (int i = 0; i < numImages; i++) {
            System.out.println("Enter input image filename " + (i + 1) +
                    " (Ex. images/input" + (i + 1) + ".ppm):");
            inputImagePaths[i] = in.nextLine();
        }
        //returns array of image paths
        return inputImagePaths;
    }

    /**
     * checks that the size of the input images are the same
     * @param imagePaths for the images
     * @return if the size of the images are the same
     * @throws IOException if the images are not the same dimensions
     */
    private static boolean checkSize(String[] imagePaths) throws IOException {
        Image referenceImage = MeanImageMedian.readImage(Path.of(imagePaths[0]));
        for (int i = 1; i < imagePaths.length; i++) {
            Image currentImage = MeanImageMedian.readImage(Path.of(imagePaths[i]));
            //same width and height between images
            if (referenceImage.getWidth() != currentImage.getWidth() ||
                    referenceImage.getHeight() != currentImage.getHeight()) {
                throw new IOException("Image dimensions do not match.");
            }
        }
        return true;
    }
}
