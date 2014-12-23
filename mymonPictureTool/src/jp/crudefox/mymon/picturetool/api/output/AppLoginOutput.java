package jp.crudefox.mymon.picturetool.api.output;


import jp.crudefox.mymon.picturetool.api.output.BaseOutputResult;
import jp.crudefox.mymon.picturetool.api.output.common.User;

/**
 * Created by chikara on 2014/09/15.
 */
public class AppLoginOutput extends BaseOutputResult {


    public String access_token;
    public User user;


    @Override
    public String toString() {
        return "AppLoginResult{" +
                "access_token='" + access_token + '\'' +
                ", user=" + user +
                '}';
    }
}
