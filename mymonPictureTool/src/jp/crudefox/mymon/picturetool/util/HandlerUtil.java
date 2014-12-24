/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.util;

import javafx.application.Platform;

/**
 *
 * @author chikara
 */
public class HandlerUtil {
    
    public static void post(Runnable task) {
        if (task == null) return;
        Platform.runLater(task);
    }
    
    public static void postBackground(Runnable task) {
        if (task == null) return;
        Thread thread = new Thread(task);
        thread.start();
    }
    
}
