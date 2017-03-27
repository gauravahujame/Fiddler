package com.gaurav;

import org.opencv.core.Mat;

/**
 * Created by gaurav on 27/3/17.
 */
public class Video {
    String fileName;
    String videoName;
    Mat bestMatch;
    double bestMatchPosition;

    public Video(String fileName, String videoName) {
        this.fileName = fileName;
        this.videoName = videoName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}

