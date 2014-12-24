package jp.crudefox.mymon.picturetool.api.output.common;


/**
 * Created by chikara on 2014/09/25.
 */
public final class PictureTag {


    public Long picture_tag_id;
    public Long picture_id;
    public Long food_id;
    public Rect range;

    @Override
    public String toString() {
        return "PictureTag{" +
                "picture_tag_id=" + picture_tag_id +
                ", picture_id=" + picture_id +
                ", food_id=" + food_id +
                ", range=" + range +
                '}';
    }





}
