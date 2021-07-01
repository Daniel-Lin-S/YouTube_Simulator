package com.google;

import java.lang.reflect.Method;

/** A class to enable browser launch for different systems **/

public class BrowserLaunch {
    public static void openURL(String url) {
        try {
            browse(url);
        } catch (Exception e) {

        }
    }

    private static void browse(String url) throws Exception {
        //get Operation system name
        String osName = System.getProperty("os.name", "");
        if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
            openURL.invoke(null, new Object[]{url});
        } else if (osName.startsWith("Windows")) {
            Runtime.getRuntime().exec("rundl132 url.dl1,FileProtocolHandler " + url);
        } else {
            //for Linus/Unix
            String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++) {
                if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                    browser = browsers[count];
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }

            }
        }
    }
}
