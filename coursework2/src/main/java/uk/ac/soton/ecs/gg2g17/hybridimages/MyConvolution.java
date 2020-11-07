package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processor.SinglebandImageProcessor;

import java.util.Arrays;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    //JUST FOR TESTING
    public static void main(String[] args){

        float[][] kernel = Gaussian2D.createKernelImage(6, 10).pixels;

        System.out.println("The value of the kernel is:");
        for(float[] inner : kernel){
            System.out.println(Arrays.toString(inner));
        }

    }

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
                initially be -1,-1 respectively, however as this is not valid the loop will know to skip them.
                It will also know to move onto looking at the next iteration once imaginaryI or imaginaryJ
                has become greater than the bounds of the image as clearly there are no longer any possible cells
                in the kernel that are currently overlayed onto the image.

                kernelI and kernelJ keep track of the relative location of the cell being looked at in the kernel
                that will be timesed with the "overlay" position of this kernel cell with the pixel in the image
                that it is "overlayed" on
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

    public static void test3(){

        float[][] pixels = {
                {1,2,3,4},
                {5,6,7,8},
                {9,10,11,12},
                {13,14,15,16}
        };

        //This is not generalised so make sure to change it later, something like this
        //float[][] output = new float[pixels.length][pixels[0].length]
        float[][] output = new float[4][4];

        //For each row
        for(int i = 0; i < pixels.length; i++){

            //For each cell in the row replace it with the output of the kernel
            for(int j = 0; j < pixels[0].length; j++){

                float sum = 0;

                /*
                The shifted "start" indices which may go out of the filter, it may not be imaginary but obviously it is if it is
                out of bounds hence the name convention
                */
                for(int imaginaryI = i - ((kernel.length - 1) / 2), kernelI = 0; kernelI < kernel.length; imaginaryI++, kernelI++){

                    if(imaginaryI >= pixels.length)
                        break;

                    if(imaginaryI < 0)
                        continue;

                    for(int imaginaryJ = j - ((kernel[0].length - 1) / 2), kernelJ = 0; kernelJ < kernel[0].length; imaginaryJ++, kernelJ++){

                        if(imaginaryJ >= pixels[0].length)
                            break;

                        if(imaginaryJ < 0)
                            continue;

                        //This block is all for testing can be deleted after
                        System.out.println("Imaginary position: " + imaginaryI + ", " + imaginaryJ);
                        System.out.println("With relative position in the kernel: " + kernelI + ", " + kernelJ);
                        float kernelAmt = kernel[kernelI][kernelJ];
                        float pixelsAmt = pixels[imaginaryI][imaginaryJ];
                        System.out.println("Will add the product of the kernel amount " + kernelAmt + " and the pixels amount " + pixelsAmt);
                        float amtToAdd = kernelAmt * pixelsAmt;
                        sum += amtToAdd;
                        //System.out.println("Added: " + amtToAdd);

                    }

                }

                output[i][j] = sum;

            }

        }

        System.out.println("The value of pixels is: ");
        for(float[] inner: output){
            System.out.println(Arrays.toString(inner));
        }

    }

    public static void dunnoThingy(){

         /* Might be useful for some kind of reporting function

        System.out.println("value of pixels is: ");
        for(float[] inner : pixels){
            System.out.println(Arrays.toString(inner));
        }

        System.out.println("value of kernel is:");
        for(float[] inner : kernel){
            System.out.println(Arrays.toString(inner));
        }

        System.out.println("value of kHeight is: " + kHeight);
        System.out.println("value of kWidth is: " + kWidth);*/

    }

}