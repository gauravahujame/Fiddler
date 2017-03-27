package com.gaurav;

import org.opencv.core.Mat;

/**
 * Created by gaurav on 25/3/17.
 */
public class Frame implements Comparable<Frame>{
    Mat image = new Mat();
    double position;
    double currentFrame;
    long matches;
    Video video;

    public long getMatches() {
        return matches;
    }

    public void setMatches(long matches) {
        this.matches = matches;
    }


    public Frame(Mat image, double position, double currentFrame) {
        this.image = image;
        this.position = position;
        this.currentFrame = currentFrame;
    }
    public Frame(double position, double currentFrame) {
        this.position = position;
        this.currentFrame = currentFrame;
    }

    public Frame() {

    }

    public double getCurrentFrame() {

        return currentFrame;
    }

    public void setCurrentFrame(double currentFrame) {
        this.currentFrame = currentFrame;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public String getPosition() {
        String pos = Util.getTimeFromMillis(this.position);
        return pos;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public int compareTo(Frame o) {
        if (this.matches < o.matches)
            return -1;
        else if (this.matches > o.matches)
            return 1;
        else
            return 0;
    }
}
