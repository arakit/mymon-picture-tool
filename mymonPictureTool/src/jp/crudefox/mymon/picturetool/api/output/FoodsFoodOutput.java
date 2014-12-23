package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.common.Food;
import jp.crudefox.mymon.picturetool.api.output.BaseOutputResult;
import jp.crudefox.mymon.picturetool.api.output.common.PictureTag;

/**
 * Created by chikara on 2014/09/15.
 */
public class FoodsFoodOutput extends BaseOutputResult {

    public Food food;


    @Override
    public String toString() {
        return "FoodsFoodOutput{" +
                "food=" + food +
                "} " + super.toString();
    }
}
