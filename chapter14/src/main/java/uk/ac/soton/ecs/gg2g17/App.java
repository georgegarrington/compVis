package uk.ac.soton.ecs.gg2g17;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.sampling.GroupSampler;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.time.Timer;
import org.openimaj.util.parallel.Parallel;
import org.openimaj.util.function.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static GroupedDataset<String, ListDataset<MBFImage>, MBFImage> images;

    public static void main( String[] args ) {

        init();
        example();
        exercise1();

    }

    public static void init(){

        VFSGroupDataset<MBFImage> allImages = null;

        try {
            allImages = Caltech101.getImages(ImageUtilities.MBFIMAGE_READER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        images = GroupSampler.sample(allImages, 8, false);

    }

    public static void example(){

        System.out.println("Now going to do a parallel version of a for loop counting from 0 to 9");
        //Parallel version of a for loop
        Parallel.forIndex(0, 10, 1, new Operation<Integer>() {
            public void perform(Integer i) {
                System.out.println(i);
            }
        });

    }

    public static void exercise1(){

        Timer t1 = Timer.timer();

        List<MBFImage> output = new ArrayList<MBFImage>();
        ResizeProcessor resize = new ResizeProcessor(200);
        for (ListDataset<MBFImage> clzImages : images.values()) {

            MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);

            for (MBFImage i : clzImages) {
                MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
                tmp.fill(RGBColour.WHITE);

                MBFImage small = i.process(resize).normalise();
                int x = (200 - small.getWidth()) / 2;
                int y = (200 - small.getHeight()) / 2;
                tmp.drawImage(small, x, y);

                current.addInplace(tmp);
            }
            current.divideInplace((float) clzImages.size());
            output.add(current);
        }

        System.out.println("Time: " + t1.duration() + "ms");
        DisplayUtilities.display("Images", output);

    }

}
