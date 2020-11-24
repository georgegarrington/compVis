package uk.ac.soton.ecs.gg2g17.ch5;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.*;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.feature.local.matcher.quantised.BasicQuantisedKeypointMatcher;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.analysis.algorithm.FourierTemplateMatcher;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.transforms.FundamentalRefinement;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.geometry.transforms.estimation.RobustFundamentalEstimator;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;

import java.io.IOException;
import java.net.URL;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static MBFImage query;
    private static MBFImage target;

    public static void main( String[] args ) {

        init();
        //example();
        exercise1();
        exercise2();


    }

    public static void init(){

        try {
            query = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/query.jpg"));
            target = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/target.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void example(){

        //Use difference of guassian feature detector which we describe with a SIFT descriptor

        //Features found are describes in a way that makes them invariant to size, rotation and position changes

        MBFImage query = App.query.clone();
        MBFImage target = App.target.clone();

        DoGSIFTEngine engine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
        LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

        //Basic matchers matches the keypoint for us

        LocalFeatureMatcher<Keypoint> matcher = new BasicMatcher<Keypoint>(80);
        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
        //DisplayUtilities.display(basicMatches);

        //in the last example many of the matches are clearly incorrect
        //a more advanced approach is to filter matches based on a given geometric model

        RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(50.0, 1500,
                new RANSAC.PercentageInliersStoppingCondition(0.5));
        matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);

        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),
                RGBColour.RED);

        //DisplayUtilities.display(consistentMatches, "BasicMatcher");

        //We can draw a polygon around the estimated location of the query


        target.drawShape(
                query.getBounds().transform(modelFitter.getModel().getTransform().inverse()), 3,RGBColour.BLUE);
        DisplayUtilities.display(target);

    }

    public static void exercise1(){

        MBFImage query = App.query.clone();
        MBFImage target = App.target.clone();

        DoGSIFTEngine engine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
        LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

        LocalFeatureMatcher<Keypoint> matcher = new BasicTwoWayMatcher<Keypoint>();
        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        LocalFeatureMatcher<Keypoint> vMatcher = new VotingKeypointMatcher<Keypoint>(80);
        vMatcher.setModelFeatures(queryKeypoints);
        vMatcher.findMatches(targetKeypoints);

        MBFImage basicMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(), RGBColour.RED);
        //DisplayUtilities.display(basicMatches);

        MBFImage vMatches = MatchingUtilities.drawMatches(query, target, vMatcher.getMatches(), RGBColour.RED);

        //The results achieved are very similar to that of using just a BasicMatcher
        DisplayUtilities.display(basicMatches, "BasicTwoWayMatcher");

        //The results are worse with a voting keypoint matcher
        DisplayUtilities.display(vMatches, "VotingKeypointMatcher");

    }

    public static void exercise2(){

        MBFImage query = App.query.clone();
        MBFImage target = App.target.clone();

        DoGSIFTEngine engine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(query.flatten());
        LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(target.flatten());

        LocalFeatureMatcher<Keypoint> matcher;

        //RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(50.0, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5));

        //The RobustAffineTransformEstimator looks to be much more effective than this one
        //RobustHomographyEstimator modelFitter = new RobustHomographyEstimator(50.0, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5), HomographyRefinement.SINGLE_IMAGE_TRANSFER);

        //LMedS algorithm version
        RobustHomographyEstimator modelFitter = new RobustHomographyEstimator(0.5, HomographyRefinement.SINGLE_IMAGE_TRANSFER);

        //This one seems like its even worse
        //RobustFundamentalEstimator modelFitter = new RobustFundamentalEstimator(50.0, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5), FundamentalRefinement.NONE);

        matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);

        matcher.setModelFeatures(queryKeypoints);
        matcher.findMatches(targetKeypoints);

        MBFImage consistentMatches = MatchingUtilities.drawMatches(query, target, matcher.getMatches(),
                RGBColour.RED);

        DisplayUtilities.display(consistentMatches, "RobustHomographyEstimator model");

    }

}
