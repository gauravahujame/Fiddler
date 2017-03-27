package com.gaurav;

import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.opencv.core.CvType.CV_32F;

/**
 * Created by gaurav on 23/3/17.
 */
public class Util {
    private static final int KNN = 2;
    public static MatOfDMatch getGoodMatches(List<MatOfDMatch> matches, float nndrRatio, float threshold) {
        //System.out.println("Calculating good match list...");
        LinkedList<DMatch> goodMatchesList = new LinkedList<>();
        MatOfDMatch goodMatches = new MatOfDMatch();

        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.add(m1);
            }
        }

        if (goodMatchesList.size() >= threshold) {
            System.out.println("Number of good matches in current frame : " + goodMatchesList.size());
            goodMatches.fromList(goodMatchesList);
        }
        return goodMatches;
    }

    public static MatOfKeyPoint detectFeatures(Mat image){
        //int minHessian = 400;
        //System.out.println("Detecting key points...");
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        FeatureDetector surf = FeatureDetector.create(FeatureDetector.SURF);
        surf.detect(image, keypoints);
        if (keypoints.empty()) {
            return null;
        }
        return keypoints;
    }

    public static MatOfKeyPoint computeDescriptors(Mat image, MatOfKeyPoint keypoints){
        //System.out.println("Computing descriptors...");
        MatOfKeyPoint descriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        descriptorExtractor.compute(image,keypoints,descriptors);
        return descriptors;
    }

    public static void drawKeypoints(Mat image, MatOfKeyPoint keypoints, String filename){
        //System.out.println("Drawing key points on image...");
        Scalar newKeypointColor = new Scalar(255, 0, 0);
        Mat outputImage = new Mat(image.rows(), image.cols(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Features2d.drawKeypoints(image, keypoints, outputImage, newKeypointColor, 0);
        Imgcodecs.imwrite("output/"+filename, outputImage);
    }

    public static List<MatOfDMatch> matchDescriptors(Mat descriptors1, Mat descriptors2){
        //System.out.println("Matching original and test images...");
        DescriptorMatcher flannMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        List<MatOfDMatch> matches = new LinkedList<>();
        descriptors1.convertTo(descriptors1, CV_32F);
        descriptors2.convertTo(descriptors2, CV_32F);

        if(descriptors1.empty() || descriptors2.empty())
        {
            return matches;
        }
        flannMatcher.knnMatch(descriptors1, descriptors2, matches, 2);
        return matches;
    }

    public static List<Frame> getVideoFrames(String videoFile){
        VideoCapture camera = new VideoCapture(videoFile);
        List<Frame> frames = new LinkedList<>();

        double fps = camera.get(Videoio.CAP_PROP_FPS);
        double totalFrames = camera.get(Videoio.CAP_PROP_FRAME_COUNT);
        double currentFrame = 0;

            while(currentFrame < totalFrames){
                camera.set(Videoio.CAP_PROP_POS_FRAMES, currentFrame);
                //Mat frame = new Mat();
                Frame frame = new Frame(camera.get(Videoio.CAP_PROP_POS_MSEC),currentFrame);
                Mat rawFrame = new Mat();
                camera.retrieve(rawFrame);
                Imgproc.cvtColor(rawFrame,frame.image,Imgproc.COLOR_BGRA2GRAY);
                if(Core.countNonZero(frame.image) > 1000){                           //Detects blank frames
                    frames.add(frame);
                }
                //Imgcodecs.imwrite("output/frames/frame" + currentFrame + ".png", frame);
                currentFrame = currentFrame + fps; //TODO: Breaks with some values eg. 10.0f
            }
        System.out.println("Retrieved frames from video : " + frames.size() + "     Total frames : " + camera.get(Videoio.CAP_PROP_FRAME_COUNT));
        if (camera.isOpened()){
            camera.release();
        }
        return frames;
    }

    public static String getTimeFromMillis(double milliseconds){
        //long millis = Double.doubleToLongBits(milliseconds);
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        String result = String.format("%d min, %d sec", minutes, seconds);
        return result;
    }
}
