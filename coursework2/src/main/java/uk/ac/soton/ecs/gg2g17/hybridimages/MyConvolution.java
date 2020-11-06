package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    //Purely for testing
    public static void main(String[] args){
        System.out.println((1 - 1) / 2);
    }

    private float[][] kernel;

    public MyConvolution(float[][] kernel) throws Exception{

        for(int i = 0; i < kernel.length - 1; i++){

            if(kernel[i].length != kernel[i + 1].length)
                throw new Exception("The kernel is of an inconsistent dimension this is not allowed!");

        }

        if(kernel.length % 2 == 0 || kernel[0].length % 2 == 0){
            throw new Exception("Both dimensions of the kernel must be odd!");
        }

        this.kernel = kernel;
    }

    @Override
    /**
     * Convolve image with kernel and store the result back in image
     * hint: use FImage#internalAssign(FImage) to set the contents
     * of your temporary buffer image to the image
     */
    public void processImage(FImage image) {

        float[][] pixels = image.pixels;

        for(int i = 0; i < pixels.length; i++){

            /*
            Each inner array of pixels should all be the same length,
            but generalise it anyway
            */
            for(int j = 0; j < pixels[i].length; j++){

                /*
                How much the starting "point" of the kernel is offset by relative to the current cell
                being looked at e.g. if you were first looking at pixel 0,0 and the template size is 3x3
                then the offset is -1,-1 and any values with negative indices will be treated as zero
                in order to implement zero padding
                */
                int kernelXOffset = (kernel[0].length - 1) / 2;
                int kernelYOffset = (kernel.length - 1) / 2;

                //for(int k = 0; k < kernel.length)

            }

        }

    }

}