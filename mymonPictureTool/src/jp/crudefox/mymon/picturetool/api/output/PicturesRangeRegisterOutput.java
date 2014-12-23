package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.common.Picture;
import jp.crudefox.mymon.picturetool.api.output.BaseOutputResult;

/**
 * Created by chikara on 2014/09/15.
 */
public class PicturesRangeRegisterOutput extends BaseOutputResult {

    public Picture picture;


    @Override
    public String toString() {
        return "PicturesPostResult{" +
                "picture=" + picture +
                '}';
    }
}
