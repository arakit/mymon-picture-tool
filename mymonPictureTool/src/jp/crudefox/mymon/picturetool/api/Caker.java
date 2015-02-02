/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.api;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.UrlEncodedParser;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.util.ArrayValueMap;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.Data;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.Types;
import com.google.api.client.util.escape.CharEscapers;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.common.net.MediaType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Pair;
import com.ning.http.client.multipart.StringPart;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.crudefox.mymon.picturetool.util.Log;
import jp.crudefox.mymon.picturetool.util.ReflectionUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.google.api.client.http.HttpContent;
import com.google.api.client.util.Data;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.SslUtils;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author chikara
 */
public class Caker {

    public static final String TAG = "Caker";

    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
    public static final String URL_ENCODE_POST_MEDIA_TYPE
            = new HttpMediaType(UrlEncodedParser.CONTENT_TYPE).setCharsetParameter(DEFAULT_CHARSET).build();

    private final HttpRequestFactory mHttpRequestFactory;

    public Caker() {
        final XmlNamespaceDictionary namespace = new XmlNamespaceDictionary();
        namespace.set("", "");

        ApacheHttpTransport transport = new ApacheHttpTransport();
        
        HttpRequestFactory factory = transport
                .createRequestFactory((HttpRequest request) -> {
                    request.setConnectTimeout(1000 * 10);
                    request.setReadTimeout(0);
                    //request.setParser(new JacksonFactory().createJsonObjectParser());
                });


        mHttpRequestFactory = factory;
    }
    
    
    

    public HttpResponse executeGet(GenericUrl url) throws IOException {
        HttpRequest request = mHttpRequestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        return httpResponse;
    }

    public HttpResponse executePost(GenericUrl url, HttpContent content) throws IOException {
        HttpRequest request = mHttpRequestFactory.buildPostRequest(url, content);
        HttpResponse httpResponse = request.execute();
        return httpResponse;
    }

    public HttpResponse executeUrlEncodedPost(GenericUrl url, List<Pair<String, Object>> list) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writeUrlEncoded(os, DEFAULT_CHARSET, list);

        InputStreamContent content = new InputStreamContent(
                URL_ENCODE_POST_MEDIA_TYPE,
                new ByteArrayInputStream(os.toByteArray()));

        return executePost(url, content);
    }

    public JsonObject executeUrlEncodedPostResponseJson(GenericUrl url, List<Pair<String, Object>> list) throws IOException {
        String strResponse = executeUrlEncodedPost(url, list).parseAsString();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(strResponse).getAsJsonObject();
        return jsonObject;
    }

    public HttpResponse executeMultipartPost(GenericUrl url, List<Pair<String, Object>> list) throws IOException {
        
//        MultipartFormDataContent mp = new MultipartFormDataContent();
//        MultipartContent mp = new MultipartContent().setMediaType(
//                new HttpMediaType("multipart/form-data")
//                .setCharsetParameter(DEFAULT_CHARSET)
//                .setParameter("boundary", "__END_OF_PART__"));
//        ArrayList<Pair<String, Object>> mpList = new ArrayList<>();
//        list.stream().map((e) -> {
//            Pair<
//            return ;
//        } );

//        list.stream().map((e) -> {
//            String name = e.first;
//            Object value = e.second;
//            MultipartContent.Part part;
//            if (value instanceof File) {
//                File fileValue = (File) value;
//                FileContent fileContent = new FileContent(
//                        null, fileValue);
//                part = new MultipartContent.Part(fileContent);
//                part.setHeaders(new HttpHeaders().set(
//                        "Content-Disposition",
//                        String.format("form-data; name=\"%s\"; filename=\"%s\"",
//                                name,
//                                fileValue.getName()))
//                );
//            } else if (value != null) {
//                part = new MultipartContent.Part(
//                        new ByteArrayContent(null, value.toString().getBytes()));
//                part.setHeaders(new HttpHeaders().set(
//                        "Content-Disposition",
//                        String.format("form-data; name=\"%s\"", name)));
//            } else {
//                part = null;
//            }
//            return part;
//        }).filter((part) -> (part != null)).forEach((part) -> {
//            mpList.add(part);
//        });

//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        mp.writeTo(os);
//        Log.d(TAG, "request(POST) -> " + mp.getParts().size());
        return executePost(url, new MultipartFormDataContent(list));
    }

    public InputStream executeResponseInputStream(GenericUrl url) throws IOException {
        HttpResponse httpResponse = executeGet(url);
        return httpResponse.getContent();
    }

    public String executeResponseString(GenericUrl url) throws IOException {
        HttpResponse httpResponse = executeGet(url);
        String strResponse = httpResponse.parseAsString();
        return strResponse;
    }

    public JsonObject executeResponseJson(GenericUrl url) throws IOException {
        String strResponse = executeResponseString(url);
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(strResponse).getAsJsonObject();
        return jsonObject;
    }

    public JsonObject responseJson(HttpResponse httpResponse) throws IOException {
        String strResponse = httpResponse.parseAsString();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(strResponse).getAsJsonObject();
        return jsonObject;
    }

    private static void writeUrlEncoded(OutputStream out, Charset charset, List<Pair<String, Object>> params) throws IOException {

        Writer writer = new BufferedWriter(new OutputStreamWriter(out, charset));
        boolean first = true;
        for (Pair<String, Object> nameValuePair : params) {
            String name = nameValuePair.first;
            Object value = nameValuePair.second;
            if (value != null) {
                appendParam(writer, name, value, first);
                first = false;
            }
        }
        writer.flush();
    }

    private static void appendParam(Writer writer, String name, Object value, boolean first)
            throws IOException {
        // append value
        if (!first) {
            writer.write("&");
        }
        writer.write(CharEscapers.escapeUri(name));
        if (value != null) {
            String stringValue = CharEscapers.escapeUri(value.toString());
            if (stringValue.length() != 0) {
                writer.write("=");
                writer.write(stringValue);
            }
        }

    }

    public static List<Pair<String, Object>> makeNameValueListFromBean(Object object) {
        ArrayList<Pair<String, Object>> list = new ArrayList<>();
        Map<Field, Object> fv = ReflectionUtils.getDeclaredFieldValues(object, null, null);
        fv.entrySet().stream().forEach((e) -> {
            String key = e.getKey().getName();
            Object value = e.getValue();
            if (value != null) {
                list.add(new Pair(key, value));
            } else {
                list.add(new Pair(key, null));
            }
        });
        return list;
    }

    public static void setupUrlFromBean(GenericUrl url, Object object) {
        List<Pair<String, Object>> list = makeNameValueListFromBean(object);
        for (Pair<String, Object> e : list) {
            String key = e.first;
            Object value = e.second;
            url.put(key, value);
        }
    }

    public static class MultipartFormDataContent implements HttpContent {

        private final MultipartEntity impl;
        private final Charset encodingForStrings;

        public MultipartFormDataContent(List<Pair<String, Object>> data, Charset encodingForStrings) {
            this.impl = new MultipartEntity();
            this.encodingForStrings = encodingForStrings;
            setData(data);
        }

        public MultipartFormDataContent(List<Pair<String, Object>> data) {
            this(data, Charsets.UTF_8);
        }


        private void setData(List<Pair<String, Object>> data) {
            for (Pair<String, Object> pair : data) {
                if (pair != null) {
                    String name = pair.first;
                   Object value = pair.second;
                    if (value == null) {
                        
                    } else if (value instanceof File) {
                        impl.addPart(name, new FileBody((File) value));
                    } else if (value instanceof ContentBody) {
                        impl.addPart(name, (ContentBody) value);
                    } else {
                        String stringValue = value.toString();
                        try {
                            impl.addPart(name, new StringBody(stringValue, encodingForStrings));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        @Override
        public String getType() {
            return impl.getContentType().getValue();
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            impl.writeTo(out);
        }

        @Override
        public long getLength() throws IOException {
            return impl.getContentLength();
        }

        //@Override
        public String getEncoding() {
            Header encoding = impl.getContentEncoding();
            return encoding == null ? null : encoding.getValue();
        }

        @Override
        public boolean retrySupported() {
            return impl.isRepeatable();
        }

    }

    
}

  