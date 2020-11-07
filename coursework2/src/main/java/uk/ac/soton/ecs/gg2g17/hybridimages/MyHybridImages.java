package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class MyHybridImages {

    //JUST FOR TESTING DELETE LATER
    public static void main(String[] args){

        /*
        This is where I have stored my images, change the path to where
        the image pairs you want to test are. Ensure that the pairs are
        stored together in a folder containing only those 2 pairs for
        instance perhaps the cat and dog images are stored together in a folder
        called "catDog"
         */
        VFSGroupDataset<MBFImage> images = importImages(new String[]{

                //Use this to get the actual value of "~" as relative paths are forbidden
                System.getProperty("user.home"), "Downloads", "data",

        });

        /*
        for(MBFImage image : images.get("catDog")){
            DisplayUtilities.display(image);
        }*/

        /*
        DisplayUtilities.display(cat);
        DisplayUtilities.display(dog);*/


        for(Map.Entry<String, VFSListDataset<MBFImage>> entry : images.entrySet()){

            //Only look at the cat and dog for now
            if(!entry.getKey().equals("borisTrump"))
                continue;

            VFSListDataset<MBFImage> pair = entry.getValue();
            MBFImage cat = pair.get(0);
            MBFImage dog = pair.get(1);
            //DisplayUtilities.display(cat, "Cat");
            //DisplayUtilities.display(dog, "Dog");

            DisplayUtilities.display(makeHybrid(dog, 4, cat, 9));

            /*
            int sigma = 4;
            int size = calcKernelSize(sigma);
            float[][] kernel = Gaussian2D.createKernelImage(size, sigma).pixels;

            try {
                MBFImage lowPassedDog = dog.process(new MyConvolution(kernel));
                DisplayUtilities.display(lowPassedDog);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sigma = 9;
            size = calcKernelSize(sigma);
            kernel = Gaussian2D.createKernelImage(size, sigma).pixels;

            try {
                MBFImage highPassedCat = cat.subtract(cat.process(new MyConvolution(kernel)));
                DisplayUtilities.display(highPassedCat.add(0.5f));
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }

    }

    /**
     * Given the path of images, import them into a VFSListDataset
     * @param path
     * @return
     */
    public static VFSGroupDataset<MBFImage> importImages(String[] path){

        VFSGroupDataset<MBFImage> images = null;

        try {
            images = new VFSGroupDataset<MBFImage>(generatePath(path), ImageUtilities.MBFIMAGE_READER);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        /*
        for(final Map.Entry<String, VFSListDataset<MBFImage>> entry : images.entrySet()){

            DisplayUtilities.display(entry.getKey(), entry.getValue());

        }*/

        return images;

    }

    /**
     * Generate the platform specific file path
     * @param path
     * @return
     */
    public static String generatePath(String[] path){

        String out = path[0];

        for(int i = 1; i < path.length; i++){

            out += File.separator + path[i];

        }

        return out;

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

        MBFImage lowPassed = null;

        try {
            lowPassed = lowImage.process(new MyConvolution(lowKernel));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int highKernelSize = calcKernelSize(highSigma);
        float[][] highKernel = Gaussian2D.createKernelImage(highKernelSize, highSigma).pixels;

        MBFImage highPassed = null;

        try {
            highPassed = highImage.subtract(highImage.process(new MyConvolution(highKernel)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return highPassed.add(lowPassed);


        /*
        int sigma = 4;
            int size = calcKernelSize(sigma);
            float[][] kernel = Gaussian2D.createKernelImage(size, sigma).pixels;

            try {
                MBFImage lowPassedDog = dog.process(new MyConvolution(kernel));
                DisplayUtilities.display(lowPassedDog);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sigma = 9;
            size = calcKernelSize(sigma);
            kernel = Gaussian2D.createKernelImage(size, sigma).pixels;

            try {
                MBFImage highPassedCat = cat.subtract(cat.process(new MyConvolution(kernel)));
                DisplayUtilities.display(highPassedCat.add(0.5f));
            } catch (Exception e) {
                e.printStackTrace();
            }
         */

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

    public static MBFImage lowPassFilter(MBFImage image, float sigma){

        int size = calcKernelSize(sigma);
        float[][] kernel = Gaussian2D.createKernelImage(size, sigma).pixels;

        //Exception handling to ensure valid dimensionality and identical "row" sizes in the image
        try {

            /*
            process will clone the instance it is being called on, then process
            it with the given Processor and return the result in a new image instance
             */
            image.process(new MyConvolution(kernel));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;

    }

    public static MBFImage highPassFilter(MBFImage original, float sigma){

        MBFImage lowPassed = lowPassFilter(original, sigma);

        /*
        subtract will clone the instance it is being called on, subtract the
        argument and then return in a new image instance
         */
        MBFImage rtn = original.clone();
        return rtn.subtract(lowPassed);

    }

}