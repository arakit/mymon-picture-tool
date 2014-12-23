package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.BaseOutputResult;
import jp.crudefox.mymon.picturetool.api.output.common.PictureTag;

import java.util.Arrays;

/**
 * Created by chikara on 2014/09/15.
 */
public class PicturesTagOutput extends BaseOutputResult {

    public PictureTag picture_tag;

    @Override
    public String toString() {
        return "PicturesTagOutput{" +
                "picture_tag=" + picture_tag +
                "} ";
    }
}
