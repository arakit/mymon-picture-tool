package jp.crudefox.mymon.picturetool.api.output;

/**
 * ステータスコード設定
 *
 * @author harakazuhiro
 * @since 2013/09/06 9:31
 */
public enum ResponseStatus {

    OK         (10, "ok."),
    NO_RESULT  (20, "no result."),
    FAILD      (50, "faild."),
    ;

    private ResponseStatus(Integer code, String status) {
        this.code = code;
        this.status = status;
    }

    private Integer code;
    private String status;

    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
