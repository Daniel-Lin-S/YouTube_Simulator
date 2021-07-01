package com.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class used to parse and execute a user Command.
 */
class CommandParser {

  private final VideoPlayer videoPlayer;
  private String lastCommand;
  private List<String> lastParameters;
  //used to store cleared video list temporarily
  private HashMap<String, List<Video>> specialContainer;

  CommandParser(VideoPlayer videoPlayer) {
    this.videoPlayer = videoPlayer;
  }

  /**
   * Executes the given user command.
   */
  public void executeCommand(List<String> command) {
    if (command.isEmpty()) {
      System.out.println(
          "Please enter a valid command, " +
              "type HELP for a list of available commands.");
      return;
    }

    switch (command.get(0).toUpperCase()) {
      case "NUMBER_OF_VIDEOS":
        this.videoPlayer.numberOfVideos();
        break;
      case "SHOW_ALL_VIDEOS":
        this.videoPlayer.showAllVideos();
        break;
      case "PLAY":
        try {
          this.videoPlayer.playVideo(command.get(1));
          this.lastCommand = "STOP";
          this.lastParameters = new ArrayList<>();
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Please enter PLAY command followed by video_id.");
        }
        break;
      case "PLAY_RANDOM":
        this.videoPlayer.playRandomVideo();
        this.lastCommand = "STOP";
        break;
      case "STOP":
        String id = this.videoPlayer.stopVideo();
        this.lastCommand = "PLAY";
        this.lastParameters = new ArrayList<>();
        this.lastParameters.add(id);
        break;
      case "PAUSE":
        this.videoPlayer.pauseVideo();
        this.lastCommand = "CONTINUE";
        break;
      case "CONTINUE":
        this.videoPlayer.continueVideo();
        this.lastCommand = "PAUSE";
        break;
      case "SHOW_PLAYING":
        this.videoPlayer.showPlaying();
        break;
      case "CREATE_PLAYLIST":
        try {
          this.videoPlayer.createPlaylist(command.get(1));
          this.lastCommand = "DELETE_PLAYLIST";
          this.lastParameters = new ArrayList<>();
          this.lastParameters.add(command.get(1));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(
              "Please enter CREATE_PLAYLIST command followed by a " +
                  "playlist name.");
        }
        break;
      case "ADD_TO_PLAYLIST":
        try {
          this.videoPlayer.addVideoToPlaylist(command.get(1), command.get(2));
          this.lastCommand = "REMOVE_FROM_PLAYLIST";
          this.lastParameters = new ArrayList<>();
          this.lastParameters.add(command.get(1));
          this.lastParameters.add(command.get(2));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(
              "Please enter ADD_TO_PLAYLIST command followed by a "
                  + "playlist name and video_id to add.");
        }
        break;
      case "PLAY_PLAYLIST":
        try {
          this.videoPlayer.playPlaylist(command.get(1));
          this.lastCommand = "CLOSE_PLAYLIST";
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Please enter PLAY_PLAYLIST command followed by playlist name.");
        }
        break;
      case "NEXT":
        this.videoPlayer.next();
        this.lastCommand = "PREVIOUS";
        break;
      case "PREVIOUS":
        this.videoPlayer.previous();
        this.lastCommand = "NEXT";
        break;
      case "SHOW_CURRENT_PLAYLIST":
        this.videoPlayer.showCurrentPlaylist();
        break;
      case "CLOSE_PLAYLIST":
        String name = this.videoPlayer.closePlaylist();
        this.lastCommand = "PLAY_PLAYLIST";
        this.lastParameters = new ArrayList<>();
        this.lastParameters.add(name);
        break;
      case "REMOVE_FROM_PLAYLIST":
        try {
          this.videoPlayer.removeFromPlaylist(command.get(1), command.get(2));
          this.lastCommand = "ADD_TO_PLAYLIST";
          this.lastParameters.add(command.get(1));
          this.lastParameters.add(command.get(2));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(
              "Please enter REMOVE_FROM_PLAYLIST command followed by a "
                  + "playlist name and video_id to remove.");
        }
        break;
      case "CLEAR_PLAYLIST":
        try {
          HashMap<String, List<Video>> map = this.videoPlayer.clearPlaylist(command.get(1));
          this.lastCommand = "RESTORE_PLAYLIST";
          this.specialContainer = map;
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(
              "Please enter CLEAR_PLAYLIST command followed by a "
                  + "playlist name.");
        }
        break;
      case "DELETE_PLAYLIST":
        try {
          String playlistName = this.videoPlayer.deletePlaylist(command.get(1));
          this.lastCommand = "CREATE_PLAYLIST";
          this.lastParameters = new ArrayList<>();
          this.lastParameters.add(playlistName);
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(
              "Please enter DELETE_PLAYLIST command followed by a " +
                  "playlist name.");
        }
        break;
      case "SHOW_PLAYLIST":
        try {
          this.videoPlayer.showPlaylist(command.get(1));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Please enter SHOW_PLAYLIST command followed by a " +
              "playlist name.");
        }
        break;
      case "SHOW_ALL_PLAYLISTS":
        this.videoPlayer.showAllPlaylists();
        break;
      case "SEARCH_VIDEOS":
        try {
          this.videoPlayer.searchVideos(command.get(1));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Please enter SEARCH_VIDEOS command followed by a " +
              "search term.");
        }
        break;
      case "SEARCH_VIDEOS_WITH_TAG":
        try {
          this.videoPlayer.searchVideosWithTag(command.get(1));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(
              "Please enter SEARCH_VIDEOS_WITH_TAG command followed by a " +
                  "video tag.");
        }
        break;
      case "FLAG_VIDEO":
        try {
          this.videoPlayer.flagVideo(command.get(1), command.get(2));
          this.lastCommand = "ALLOW_VIDEO";
          this.lastParameters = new ArrayList<>();
          this.lastParameters.add(command.get(1));
          this.lastParameters.add(command.get(2));
        } catch (ArrayIndexOutOfBoundsException e) {
          try {
            this.videoPlayer.flagVideo(command.get(1));
            this.lastCommand = "ALLOW_VIDEO";
            this.lastParameters = new ArrayList<>();
            this.lastParameters.add(command.get(1));
          } catch (ArrayIndexOutOfBoundsException f) {
            System.out.println("Please enter FLAG_VIDEO command followed by a" +
                "video_id and an optional flag reason.");
          }
        }
        break;
      case "ALLOW_VIDEO":
        try {
          String reason = this.videoPlayer.allowVideo(command.get(1));
          this.lastCommand = "FLAG_VIDEO";
          this.lastParameters = new ArrayList<>();
          this.lastParameters.add(command.get(1));
          this.lastParameters.add(reason);
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println("Please enter ALLOW_VIDEO command followed by a " +
              "video_id.");
        }
        break;
      case "RATE_VIDEO":
        try{
          this.videoPlayer.rateVideo(command.get(1), Integer.parseInt(command.get(2)));
        } catch (ArrayIndexOutOfBoundsException e){
          System.out.println("Please enter RATE_VIDEO followed by a video_id and an integer rating.");
        }
        break;
      case "HELP":
        this.getHelp();
        break;
      case "UNDO":
        this.undo();
        break;
      default:
        System.out.println(
            "Please enter a valid command, type HELP for a list of "
            + "available commands.");
        break;
    }
  }

  /**
   * Displays all available commands to the user.
   */
  private void getHelp() {
    String helpText =
        "Available commands:\n"
            + "Note: Names(video names and playlist names) are not case-sensitive. But for convenience, most videos are accessed using ID. \n"
            + "    NUMBER_OF_VIDEOS - Shows how many videos are in the library.\n"
            + "    SHOW_ALL_VIDEOS - Lists all videos from the library in a format of NAME (ID) [TAGS] RATE.\n"
            + "    PLAY <video_id> - Plays specified video, there may be a warning message when opening the browser, just ignore.\n"
            + "    PLAY_RANDOM - Plays a random video from the library.\n"
            + "    STOP - Stop the current video. Unfortunately, java cannot really close your video on the browser.\n"
            + "    PAUSE - Pause the current video. Unfortunately, java cannot really pause your video on the browser.\n"
            + "    CONTINUE - Resume the current paused video.\n"
            + "    UNDO - undo last command, may not work for every command. \n"
            + "    HELP - Displays help.\n"
            + "    EXIT - Terminates the program execution.\n"
            + "    SHOW_PLAYING - Displays the title, id and paused status of the video that is currently playing (or paused).\n"
            + "    -----------Playlist Operations----------------\n"
            + "    CREATE_PLAYLIST <playlist_name> - Creates a new (empty) playlist with the provided name.\n"
            + "    ADD_TO_PLAYLIST <playlist_name> <video_id> - Adds the requested video to the playlist.\n"
            + "    PLAY_PlAYLIST <playlist_name> - Play a playlist, automatically begins with first video. \n"
            + "    NEXT - Play next video in the current playlist. \n"
            + "    PREVIOUS - Play previous video in the current playlist. \n"
            + "    SHOW_CURRENT_PLAYLIST - Show the name of playlist playing now. \n"
            + "    CLOSE_PLAYLIST - Close the current playlist. \n"
            + "    REMOVE_FROM_PLAYLIST <playlist_name> <video_id> - Removes the specified video from the specified playlist\n"
            + "    CLEAR_PLAYLIST <playlist_name> - Removes all the videos from the playlist.\n"
            + "    DELETE_PLAYLIST <playlist_name> - Deletes the playlist.\n"
            + "    SHOW_PLAYLIST <playlist_name> - List all the videos in this playlist.\n"
            + "    SHOW_ALL_PLAYLISTS - Display all the available playlists.\n"
            + "    -----------Searching operations----------------\n"
            + "    SEARCH_VIDEOS <search_term> - Display all the videos whose titles contain the search_term.\n"
            + "    SEARCH_VIDEOS_WITH_TAG <tag_name> -Display all videos whose tags contains the provided tag.\n"
            + "    --------------Flagging operations-----------------\n"
            + "    FLAG_VIDEO <video_id> <flag_reason> - Mark a video as flagged.\n"
            + "    ALLOW_VIDEO <video_id> - Removes a flag from a video.\n"
            + "    ---------------Other operations--------------------\n"
            + "    RATE_VIDEO <video_id> <score> - Rate a video, with an integer between 1-5. \n";
    System.out.println(helpText);
  }

  /** UNDO last command **/
  private void undo() {
    String errorMessage = "Cannot undo command";
    System.out.println("Undoing...");
    switch (this.lastCommand){
      case "PLAY" :
        try {
          this.videoPlayer.playVideo(lastParameters.get(0));
        } catch (Exception e) {
          System.out.println(errorMessage);
        }
        break;
      case "STOP" :
        this.videoPlayer.stopVideo();
        break;
      case "CONTINUE" :
        this.videoPlayer.continueVideo();
        break;
      case "PAUSE" :
        this.videoPlayer.pauseVideo();
        break;
      case "DELETE_PLAYLIST" :
        this.videoPlayer.deletePlaylist(this.lastParameters.get(0));
        break;
      case "CREATE_PLAYLIST":
        this.videoPlayer.createPlaylist(this.lastParameters.get(0));
      case "REMOVE_FROM_PLAYLIST":
        try {
          this.videoPlayer.removeFromPlaylist(this.lastParameters.get(1), this.lastParameters.get(2));
        } catch (Exception e) {
          System.out.println(errorMessage);
        }
        break;
      case "ADD_TO_PLAYLIST":
        try {
          this.videoPlayer.addVideoToPlaylist(this.lastParameters.get(1), this.lastParameters.get(2));
        } catch (ArrayIndexOutOfBoundsException e) {
          System.out.println(errorMessage);
        }
        break;
      case "PLAY_PLAYLIST":
        try {
          this.videoPlayer.playPlaylist(this.lastParameters.get(0));
        } catch (Exception e){
          System.out.println(errorMessage);
        }
        break;
      case "CLOSE_PLAYLIST":
        this.videoPlayer.closePlaylist();
        break;
      case "NEXT":
        this.videoPlayer.next();
        break;
      case "PREVIOUS":
        this.videoPlayer.previous();
        break;
      case "RESTORE_PLAYLIST":
        if (!this.specialContainer.keySet().isEmpty()){
          String name = this.specialContainer.keySet().iterator().next();
          for (Video video: this.specialContainer.get(name)){
            this.videoPlayer.addVideoToPlaylist(name, video.getVideoId());
          }
        }else {
          System.out.println(errorMessage);
        }
        break;
      case "ALLOW_VIDEO":
        try {
          this.videoPlayer.allowVideo(this.lastParameters.get(0));
        } catch (Exception e) {
          System.out.println(errorMessage);
        }
      case "FLAG_VIDEO":
        try {
          this.videoPlayer.flagVideo(this.lastParameters.get(0), this.lastParameters.get(1));
        } catch (Exception e) {
          System.out.println(errorMessage);
        }
      default :
        System.out.println(errorMessage + ": nothing to undo or command undo still developing.");
        break;
    }
  }
}
