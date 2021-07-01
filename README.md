# Youtube Simulator

Go to src/main/java/com/google/Run.java to run the simulator.

You should use keyboard to interact with the system. All commands should be written in bold

Enter HELP to get help with how to start. 

For example, try "SEARCH_VIDEO keyword" to search for a keyword. Try "SHOW_ALL_VIDEOS" to see what videos are already there. 

To edit the library of videos, open src/main/resources/videos.txt. Remember to use the same format! And please try to not include special characters in the video name like "//", "|", "\$" as they may cause problems. And do not enter two videos with the same id. 

Warning: You should not put video into a directory with weird symbols like whitespace, "\$", "\&", etc.

Updates:
1. I managed to actually open URL of videos on either Windows or Mac or Linux. But to do this, you need to add URL for each new video entry. 
2. UNDO command. You can undo command if it is possible. But commands like "show_all_videos" cannot be undone as there is nothing to undo. 
3. You can play a playlist, and use NEXT, PREVIOUS to move within playlist
4. Rating system: you can rate videos. From 1 to 5. \(1, 2, 3, 4, 5\)
5. When showing all playlists, number of videos will also be displayed. 