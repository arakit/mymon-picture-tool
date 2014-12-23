package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.common.Picture;
import jp.crudefox.mymon.picturetool.api.output.BaseOutputResult;

import java.util.Arrays;

/**
 * Created by chikara on 2014/09/15.
 */
public class PicturesPictureOutput extends BaseOutputResult {

    public Picture picture;

    @Override
    public String toString() {
        return "PicturesPictureOutput{" +
                "picture=" + picture +
                "} " + super.toString();
    }
}
