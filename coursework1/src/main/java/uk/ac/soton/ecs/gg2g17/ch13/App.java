package uk.ac.soton.ecs.gg2g17.ch13;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.model.EigenImages;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static VFSGroupDataset<FImage> dataset;
    private static GroupedDataset<String, ListDataset<FImage>, FImage> training;
    private static GroupedDataset<String, ListDataset<FImage>, FImage> testing;

    //The learned PCA
    private static EigenImages eigen;

    public static void main( String[] args ) {

        init();
        //example();
        //exercise1();
        exercise2();
        exercise3();

    }

    public static void init(){

        try {
            dataset = new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
        } catch (FileSystemException e) {

        }

    }

    public static void example() {

        int nTraining = 5;
        int nTesting = 5;
        GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
        training = splits.getTrainingDataset();
        testing = splits.getTestDataset();

        List<FImage> basisImages = DatasetAdaptors.asList(training);
        int nEigenvectors = 100;
        eigen = new EigenImages(nEigenvectors);
        eigen.train(basisImages);

        List<FImage> eigenFaces = new ArrayList<FImage>();
        for (int i = 0; i < 12; i++) {
            eigenFaces.add(eigen.visualisePC(i));
        }
        DisplayUtilities.display("EigenFaces", eigenFaces);

        Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
        for (final String person : training.getGroups()) {
            final DoubleFV[] fvs = new DoubleFV[nTraining];

            for (int i = 0; i < nTraining; i++) {
                final FImage face = training.get(person).get(i);
                fvs[i] = eigen.extractFeature(face);
            }
            features.put(person, fvs);
        }

        double correct = 0, incorrect = 0;
        for (String truePerson : testing.getGroups()) {
            for (FImage face : testing.get(truePerson)) {
                DoubleFV testFeature = eigen.extractFeature(face);

                String bestPerson = null;
                double minDistance = Double.MAX_VALUE;
                for (final String person : features.keySet()) {
                    for (final DoubleFV fv : features.get(person)) {
                        double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);



                        if (distance < minDistance) {
                            minDistance = distance;
                            bestPerson = person;
                        }
                    }
                }

                System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);

                if (truePerson.equals(bestPerson))
                    correct++;
                else
                    incorrect++;
            }
        }
        System.out.println("Accuracy: " + (correct / (correct + incorrect)));

    }

    public static void exercise1() {

        DoubleFV feature = eigen.extractFeature(testing.getRandomInstance());
        DisplayUtilities.display(eigen.reconstruct(feature).normalise(), "Exercise 1");

    }

    public static void exercise2(){

        int nTesting = 5;

        //See what happens as the number of training images reduces
        for(int nTraining = 5; nTraining > 0; nTraining--){

            GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
            training = splits.getTrainingDataset();
            testing = splits.getTestDataset();

            List<FImage> basisImages = DatasetAdaptors.asList(training);
            int nEigenvectors = 100;
            eigen = new EigenImages(nEigenvectors);
            eigen.train(basisImages);

            List<FImage> eigenFaces = new ArrayList<FImage>();
            for (int i = 0; i < 12; i++) {
                eigenFaces.add(eigen.visualisePC(i));
            }
            DisplayUtilities.display("EigenFaces with training no: " + nTraining, eigenFaces);

            Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
            for (final String person : training.getGroups()) {
                final DoubleFV[] fvs = new DoubleFV[nTraining];

                for (int i = 0; i < nTraining; i++) {
                    final FImage face = training.get(person).get(i);
                    fvs[i] = eigen.extractFeature(face);
                }
                features.put(person, fvs);
            }

            double correct = 0, incorrect = 0;
            for (String truePerson : testing.getGroups()) {
                for (FImage face : testing.get(truePerson)) {
                    DoubleFV testFeature = eigen.extractFeature(face);

                    String bestPerson = null;
                    double minDistance = Double.MAX_VALUE;
                    for (final String person : features.keySet()) {
                        for (final DoubleFV fv : features.get(person)) {
                            double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);

                            if (distance < minDistance) {
                                minDistance = distance;
                                bestPerson = person;
                            }
                        }
                    }

                    if (truePerson.equals(bestPerson))
                        correct++;
                    else
                        incorrect++;
                }
            }

            //As the number of training images decrease, you can see that the accuracy becomes worse
            System.out.println("Training no " + nTraining + " accuracy: " + (correct / (correct + incorrect)));

        }

    }

    public static void exercise3() {

        int nTraining = 5;
        int nTesting = 5;
        GroupedRandomSplitter<String, FImage> splits = new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
        training = splits.getTrainingDataset();
        testing = splits.getTestDataset();

        List<FImage> basisImages = DatasetAdaptors.asList(training);
        int nEigenvectors = 100;
        eigen = new EigenImages(nEigenvectors);
        eigen.train(basisImages);

        List<FImage> eigenFaces = new ArrayList<FImage>();
        for (int i = 0; i < 12; i++) {
            eigenFaces.add(eigen.visualisePC(i));
        }
        DisplayUtilities.display("EigenFaces", eigenFaces);

        Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
        for (final String person : training.getGroups()) {
            final DoubleFV[] fvs = new DoubleFV[nTraining];

            for (int i = 0; i < nTraining; i++) {
                final FImage face = training.get(person).get(i);
                fvs[i] = eigen.extractFeature(face);
            }
            features.put(person, fvs);
        }

        double threshold = 1;

        double correct = 0, incorrect = 0;
        for (String truePerson : testing.getGroups()) {
            for (FImage face : testing.get(truePerson)) {
                DoubleFV testFeature = eigen.extractFeature(face);

                String bestPerson = null;
                double minDistance = Double.MAX_VALUE;
                for (final String person : features.keySet()) {

                    for (final DoubleFV fv : features.get(person)) {
                        double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);

                        if (distance < minDistance) {
                            minDistance = distance;
                            bestPerson = person;
                        }

                    }

                }

                System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);

                if (truePerson.equals(bestPerson))
                    correct++;
                else
                    incorrect++;
            }
        }
        System.out.println("Accuracy: " + (correct / (correct + incorrect)));

    }

}
