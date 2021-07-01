package com.google;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A class used to represent a video. */
class Video {

  private final String title;
  private final String videoId;
  private final List<String> tags;
  private boolean flag;
  //video rating from 1-5
  private List<Integer> scores;
  private String flagReason;
  private final String URL;


  Video(String title, String videoId, List<String> tags, String url) {
    this.title = title;
    this.videoId = videoId;
    this.tags = Collections.unmodifiableList(tags);
    this.flag = false;
    this.flagReason = "Not supplied";
    this.scores = new ArrayList<>();
    this.URL = url;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  public void setFlagReason(String flagReason) {
    this.flagReason = flagReason;
  }

  /** add a new score **/
  public void addScore(int score) { this.scores.add(score); }

  /** Checks if the video is flagged **/
  public boolean isFlag() {
    return flag;
  }

  /** Returns the reason for this video being flagged **/
  public String getFlagReason() {
    return flagReason;
  }

  public String getURL() {
    return URL;
  }

  /** Returns the title of the video. */
  String getTitle() { return title;}

  /** Returns the video id of the video. */
  String getVideoId() { return videoId; }

  /** Returns a readonly collection of the tags of the video. */
  List<String> getTags() {
    return tags;
  }

  public float averageScore(){
    Object[] scores = this.scores.toArray();
    int size = scores.length;
    if (size == 0){
      return 0;
    }
    int sum = 0;
    for (Object score: scores){
      sum += (int)score;
    }
    return (float)sum/size;
  }

}
