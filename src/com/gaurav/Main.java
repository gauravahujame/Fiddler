package com.gaurav;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Video> videoFiles = new ArrayList<>();

        String imageFile = "images/test_coldplay.png";

        videoFiles.add(new Video("images/test.mp4", "Test Video"));
        videoFiles.add(new Video("images/test_coldplay.mp4", "Up and Up - Coldplay"));
        videoFiles.add(new Video("images/test_sheeran.mp4", "Shape of You - Ed Sheeran"));

        List<Frame> bestFrames = new LinkedList<>();

        for (Video video:videoFiles) {
            Fiddler fiddler = new Fiddler(video.fileName,imageFile);
            Frame bestMatchedFrame = fiddler.execute();
            if(bestMatchedFrame != null){
                bestFrames.add(bestMatchedFrame);
                bestMatchedFrame.video = video;
            }
        }

        if (!bestFrames.isEmpty()){
            Collections.sort(bestFrames, Collections.reverseOrder());
            System.out.println(String.format("\n\n\nRESULT :\nYou were watching : %s\nPosition : %s\nMatches found : %d", bestFrames.get(0).video.videoName , bestFrames.get(0).getPosition(), bestFrames.get(0).matches));
        }
        else
        {
            System.out.println("Sorry we could not find a match in any video. Try tweaking the threshold.");
        }
    }
}
