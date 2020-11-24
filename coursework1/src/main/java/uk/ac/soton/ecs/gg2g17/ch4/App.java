package uk.ac.soton.ecs.gg2g17.ch4;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main( String[] args ) {
        foo();
    }

    public static void foo(){

        URL[] imageURLs = null;

        try {
            imageURLs = new URL[] {
                    new URL( "http://openimaj.org/tutorial/figs/hist1.jpg" ),
                    new URL( "http://openimaj.org/tutorial/figs/hist2.jpg" ),
                    new URL( "http://openimaj.org/tutorial/figs/hist3.jpg" )
            };
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        List<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
        List<MBFImage> images = new ArrayList<MBFImage>();
        HistogramModel model = new HistogramModel( 4, 4, 4 );

        for( URL u : imageURLs ) {
            try {
                MBFImage image = ImageUtilities.readMBF(u);

                //The indices of the histograms and the images will correspond to one another
                images.add(image);
                model.estimateModel(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            histograms.add( model.histogram.clone() );
        }

        //Used to calculate a comparison between 2 histograms, think of the score as a distance apart in space

        //2 very similar histograms will be very close together so have a small distance score, whereas two dissimilar
        //histograms will be far apart and so have a large distance score

        //double distanceScore = histograms.get(0).compare( histograms.get(1), DoubleFVComparison.EUCLIDEAN );

        //The Euclidean distance measure is symmetric meaning if you compare A with B this is then the same as comparing
        //B with A, therefore we can compare all the histograms with each other in a simple, efficient nested loop

        /* EXERCISE 1 */

        double smallest = Double.MAX_VALUE;

        //Store the indices of the images which are most similar
        int fstIndex = 0;
        int sndIndex = 0;

        for(int i = 0; i < histograms.size(); i++){

            for(int j = i; j < histograms.size(); j++){

                //Don't compare an image with itself
                if(j == i)
                    continue;

                //When running this you will see that images 0 and 1 are the most similar which is to be expected
                //as they look the most similar visually and have similar colours
                double distance = histograms.get(i).compare(histograms.get(j), DoubleFVComparison.EUCLIDEAN);

                if(distance < smallest){
                    smallest = distance;
                    fstIndex = i;
                    sndIndex = j;
                }

                System.out.println("The distance between histograms " + i + " and " + j + " is: " + distance);

            }

        }

        System.out.println("Value of smallest is: " + smallest);
        System.out.println("Value of the indices are: " + fstIndex + " and " + sndIndex);

        System.out.println();
        System.out.println("Now going to display the two most similar images...");
        DisplayUtilities.display(images.get(fstIndex));
        DisplayUtilities.display(images.get(sndIndex));

        /* EXERCISE 2 */

        System.out.println();
        System.out.println("Now going to use intersection comparison instead.");
        System.out.println();

        smallest = Integer.MAX_VALUE;

        for(int i = 0; i < histograms.size(); i++){

            for(int j = i; j < histograms.size(); j++){

                //Don't compare an image with itself
                if(j == i)
                    continue;

                //Now instead the intersection of the histograms is used as the distance measure so images
                //that are the most different have the closest distance between them e.g. in this example
                // image 1 and 2 (the lightest and the darkest)
                double distance = histograms.get(i).compare(histograms.get(j), DoubleFVComparison.INTERSECTION);

                if(distance < smallest){
                    smallest = distance;
                    fstIndex = i;
                    sndIndex = j;
                }

                System.out.println("The distance between histograms " + i + " and " + j + " is: " + distance);

            }

        }

        System.out.println("Value of smallest is: " + smallest);
        System.out.println("Value of the indices are: " + fstIndex + " and " + sndIndex);

    }

}