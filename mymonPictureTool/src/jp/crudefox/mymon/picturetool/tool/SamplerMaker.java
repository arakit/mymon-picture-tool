/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.tool;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 *
 * @author chikara
 */
public class SamplerMaker {

    public enum Mode{
        ok,
        ng
    }
  
    public interface OnProgressListener {
        public void onProgress (int current, int max);
        public void onCurrentTask (String text) ;
    }
    

    
    private OnProgressListener mOnProgressListener;
    
    public void setOnProgressListener (OnProgressListener listener) {
        mOnProgressListener = listener;
    }
    

    
    public boolean execute (Mode mode, File dir, File outFile) {
        
        if( !dir.isDirectory() ){
            System.err.println("ディレクトリではありません。");
            return false;
        }

        final Set<String> extensions = new HashSet<>();
        extensions.add("jpg");
        extensions.add("jpeg");
        extensions.add("png");
        //extensions.add("gif");
        

        try {
            final Map<String, Rectangle[]> known_files = new HashMap<>();
            final Set<String> del_files = new HashSet<>();

            //
            if(outFile.exists()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outFile)));
                String line;
                while ( (line = reader.readLine())!=null ) {
                    String[] arr = line.split(" ", -1);
                    File file = new File(arr[0]);
                    publishCurrentTask("pre " + file.getAbsolutePath());
                    if(file.isFile()) {
                        if(arr.length>1) {
                            int pointer = 1;
                            int targetNum = Integer.valueOf( arr[pointer++] );
                            java.util.List<Rectangle> rcList = new ArrayList<>();
                            for(int i=0;i<targetNum;i++) {
                                Rectangle rc = new Rectangle(
                                        Integer.valueOf( arr[pointer++] ),
                                        Integer.valueOf( arr[pointer++] ),
                                        Integer.valueOf( arr[pointer++] ),
                                        Integer.valueOf( arr[pointer++] )
                                );
                                rcList.add(rc);
                            }
                            known_files.put(file.getPath(), rcList.toArray(new Rectangle[0]));
                        }
                    }else{
                        del_files.add( file.getPath() );
                    }
                }
                reader.close();
            }

            File[] real_files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (!pathname.isFile()) return false;
                    String name = pathname.getName();
                    int idxPeriod = name.lastIndexOf('.');
                    if (idxPeriod == -1) return false;
                    String extension = name.substring(idxPeriod + 1, name.length());
                    return extensions.contains(extension);
                }
            });
            

            //
            {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
                for (int i = 0; i < real_files.length; i++) {
                    File file = real_files[i];
                    String path = file.getPath();
                    boolean known = known_files.containsKey(path);
                    
                    publishProgress(i, real_files.length);
                    publishCurrentTask("file " + path);
                    
                    try {
                        if (mode == Mode.ng) {
                            BufferedImage image = ImageIO.read(file);
                            if (image.getWidth() < 200 || image.getHeight() < 200) {
                                continue;
                            }

                            writer.print(path);
                        } else if (mode == Mode.ok) {

                            BufferedImage image = ImageIO.read(file);
                            if (image.getWidth() < 200 || image.getHeight() < 200) {
                                continue;
                            }

                            int paddingX = (int) (image.getWidth() * 0.10f);
                            int paddingY = (int) (image.getHeight() * 0.10f);

                            writer.append(path);
                            writer.append(' ');
                            writer.printf("%d %d %d %d %d",
                                    1,
                                    paddingX,
                                    paddingY,
                                    image.getWidth() - paddingX * 2,
                                    image.getHeight() - paddingY * 2
                            );

                        }

                        writer.println("");

                    } catch (Exception ex) {
                        System.err.println("error, skip "+path);
                        ex.printStackTrace(System.err);
                    }
                }
                writer.close();
            }

        } catch (FileNotFoundException e) {
            System.err.println("ファイルを開けません.");
            e.printStackTrace(System.err);
            return false;
        } catch (IOException e) {
            System.err.println("ファイルエラー.");
            e.printStackTrace(System.err);
            return false;
        }

        System.out.println("DONE.");       
        
        return true;
    }
    
    
    
    
    private void publishProgress (int current, int max) {
        if (mOnProgressListener!=null) {
            mOnProgressListener.onProgress(current, max);
        }
    }
        private void publishCurrentTask (String text) {
        if (mOnProgressListener!=null) {
            mOnProgressListener.onCurrentTask(text);
        }
    }
    
    

    public static void main(String[] args) {

        if(args.length < 3){
            System.err.println("引数が足りません。(0:mode, 1:dir, 2:outFile)");
            return;
        }

        Mode mode = Mode.valueOf(args[0]);
        File dir = new File( args[1] );
        File outFile = new File( args[2] );  

        SamplerMaker maker = new SamplerMaker();
        maker.execute(mode, dir, outFile);
    }    
    
}
