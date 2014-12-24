/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.api;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import jp.crudefox.mymon.picturetool.util.ReflectionUtils;

/**
 *
 * @author chikara
 */
public class Caker {
    

    private HttpRequestFactory mHttpRequestFactory;
    
    public Caker () {
        final XmlNamespaceDictionary namespace = new XmlNamespaceDictionary();
        namespace.set("", "");        

        ApacheHttpTransport transport = new ApacheHttpTransport();
         HttpRequestFactory factory = transport
                .createRequestFactory((HttpRequest request) -> {
                    request.setConnectTimeout(0);
                    request.setReadTimeout(0);
                    //request.setParser(new JacksonFactory().createJsonObjectParser());
        });
         
         mHttpRequestFactory = factory;        
    }
    
    
    public HttpResponse execute (GenericUrl url) throws IOException {
        HttpRequest request = mHttpRequestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        return httpResponse;
    }
    
    public InputStream executeResponseInputStream (GenericUrl url) throws IOException {
        HttpResponse httpResponse = execute(url);
        return httpResponse.getContent();
    }

    public String executeResponseString (GenericUrl url) throws IOException {
        HttpResponse httpResponse = execute(url);
        String strResponse = httpResponse.parseAsString();
        return strResponse;
    }
    
    public JsonObject executeResponseJson (GenericUrl url) throws IOException {
        String strResponse = executeResponseString(url);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(strResponse).getAsJsonObject();
        return jsonObject;
    }
    

    
    public static void setupUrlFromBean (GenericUrl url, Object object) {
        Map<Field, Object> fv = ReflectionUtils.getDeclaredFieldValues(object, null, null);
        for ( Map.Entry<Field, Object> e : fv.entrySet() ) {
            String key = e.getKey().getName();
            Object value = e.getValue();
            if (value!=null) {
                url.put(key, value.toString());
            } else {
                url.put(key, null);
            }
        }
    }
    
    
}
