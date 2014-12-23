package jp.crudefox.mymon.picturetool.api.input;


/**
 * Created by chikara on 2014/09/15.
 */
public abstract class BaseAuthorizedInput extends BaseInput {

    /**
     *
     */
    public String access_token;

    @Override
    public String toString() {
        return "BaseAuthorizedInput{" +
                "access_token='" + access_token + '\'' +
                '}';
    }
}
