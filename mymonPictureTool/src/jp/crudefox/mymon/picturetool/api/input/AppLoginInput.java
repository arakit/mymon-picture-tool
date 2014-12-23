package jp.crudefox.mymon.picturetool.api.input;



/**
 * Created by chikara on 2014/09/15.
 */
public class AppLoginInput extends BaseInput {

    public String name;
    public String password;

    @Override
    public String toString() {
        return "AppLoginInput{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
