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
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Ellipse;
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

                    //cornerPos contains the x and y coordinates of where the corner of the mouth is
                    FacialKeypoint corner = FacialKeypoint.getKeypoint(kps, FacialKeypoint.FacialKeypointType.MOUTH_LEFT);
                    Point2dImpl cornerPos = corner.position.clone();
                    cornerPos.translate(face.getBounds().x, face.getBounds().y);
                    //frame.drawPoint(cornerPos, RGBColour.BLUE, 5); simply draws a blue dot where the corner is

                    frame.drawShapeFilled(new Ellipse(cornerPos.x, cornerPos.y, 6, 6, 0), RGBColour.WHITE);
                    frame.drawShapeFilled(new Ellipse(cornerPos.x - 20, cornerPos.y - 20, 10, 10, 0), RGBColour.WHITE);
                    frame.drawShapeFilled(new Ellipse(cornerPos.x - 50, cornerPos.y - 50, 20, 20, 0), RGBColour.WHITE);
                    frame.drawShapeFilled(new Ellipse(cornerPos.x - 150, cornerPos.y - 150, 100, 120, 0), RGBColour.WHITE);
                    frame.drawText("OpenIMAJ is", (int) cornerPos.x - 220, (int) cornerPos.y - 140, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
                    frame.drawText("Awesome", (int) cornerPos.x - 210, (int) cornerPos.y - 110, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
                    //frame.drawShapeFilled(new Ellipse(cornerPos.x - 10, cornerPos.y - 10, 10, 10, 0), RGBColour.WHITE);

                    /*
                    frame.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
                    frame.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE);
                    frame.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
                    frame.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
                    frame.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
                    */
                    //DisplayUtilities.display(frame);



                }

            }

        });

    }

}
