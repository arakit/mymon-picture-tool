/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import jp.crudefox.mymon.picturetool.tool.SamplerMaker;

/**
 *
 * @author chikara
 */
public class InputStreamThread extends Thread {
    
    public interface OneLineReader {
        public void onLine (String line);
    }
        

    private BufferedReader br;
    //private PrintStream out;
    private InputStreamThread.OneLineReader listener;

    /** コンストラクター */
    public InputStreamThread(InputStream is, InputStreamThread.OneLineReader listener) {
        br = new BufferedReader(new InputStreamReader(is));
        //this.out = out;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                String line = br.readLine();
                if (line == null) break;
                //out.println(line);
                if (listener!=null) {
                    listener.onLine(line);
                }
            }
        } catch (IOException e) {
            listener.onLine(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                br.close();
            } catch (Exception ex) {
            }
        }
    }    

}
