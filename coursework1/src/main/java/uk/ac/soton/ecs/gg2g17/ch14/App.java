package uk.ac.soton.ecs.gg2g17.ch14;

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
import org.openimaj.util.parallel.partition.RangePartitioner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static GroupedDataset<String, ListDataset<MBFImage>, MBFImage> images;

    public static void main( String[] args ) {

        init();
        //example();
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

        //Parallel version of a for loop, new thread started to handle each iteration (I think?)
        //Removes most of the boilerplate code from having to this sort of thing manually
        Parallel.forIndex(0, 10, 1, new Operation<Integer>() {

            //Perform an operation on the Object with type of the type parameter given
            public void perform(Integer i) {
                System.out.println(i);
            }

        });


        //Part 1, perform this chunk of code and time how long it takes
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
        System.out.println("1st method time: " + t1.duration() + "ms");
        DisplayUtilities.display("1st method", output);


        //Part 2, parallelise the inner for loop
        t1 = Timer.timer();
        output = new ArrayList<MBFImage>();
        final ResizeProcessor resize2 = new ResizeProcessor(200);
        for (ListDataset<MBFImage> clzImages : images.values()) {

            final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);

            Parallel.forEach(clzImages, new Operation<MBFImage>() {
                public void perform(MBFImage i) {
                    final MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);
                    tmp.fill(RGBColour.WHITE);

                    final MBFImage small = i.process(resize2).normalise();
                    final int x = (200 - small.getWidth()) / 2;
                    final int y = (200 - small.getHeight()) / 2;
                    tmp.drawImage(small, x, y);

                    //Make sure that only one thread can add to the image at a time
                    synchronized (current) {
                        current.addInplace(tmp);
                    }
                }
            });

            current.divideInplace((float) clzImages.size());
            output.add(current);
        }
        System.out.println("2nd method time: " + t1.duration() + "ms");
        DisplayUtilities.display("2nd method", output);


        //PART 3
        t1 = Timer.timer();
        output = new ArrayList<MBFImage>();
        final ResizeProcessor resize3 = new ResizeProcessor(200);
        for (ListDataset<MBFImage> clzImages : images.values()) {

            final MBFImage current = new MBFImage(200, 200, ColourSpace.RGB);

            Parallel.forEachPartitioned(new RangePartitioner<MBFImage>(clzImages), new Operation<Iterator<MBFImage>>() {
                public void perform(Iterator<MBFImage> it) {
                    MBFImage tmpAccum = new MBFImage(200, 200, 3);
                    MBFImage tmp = new MBFImage(200, 200, ColourSpace.RGB);

                    while (it.hasNext()) {
                        final MBFImage i = it.next();
                        tmp.fill(RGBColour.WHITE);

                        final MBFImage small = i.process(resize3).normalise();
                        final int x = (200 - small.getWidth()) / 2;
                        final int y = (200 - small.getHeight()) / 2;
                        tmp.drawImage(small, x, y);
                        tmpAccum.addInplace(tmp);
                    }
                    synchronized (current) {
                        current.addInplace(tmpAccum);
                    }
                }
            });

            current.divideInplace((float) clzImages.size());
            output.add(current);
        }
        System.out.println("3rd method time: " + t1.duration() + "ms");
        DisplayUtilities.display("3rd method", output);


    }

    public static void exercise1(){

        Timer t1 = Timer.timer();
        List<MBFImage> output = new ArrayList<MBFImage>();
        ResizeProcessor resize = new ResizeProcessor(200);

        //Parallelised version
        Parallel.forEach(images.values(), new Operation<ListDataset<MBFImage>>() {

            @Override
            public void perform(ListDataset<MBFImage> clzImages) {

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

        });
        System.out.println("Parallelised outer loop time: " + t1.duration() + "ms");
        DisplayUtilities.display("Parallelised outer loop: ", output);

    }

}
