package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    //Purely for testing
    public static void main(String[] args){
        System.out.println(-(4/2));
    }

    private float[][] kernel;
    private int kWidth;
    private int kHeight;

    public MyConvolution(float[][] kernel) throws Exception{

        for(int i = 0; i < kernel.length - 1; i++){

            if(kernel[i].length != kernel[i + 1].length)
                throw new Exception("The kernel is of an inconsistent dimension this is not allowed!");

        }

        if(kernel.length % 2 == 0 || kernel[0].length % 2 == 0){
            throw new Exception("Both dimensions of the kernel must be odd!");
        }

        this.kernel = kernel;

        //The dimensionality has been verified so the "width" of the kernel is simply the length of the first inner array
        kWidth = kernel[0].length;
        kHeight = kernel.length;

    }

    @Override
    /**
     * Convolve image with kernel and store the result back in image
     * hint: use FImage#internalAssign(FImage) to set the contents
     * of your temporary buffer image to the image
     */
    public void processImage(FImage image) {

        float[][] pixels = image.pixels;

        for(int i = 0; i < kHeight; i++){

            /*
            Each inner array of pixels should all be the same length,
            but generalise it anyway
            */
            for(int j = 0; j < kWidth; j++){

                /*
                How much the starting "point" of the kernel is offset by relative to the current cell
                being looked at e.g. if you were first looking at pixel 0,0 and the template size is 3x3
                then the offset is -1,-1 and any values with negative indices will be treated as zero
                in order to implement zero padding
                */
                int xOffset = -((kHeight - 1) / 2);
                int yOffset = -((kWidth - 1) / 2);

                int sum = 0;

                //kernelX and kernelY are the relative indices of the template being looked at
                for(int k = yOffset, kernelY = 0; k < kHeight; k++, kernelY++){

                    //Adding zero is clearly the same as doing nothing, so just do nothing
                    if(k < 0)
                        continue;

                    for(int l = xOffset, kernelX = 0; l < kWidth; l++, kernelX++){

                        //Adding zero is clearly the same as doing nothing, so just do nothing
                        if(l < 0)
                            continue;

                        sum += kernel[kernelY][kernelX] * pixels[k][l];

                    }

                }

                image.setPixelNative(j, i, sum);

            }

        }

    }

}