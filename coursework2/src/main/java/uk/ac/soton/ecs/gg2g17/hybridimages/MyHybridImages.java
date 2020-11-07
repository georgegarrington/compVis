package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;

public class MyHybridImages {

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

        //implement your hybrid images functionality here.
        //Your submitted code must contain this method, but you can add
        //additional static methods or implement the functionality through
        //instance methods on the `MyHybridImages` class of which you can create
        //an instance of here if you so wish.
        //Note that the input images are expected to have the same size, and the output
        //image will also have the same height & width as the inputs.

        return null;

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
        return original.subtract(lowPassed);

    }

    /*

    Don't think this is necessary possibly remove it...

    //Subtract the first argument by the second argument and return the result
    public static float[][] subtract(float[][] fst, float[][] snd) throws Exception {

        if(fst.length != snd.length || fst[0].length != snd[0].length){
            throw new Exception("Can only subtract matrices of the same dimension!");
        }

        float[][] subtracted = new float[fst.length][fst[0].length];

        for(int i = 0; i < subtracted.length; i++){

            for(int j = 0; j < subtracted[0].length; j++){

                subtracted[i][j] = fst[i][j] - snd[i][j];

            }

        }

        return subtracted;

    }*/

}