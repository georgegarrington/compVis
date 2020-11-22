package uk.ac.soton.ecs.gg2g17;

import jogamp.opengl.glu.nurbs.Bin;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.dataset.BingImageDataset;
import org.openimaj.image.dataset.FlickrImageDataset;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.util.api.auth.DefaultTokenFactory;
import org.openimaj.util.api.auth.common.BingAPIToken;
import org.openimaj.util.api.auth.common.FlickrAPIToken;
import org.openimaj.web.flickr.FlickrImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static VFSListDataset<FImage> images;
    private static FlickrAPIToken flickrToken;

    public static void main(String[] args) {

        init();
        //example();
        exercise1();
        //exercise2();
        exercise3();
        exercise4();

    }

    public static void init(){

        /*
        try {
            images = new VFSListDataset<FImage>("/Users/georgegarrington/Documents/GitHub/compVis/chapter6IMages", ImageUtilities.FIMAGE_READER);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }*/

        flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);

        FlickrImageDataset<FImage> cats = null;
        try {
            //FlickrImageDataset is used to retrieve a dataset of images from flickr with the space seperated search terms (tags of images)
            cats = FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DisplayUtilities.display("Cats", cats);

    }

    public static void example(){

        //Use this method to get a random thing from the list of images
        //DisplayUtilities.display(images.getRandomInstance(), "A random image from the dataset");

        //Displays the list of images in a single window
        //DisplayUtilities.display("My images: ", images);


        VFSListDataset<FImage> faces =
                null;
        try {
            //Loads in some faces, see how you can use zip files
            faces = new VFSListDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
        //DisplayUtilities.display("ATT faces", faces);

        //When we do the above we lost the group information i.e. the fact that different pictures of the same inidivudals face
        //are grouped together in a directory. Using a GroupDataset can mitigate this

        VFSGroupDataset<FImage> groupedFaces = null;

        try {
            groupedFaces =
                    new VFSGroupDataset<FImage>( "zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        for(final Map.Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet()){

            //The key is the string name of the directory, and the value mapped to each key in the dataset
            //is a list of images (that were contained in the directory)
            DisplayUtilities.display(entry.getKey(), entry.getValue());

        }

        //This is to authenticate Flickr. The first time you use it it will give instructions on how to
        //authenticate and prompt for token information, then once you have done it once it will be
        //stored persistently (I think) in the DefaultTokenFactory so you don't have to do it again

        /*

        use for when the key messes up you can delete it using this method and do it again

        try {
            DefaultTokenFactory.delete(FlickrAPIToken.class);
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }*/

        FlickrAPIToken flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);

        FlickrImageDataset<FImage> cats = null;
        try {
            //FlickrImageDataset is used to retrieve a dataset of images from flickr with the space seperated search terms (tags of images)
            cats = FlickrImageDataset.create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DisplayUtilities.display("Cats", cats);

    }

    public static void exercise1(){

        List<FImage> randomSelections = new ArrayList<FImage>();

        VFSGroupDataset<FImage> grouped = null;

        try {
            grouped = new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        for(final Map.Entry<String, VFSListDataset<FImage>> entry : grouped.entrySet()){

            //Add a random image for each person
            randomSelections.add(entry.getValue().getRandomInstance());

        }

        DisplayUtilities.display("Randomly selected photo of each individual", randomSelections);

    }

    public static void exercise2(){

        /*
        HTTP server, FTP server, WebDav server, SFTP server, FTPS server, CIFS server (Samba)
        are examples of other kinds of sources supported for building datasets.

        File systems supported include many well known formats like BZIP2, GZIP, TAR,
        Jar, RAM, Zip and of course normal files
         */

    }

    public static void exercise3(){

        BingAPIToken bingToken = DefaultTokenFactory.get(BingAPIToken.class);

        BingImageDataset<FImage> cats = null;
        try {
            //FlickrImageDataset is used to retrieve a dataset of images from flickr with the space seperated search terms (tags of images)
            cats = BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, "cat", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DisplayUtilities.display("Cats", cats);

    }

    public static void exercise4(){

        String[] celebs = new String[]{"Lewis Hamilton", "Meghan Markle", "Adele"};

        BingAPIToken bingToken = DefaultTokenFactory.get(BingAPIToken.class);
        List<BingImageDataset<FImage>> celebDatasets = null;

        for(String celeb : celebs){

            try {
                //Find 5 pictures of each celeb and create a BingImageDataset to contain these images for each celeb
                celebDatasets.add(BingImageDataset.create(ImageUtilities.FIMAGE_READER, bingToken, celeb, 5));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        MapBackedDataset mapBackedDataset = MapBackedDataset.of(celebDatasets);

        for(Object entry : mapBackedDataset.entrySet()){

            Map.Entry<String, BingImageDataset<FImage>> casted = (Map.Entry<String, BingImageDataset<FImage>>) entry;
            DisplayUtilities.display(casted.getKey(), casted.getValue());

        }

    }

}