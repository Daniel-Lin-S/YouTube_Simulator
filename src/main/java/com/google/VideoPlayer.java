package com.google;

import org.apache.maven.shared.utils.StringUtils;

import java.util.*;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private Video videoPlaying;
  private String playlistPlaying;
  private int currentVideoNum;
  private boolean paused;
  private HashMap<String, List<Video>> playLists;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.playLists = new HashMap<>();
    this.currentVideoNum = 0;
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public String getVideoInfo(Video video){
    String output = video.getTitle() + " (" + video.getVideoId() + ") [";
    output  = output.concat(StringUtils.join(video.getTags().toArray(), " "));
    float score = video.averageScore();
    if (score == 0){
      output = output.concat("] ");
      return output;
    }
    output = output.concat("] " + video.averageScore());
    return output;
  }

  public List<Video> sortVideos(List<Video> videos){
    Collections.sort(videos, new Comparator<Video>() {
      @Override
      public int compare(Video o1, Video o2) {
        //compare in lexicographical order
        return o1.getTitle().compareTo(o2.getTitle());
      }
    });
    return videos;
  }

  public void showAllVideos() {
    List<Video> videos= videoLibrary.getVideos();
    videos = this.sortVideos(videos);
    System.out.println("Here's a list of all available videos:");
    for(Video video: videos){
      if (video.isFlag()){
        System.out.println(this.getVideoInfo(video) + " - FLAGGED (reason: " + video.getFlagReason() + ")");
      }else {
        System.out.println(this.getVideoInfo(video));
      }
    }
  }

  public void playVideo(String videoId) {
    //what to do when video does not exist
    String errorMessage = "Cannot play video: ";
    if (videoLibrary.getVideo(videoId) == null) {
      System.out.println(errorMessage + "Video does not exist");
      return;
    }
    Video video = videoLibrary.getVideo(videoId);
    if(video.isFlag()){
      System.out.println(errorMessage + "Video is currently flagged (reason: " + video.getFlagReason() + ")");
      return;
    }

    if (this.videoPlaying != null) {
      System.out.println("Stopping video: " + videoPlaying.getTitle());
    }
    this.videoPlaying = video;
    this.paused = false;
    System.out.println("Playing video: " + videoPlaying.getTitle());
    try {
      BrowserLaunch.openURL(video.getURL());
    } catch (Exception e){
      System.out.println("Cannot open URL: either because there is no URL available or cannot find a browser");
    }
  }

  /** Stop a video and return its video_id **/
  public String stopVideo() {
    if(this.videoPlaying == null){
      System.out.println("Cannot stop video: No video is currently playing");
      return "";
    }
    String videoId = videoPlaying.getVideoId();
    System.out.println("Stopping video: " + videoPlaying.getTitle());
    this.videoPlaying = null;
    this.paused = false;
    return videoId;
  }

  public void playRandomVideo() {
    //remove flagged videos
    List<Video> notFlaggedVideos = new ArrayList<>();
    for (Video video: videoLibrary.getVideos()){
      if(!video.isFlag()){
        notFlaggedVideos.add(video);
      }
    }

    int size = notFlaggedVideos.size();
    if (size == 0){
      System.out.println("No videos available");
      return;
    }


    Random random = new Random();
    int randomNum = random.nextInt(size);
    Video video = notFlaggedVideos.get(randomNum);
    this.playVideo(video.getVideoId());
  }

  public void pauseVideo() {
    if(this.videoPlaying == null){
      System.out.println("Cannot pause video: No video is currently playing");
      return;
    }

    if (this.paused) System.out.println("Video already paused: " + videoPlaying.getTitle());
    else{
      System.out.println("Pausing video: " + videoPlaying.getTitle());
      this.paused = true;
    }

  }

  public void continueVideo() {
    if(this.videoPlaying == null){
      System.out.println("Cannot continue video: No video is currently playing");
      return;
    }

    if (this.paused) {
      this.paused = false;
      System.out.println("Continuing video: " + videoPlaying.getTitle());
    }else{
      System.out.println("Cannot continue video: Video is not paused");
    }
  }

  public void showPlaying() {
    if (videoPlaying == null){
      System.out.println("No video is currently playing");
      return;
    }

    String output = "Currently playing: " + this.getVideoInfo(videoPlaying);
    if (paused){
      output = output.concat(" - PAUSED");
    }
    System.out.println(output);
  }



  //------------------------------Playlist Management----------------------------------

  //used to test if a playlist name already exist
  public boolean nameExists(String name){
    name = name.toLowerCase(Locale.ROOT);
    boolean flag = false;
    try{
      //noinspection ResultOfMethodCallIgnored
      playLists.keySet();
    }catch (Exception e){
      return false;
    }
    for (String playlistName : playLists.keySet()) {
      if (playlistName.toLowerCase(Locale.ROOT).equals(name)) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  //ask if the user want to create a new playlist
  public void userCreatePlayListAsk(String name){
    Scanner scanner = new Scanner(System.in);
    System.out.println("Do you want to create a new playlist with name: " + name + "?");
    System.out.println("Type \"yes\" to create, anything else to cancel.");
    String input = scanner.next();
    if (input.toLowerCase(Locale.ROOT).equals("yes")){
      this.createPlaylist(name);
    }
  }

  public void createPlaylist(String playlistName) {
    //test if playlistName exists up to lowercase
    if(nameExists(playlistName)){
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
      return;
    }

    playLists.put(playlistName, new ArrayList<>());
    System.out.println("Successfully created new playlist: " + playlistName);
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    //Basic errors
    String errorMessage = "Cannot add video to " + playlistName + ": ";
    String playlistNameLower = playlistName.toLowerCase(Locale.ROOT);
    String actualName = "";
    boolean flag = false;
    for (String name : playLists.keySet()) {
      if (name.toLowerCase(Locale.ROOT).equals(playlistNameLower)){
        flag = true;
        actualName = name;
      }
    }
    if (!flag){
      System.out.println(errorMessage + "Playlist does not exist");
      userCreatePlayListAsk(playlistName);
      return;
    }
    if (videoLibrary.getVideo(videoId) == null){
      System.out.println(errorMessage + "Video does not exist");
      return;
    }

    //take out the list, add video and put back

    List<Video> videos = playLists.get(actualName);
    Video newVideo = videoLibrary.getVideo(videoId);
    if(newVideo.isFlag()){
      System.out.println(errorMessage + "Video is currently flagged (reason: " + newVideo.getFlagReason() + ")");
      return;
    }
    if (videos.contains(newVideo)){
      System.out.println(errorMessage + "Video already added");
    }else {
      videos.add(newVideo);
      playLists.put(playlistName, videos);
      System.out.println("Added video to " + playlistName + ": " + newVideo.getTitle());
    }
  }

  public void playPlaylist(String playlistName) {
    String errorMessage = "Cannot play playlist: " + playlistName + ": ";
    String playlistNameLower = playlistName.toLowerCase(Locale.ROOT);
    String actualName = "";
    boolean flag = false;
    for (String name : playLists.keySet()) {
      if (name.toLowerCase(Locale.ROOT).equals(playlistNameLower)){
        flag = true;
        actualName = name;
      }
    }
    if (!flag){
      System.out.println(errorMessage + "Playlist does not exist");
      userCreatePlayListAsk(playlistName);
      return;
    }

    List<Video> playList = this.playLists.get(actualName);

    if (playList.size() == 0){
      System.out.println(errorMessage + "Playlist is empty");
    }

    this.playlistPlaying = actualName;
    currentVideoNum = 0;
    this.playVideo(playList.get(currentVideoNum).getVideoId());
  }

  public void next() {
    String errorMessage = "Cannot play next video: ";
    if(playlistPlaying == null){
      System.out.println(errorMessage + "No playlist playing");
      return;
    }
    if(playLists.get(playlistPlaying).size() - 1 == currentVideoNum){
      System.out.println(errorMessage + "Already playing last video in playlist");
      return;
    }

    currentVideoNum++;
    this.playVideo(playLists.get(playlistPlaying).get(currentVideoNum).getVideoId());
  }

  public void previous() {
    String errorMessage = "Cannot play previous video: ";
    if(playlistPlaying == null){
      System.out.println(errorMessage + "No playlist playing");
      return;
    }
    if(currentVideoNum == 0) {
      System.out.println(errorMessage + "Already playing first video in playlist");
      return;
    }

    currentVideoNum--;
    this.playVideo(playLists.get(playlistPlaying).get(currentVideoNum).getVideoId());
  }

  public void showCurrentPlaylist() {
    if(playlistPlaying == null){
      System.out.println("No playlist playing");
      return;
    }

    System.out.println("Current playlist: " + playlistPlaying);
  }

  /** Close current playlist and return the playlist name **/
  public String closePlaylist(){
    if(playlistPlaying == null){
      System.out.println("Cannot close playlist: No playlist playing");
      return "";
    }

    String name = playlistPlaying;
    this.playlistPlaying = null;
    System.out.println("Successfully closed playlist: " + name);
    return name;
  }

  public void showAllPlaylists() {
    if (playLists.size() == 0){
      System.out.println("No playlists exist yet");
      return;
    }

    Set<String> names = playLists.keySet();
    System.out.println("Showing all playlists:");
    for (String name: names){
      int size = playLists.get(name).size();
      if (size == 1 || size == 0){
        System.out.println("  " + name + "(" + size + " video)");
      }else {
        System.out.println("  " + name + "(" + size + " videos)");
      }
    }
  }

  public void showPlaylist(String playlistName) {
    String errorMessage = "Cannot show playlist " + playlistName + ": ";
    String playlistNameLower = playlistName.toLowerCase(Locale.ROOT);
    String actualName = "";
    boolean flag = false;
    for (String name : playLists.keySet()) {
      if (name.toLowerCase(Locale.ROOT).equals(playlistNameLower)){
        flag = true;
        actualName = name;
      }
    }
    if (!flag){
      System.out.println(errorMessage + "Playlist does not exist");
      userCreatePlayListAsk(playlistName);
      return;
    }

    System.out.println("Showing playlist: " + playlistName);
    List<Video> videos = playLists.get(actualName);
    if (videos.size() == 0){
      System.out.println("No videos here yet");
    }else{
      for(Video video: videos){
        if(video.isFlag()){
          System.out.println("  " + this.getVideoInfo(video) + " - FLAGGED (reason: " + video.getFlagReason() + ")");
        }else{
          System.out.println("  " + this.getVideoInfo(video));
        }
      }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    String errorMessage = "Cannot remove video from " + playlistName + ": ";
    String playlistNameLower = playlistName.toLowerCase(Locale.ROOT);
    String actualName = "";
    boolean flag = false;
    for (String name : playLists.keySet()) {
      if (name.toLowerCase(Locale.ROOT).equals(playlistNameLower)){
        flag = true;
        actualName = name;
      }
    }
    if (!flag){
      System.out.println(errorMessage + "Playlist does not exist");
      return;
    }
    if (videoLibrary.getVideo(videoId) == null){
      System.out.println(errorMessage + "Video does not exist");
      return;
    }

    List<Video> videos = playLists.get(actualName);
    Video videoToRemove = videoLibrary.getVideo(videoId);
    if (!videos.contains(videoToRemove)){
      System.out.println(errorMessage + "Video is not in playlist");
    }else{
      videos.remove(videoToRemove);
      playLists.put(actualName, videos);
      System.out.println("Removed video from " + playlistName + ": " + videoToRemove.getTitle());
    }
  }

  /** clear playlist and returns name of playlist and a list of videos in there, return null if failed **/
  public HashMap<String, List<Video>> clearPlaylist(String playlistName) {
    String errorMessage = "Cannot clear playlist " + playlistName + ": ";
    String playlistNameLower = playlistName.toLowerCase(Locale.ROOT);
    String actualName = "";
    boolean flag = false;
    for (String name : playLists.keySet()) {
      if (name.toLowerCase(Locale.ROOT).equals(playlistNameLower)){
        flag = true;
        actualName = name;
      }
    }
    if (!flag){
      System.out.println(errorMessage + "Playlist does not exist");
      return null;
    }

    List<Video> videos = playLists.get(actualName);
    playLists.put(actualName, new ArrayList<>());
    System.out.println("Successfully removed all videos from " + playlistName);
    HashMap<String, List<Video>> map = new HashMap<>();
    map.put(actualName, videos);
    return map;
  }

  /**  Delete playlist and return the name**/
  public String deletePlaylist(String playlistName) {
    String errorMessage = "Cannot delete playlist " + playlistName + ": ";
    String playlistNameLower = playlistName.toLowerCase(Locale.ROOT);
    String actualName = "";
    boolean flag = false;
    for (String name : playLists.keySet()) {
      if (name.toLowerCase(Locale.ROOT).equals(playlistNameLower)){
        flag = true;
        actualName = name;
      }
    }
    if (!flag){
      System.out.println(errorMessage + "Playlist does not exist");
      return "";
    }

    playLists.remove(actualName);
    System.out.println("Deleted playlist: " + playlistName);
    return actualName;
  }


  // -----------Video searching-----------//
  public void searchVideos(String searchTerm) {
    //search and sort
    List<Video> result = new ArrayList<>();
    for (Video video: videoLibrary.getVideos()){
      if (video.isFlag()){
        continue;
      }
      if(video.getTitle().toLowerCase(Locale.ROOT).contains(searchTerm.toLowerCase(Locale.ROOT))){
        result.add(video);
      }
    }
    result = this.sortVideos(result);

    //display results
    if(result.size() == 0){
      System.out.println("No search results for " + searchTerm);
      return;
    }

    Scanner scanner = new Scanner(System.in);
    System.out.println("Here are the results for " + searchTerm + ":");
    for (int i = 0; i < result.size();i++){
      System.out.println("  " + (i+1) + ") " + this.getVideoInfo(result.get(i)));
    }

    //Begin interaction
    System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
    System.out.println("If your answer is not a valid number, we will assume it's a no.");

    String input = scanner.next();
    int index;
    if (!StringUtils.isNumeric(input)){
      return;
    }else{
      index = Integer.parseInt(input) - 1;
    }

    if (index>=0 && index < result.size()){
      Video videoToPlay = result.get(index);
      this.playVideo(videoToPlay.getVideoId());
    }
  }



  public void searchVideosWithTag(String videoTag) {
    List<Video> result = new ArrayList<>();
    for (Video video: videoLibrary.getVideos()){
      boolean flag = false;
      if (video.isFlag()){
        continue;
      }
      for(String tag: video.getTags()){
        if (tag.toLowerCase(Locale.ROOT).equals(videoTag.toLowerCase(Locale.ROOT))){
          flag = true;
          break;
        }
      }

      if(flag){
        result.add(video);
      }
    }
    result = this.sortVideos(result);

    //display results
    if(result.size() == 0){
      System.out.println("No search results for " + videoTag);
      return;
    }

    Scanner scanner = new Scanner(System.in);
    System.out.println("Here are the results for " + videoTag + ":");
    for (int i = 0; i < result.size();i++){
      System.out.println("  " + (i+1) + ") " + this.getVideoInfo(result.get(i)));
    }

    //Begin interaction
    System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
    System.out.println("If your answer is not a valid number, we will assume it's a no.");

    String input = scanner.next();
    int index;
    if (!StringUtils.isNumeric(input)){
      return;
    }else{
      index = Integer.parseInt(input) - 1;
    }

    if (index>=0 && index < result.size()){
      Video videoToPlay = result.get(index);
      this.playVideo(videoToPlay.getVideoId());
    }
  }


  //-----------Video flagging------------------
  public void flagVideo(String videoId) {
    String errorMessage = "Cannot flag video: ";

    if(videoLibrary.getVideo(videoId)==null){
      System.out.println(errorMessage + "Video does not exist");
      return;
    }

    Video video = videoLibrary.getVideo(videoId);
    if(video.isFlag()){
      System.out.println(errorMessage + "Video is already flagged");
      return;
    }

    video.setFlag(true);
    if (this.videoPlaying == video){
      this.stopVideo();
    }
    System.out.println("Successfully flagged video: " + video.getTitle() + " (reason: " + video.getFlagReason() + ")");
  }

  public void flagVideo(String videoId, String reason) {
    String errorMessage = "Cannot flag video: ";

    if(videoLibrary.getVideo(videoId)==null){
      System.out.println(errorMessage + "Video does not exist");
      return;
    }

    Video video = videoLibrary.getVideo(videoId);
    if(video.isFlag()){
      System.out.println(errorMessage + "Video is already flagged");
      return;
    }

    if (this.videoPlaying == video){
      this.stopVideo();
    }
    video.setFlag(true);
    video.setFlagReason(reason);
    System.out.println("Successfully flagged video: " + video.getTitle() + " (reason: " + video.getFlagReason() + ")");
  }

  /** allow video and returns the flag reason**/
  public String allowVideo(String videoId) {
    String errorMessage = "Cannot remove flag from video: ";
    if(videoLibrary.getVideo(videoId) == null){
      System.out.println(errorMessage + "Video does not exist");
      return "";
    }

    Video video = videoLibrary.getVideo(videoId);
    if(!video.isFlag()){
      System.out.println(errorMessage + "Video is not flagged");
      return "";
    }else {
      String reason = video.getFlagReason();
      video.setFlag(false);
      video.setFlagReason("Not supplied");
      System.out.println("Successfully removed flag from video: " + video.getTitle());
      return reason;
    }
  }

  //---------------Rating system---------------
  public void rateVideo(String videoId, int score){
    String errorMessage = "Cannot rate video: ";
    Video video = videoLibrary.getVideo(videoId);
    if (video == null){
      System.out.println(errorMessage + "Video does not exist");
      return;
    }

    if(score < 1 || score > 5){
      System.out.println(errorMessage + "Score should be from 1 to 5, and should be an integer");
      return;
    }

    video.addScore(score);
    System.out.println("Rated video: " + video.getTitle() + ", score: " + score);
  }
}