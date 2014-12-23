package jp.crudefox.mymon.picturetool.api.input;


/**
 * Created by chikara on 2014/09/15.
 */
public class PicturesSetTagInput extends BaseAuthorizedInput {

    public Long picture_id;
    public Long picture_tag_id;
    public Long food_id;

    public String method;

    public Integer range_left;
    public Integer range_top;
    public Integer range_right;
    public Integer range_bottom;


}
