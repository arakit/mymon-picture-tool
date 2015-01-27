/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.app;

import java.util.prefs.Preferences;
import jp.crudefox.mymon.picturetool.MainApplication;

/**
 *
 * @author chikara
 */
public class PreferenceManager {
    
    
    private static Preferences sPreferences;
    
    private static Preferences p(){
        if (sPreferences == null) {
            sPreferences = Preferences.userNodeForPackage(MainApplication.class);
        }
        return sPreferences;
    }
    
    
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_HOST_AND_POST = "host_and_port";
    public static final String KEY_LATEST_URL = "latest_url";    
    

    // access token
    public static void putAccessToken(String value){
        p().put(KEY_ACCESS_TOKEN, value);        
    }
    public static String getAccessToken(){
        return p().get(KEY_ACCESS_TOKEN, "");
    }
    
    // host and port
    public static void putHostAndPort(String value){
        p().put(KEY_HOST_AND_POST, value);
    }
    public static String getHostAndPort(){
        return p().get(KEY_HOST_AND_POST, ApiUrl.DEFAULT_HOST_AND_PORT);
    }
    
    // latest
    public static void putLatestUrl(String value){
        p().put(KEY_LATEST_URL, value);
    }
    public static String getLatestUrl(){
        return p().get(KEY_LATEST_URL, "");
    }    
    
}
