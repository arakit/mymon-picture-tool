package jp.crudefox.mymon.picturetool.tool;

import com.google.api.client.http.GenericUrl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import jp.crudefox.mymon.picturetool.api.Caker;
import jp.crudefox.mymon.picturetool.api.input.PicturesListInput;
import jp.crudefox.mymon.picturetool.api.input.PicturesPostInput;
import jp.crudefox.mymon.picturetool.api.input.PicturesSetTagInput;
import jp.crudefox.mymon.picturetool.api.input.PicturesUpdateInput;
import jp.crudefox.mymon.picturetool.api.output.PicturesListOutput;
import jp.crudefox.mymon.picturetool.api.output.PicturesPictureOutput;
import jp.crudefox.mymon.picturetool.api.output.PicturesTagOutput;
import jp.crudefox.mymon.picturetool.api.output.common.Picture;
import jp.crudefox.mymon.picturetool.api.output.common.PictureTag;
import jp.crudefox.mymon.picturetool.app.ApiMethod;
import jp.crudefox.mymon.picturetool.app.ApiUrl;
import jp.crudefox.mymon.picturetool.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * User: Mathias Lux, mathias@juggle.at Date: 25.05.12 Time: 12:19
 */
public class RRCroller {
    
    public static String TAG = "RRCroller";

    private final ProgressPublisher mPublisher = new ProgressPublisher();

    private String mHostAndUrl = ApiUrl.DEFAULT_HOST_AND_PORT;
    private String mAccessToken;
    private final Caker mCaker;
    

    public ProgressPublisher getPublisher() {
        return mPublisher;
    }
    
        public void setHostAndPort (String hostAndPort) {
        mHostAndUrl = hostAndPort;
    }
    
    public void setAccessToken (String accessToken) {
        mAccessToken = accessToken;
    }

    public static class CrollItem {

        public String title;
        public String image_url;
        public String recipe_id;
    }

    public RRCroller() {
        mCaker = new Caker();
    }
    
    public String parseNextPage(Document document) {
        try {
            List<Node> pagerNextList = findNodesById(document.getElementsByTagName("a"), "pager_next");
            if (pagerNextList.size() != 1) {
                publish("not found next.");
                return null;
            }
            Node pagerNext = pagerNextList.get(0);
            URI href = new URI( getAttributeValue(pagerNext, "href", null) ) ;
            GenericUrl nextUrl = new GenericUrl(document.getDocumentURI());
            if (!href.isAbsolute()) {
                nextUrl.setRawPath(href.getRawPath());
            } else {
                nextUrl.appendRawPath(href.toString());
            }
            publish("found next "+ nextUrl.build());

            return nextUrl.build();
        } catch (Exception ex) {
            publishError(ex);
            return null;
        }
    }

    public List<CrollItem> parse(Document document) {
        ArrayList<CrollItem> results = new ArrayList<>();
        //Document document = engine.getDocument();

        try {
            List<Node> recipeBox02List = findNodesByClassName(document.getElementsByTagName("*"), "recipeBox02");
            if (recipeBox02List.size() != 1) {
                throw new IllegalArgumentException("not target page.");
            }
            Node recipeBox02 = recipeBox02List.get(0);

            Node ul = findNodesByTagName(recipeBox02.getChildNodes(), "ul", true).get(0);
            List<Node> liList = findNodesByTagName(ul.getChildNodes(), "li", true);
            for (int i = 0; i < liList.size(); ++i) {
                Node li = liList.get(i);
                try {
                    List<Node> allChildren = listAllChildren(li);
                    if (findNodesByTagName(allChildren, "h3", true).isEmpty()) {
                        continue;
                    }                    
                    Node h3 = findNodesByTagName(allChildren, "h3", true).get(0);
                    Node img = findNodesByTagName(allChildren, "img", true).get(0);
                    Node a = findNodesByTagName(allChildren, "a", true).get(0);

                    CrollItem ci = new CrollItem();
                    ci.title = h3.getTextContent();
                    ci.image_url = getAttributeValue(img, "src", null);
                    ci.recipe_id = "" + Long.valueOf( getAttributeValue(a, "href", null).split("/")[2] );

                    if (ci.image_url != null && ci.title != null) {
                        GenericUrl imageUrl = new GenericUrl(ci.image_url);
                        imageUrl.remove("thum");
                        ci.image_url = imageUrl.build();
                        results.add(ci);
                    }

                    publish("title=[" + ci.title + "]; image=[" + ci.image_url + "]; recipe_id=["+ci.recipe_id+"]");

                } catch (Exception ex) {
                    Log.w(TAG, "li", ex);
                }
            }

        } catch (Exception ex) {
            publishError(ex);
            return null;
        }

        publish("result count is " + results.size() + ".");
        return results;
    }
    public boolean execute(List<CrollItem> results, Long foodId) {

        try {            
            if (mHostAndUrl.length()==0 || mAccessToken.length()==0 || foodId==null) {
                throw new IllegalArgumentException();
            }

            String basePicturePostUrl = new ApiUrl(mHostAndUrl, ApiMethod.pictures_post).url();

            //boolean completedOne = false;
            for (int i = 0; i < results.size(); i++) {
                //if (completedOne) return;
                
                CrollItem ci = results.get(i);
                publishPogress(i, results.size());
                publish("start "+ci.title);
                
                InputStream is = mCaker.executeResponseInputStream(new GenericUrl(ci.image_url));
                
                // 画像が保存できない。
                File tmpSaveFile = File.createTempFile("tmp_save_picture", null);
                if (!saveFileFromStream(tmpSaveFile, is)) {
                    continue;
                }
                
                // 既に画像がシステムに存在する
                if (existsPictureAlready(ci)) {
                    publish("skip for exists "+ci.title);                
                    continue;
                }
                
                Picture picture;
                
                // アップロード失敗
                picture = requestPostPicture(ci, tmpSaveFile);
                if ( picture == null ){
                    continue;
                }
                
                // システムタグ更新失敗
                picture = requestUpdatePictureSystemTag(picture, systemTag(ci));
                if ( picture == null ){
                    throw new Exception("failed update picture system tag.");
                }
                
                // タグ更新失敗
                PictureTag pictureTag = requestUpdatePictureFoodTagAuto(picture, foodId);
                if ( pictureTag == null ){
                    throw new Exception("failed update picture tag.");
                }
                
                //completedOne = true;
                publish("completed "+ci.title);

            }
            
            publishPogress(results.size(), results.size());
            return true;

        } catch (Exception ex) {
            publishError(ex);
            return false;
        }

    }
    
    private PictureTag requestUpdatePictureFoodTagAuto (Picture picture, Long foodId) throws IOException {
        Gson gson = new Gson();
        String baseUrl = new ApiUrl(mHostAndUrl, ApiMethod.pictures_postTag).url();
        GenericUrl apiUrl = new GenericUrl(baseUrl);
        PicturesSetTagInput in = new PicturesSetTagInput();
        in.access_token = mAccessToken;
        in.picture_id = picture.id;
        in.food_id = foodId;
        in.method = "new";
        
        int minFrameSize = Math.min(picture.width, picture.height);
        float cropFrameRadius = minFrameSize * 0.5f * 0.9f;
        int cx = picture.width / 2;
        int cy = picture.height / 2;
        in.range_left = (int) ( cx - cropFrameRadius );
        in.range_right = (int) ( cx + cropFrameRadius );
        in.range_top = (int) ( cy - cropFrameRadius );
        in.range_bottom = (int) ( cy + cropFrameRadius );

        JsonObject jsonObject = 
                mCaker.executeUrlEncodedPostResponseJson(
                    apiUrl,
                    Caker.makeNameValueListFromBean(in));
        PicturesTagOutput response = gson.fromJson(jsonObject.get("result"), PicturesTagOutput.class);
        return response.picture_tag;
    }    
    
    private Picture requestUpdatePictureSystemTag (Picture picture, String systemTag) throws IOException {
        Gson gson = new Gson();
        String baseUrl = new ApiUrl(mHostAndUrl, ApiMethod.pictures_update).url();
        GenericUrl pictureUpdateUrl = new GenericUrl(baseUrl);
        PicturesUpdateInput in = new PicturesUpdateInput();
        in.access_token = mAccessToken;
        in.picture_id = picture.id;
        //in.title = picture.title;
        in.system_tag = systemTag;

        JsonObject jsonObject = 
                mCaker.executeUrlEncodedPostResponseJson(
                    pictureUpdateUrl,
                    Caker.makeNameValueListFromBean(in));
        PicturesPictureOutput response = gson.fromJson(jsonObject.get("result"), PicturesPictureOutput.class);
        return response.picture;
    }
    
    private Picture requestPostPicture (CrollItem ci, File file) throws IOException {
        Gson gson = new Gson();
        String baseUrl = new ApiUrl(mHostAndUrl, ApiMethod.pictures_post).url();
        GenericUrl picturePostUrl = new GenericUrl(baseUrl);
        PicturesPostInput in = new PicturesPostInput();
        in.access_token = mAccessToken;
        in.title = ci.title;
        in.picture = file;

        JsonObject jsonObject = mCaker.responseJson(
                mCaker.executeMultipartPost(
                    picturePostUrl,
                    Caker.makeNameValueListFromBean(in)));
        PicturesPictureOutput response = gson.fromJson(jsonObject.get("result"), PicturesPictureOutput.class);
        return response.picture;
    }
    
    
    private static String systemTag (CrollItem ci) {
        return "rakuten_recipe_"+ci.recipe_id;
    }

    private boolean existsPictureAlready (CrollItem ci) throws IOException {
        Gson gson = new Gson();
        String basePictureListUrl = new ApiUrl(mHostAndUrl, ApiMethod.pictures_list).url();
        GenericUrl pictureListUrl = new GenericUrl(basePictureListUrl);
        PicturesListInput in = new PicturesListInput();
        in.access_token = mAccessToken;
        in.system_tag = systemTag(ci);
        Caker.setupUrlFromBean(pictureListUrl, in);
        JsonObject jsonObject = mCaker.executeResponseJson(pictureListUrl);
        PicturesListOutput pictureListResponse = gson.fromJson(jsonObject.get("result"), PicturesListOutput.class);
        publish("already picture count is "+ pictureListResponse.all_num);
        return pictureListResponse.all_num > 0;
    }

    private String getAttributeValue(Node node, String attrName, String def) {
        NamedNodeMap attrs = node.getAttributes();
        Node attrValue = attrs.getNamedItem(attrName);
        if (attrValue != null) {
            String value = attrValue.getNodeValue();
            if (value != null) {
                return value;
            }
        }
        return def;
    }

    private List<Node> listAllChildren(Node parent) {
        ArrayList<Node> list = new ArrayList<>();
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            list.add(node);
            list.addAll(listAllChildren(node));
        }
        return list;
    }

    private List<Node> findNodesByClassName(NodeList nodes, String findClassName) {
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();
            Node attrClass = attrs.getNamedItem("class");
            String className = attrClass != null ? attrClass.getNodeValue() : null;
            if (className != null && className.equals(findClassName)) {
                //Log.d(TAG, "<" + node.getNodeName() + "> class=" + className);
                list.add(node);
            }
        }
        return list;
    }
    
    private List<Node> findNodesById(NodeList nodes, String findId) {
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attrs = node.getAttributes();
            String id = getAttributeValue(node, "id", null);
            if (id != null && id.equals(findId)) {
                list.add(node);
            }
        }
        return list;
    }    

    private List<Node> findNodesByClassName(List<Node> nodes, String findClassName) {
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < nodes.size(); ++i) {
            Node node = nodes.get(i);
            NamedNodeMap attrs = node.getAttributes();
            Node attrClass = attrs.getNamedItem("class");
            String className = attrClass != null ? attrClass.getNodeValue() : null;
            if (className != null && className.equals(findClassName)) {
                //Log.d(TAG, "<" + node.getNodeName() + "> class=" + className);
                list.add(node);
            }
        }
        return list;
    }

    private List<Node> findNodesByTagName(NodeList nodes, String findTagName, boolean ignoreCase) {
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName != null && ((ignoreCase && nodeName.equalsIgnoreCase(findTagName))
                    || (!ignoreCase && nodeName.equals(findTagName)))) {
                //Log.d(TAG, "<" + node.getNodeName() + "> class=" + nodeName);
                list.add(node);
            }
        }
        return list;
    }

    private List<Node> findNodesByTagName(List<Node> nodes, String findTagName, boolean ignoreCase) {
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < nodes.size(); ++i) {
            Node node = nodes.get(i);
            String nodeName = node.getNodeName();
            if (nodeName != null && ((ignoreCase && nodeName.equalsIgnoreCase(findTagName))
                    || (!ignoreCase && nodeName.equals(findTagName)))) {
                //Log.d(TAG, "<" + node.getNodeName() + "> class=" + nodeName);
                list.add(node);
            }
        }
        return list;
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
    
    

    private void publish(String text) {
        mPublisher.publishCurrentTask(text);
    }

    private void publishPogress(int now, int max) {
        mPublisher.publishProgress(now, max);
    }

    private void publishError(String text) {
        mPublisher.publishError(text);
    }

    private void publishError(Throwable throwable) {
        mPublisher.publishError(throwable);
    }

}
