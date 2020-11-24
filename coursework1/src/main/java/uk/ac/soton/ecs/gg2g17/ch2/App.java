package uk.ac.soton.ecs.gg2g17.ch2;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class App {

    private static MBFImage image;

    public static void main( String[] args ) {

        init();
        //example();
        exercise1();
        exercise2();

    }

    private static void init() {

        //How to read an image from a file
        //MBFImage image = ImageUtilities.readMBF(new File("file.jpg"));

        try {
            //How to read an image from a URL
            image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Field to store the image's colour space
        System.out.println(image.colourSpace);

    }

    private static void example(){

        /*
        How to get the FImage of specific bands of an MBFImage e.g. the R,G,B channels are
        bands 0, 1, 2 respectively, L,A,B channels for a LAB colour space MBFImage are also
        0, 1, 2 etc.
         */
        DisplayUtilities.display(image);
        DisplayUtilities.display(image.getBand(0), "Red Channel");

        //clone the instance so we can apply changes to a clone and leave the original image unedited
        MBFImage clone = image.clone();
        for (int y=0; y<image.getHeight(); y++) {
            for(int x=0; x<image.getWidth(); x++) {

                //Make all pixels in the green and blue channels black i.e. 0 is black 255 is white
                clone.getBand(1).pixels[y][x] = 0;
                clone.getBand(2).pixels[y][x] = 0;
            }
        }
        DisplayUtilities.display(clone);

        /*
        a simpler way of doing the above
        clone.getBand(1).fill(0f);
        clone.getBand(2).fill(0f);
        */

        //apply canny edge detector
        image.processInplace(new CannyEdgeDetector());
        //DisplayUtilities.display(image);

        //draw a speech bubble using a series of ellipses
        image.drawShapeFilled(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
        image.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
        image.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
        DisplayUtilities.display(image);

    }

    //Repeat the example but display everything using only a named window
    public static void exercise1(){

        DisplayUtilities.createNamedWindow("Bob", "The named window");

        /*
        How to get the FImage of specific bands of an MBFImage e.g. the R,G,B channels are
        bands 0, 1, 2 respectively, L,A,B channels for a LAB colour space MBFImage are also
        0, 1, 2 etc.
         */
        DisplayUtilities.displayName(image, "Bob");
        pause();
        DisplayUtilities.displayName(image.getBand(0), "Bob");
        pause();

        //clone the instance so we can apply changes to a clone and leave the original image unedited
        MBFImage clone = image.clone();
        for (int y=0; y<image.getHeight(); y++) {
            for(int x=0; x<image.getWidth(); x++) {

                //Make all pixels in the green and blue channels black i.e. 0 is black 255 is white
                clone.getBand(1).pixels[y][x] = 0;
                clone.getBand(2).pixels[y][x] = 0;
            }
        }
        DisplayUtilities.displayName(clone, "Bob");
        pause();

        /*
        a simpler way of doing the above
        clone.getBand(1).fill(0f);
        clone.getBand(2).fill(0f);
        */

        //apply canny edge detector
        image.processInplace(new CannyEdgeDetector());
        //DisplayUtilities.display(image);

        //draw a speech bubble using a series of ellipses
        image.drawShapeFilled(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
        image.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
        image.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
        DisplayUtilities.displayName(image, "Bob");
        pause();

    }

    //Draw a border around the speech bubbles
    public static void exercise2(){

        image.drawShape(new Ellipse(700f, 450f, 20f, 10f, 0f), 5, RGBColour.BLUE);
        image.drawShape(new Ellipse(650f, 425f, 25f, 12f, 0f), 5, RGBColour.BLUE);
        image.drawShape(new Ellipse(600f, 380f, 30f, 15f, 0f), 5, RGBColour.BLUE);
        image.drawShape(new Ellipse(500f, 300f, 100f, 70f, 0f), 5, RGBColour.BLUE);

        //Refresh the named display to show the speech bubble borders
        DisplayUtilities.displayName(image, "Bob");

    }

    //A method to pause between window updates
    public static void pause(){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}