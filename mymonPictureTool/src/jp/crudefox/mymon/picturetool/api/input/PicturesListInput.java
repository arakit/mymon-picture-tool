package jp.crudefox.mymon.picturetool.api.input;

/**
 * Created by chikara on 2014/09/15.
 */
public class PicturesListInput extends BaseAuthorizedInput {

    public Long since_id;
    public Integer limit;


    // new,
    public String type;

    // search by user.
    public Long user_id;
    // search by food.
    public Long food_id;


}
