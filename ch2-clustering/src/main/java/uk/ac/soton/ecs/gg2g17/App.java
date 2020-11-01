package uk.ac.soton.ecs.gg2g17;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.FloatKMeans;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static MBFImage input;
    private static MBFImage input2;

    public static void main( String[] args ) {

        input = null;

        try {
            input = ImageUtilities.readMBF(new URL("https://images.saatchiart.com/saatchi/968237/art/4638665/3708501-SGDXRGQC-7.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        input2 = input.clone();
        example();

    }

    public static void example(){

        input = ColourSpace.convert(input, ColourSpace.CIE_Lab);
        FloatKMeans cluster = FloatKMeans.createExact(2);
        float[][] imageData = input.getPixelVectorNative(new float[input.getWidth() * input.getHeight()][3]);
        FloatCentroidsResult result = cluster.cluster(imageData);
        float[][] centroids = result.centroids;
        for (float[] fs : centroids) {
            System.out.println(Arrays.toString(fs));
        }

        HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();
        for (int y=0; y<input.getHeight(); y++) {
            for (int x=0; x<input.getWidth(); x++) {
                float[] pixel = input.getPixelNative(x, y);
                int centroid = assigner.assign(pixel);
                input.setPixelNative(x, y, centroids[centroid]);
            }
        }

        input = ColourSpace.convert(input, ColourSpace.RGB);
        //DisplayUtilities.display(input);

        GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();

        //.flatten() method is used to merge colour into their grey values by averaging their RGB values
        List<ConnectedComponent> components = labeler.findComponents(input.flatten());

        int i = 0;
        for (ConnectedComponent comp : components) {
            if (comp.calculateArea() < 50)
                continue;
            input.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
        }

        DisplayUtilities.display(input);

    }

    public static void exercise3_1(){

        input2 = ColourSpace.convert(input2, ColourSpace.CIE_Lab);
        FloatKMeans cluster = FloatKMeans.createExact(2);
        float[][] imageData = input2.getPixelVectorNative(new float[input2.getWidth() * input2.getHeight()][3]);
        FloatCentroidsResult result = cluster.cluster(imageData);
        float[][] centroids = result.centroids;
        for (float[] fs : centroids) {
            System.out.println(Arrays.toString(fs));
        }

        HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();
        for (int y=0; y<input.getHeight(); y++) {
            for (int x=0; x<input.getWidth(); x++) {
                float[] pixel = input.getPixelNative(x, y);
                int centroid = assigner.assign(pixel);
                input.setPixelNative(x, y, centroids[centroid]);
            }
        }

        input.processInplace(new PixelProcessor<Float[]>() {

            @Override
            public Float[] processPixel(Float[] pixel) {

                int centroid = assigner.assign(pixel);
                return pixel;

            }

        });

    }

    public static void exercise3_2(){



    }

}
