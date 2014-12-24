/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.tool;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Joiner;
import com.google.api.client.util.Key;
import com.google.api.client.util.Lists;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.crudefox.mymon.picturetool.api.Caker;
import jp.crudefox.mymon.picturetool.api.input.PicturesListInput;
import jp.crudefox.mymon.picturetool.api.output.OutputRoot;
import jp.crudefox.mymon.picturetool.api.output.PicturesListOutput;
import jp.crudefox.mymon.picturetool.api.output.common.Picture;
import jp.crudefox.mymon.picturetool.api.output.common.PictureTag;
import jp.crudefox.mymon.picturetool.app.ApiMethod;
import jp.crudefox.mymon.picturetool.app.ApiUrl;
import jp.crudefox.mymon.picturetool.util.HandlerUtil;
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
    
    public boolean executeByFoodId (long food_id, File saveDir, File outEntityFile) {
                        
         
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

                mPublisher.publishCurrentTask("request = " +url.build());

                JsonObject jsonObject = mCaker.executeResponseJson(url);
                PicturesListOutput pictureListResponse = gson.fromJson(jsonObject.get("result"), PicturesListOutput.class);

                Log.d(TAG, "response = " +pictureListResponse.toString() );
                
                pictures.addAll(Arrays.asList(pictureListResponse.pictures));
                since_id = pictureListResponse.next_id;
                counter += pictureListResponse.num;

                mPublisher.publishCurrentTask("response = " +pictureListResponse.toString());
                mPublisher.publishProgress(counter, pictureListResponse.all_num);
                
            } while (since_id != null);

        } catch (IOException e) {
            mPublisher.publishError(e);
            return false;
        }
        
        
        for (int i=0; i<pictures.size(); i++) {
            Picture picture = pictures.get(i);
            mPublisher.publishCurrentTask("" + picture.toString());

            try {
                File outPictureFile = new File(saveDir, ""+picture.id);
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
        
        mPublisher.publishCurrentTask("complete picture download.");
             
        
        try ( PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outEntityFile)))) {
//            Gson gson = new Gson();
//            String str = gson.toJson(pictures);
//            pw.print(str);
//            pw.close();
            
            for (int i=0; i<pictures.size(); i++) {
                Picture picture = pictures.get(i);
                File outPictureFile = new File(saveDir, ""+picture.id);
                List<PictureTag> targetTags = new ArrayList<>();
                
                for (int j=0; j<picture.picture_tags.length; j++) {
                    PictureTag tag = picture.picture_tags[j];
                    if ( tag.food_id!=null && tag.food_id.equals(food_id) ) {
                        targetTags.add(tag);
                    }
                }
                
                pw.append(outPictureFile.getPath());
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
                pw.println();
            }
            
            pw.close();
            
        } catch (Exception ex) {
            mPublisher.publishError(ex);
            return false;
        }        
        
        mPublisher.publishCurrentTask("complete save json file.");
        
        return true;
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
    
}
