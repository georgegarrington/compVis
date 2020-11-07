package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processor.SinglebandImageProcessor;

import java.util.Arrays;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    private static float[][] kernel;
    private static int kWidth;
    private static int kHeight;

    /**
     * Constructor ensures that dimensionality is matching (all rows same size) and non even
     * @param kernel
     * @throws Exception
     */
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

        //Make the output image the same size as the image in the argument
        float[][] convoluted = new float[pixels.length][pixels[0].length];

        //For each row
        for(int i = 0; i < pixels.length; i++){

            //For each cell in the row replace it with the convoluted of the kernel
            for(int j = 0; j < pixels[0].length; j++){

                /*
                The convoluted of the kernel where the product of each cell in the kernel with their respective
                "overlayed" pixels in the image will be added to. As zero padding is required, those cells in the
                kernel that are outside the bounds of the image will simply be ignored and instead just add
                all possible products possible from the valid section of the kernel that is within the image
                 */
                float sum = 0;

                /*
                imaginaryI and imaginaryJ keep track of the "imaginary" cell location in the kernel for instance
                if you were looking at pixel 0,0 and your kernel was 3x3 then imaginaryI and imaginaryJ would
                initially be -1,-1 respectively, however as this is not valid the loop will know to skip them,
                so you will never have an index out of bounds problem.

                It will also know to move onto looking at the next iteration once imaginaryI or imaginaryJ
                has become greater than the bounds of the image as clearly there are no longer any possible cells
                in the kernel that are currently overlayed onto the image.

                kernelI and kernelJ keep track of the relative location of the cell being looked at in the kernel
                that will be multiplied with the "overlay" position of this kernel cell with the pixel in the image
                that it is "overlaid" on
                */
                for(int imaginaryI = i - ((kHeight - 1) / 2), kernelI = 0; kernelI < kHeight; imaginaryI++, kernelI++){

                    if(imaginaryI >= pixels.length)
                        break;

                    if(imaginaryI < 0)
                        continue;

                    for(int imaginaryJ = j - ((kWidth - 1) / 2), kernelJ = 0; kernelJ < kWidth; imaginaryJ++, kernelJ++){

                        if(imaginaryJ >= pixels[0].length)
                            break;

                        if(imaginaryJ < 0)
                            continue;

                        sum += kernel[kernelI][kernelJ] * pixels[imaginaryI][imaginaryJ];

                    }

                }

                convoluted[i][j] = sum;

            }

        }

        image.pixels = convoluted;

    }

}