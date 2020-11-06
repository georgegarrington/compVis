package uk.ac.soton.ecs.gg2g17;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.algorithm.AnisotropicDiffusion;
import org.openimaj.image.processing.convolution.AverageBoxFilter;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.processing.edges.CannyEdgeDetector2;
import org.openimaj.image.processing.effects.DioramaEffect;
import org.openimaj.image.processing.resize.BilinearInterpolation;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.processing.transform.RemapProcessor;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.xuggle.XuggleVideo;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    //MBFImage is the type of the frame contained within the video collection
    private static Video<MBFImage> video;

    public static void main(String[] args) {

        init();
        exercise1();

    }

    public static void init(){
        try {
            video = new XuggleVideo(new URL("http://static.openimaj.org/media/tutorial/keyboardcat.flv"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //If we wanted to use our camera as live input we could do it like so:
        //video = new VideoCapture(320, 240)
    }

    public static void exercise1(){

        VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);

        //We can treat the video simply as a collection of MBFImages

        /*
        for (MBFImage mbfImage : video) {
            DisplayUtilities.displayName(mbfImage.process(new CannyEdgeDetector()), "videoFrames");
        }*/

        //add a video listener to process frames as they change
        //This event driven approach is superior to the looping/iteration approach
        display.addVideoListener(new VideoDisplayListener<MBFImage>() {

            //process the frame before it is displayed
            public void beforeUpdate(MBFImage frame) {
                //frame.processInplace(new CannyEdgeDetector());

                //Apply the Gaussian blur function instead to achieve a blurred video instead of the Canny edge detector
                frame.processInplace(new Gaussian2D(10, (float) 10));
            }

            public void afterUpdate(VideoDisplay<MBFImage> display) {

            }

        });

    }

}
