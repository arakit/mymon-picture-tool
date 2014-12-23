package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.common.Picture;
import jp.crudefox.mymon.picturetool.api.output.BaseOutputResult;

import java.util.Arrays;

/**
 * Created by chikara on 2014/09/15.
 */
public class PicturesListOutput extends BaseOutputResult {

    public Integer num;
    public Integer all_num;
    public Long next_id;

    public Picture[] pictures;


    //public Long[] all_ids;


    @Override
    public String toString() {
        return "PicturesListOutput{" +
                "num=" + num +
                ", all_num=" + all_num +
                ", next_id=" + next_id +
                ", pictures=" + Arrays.toString(pictures) +
                "} " + super.toString();
    }
}


