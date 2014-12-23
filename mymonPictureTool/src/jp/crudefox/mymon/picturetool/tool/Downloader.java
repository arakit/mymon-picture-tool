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
import java.io.IOException;
import java.util.List;
import jp.crudefox.mymon.picturetool.api.input.PicturesListInput;
import jp.crudefox.mymon.picturetool.api.output.OutputRoot;
import jp.crudefox.mymon.picturetool.api.output.PicturesListOutput;
import jp.crudefox.mymon.picturetool.app.ApiMethod;
import jp.crudefox.mymon.picturetool.app.ApiUrl;
import jp.crudefox.mymon.picturetool.util.Log;

/**
 *
 * @author chikara
 */
public class Downloader {
    
    public static final String TAG = "Downloader";
    
    
    public boolean executeAllDownload () {
        
        test();
        
        
        return true;
    }
    
    
     private void test() {

        final XmlNamespaceDictionary namespace = new XmlNamespaceDictionary();
        namespace.set("", "");        
 
         ApacheHttpTransport transport = new ApacheHttpTransport();
         HttpRequestFactory factory = transport
                .createRequestFactory((HttpRequest request) -> {
                    request.setConnectTimeout(0);
                    request.setReadTimeout(0);
                    request.setParser(new JacksonFactory().createJsonObjectParser());
        });
 
        try {
            //String url = "http://weather.livedoor.com/forecast/webservice/json/v1?city=400040";
            Gson gson = new Gson();
            String baseUrl = new ApiUrl(ApiMethod.pictures_list).url();
            GenericUrl url = new GenericUrl(baseUrl);
            
            PicturesListInput in = new PicturesListInput();            
            in.access_token = "XcrowxWdWhdokCjm";
            
            url.put("access_token", in.access_token);
            
            HttpRequest request = factory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            String strResponse = httpResponse.parseAsString();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(strResponse).getAsJsonObject();
            PicturesListOutput response = gson.fromJson(jsonObject.get("result"), PicturesListOutput.class);
                    
            Log.d(TAG, "response = " +response.toString() );
            
//            JsonObject json = response.parseAs(JsonObject.class);
//            List<JsonObject.PinpointLocations> stations = json.pinpointLocations;
//            for (JsonObject.PinpointLocations s : stations) {
//                List<String> list = Lists.newArrayList(s.link, s.name);
//                System.out.println(Joiner.on(',').join(list));
//            }
        } catch (IOException e) {
            e.printStackTrace();
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
