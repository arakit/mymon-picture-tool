package jp.crudefox.mymon.picturetool.api.input;


/**
 * Created by chikara on 2014/09/15.
 */
public class AppRegisterInput extends BaseInput {

    public String name;
    public String full_name;
    public String password;
    public String gender;

    @Override
    public String toString() {
        return "AppRegisterInput{" +
                "name='" + name + '\'' +
                ", full_name='" + full_name + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                "} " + super.toString();
    }
}
