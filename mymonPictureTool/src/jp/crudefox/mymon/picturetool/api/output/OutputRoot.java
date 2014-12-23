package jp.crudefox.mymon.picturetool.api.output;



import java.util.Map;
import jp.crudefox.mymon.picturetool.util.Log;

/**
 * DESCRIPTION
 *
 * @author chikara
 * @since 2013/08/12 23:25
 */
public class OutputRoot<T extends BaseOutputResult>{

    public Integer code;
    public String status;
    public String message;
    public T result;

    public OutputRoot() {
    }


}
