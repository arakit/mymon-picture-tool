package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.common.Food;

import java.util.Arrays;

/**
 * Created by chikara on 2014/09/15.
 */
public class FoodsListOutput extends BaseOutputResult {

    public Integer all_num;
    public Food[] foods;


    @Override
    public String toString() {
        return "FoodsListOutput{" +
                "all_num=" + all_num +
                ", foods=" + Arrays.toString(foods) +
                "} " + super.toString();
    }
}
