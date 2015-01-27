/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.tool;

import com.google.api.client.http.GenericUrl;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import jp.crudefox.mymon.picturetool.api.Caker;
import jp.crudefox.mymon.picturetool.api.input.PicturesListInput;
import jp.crudefox.mymon.picturetool.api.output.PicturesListOutput;
import jp.crudefox.mymon.picturetool.api.output.common.Picture;
import jp.crudefox.mymon.picturetool.api.output.common.PictureTag;
import jp.crudefox.mymon.picturetool.app.ApiMethod;
import jp.crudefox.mymon.picturetool.app.ApiUrl;
import jp.crudefox.mymon.picturetool.util.Log;

/**
 *
 * @author chikara
 */
public class Downloader {
    
    public static final String TAG = "Downloader";
    
   
        
    private String mHostAndUrl = ApiUrl.DEFAULT_HOST_AND_PORT;
    private String mAccessToken;
    
    private Caker mCaker;
    
    private final ProgressPublisher mPublisher = new ProgressPublisher();
    
    public ProgressPublisher getPublisher () {
        return mPublisher;
    }
    
    public void setHostAndPort (String hostAndPort) {
        mHostAndUrl = hostAndPort;
    }
    
    public void setAccessToken (String accessToken) {
        mAccessToken = accessToken;
    }
    
    public Downloader () {
        mCaker = new Caker();
    }
    
    public boolean executeByFoodId (long food_id, File saveDir, File outEntityFile, boolean isOutRect) {
                        
         
         final List<Picture> pictures = new ArrayList<>();
 
        try {
            Gson gson = new Gson();
            String baseUrl = new ApiUrl(mHostAndUrl, ApiMethod.pictures_list).url();
            Long since_id = null;
            int counter = 0;
            
            do {
                GenericUrl url = new GenericUrl(baseUrl);

                PicturesListInput in = new PicturesListInput();
                in.access_token = mAccessToken;
                in.food_id = food_id;
                in.since_id = since_id;

                Caker.setupUrlFromBean(url, in);

                publish("request = " +url.build());

                JsonObject jsonObject = mCaker.executeResponseJson(url);
                PicturesListOutput pictureListResponse = gson.fromJson(jsonObject.get("result"), PicturesListOutput.class);

                Log.d(TAG, "response = " +pictureListResponse.toString() );
                
                pictures.addAll(Arrays.asList(pictureListResponse.pictures));
                since_id = pictureListResponse.next_id;
                counter += pictureListResponse.num;

                publish("response = " +pictureListResponse.toString());
                publishProgress(counter, pictureListResponse.all_num);
                
            } while (since_id != null);

        } catch (IOException e) {
            publishError(e);
            return false;
        }
        
        
        for (int i=0; i<pictures.size(); i++) {
            Picture picture = pictures.get(i);
            publish("" + picture.toString());

            try {
                File outPictureFile = pictureFile(saveDir, picture);
                if ( outPictureFile.exists() ) {
                    // do nothing.
                } else {
                    InputStream is = mCaker.executeResponseInputStream( new GenericUrl(picture.original_image_url) );
                    saveFileFromStream(outPictureFile, is);
                }
            } catch (Exception ex) {
                mPublisher.publishError(ex);
            }
            
            mPublisher.publishProgress(i+1, pictures.size());
        }
        
        publish("complete picture download.");
             
        
        try ( PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outEntityFile)))) {
//            Gson gson = new Gson();
//            String str = gson.toJson(pictures);
//            pw.print(str);
//            pw.close();
            
            Path relativeParent = Paths.get( outEntityFile.getParentFile().getAbsolutePath() );
            for (int i=0; i<pictures.size(); i++) {
                Picture picture = pictures.get(i);
                File outPictureFile = pictureFile(saveDir, picture);
                Path relativePictureFilePath = relativeParent.relativize(Paths.get(outPictureFile.getAbsolutePath()));
                
                publish("" + outPictureFile);
                publishProgress(i, pictures.size());
                
                try {
                    BufferedImage image = ImageIO.read(outPictureFile);
                    image.getHeight();
                } catch (Exception ex) {
                    publishError(ex);
                    continue;
                }                
                
                List<PictureTag> targetTags = new ArrayList<>();
                
                for (int j=0; j<picture.picture_tags.length; j++) {
                    PictureTag tag = picture.picture_tags[j];
                    if ( tag.food_id!=null && tag.food_id.equals(food_id) ) {
                        targetTags.add(tag);
                    }
                }
                
                pw.append(relativePictureFilePath.toString());
                
                if (isOutRect) {
                    pw.append(" ");
                    pw.append(String.valueOf(targetTags.size()));

                    for (int j=0; j<targetTags.size(); j++) {

                        PictureTag tag = targetTags.get(j);
                        pw.printf(" %d %d %d %d",
                            tag.range.left, // x
                            tag.range.top, // y
                            tag.range.width() , // width
                            tag.range.height()  // height
                        );
                    }
                }
                pw.println();
            }
            
            publish("completed check imag and write list file.");
            publishProgress(pictures.size(), pictures.size());            
            
            pw.close();
            
        } catch (Exception ex) {
            mPublisher.publishError(ex);
            return false;
        }        
        
        mPublisher.publishCurrentTask("complete save list file.");
        
        return true;
    }
    
    public static File pictureFile (File dir, Picture picture) {
        return new File(dir, ""+picture.id);
    }
    
    private boolean saveFileFromStream (File outFile, InputStream is) {

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile))) {
            int length;
            byte[] buf = new byte[8192];
            while ( (length = is.read(buf, 0, buf.length)) != -1 ) {
                os.write(buf, 0, length);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            mPublisher.publishError(ex.getMessage());
            return false;
        }
        
    }
    
    

     
     
//     public static class JsonObject {
//        @Key("pinpointLocations")
//        public List<PinpointLocations> pinpointLocations;
// 
//        public static class PinpointLocations {
//            @Key public String link;
//            @Key public String name;
//        }
//    }
    
    
    
    
    
        
    public boolean executeCreateNgFileList (File dir, File outFile, boolean ignoreFileExtention) {
        
        if( !dir.isDirectory() ){
            mPublisher.publishError("ディレクトリではありません。");
            return false;
        }

        final Set<String> extensions = new HashSet<>();
        extensions.add("jpg");
        extensions.add("jpeg");
        extensions.add("png");
        //extensions.add("gif");

        try {
//            final Map<String, Rectangle[]> known_files = new HashMap<>();
//            final Set<String> del_files = new HashSet<>();
//
//            //
//            if(outFile.exists()){
//                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outFile)));
//                String line;
//                while ( (line = reader.readLine())!=null ) {
//                    String[] arr = line.split(" ", -1);
//                    File file = new File(arr[0]);
//                    mPublisher.publishCurrentTask("pre " + file.getAbsolutePath());
//                    if(file.isFile()) {
//                        if(arr.length>1) {
//                            int pointer = 1;
//                            int targetNum = Integer.valueOf( arr[pointer++] );
//                            java.util.List<Rectangle> rcList = new ArrayList<>();
//                            for(int i=0;i<targetNum;i++) {
//                                Rectangle rc = new Rectangle(
//                                        Integer.valueOf( arr[pointer++] ),
//                                        Integer.valueOf( arr[pointer++] ),
//                                        Integer.valueOf( arr[pointer++] ),
//                                        Integer.valueOf( arr[pointer++] )
//                                );
//                                rcList.add(rc);
//                            }
//                            known_files.put(file.getPath(), rcList.toArray(new Rectangle[0]));
//                        }
//                    }else{
//                        del_files.add( file.getPath() );
//                    }
//                }
//                reader.close();
//            }

            File[] real_files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (!pathname.isFile()) return false;
                    String name = pathname.getName();
                    if (ignoreFileExtention) return true;
                    int idxPeriod = name.lastIndexOf('.');
                    if (idxPeriod == -1) return false;
                    String extension = name.substring(idxPeriod + 1, name.length());
                    return extensions.contains(extension);
                }
            });
            

            //
            try ( PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile))) ) {
                
                Path relativeParent = Paths.get( outFile.getParentFile().getAbsolutePath() );
                            
                for (int i = 0; i < real_files.length; i++) {
                    File file = real_files[i];
                    //String path = file.getPath();
                    //boolean known = known_files.containsKey(path);   
                    Path relativePictureFilePath = relativeParent.relativize(Paths.get(file.getAbsolutePath()));
                    String relativePath = relativePictureFilePath.toString();
                    
                    publish("file " + relativePath);
                    publishProgress(i, real_files.length);
                    
                    try {
                        BufferedImage image = ImageIO.read(file);
                        if (image.getWidth() < 200 || image.getHeight() < 200) {
                            continue;
                        }

                        writer.print(relativePath);
                        writer.println();

                    } catch (Exception ex) {
                        publishError("error, skip "+relativePath);
                        //ex.printStackTrace(System.err);
                    }

                }
                writer.close();
                
                publish("completed image check and write list file.");
                publishProgress(real_files.length, real_files.length);                
            }

        } catch (IOException e) {
            mPublisher.publishError(e);
            return false;
        }

        mPublisher.publishCurrentTask("DONE.");
        
        return true;
    }
    
    
    
    
    public static boolean mergeFile (List<File> files, File outFile) {
        try ( PrintWriter writer = new PrintWriter( new BufferedOutputStream(new FileOutputStream(outFile)) )) {
            for (File file : files) {
                try( BufferedReader reader = new BufferedReader( new FileReader(file) ) ){
                    String line;
                    while ((line=reader.readLine())!=null) {
                        if (line.length() == 0) {continue;}
                        writer.println(line);
                    }
                    reader.close();
                } 
            }
            writer.close();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "error merge file.", ex);
            return false;
        }
    }
    
    
    private void publish(String text) {
        mPublisher.publishCurrentTask(text);
    }

    private void publishProgress(int now, int max) {
        mPublisher.publishProgress(now, max);
    }

    private void publishError(String text) {
        mPublisher.publishError(text);
    }

    private void publishError(Throwable throwable) {
        mPublisher.publishError(throwable);
    }    
    
}
