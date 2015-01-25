/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.util;

/**
 *
 * @author chikara
 */
public class Log {
    
  public static final void d(String tag, String message) {
        d(tag, message, null);
    }
    public static final void d(String tag, String message, Throwable throwable) {
        //Logger.debug(tag+": "+message+"");
        System.out.println(tag+": "+message+"");
        if (throwable!=null) throwable.printStackTrace( System.out );
    }

    public static final void i(String tag, String message) {
        i(tag, message, null);
    }
    public static final void i(String tag, String message, Throwable throwable) {
        //Logger.info(tag+": "+message+"");
        System.out.println(tag+": "+message+"");
        if (throwable!=null) throwable.printStackTrace( System.out );
    }

    public static final void w(String tag, String message) {
        w(tag, message, null);
    }
    public static final void w(String tag, String message, Throwable throwable) {
        //Logger.error(tag+": "+message+"");
        System.err.println(tag+": "+message+"");
        if (throwable!=null) throwable.printStackTrace( System.err );
    }

    public static final void e(String tag, String message) {
        e(tag, message, null);
    }
    public static final void e(String tag, String message, Throwable throwable) {
        //Logger.error(tag+": "+message+"");
        System.err.println(tag+": "+message+"");
        if (throwable!=null) throwable.printStackTrace( System.err );
    }
    
}

