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
import java.util.HashMap;
import jp.crudefox.mymon.picturetool.api.Caker;
import jp.crudefox.mymon.picturetool.api.input.FoodListInput;
import jp.crudefox.mymon.picturetool.api.output.FoodsListOutput;
import jp.crudefox.mymon.picturetool.api.output.common.Food;
import jp.crudefox.mymon.picturetool.app.ApiMethod;
import jp.crudefox.mymon.picturetool.app.ApiUrl;

/**
 * 
 */
public class ApiExecutor {
    
    public static String TAG = "ApiExecutor";

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

    
    private final HashMap<Long, Food> mFoodsCache = new HashMap<>(); 


    public ApiExecutor() {
        mCaker = new Caker();
    }
    
    

    public Food[] executeRequestFoodList () {
        try {
            Gson gson = new Gson();
            String baseUrl = new ApiUrl(mHostAndUrl, ApiMethod.foods_list).url();
            GenericUrl apiUrl = new GenericUrl(baseUrl);
            FoodListInput in = new FoodListInput();
            in.access_token = mAccessToken;

            Caker.setupUrlFromBean(apiUrl, in);
            JsonObject jsonObject = mCaker.executeResponseJson(apiUrl);
            FoodsListOutput listResponse = gson.fromJson(jsonObject.get("result"), FoodsListOutput.class);
            for (Food food : listResponse.foods) {
                mFoodsCache.put(food.food_id, food);
            }
            return listResponse.foods;
        } catch (Exception ex) {
            publishError(ex);
            return null;
        }
    }

    public Food getCacheFood (long foodId) {
        return mFoodsCache.get(foodId);
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
