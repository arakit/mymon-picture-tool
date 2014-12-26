/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.tool;

import jp.crudefox.mymon.picturetool.util.HandlerUtil;
import jp.crudefox.mymon.picturetool.util.Log;

/**
 *
 * @author chikara
 */
public class ProgressPublisher {
    
     public interface OnProgressListener {
        public void onProgress (int current, int max);
        public void onCurrentTask (String text) ;
        public void onError (String text) ;
    }
     
    private OnProgressListener mOnProgressListener;
    
    public void setOnProgressListener (OnProgressListener listener) {
        mOnProgressListener = listener;
    }
    
    
    public void publishProgress (int current, int max) {
        OnProgressListener listener = mOnProgressListener;
        if (listener!=null) {
            HandlerUtil.post(() -> {
                listener.onProgress(current, max);
            });
        }
    }
    public  void publishCurrentTask (String text) {
        if (text == null) return;
        System.out.println(text);
        OnProgressListener listener = mOnProgressListener;
        if (listener!=null) {
            HandlerUtil.post(() -> {
                listener.onCurrentTask(text);
            });
        }
    }
    public void publishError (String text) {
        if (text == null) return;
        OnProgressListener listener = mOnProgressListener;
        if (listener!=null) {
            HandlerUtil.post(() -> {
                listener.onError(text);
            });
        }        
    }
    
    public void publishError (Throwable throwable) {
        if (throwable == null) return;
        throwable.printStackTrace();
        OnProgressListener listener = mOnProgressListener;
        if (listener!=null) {
            HandlerUtil.post(() -> {
                listener.onError("" + throwable.getClass().getSimpleName() + " : " + throwable.getMessage());
            });
        }
    }
    
}
