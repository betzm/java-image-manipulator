/*
 * Course: CSC1120A - 121
 * Spring 2024
 * Lab 1 - Mean Image Median
 * Name: Madison Betz
 * Created: 1/16/2024
 */
package betzm;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Contains methods for the Lab1 class that will
 * find the mean or median of the red, green, and
 * blue values in each pixel of several images
 * based on user input in Lab1
 */
public class MeanImageMedian {
    
    /**
     * Maximum color value
     */
    public static final int MAX_COLOR = 255;

    /**
     * Number at beginning of msoe image file
     */
    public static final int MSOE_MAGIC_NUMBER = 1297305413;

    /**
     * Calculates the median of all the images passed to the method.
     * <br />
     * Each pixel in the output image consists is calculated as the median
     * red, green, and blue components of the input images at the same location.
     * @param inputImages Images to be used as input
     * @return An image containing the median color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than two, or  if any of the input images differ in size.
     *
     * @deprecated use {@link #generateImage()} instead
     */
    @Deprecated
    public static Image calculateMedianImage(Image[] inputImages) {
        checkImages(inputImages);
        int width = (int) inputImages[0].getWidth();
        int height = (int) inputImages[0].getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter resultPixelWriter = resultImage.getPixelWriter();
        //find the median of the colors in pixels
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                //create an array for each color
                int[] red = new int[inputImages.length];
                int[] green = new int[inputImages.length];
                int[] blue = new int[inputImages.length];
                int[] alphaValue = new int[inputImages.length];

                for (int i = 0; i < inputImages.length; i++) {
                    PixelReader pixelReader = inputImages[i].getPixelReader();
                    int argb = pixelReader.getArgb(w, h);
                    red[i] = argbToRed(argb);
                    green[i] = argbToGreen(argb);
                    blue[i] = argbToBlue(argb);
                    alphaValue[i] = argbToAlpha(argb);
                }
                //sort the pixel colors
                Arrays.sort(red);
                Arrays.sort(green);
                Arrays.sort(blue);
                Arrays.sort(alphaValue);
                //for odd number of images
                if (inputImages.length % 2 == 1) {
                    int medianRed = red[red.length / 2];
                    int medianGreen = green[green.length / 2];
                    int medianBlue = blue[blue.length / 2];
                    int medianAlpha = alphaValue[alphaValue.length / 2];
                    int medianArgb = argbToInt(medianAlpha, medianRed, medianGreen, medianBlue);
                    resultPixelWriter.setArgb(w, h, medianArgb);
                } else {
                    //for even number of images
                    int first = red.length / 2 - 1;
                    int second = red.length / 2;
                    int medianRed = (red[first] + red[second]) / 2;
                    int medianGreen = (green[first] + green[second]) / 2;
                    int medianBlue = (blue[first] + blue[second]) / 2;
                    int medianAlpha = alphaValue[first] + alphaValue[second] / 2;
                    int medianArgb = argbToInt(medianAlpha, medianRed, medianGreen, medianBlue);
                    resultPixelWriter.setArgb(w, h, medianArgb);
                }
            }
        }
        //return new image
        return resultImage;
    }

    /**
     * Calculates the mean of all the images passed to the method.
     * <br />
     * Each pixel in the output image consists is calculated as the average of the
     * red, green, and blue components of the input images at the same location.
     * @param inputImages Images to be used as input
     * @return An image containing the mean color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than two, or  if any of the input images differ in size.
     *
     * @deprecated use {@link #generateImage()} instead
     */
    @Deprecated
    public static Image calculateMeanImage(Image[] inputImages) {
        checkImages(inputImages);
        int width = (int) inputImages[0].getWidth();
        int height = (int) inputImages[0].getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter resultPixelWriter = resultImage.getPixelWriter();
        //find the mean of the colors in pixels
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int meanArgb = getMeanArgb(inputImages, w, h);
                resultPixelWriter.setArgb(w, h, meanArgb);
            }
        }
        //return new image
        return resultImage;
    }

    /**
     * Uses a specified operation on the input images to determine the output
     * image. The deprecated methods are replaced by calling this method with
     * either "mean" or "median" as the value for operation.
     * @param images inputted
     * @param operation of what will be applied to images
     * @return an image where the specified operation is applied
     * @throws NullPointerException if image array is empty
     */
    public static Image generateImage(Image[] images, String operation) {
        //checks images is not empty
        Image outputImage = null;
        if (images != null) {
            if (operation.equals("mean")) {
                outputImage = applyTransform(images, a -> Arrays.stream(a).sum() / a.length);
            } else if (operation.equals("median")) {
                outputImage = applyTransform(images, MeanImageMedian::findMedian);
            } else if (operation.equals("random")) {
                outputImage = applyTransform(images, MeanImageMedian::findRandom);
            } else if (operation.equals("min")) {
                outputImage = applyTransform(images, a -> Arrays.stream(a).min().getAsInt());
            } else if (operation.equals("max")) {
                outputImage = applyTransform(images, a-> Arrays.stream(a).max().getAsInt());
            }
        } else {
            throw new NullPointerException("No images inputted.");
        }
        return outputImage;
    }

    /**
     * returns an image that is generated by applying
     * the transformation to each component of each pixel in the images.
     * @param images inputted
     * @param transformation performed on image
     * @return images transformed
     */
    public static Image applyTransform(Image[] images, Transform transformation) {
        int width = (int) images[0].getWidth();
        int height = (int) images[0].getHeight();
        WritableImage resultImage = new WritableImage(width, height);
        PixelWriter resultPixelWriter = resultImage.getPixelWriter();
        //find the mean of the colors in pixels
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int[] red = new int[images.length];
                int[] green = new int[images.length];
                int[] blue = new int[images.length];
                int[] alphaValue = new int[images.length];
                for (int i = 0; i < images.length; i++) {
                    PixelReader pixelReader = images[i].getPixelReader();
                    int argb = pixelReader.getArgb(w, h);
                    red[i] = argbToRed(argb);
                    green[i] = argbToGreen(argb);
                    blue[i] = argbToBlue(argb);
                    alphaValue[i] = argbToAlpha(argb);

                }
                int newRed = transformation.apply(red);
                int newGreen = transformation.apply(green);
                int newBlue = transformation.apply(blue);
                int newAlphaValue = transformation.apply(alphaValue);
                int medianArgb = argbToInt(newAlphaValue, newRed, newGreen, newBlue);
                resultPixelWriter.setArgb(w, h, medianArgb);
            }
        }
        return resultImage;
    }

    private static int getMeanArgb(Image[] inputImages, int w, int h) {
        int alphaValue = 0;
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        for (Image inputImage : inputImages) {
            PixelReader pixelReader = inputImage.getPixelReader();
            int argb = pixelReader.getArgb(w, h);
            totalRed += argbToRed(argb);
            totalGreen += argbToGreen(argb);
            totalBlue += argbToBlue(argb);
            alphaValue += argbToAlpha(argb);
        }
        //get the mean of each pixel color
        int meanRed = totalRed / inputImages.length;
        int meanGreen = totalGreen / inputImages.length;
        int meanBlue = totalBlue / inputImages.length;
        return argbToInt(alphaValue, meanRed, meanGreen, meanBlue);
    }

    /**
     * Reads an image in PPM format. The method only supports
     * the plain PPM (P3) format with 24-bit color
     * and does not support comments in the image file.
     * @param imagePath the path to the image to be read
     * @return An image object containing the image read from the file.
     *
     * @throws IllegalArgumentException Thrown if imagePath is null.
     * @throws IOException Thrown if the image format is invalid or
     * there was trouble reading the file.
     */
    private static Image readPPMImage(Path imagePath) throws IOException {
        //create new file from user input
        if (imagePath == null) {
            throw new IllegalArgumentException("Image pathway is empty.");
        }
        //create scanner for image path
        Scanner in = new Scanner(imagePath);
        //validates the header "P3" is there
        String identifier = in.nextLine();
        if (!identifier.equals("P3")) {
            throw new IOException("Header does not meet requirements.");
        }
        //width for pixel coordinate
        int width;
        width = in.nextInt();
        //height for pixel coordinate
        int height;
        height = in.nextInt();
        //validates that alpha value is 255
        int alphaValue;
        alphaValue = in.nextInt();
        if (alphaValue != MAX_COLOR) {
            throw new IOException("Alpha number does not meet requirements.");
        }
        //take text and make image
        WritableImage wI = new WritableImage(width, height);
        PixelWriter pW = wI.getPixelWriter();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                // Read r, g, and b values and process with setArgb() & argbToInt()
                int redColor = in.nextInt();
                int greenColor = in.nextInt();
                int blueColor = in.nextInt();
                pW.setArgb(w, h, argbToInt(alphaValue, redColor, greenColor, blueColor));
            }
            // drop to a new line
        }
        //return image
        return wI;
    }

    /**
     * Writes an image in PPM format. The method only supports
     * the plain PPM (P3) format with 24-bit color
     * and does not support comments in the image file.
     * @param imagePath the path to where the file should be written
     * @param image the image containing the pixels to be written to the file
     *
     * @throws IllegalArgumentException Thrown if imagePath is null.
     * @throws IOException Thrown if the image format is invalid or
     * there was trouble reading the file.
     */
    private static void writePPMImage(Path imagePath, Image image) throws IOException {
        //check that image path is valid
        if (imagePath == null) {
            throw new IllegalArgumentException("Image pathway is empty.");
        }
        //try making new image
        try (PrintWriter newImage = new PrintWriter(imagePath.toFile())) {
            //add header to image
            newImage.println("P3");
            //get width and height
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            //print height and width in format
            newImage.println(width + " " + height);
            //print alpha value
            newImage.println(MAX_COLOR);
            for(int h = 0; h < height; h++) {
                for(int w = 0; w < width; w++) {
                    //each value for that height and width and goes to argbToCOLOR method
                    PixelReader pR = image.getPixelReader();
                    int argb = pR.getArgb(w, h);
                    int redColor = argbToRed(argb);
                    int greenColor = argbToGreen(argb);
                    int blueColor = argbToBlue(argb);
                    newImage.write(redColor + " " + greenColor + " " + blueColor + " ");
                }
                //print new image
                newImage.println();
            }
        }
    }

    /**
     * Determines what image type is provided and
     * reads the image
     * @param imagePath of image
     * @throws IOException when needed
     * @throws IllegalArgumentException if invalid image type provided
     */
    public static Image readImage(Path imagePath) throws IOException {
        Image image;
        String imagePathString = imagePath.toString();
        if (imagePathString.endsWith(".ppm")) {
            image = readPPMImage(imagePath);
        } else if (imagePathString.endsWith(".png") || imagePathString.endsWith(".jpg")) {
            image = new Image(Files.newInputStream(imagePath));
        } else if (imagePathString.endsWith(".msoe")) {
            image = readMSOEImage(imagePath);
        } else {
            throw new IllegalArgumentException("File type not supported.");
        }
        return image;
    }

    /**
     * Writes image depending on user input of image for various
     * image types
     *
     * @param imagePath of new image
     * @param image     of new image
     * @throws IOException    when needed
     * @throws IllegalArgumentException if file type is not supported
     */
    public static void writeImage(Path imagePath, Image image) throws IOException {
        String imagePathString = imagePath.toString();
        if (imagePathString.endsWith(".ppm")) {
            writePPMImage(imagePath, image);
        } else if (imagePathString.endsWith(".msoe")) {
            writeMSOEImage(imagePath, image);
        } else if (imagePathString.endsWith(".png") || imagePathString.endsWith(".jpg")) {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, getImageFormat(imagePath), imagePath.toFile());
        } else {
            throw new IllegalArgumentException("File type not supported.");
        }
    }

    //reads msoe image
    private static Image readMSOEImage(Path imagePath) throws IOException {
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(imagePath))) {
            //read the magic number
            int magicNumber = dis.readInt();
            if (magicNumber != MSOE_MAGIC_NUMBER) {
                throw new IOException("Invalid MSOE image file.");
            }
            //read width and height
            int width = dis.readInt();
            int height = dis.readInt();
            //create writable image
            WritableImage wI = new WritableImage(width, height);
            PixelWriter pW = wI.getPixelWriter();
            //read pixel values
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    int argb = dis.readInt();
                    pW.setArgb(w, h, argb);
                }
            }

            return wI;
        }
    }

    //writes msoe image from inputted images
    private static void writeMSOEImage(Path imagePath, Image image) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(imagePath))) {
            dos.writeInt(MSOE_MAGIC_NUMBER);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            dos.writeInt(width);
            dos.writeInt(height);
            PixelReader pR = image.getPixelReader();
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    dos.writeInt(pR.getArgb(w, h));
                }
            }
        }
    }

    /**
     * Extract 8-bit Alpha value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Alpha value of the color.
     */
    private static int argbToAlpha(int argb) {
        final int bitShift = 24;
        return argb >> bitShift;
    }

    /**
     * Extract 8-bit Red value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Red value of the color.
     */
    private static int argbToRed(int argb) {
        final int bitShift = 16;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Extract 8-bit Green value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Green value of the color.
     */
    private static int argbToGreen(int argb) {
        final int bitShift = 8;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Extract 8-bit Blue value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Blue value of the color.
     */
    private static int argbToBlue(int argb) {
        final int bitShift = 0;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Converts argb components into a single int that represents the argb value of a color.
     * @param a the 8-bit Alpha channel value of the color
     * @param r the 8-bit Red channel value of the color
     * @param g the 8-bit Green channel value of the color
     * @param b the 8-bit Blue channel value of the color
     * @return a 32-bit representation of the color in the
     * format described by the INT_ARGB PixelFormat type.
     */
    private static int argbToInt(int a, int r, int g, int b) {
        final int alphaShift = 24;
        final int redShift = 16;
        final int greenShift = 8;
        final int mask = 0xff;
        return a << alphaShift | ((r & mask) << redShift) | (g & mask) << greenShift | b & mask;
    }

    /**
     * checks that there are more than two input images and that
     * the images have the same dimensions of width and height
     * @param inputImages for array of input images
     */
    private static void checkImages(Image[] inputImages) {
        //if there are no images or less than required
        if (inputImages == null || inputImages.length < 2) {
            throw new IllegalArgumentException("Invalid input images.");
        }
        //get height and width of images
        int width = (int) inputImages[0].getWidth();
        int height = (int) inputImages[0].getHeight();
        //if images are not the same size
        for (Image image : inputImages) {
            if (image == null || (int) image.getWidth() != width ||
                    (int) image.getHeight() != height) {
                throw new IllegalArgumentException("Input images must have the same size.");
            }
        }
    }

    /**
     * Checks what the file type of the image is
     * @param imagePath of input image
     * @return file type (ex. ppm, png)
     */
    private static String getImageFormat(Path imagePath) {
        //makes filename a string
        String fileName = imagePath.getFileName().toString();
        //finds "." at the end of the image path to find image type
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        throw new IllegalArgumentException("Unable to read file type.");
    }

    private static int findMedian(int[] array) {
        int newValue;
        //sort the pixel colors
        Arrays.sort(array);
        //for odd number of images
        if (array.length % 2 == 1) {
            newValue = array[array.length / 2];
        } else {
            //for even number of images
            int first = array.length / 2 - 1;
            int second = array.length / 2;
            newValue = (array[first] + array[second]) / 2;
        }
        return newValue;
    }

    private static int findRandom(int[] array) {
        int randomValue = (int) (Math.random() * array.length);
        return array[randomValue];
    }
}
