package uk.ac.soton.ecs.gg2g17;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.BasicMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.MatchingUtilities;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

import java.io.IOException;
import java.net.URL;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {

        MBFImage query = null;
        MBFImage target = null;

        try {
            query = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/query.jpg"));
            target = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/target.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DoGSIFTEngine engine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
        LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

        LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(80);
        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
        DisplayUtilities.display(basicMatches);

    }
}
