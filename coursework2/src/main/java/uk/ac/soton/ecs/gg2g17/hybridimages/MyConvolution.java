package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    private static float[][] kernel;

    //The width and height of the kernel
    private static int kWidth;
    private static int kHeight;

    /**
     * Constructor ensures that dimensionality is matching (all rows same size) and non even
     * @param kernel
     */
    public MyConvolution(float[][] kernel) {

        /*
        Ideally I would have liked to use checked exceptions for error handling, however
        as the test harness tries to create an instance of the MyConvolution class I was
        worried that adding a throws declaration to the constructor would affect this so I
        have used the System.err.println() method instead which I think is perfectly suitable
        for the scope of this task which only requires very simple error handling
         */
        if(kernel == null){
            System.err.println("Kernel cannot be null!");
            return;
        }

        //Check that both dimensions are odd in size
        if(kernel.length % 2 == 0 || kernel[0].length % 2 == 0){
            System.err.println("Both dimensions of the kernel must be odd!");
            return;
        }

        //Check that all "rows" are the same length
        for(int i = 0; i < kernel.length - 1; i++){
            if(kernel[i].length != kernel[i + 1].length){
                System.err.println("The kernel is of an inconsistent dimension this is not allowed!");
                return;
            }
        }

        //The dimensionality has been verified so the "width" of the kernel is simply the length of the first inner array
        kWidth = kernel[0].length;
        kHeight = kernel.length;

        /*
        flip the kernel horizontally and vertically, which is the same as "rotating" 180 degrees
        in order to make the process a true convolution and not a correlation
         */
        float[][] rotated = new float[kernel.length][kernel[0].length];
        for(int i = 0; i < kernel.length; i++){
            for(int j = 0; j < kernel[0].length; j++){
                rotated[i][kernel[0].length - 1 - j] = kernel[kernel.length - 1 - i][j];
            }
        }

        MyConvolution.kernel = rotated;

    }

    @Override
    /**
     * Convolve image with kernel and store the result back in image
     * hint: use FImage#internalAssign(FImage) to set the contents
     * of your temporary buffer image to the image
     */
    public void processImage(FImage image) {

        float[][] pixels = image.pixels;

        int height = pixels.length;
        int width = pixels[0].length;

        //Make the output image the same size as the image in the argument
        float[][] convoluted = new float[height][width];

        //For each row
        for(int i = 0; i < height; i++){

            //For each cell in the row replace it with the convolution output of the kernel
            for(int j = 0; j < width; j++){

                /*
                The convolution of the kernel where the product of each cell in the kernel with their respective
                "overlayed" pixels in the image will be added to. As zero padding is required, those cells in the
                kernel that are outside the bounds of the image will simply be ignored and instead the
                method will only add all possible products between all the cells in the valid section of the kernel
                that is overlayed within the image and their corresponding cells in the image
                 */
                float sum = 0;

                /*
                imaginaryI and imaginaryJ keep track of the "imaginary" relative cell location in the kernel for instance
                if you were looking at pixel 0,0 and your kernel was 3x3 then imaginaryI and imaginaryJ would
                initially be -1,-1 respectively, however as this is not a valid index the loop will know to skip them,
                so you will never have an index out of bounds problem.

                It will also know to move onto looking at the next iteration once imaginaryI or imaginaryJ
                has become greater than the bounds of the image as clearly there are no longer any possible cells
                in the kernel that are currently overlaid onto the image.

                kernelI and kernelJ keep track of the relative location of the cell being looked at in the kernel
                that will be multiplied with the pixel in the image that it is "overlaid" on
                */
                for(int imaginaryI = i - ((kHeight - 1) / 2), kernelI = 0; kernelI < kHeight; imaginaryI++, kernelI++){

                    //Ignore the parts of the template positioned past the bottom of the image
                    if(imaginaryI >= height)
                        break;

                    //Ignore the parts of the template positioned above the top of the image
                    if(imaginaryI < 0)
                        continue;

                    for(int imaginaryJ = j - ((kWidth - 1) / 2), kernelJ = 0; kernelJ < kWidth; imaginaryJ++, kernelJ++){

                        //Ignore the parts of the template positioned past the right of the image
                        if(imaginaryJ >= width)
                            break;

                        //Ignore the parts of the template positioned before the left of the image
                        if(imaginaryJ < 0)
                            continue;

                        sum += kernel[kernelI][kernelJ] * pixels[imaginaryI][imaginaryJ];

                    }

                }

                convoluted[i][j] = sum;

            }

        }

        //Replace the pixels in the image in the argument with the convolution that has just been calculated
        image.pixels = convoluted;

    }

}