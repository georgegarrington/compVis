package uk.ac.soton.ecs.gg2g17.hybridimages;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.convolution.Gaussian2D;
import org.openimaj.image.processing.resize.ResizeProcessor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
     * Display a downsampling visualization of an image as shown in the spec
     * @param image
     */
    public static void visualizeDownSample(MBFImage image){

        /*
        The images that will be displayed, show 5 different sizes where each successive
        image in the array is half the size of the preceding image
         */
        MBFImage[] images = new MBFImage[5];
        images[0] = image;

        for(int i = 1; i < images.length; i++){

            images[i] = ResizeProcessor.halfSize(images[i - 1]);

        }

        int gapSize = 10;
        int diagramHeight = images[0].getHeight();
        int diagramWidth = (images.length - 1) * gapSize;

        for(MBFImage i : images){

            diagramWidth += i.getWidth();

        }

        MBFImage diagram = new MBFImage(diagramWidth, diagramHeight);

        //Make the background white
        diagram.addInplace(255f);
        diagram.drawImage(images[0], 0, 0);

        //x and y coordinates to plot the image, i to count the index of the array
        for(int x = 0, y = 0, i = 1; i < images.length; i++){

            x += images[i - 1].getWidth() + gapSize;
            y += images[i - 1].getHeight() - images[i].getHeight();
            diagram.drawImage(images[i], x, y);

        }

        DisplayUtilities.display(diagram, "Diagram");

    }

    /**
     * Save the given image as a jpeg with the given file name
     * @param image
     * @param fileName
     */
    public static void export(MBFImage image, String fileName){

        try {
            ImageUtilities.write(image, new File(fileName + ".jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Given the path of image pairs contained in individual directories, import them into a VFSGroupDataset
     * @param arr the path of the directory in array form
     * @return images grouped in pairs by the directory they are both contained in
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
     * An example of using the test method with a directory on my device, you can
     * simply change the string array in the definition to the string array of the
     * directory containing the pairs you want to test (and obviously tweak sigma
     * vals and image order etc in the test method)
     */
    public static void testWithDefault(){

        test(new String[]{System.getProperty("user.home"), "Downloads", "data"});

    }

    /**
     * Test the images with the pairs contained in the path given in the
     * form of an array of strings so that it works on all platforms
     */
    public static void test(String[] arr) {

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

            if(pair.size() != 2) System.err.println("Make sure that you have only given pairs of images contained in" +
                        "\n individual directories within the directory for images!");

            MBFImage fst = pair.get(0);
            MBFImage snd = pair.get(1);

            /*
            This is the best parameters and orientation for generating the trump/oompa loompa hybrid
            It is different for each two images so change this for each e.g. sigma vals and swapping
            image arguments
             */
            MBFImage hybrid = makeHybrid(fst, 3, snd, 10);
            visualizeDownSample(hybrid);

        }

    }

}