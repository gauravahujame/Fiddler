package com.gaurav;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaurav on 26/3/17.
 */
public class Fiddler {
    private static final int SEARCH_THRESHOLD = 200;    //150-200 works best for screenshots taken from video,
    private static final int DEBUG_THRESHOLD = 50;

    private String videoFile = "" ;
    private String imageFile = "" ;
    private Frame bestFrame;

    public Fiddler(String videoFile, String imageFile) {
        this.videoFile = videoFile;
        this.imageFile = imageFile;
        if (videoFile.isEmpty() || imageFile.isEmpty()){
            System.out.println("Please supply valid files.");
        }
    }


    // returns best frame
    Frame execute(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        String originalFile = this.imageFile;
        List<Frame> goodMatches = new LinkedList<>();
        //Read images
        Mat originalImage = Imgcodecs.imread(originalFile,Imgcodecs.IMREAD_GRAYSCALE);
        List<Frame> videoFrames = Util.getVideoFrames(this.videoFile);

        if (originalImage.empty() || videoFrames.isEmpty())
        {
            System.out.println("Cannot read files");
            return null;
        }
        MatOfKeyPoint originalKP = Util.detectFeatures(originalImage);
        MatOfKeyPoint originalDescriptors = Util.computeDescriptors(originalImage,originalKP);
        int i=0;
        for (Frame frame : videoFrames) {
            MatOfKeyPoint frameKP = Util.detectFeatures(frame.image);
            MatOfKeyPoint frameDescriptors = Util.computeDescriptors(frame.image,frameKP);
            List<MatOfDMatch> matches = Util.matchDescriptors(originalDescriptors,frameDescriptors);
            MatOfDMatch tempMatches = Util.getGoodMatches(matches, 0.4f, 15);

            //DEBUG
            System.out.println("Current position : " + Util.getTimeFromMillis(frame.position) + "    Current frame : " + frame.currentFrame);
            Mat debugMatches = new Mat();
            Features2d.drawMatches(originalImage, originalKP, frame.image, frameKP, tempMatches, debugMatches);
            if (tempMatches.total() > DEBUG_THRESHOLD){
                Imgcodecs.imwrite("output/debug/"+ (i++) + ".png", debugMatches);
            }
            //DEBUG ENDS

            if(!tempMatches.empty()){
                frame.setMatches(tempMatches.total());
                goodMatches.add(frame);
            }

            if (tempMatches.total()>SEARCH_THRESHOLD){                                //MAIN THRESHOLD FOR BREAKING SEARCH
                break;
            }

        }

        //FINAL RESULT
        if (!goodMatches.isEmpty()){
            Collections.sort(goodMatches, Collections.reverseOrder());
            Frame bestMatch = goodMatches.get(0);    //Gets the best match
            Imgcodecs.imwrite("output/result/best_match.png", bestMatch.image);
            System.out.println(String.format("Total Matching Frames : %d  " +
                    "Maximum matches (%d) found at time : %s", goodMatches.size(), bestMatch.matches, Util.getTimeFromMillis(bestMatch.position)));
            //TODO: matches count is same for all
            bestFrame = bestMatch;
        }
        else
        {
            System.out.println("No match found in current video");
        }
        return bestFrame;
    }
}
