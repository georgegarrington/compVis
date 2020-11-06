package uk.ac.soton.ecs.gg2g17;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.util.KEDetectedFaceRenderer;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

import java.util.List;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {

    private static VideoDisplay<MBFImage> vd;

    public static void main( String[] args ) {

        init();

    }

    public static void init(){

        VideoCapture vc = null;

        try {
            vc = new VideoCapture(600, 400);
        } catch (VideoCaptureException e) {
            e.printStackTrace();
        }

        vd = VideoDisplay.createVideoDisplay(vc);

        vd.addVideoListener(new VideoDisplayListener<MBFImage>() {

            @Override
            public void afterUpdate(VideoDisplay<MBFImage> display) {

            }

            //Manipulates a frame before it is shown on the video display
            @Override
            public void beforeUpdate(MBFImage frame) {

                /*
                FaceDetector<DetectedFace, FImage> fd = new HaarCascadeDetector(40);
                List<DetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));

                for(DetectedFace face : faces){
                    frame.drawShape(face.getBounds(), RGBColour.RED);
                }*/

                FaceDetector<KEDetectedFace, FImage> fd = new FKEFaceDetector();
                List<KEDetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(frame));

                for(KEDetectedFace face : faces){

                    //Easy way of doing it :) found from looking at documentation
                    /*
                    KEDetectedFaceRenderer r = new KEDetectedFaceRenderer();
                    r.drawDetectedFace(frame, 3, face);
                    frame.drawShape(face.getBounds(), RGBColour.RED);
                    */

                    frame.drawShape(face.getBounds(), RGBColour.RED);
                    FacialKeypoint[] kps = face.getKeypoints();

                    for(FacialKeypoint kp : kps){

                        Point2dImpl pos = kp.position.clone();
                        pos.translate(face.getBounds().x, face.getBounds().y);
                        frame.drawPoint(pos, RGBColour.GREEN, 3);

                    }

                }

            }

        });

    }

}
