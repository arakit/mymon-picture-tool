package jp.crudefox.mymon.picturetool.api.output.common;



import java.util.Arrays;
import java.util.List;

/**
 * Created by chikara on 2014/09/17.
 */
public class Picture {

    public Long id;
    public String title;

    public Integer width;
    public Integer height;

    public String image_url;
    public String original_image_url;

    public User user;
    public PictureTag[] picture_tags;





    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", image_url='" + image_url + '\'' +
                ", original_image_url='" + original_image_url + '\'' +
                ", user=" + user +
                ", picture_tags=" + Arrays.toString(picture_tags) +
                '}';
    }
}
