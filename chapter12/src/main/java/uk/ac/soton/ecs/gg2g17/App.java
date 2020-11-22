package uk.ac.soton.ecs.gg2g17;

import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.dataset.sampling.GroupSampler;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.annotation.evaluation.datasets.Caltech101;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

import java.io.IOException;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static GroupedDataset<String, ListDataset<Caltech101.Record<FImage>>, Caltech101.Record<FImage>> data;

    public static void main( String[] args ) {

        init();

    }

    public static void init(){

        VFSGroupDataset<Caltech101.Record<FImage>> allData = null;

        try {
            allData = Caltech101.getData(ImageUtilities.FIMAGE_READER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        data = GroupSampler.sample(allData, 5, false);

    }

}
