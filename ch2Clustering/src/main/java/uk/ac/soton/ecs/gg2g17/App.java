package uk.ac.soton.ecs.gg2g17;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processor.PixelProcessor;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
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

    private static MBFImage input = null;
    private static MBFImage input2 = null;
    private static MBFImage input3 = null;

    public static void main( String[] args ) {

        init();
        //example();
        //exercise1();
        exercise2();

    }

    public static void init(){

        try {
            //Download a pic of a doggy :)
            input = ImageUtilities.readMBF(new URL("https://cdn.theatlantic.com/thumbor/8nRzlZIyk-7iJSq8qz68WZA14n8=/32x354:2498x1638/960x500/media/img/mt/2019/07/GettyImages_138965532/original.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        input2 = input.clone();
        input3 = input.clone();

    }

    public static void example(){

        //Convert the image from RGB colour space into LAB colour space
        input = ColourSpace.convert(input, ColourSpace.CIE_Lab);

        //The argument is the number of clusters you want, optional second argument of max iterations
        //you want (the default is 30 if this is not specified)
        FloatKMeans cluster = FloatKMeans.createExact(2);

        //Flatten the pixels into a single list of pixels
        float[][] imageData = input.getPixelVectorNative(new float[input.getWidth() * input.getHeight()][3]);

        //Returns the cluster centroids which is the result of clustering the image data into the k clusters
        FloatCentroidsResult result = cluster.cluster(imageData);
        //It has a centroids field that can be accessed which is an array of all the centroids of each cluster

        float[][] centroids = result.centroids;

        for(float[] fs: centroids){
            //As we have only chosen 2 for k, this will print out the centroid for each of the
            //2 clusters after doing 30 iterations of the k means clustering algorithm (as this is the
            //default if we don't specify how many iterations)
            System.out.println(Arrays.toString(fs));
        }

        /*
        Ok so the k means clustering algorithm has determined the centroids of our clusters, but now
        we need to go back through our image data and classify each pixel into a cluster (it will be
        assigned to the cluster whose centroid it is most similar to)
         */

        //HardAssigner is used to classify pixels into clusters by their centroids
        HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();
        for (int y=0; y<input.getHeight(); y++) {
            for (int x=0; x<input.getWidth(); x++) {

                //Get the RGB (or as its LAB rather the L, A, B) values in a 3 element array (or vector) of a given pixel with coordinates x, y
                float[] pixel = input.getPixelNative(x, y);

                //the assign method will return the index in the array of centroids of which centroid (so which cluster) the
                //pixel was classified as
                int centroidIndex = assigner.assign(pixel);

                //Change the colour of pixel with coordinates x, y to the average value of the cluster (the centroid)
                //This means that all pixels in the same cluster will now have the same colour as the centroid
                input.setPixelNative(x, y, centroids[centroidIndex]);

            }

        }

        //Can be used to find all the different segments of the image where the pixels are touching
        GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
        List<ConnectedComponent> components = labeler.findComponents(input.flatten());

        for(int i = 0; i < components.size(); i++){

            if(components.get(i).calculateArea() < 50)
                continue;

            input.drawText("Point:" + i, components.get(i).calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);

        }

        //Convert back into RGB colour space in order to display properly
        input = ColourSpace.convert(input, ColourSpace.RGB);
        DisplayUtilities.display(input);

        /*
        int i = 0;
        for (ConnectedComponent comp : components) {
            if (comp.calculateArea() < 50)
                continue;
            input.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
        }*/

    }

    public static void exercise1(){

        //Convert the image from RGB colour space into LAB colour space
        input2 = ColourSpace.convert(input2, ColourSpace.CIE_Lab);

        //The argument is the number of clusters you want, optional second argument of max iterations
        //you want (the default is 30 if this is not specified)
        FloatKMeans cluster = FloatKMeans.createExact(2);

        //Flatten the pixels into a single list of pixels
        float[][] imageData = input2.getPixelVectorNative(new float[input2.getWidth() * input2.getHeight()][3]);

        //Returns the cluster centroids which is the result of clustering the image data into the k clusters
        FloatCentroidsResult result = cluster.cluster(imageData);
        //It has a centroids field that can be accessed which is an array of all the centroids of each cluster

        final float[][] centroids = result.centroids;

        //HardAssigner is used to classify pixels into clusters by their centroids
        final HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();

        /*

        for (int y=0; y<input.getHeight(); y++) {
            for (int x=0; x<input.getWidth(); x++) {

                //Get the RGB (or as its LAB rather the L, A, B) values in a 3 element array (or vector) of a given pixel with coordinates x, y
                float[] pixel = input.getPixelNative(x, y);

                //the assign method will return the index in the array of centroids of which centroid (so which cluster) the
                //pixel was classified as
                int centroidIndex = assigner.assign(pixel);

                //Change the colour of pixel with coordinates x, y to the average value of the cluster (the centroid)
                //This means that all pixels in the same cluster will now have the same colour as the centroid
                input.setPixelNative(x, y, centroids[centroidIndex]);

            }
        }

        */

        input2.processInplace(new PixelProcessor<Float[]>() {

            public Float[] processPixel(Float[] pixel){

                float[] prim = new float[3];

                for(int i = 0; i < 3; i++){

                    prim[i] = pixel[i];

                }

                int centroidIndex = assigner.assign(prim);

                Float[] output = new Float[3];

                for(int i = 0; i < 3; i++){

                    output[i] = centroids[centroidIndex][i];

                }

                return output;

            }

        });

        //Can be used to find all the different segments of the image where the pixels are touching
        GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
        List<ConnectedComponent> components = labeler.findComponents(input2.flatten());

        for(int i = 0; i < components.size(); i++){

            if(components.get(i).calculateArea() < 50)
                continue;

            input2.drawText("Point:" + i, components.get(i).calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);

        }

        //Convert back into RGB colour space in order to display properly
        input2 = ColourSpace.convert(input2, ColourSpace.RGB);
        DisplayUtilities.display(input2);

    }

    public static void exercise2(){

        //Convert the image from RGB colour space into LAB colour space
        input3 = ColourSpace.convert(input3, ColourSpace.CIE_Lab);

        //The argument is the number of clusters you want, optional second argument of max iterations
        //you want (the default is 30 if this is not specified)
        FloatKMeans cluster = FloatKMeans.createExact(2);

        //Flatten the pixels into a single list of pixels
        float[][] imageData = input3.getPixelVectorNative(new float[input3.getWidth() * input3.getHeight()][3]);

        //Returns the cluster centroids which is the result of clustering the image data into the k clusters
        FloatCentroidsResult result = cluster.cluster(imageData);
        //It has a centroids field that can be accessed which is an array of all the centroids of each cluster

        final float[][] centroids = result.centroids;

        //HardAssigner is used to classify pixels into clusters by their centroids
        final HardAssigner<float[],?,?> assigner = result.defaultHardAssigner();

        /*

        for (int y=0; y<input.getHeight(); y++) {
            for (int x=0; x<input.getWidth(); x++) {

                //Get the RGB (or as its LAB rather the L, A, B) values in a 3 element array (or vector) of a given pixel with coordinates x, y
                float[] pixel = input.getPixelNative(x, y);

                //the assign method will return the index in the array of centroids of which centroid (so which cluster) the
                //pixel was classified as
                int centroidIndex = assigner.assign(pixel);

                //Change the colour of pixel with coordinates x, y to the average value of the cluster (the centroid)
                //This means that all pixels in the same cluster will now have the same colour as the centroid
                input.setPixelNative(x, y, centroids[centroidIndex]);

            }
        }

        */

        input3.processInplace(new PixelProcessor<Float[]>() {

            public Float[] processPixel(Float[] pixel){

                float[] prim = new float[3];

                for(int i = 0; i < 3; i++){

                    prim[i] = pixel[i];

                }

                int centroidIndex = assigner.assign(prim);

                Float[] output = new Float[3];

                for(int i = 0; i < 3; i++){

                    output[i] = centroids[centroidIndex][i];

                }

                return output;

            }

        });

        FelzenszwalbHuttenlocherSegmenter segmenter = new FelzenszwalbHuttenlocherSegmenter();
        List<ConnectedComponent> segments = segmenter.segment(input3);

        /*
        for(int i = 0; i < segments.size(); i++){

            if(segments.get(i).calculateArea() < 50)
                continue;

            input3.drawText("Point:" + i, segments.get(i).calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);

        }

        //Convert back into RGB colour space in order to display properly
        input3 = ColourSpace.convert(input3, ColourSpace.RGB);
        DisplayUtilities.display(input3);*/

        

    }

}
