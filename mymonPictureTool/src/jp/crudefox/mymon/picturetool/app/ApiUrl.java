/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.app;

import jp.crudefox.mymon.picturetool.util.BaseUrl;





/**
 * Created by chikara on 2014/09/19.
 */
public class ApiUrl extends BaseUrl {

    public static final String SCHEME = "http://";
    public static final String HOST_AND_PORT = "192.168.1.11:9000";
    //public static final String HOST_AND_PORT = "192.168.11.20:9000";
    //public static final String HOST_AND_PORT = "192.168.11.3:9000";
    //public static final String HOST_AND_PORT = "192.168.11.34:9000";
    //public static final String HOST_AND_PORT = "www9108up.sakura.ne.jp:9000";

    public static final String API = "api";
    public static final String VERSION = "v0";

    private ApiMethod mApiMethod;

    public ApiUrl(ApiMethod apiMethod)
    {
        mApiMethod = apiMethod;
    }

    @Override
    public int method() {
        return mApiMethod.method();
    }
    @Override
    public String url() {
        return BaseUrl.combine( SCHEME, HOST_AND_PORT , API, VERSION, mApiMethod.path() );
    }



}

