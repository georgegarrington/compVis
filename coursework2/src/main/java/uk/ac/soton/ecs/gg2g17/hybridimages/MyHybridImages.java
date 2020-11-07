package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;

import java.io.File;
import java.util.Map;

public class MyHybridImages {

    public static void main(String[] args){
        testWithDefault();
    }

    /**
     * Compute a hybrid image combining low-pass and high-pass filtered images
     *
     * @param lowImage
     *            the image to which apply the low pass filter
     * @param lowSigma
     *            the standard deviation of the low-pass filter
     * @param highImage
     *            the image to which apply the high pass filter
     * @param highSigma
     *            the standard deviation of the low-pass component of computing the
     *            high-pass filtered image
     * @return the computed hybrid image
     */
    public static MBFImage makeHybrid(MBFImage lowImage, float lowSigma, MBFImage highImage, float highSigma) {

        int lowKernelSize = calcKernelSize(lowSigma);
        float[][] lowKernel = Gaussian2D.createKernelImage(lowKernelSize ,lowSigma).pixels;
        MBFImage lowPassed = lowImage.process(new MyConvolution(lowKernel));

        int highKernelSize = calcKernelSize(highSigma);
        float[][] highKernel = Gaussian2D.createKernelImage(highKernelSize, highSigma).pixels;
        MBFImage highPassed = highImage.subtract(highImage.process(new MyConvolution(highKernel)));

        return highPassed.add(lowPassed);

    }

    /**
     * Calculate the size of the kernel window given sigma
     * @param sigma
     * @return
     */
    public static int calcKernelSize(float sigma){
        int size = (int) (8.0f * sigma + 1.0f);
        if (size % 2 == 0) size++;
        return size;
    }

    /**
     * The code I used to generate my scale visualisation of the hybrid image of trump and an oompa loompa
     */
    public static void generateTrumpOompaLoompaHybridImage(){



    }

    /**
     * Given the path of image pairs contained in individual directories, import them into a VFSGroupDataset
     * @param arr the path of the directory in array form
     * @return
     */
    public static VFSGroupDataset<MBFImage> importImages(String[] arr){

        VFSGroupDataset<MBFImage> images = null;

        String path = arr[0];

        for(int i = 1; i < arr.length; i++){

            path += File.separator + arr[i];

        }

        try {
            images = new VFSGroupDataset<MBFImage>(path, ImageUtilities.MBFIMAGE_READER);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        return images;

    }

    /**
     * An example of using the test method with a directory on my device
     */
    public static void testWithDefault(){

        try {
            test(new String[]{System.getProperty("user.home"), "Downloads", "data"});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test the images with some pairs from the path given in the form of an array of strings
     * so that it works on all platforms
     */
    public static void test(String[] arr) throws Exception {

        /*
        This is where I have stored my images, change the path to where
        the image pairs you want to test are. Ensure that the pairs are
        stored together in a folder containing only those 2 pairs for
        instance perhaps the cat and dog images are stored together in a folder
        called "catDog"
         */
        VFSGroupDataset<MBFImage> images = importImages(arr);

        for(Map.Entry<String, VFSListDataset<MBFImage>> entry : images.entrySet()){

            VFSListDataset<MBFImage> pair = entry.getValue();

            if(pair.size() != 2)
                throw new Exception("Make sure that you have only given pairs of images contained in" +
                        "\n individual directories within the directory for images");

            MBFImage fst = pair.get(0);
            MBFImage snd = pair.get(1);

            DisplayUtilities.display(makeHybrid(fst, 5, snd, 9), entry.getKey());

        }

    }

}