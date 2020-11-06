package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.SinglebandImageProcessor;

import java.util.Arrays;

public class MyConvolution implements SinglebandImageProcessor<Float, FImage> {

    //Purely for testing
    public static void main(String[] args){
        //test();
        //test2();
        test3();
    }

    //private float[][] kernel;

    private static float[][] kernel = new float[][]{
            new float[]{0,1,-1},
            new float[]{1,1,0},
            new float[]{-1,1,1}
    };

    private static int kWidth /* remove after testing */ = kernel[0].length;
    private static int kHeight /* remove after testing */ = kernel.length;

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

        //float[][] pixels = image.pixels;

        float[][] pixels = new float[][]{
                new float[]{1,2,3,4},
                new float[]{5,6,7,8},
                new float[]{9,10,11,12},
                new float[]{13,14,15,16}
        };

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

                System.out.println("sum is: " + sum);
                pixels[i][j] = sum;
                //image.setPixelNative(j, i, sum);

            }

        }

    }

    public static void test(){

        float[][] pixels = new float[][]{
                new float[]{1,2,3,4},
                new float[]{5,6,7,8},
                new float[]{9,10,11,12},
                new float[]{13,14,15,16}
        };

        for(int i = 0; i < pixels.length; i++){

            /*
            Each inner array of pixels should all be the same length,
            but generalise it anyway
            */
            for(int j = 0; j < pixels[0].length; j++){

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

                System.out.println("sum is: " + sum);
                pixels[i][j] = sum;

            }

        }

        for(float[] inner : pixels){

            System.out.println(Arrays.toString(inner));

        }

    }

    public static void test2(){

        float[][] pixels = new float[][]{
                new float[]{1,2,3,4},
                new float[]{5,6,7,8},
                new float[]{9,10,11,12},
                new float[]{13,14,15,16}
        };

        float[][] output = new float[4][4];

        //for each row
        for(int i = 0; i < pixels.length; i++){

            //for each cell in the row
            for(int j = 0; j < pixels[0].length; j++){

                System.out.println("ITERATION " + i + " " + j);

                float sum = 0;

                for(int yOffset = i - ((kernel.length - 1) / 2), kernelY = 0; kernelY < kernel.length; yOffset++, kernelY++){

                    System.out.println("yOffset is: " + yOffset);

                    if(yOffset < 0)
                        continue;

                    for(int xOffset = j -((kernel[0].length - 1) / 2), kernelX = 0; kernelX < kernel[kernelY].length; xOffset++, kernelX++){

                        System.out.println("xOffset is: " + xOffset);

                        if(xOffset < 0)
                            continue;

                        //This block is all for testing can be deleted after
                        System.out.println("Imaginary position: " + xOffset + ", " + yOffset);
                        System.out.println("With relative position in the kernel: " + kernelX + ", " + kernelY);
                        float amtToAdd = kernel[kernelY][kernelX] * pixels[yOffset][xOffset];
                        sum += amtToAdd;
                        System.out.println("Added: " + amtToAdd);

                    }

                }

                output[i][j] = sum;

            }

        }

        System.out.println("The value of output is: ");
        for(float[] inner : output){
            System.out.println(Arrays.toString(inner));
        }

    }

    public static void test3(){

        float[][] pixels = new float[][]{
                new float[]{1,2,3,4},
                new float[]{5,6,7,8},
                new float[]{9,10,11,12},
                new float[]{13,14,15,16}
        };

        //This is not generalised so make sure to change it later
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