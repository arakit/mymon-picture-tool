/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.app;

import jp.crudefox.mymon.picturetool.util.MethodFactor;

/**
 * Created by chikara on 2014/09/19.
 */
public enum ApiMethod implements MethodFactor {

    app_register(ApiMethod.POST, "/app/register"),
    app_login(ApiMethod.GET, "/app/login"),

    pictures_list(ApiMethod.GET, "/pictures/list"),
    pictures_picture(ApiMethod.GET, "/pictures/picture"),
    pictures_post(ApiMethod.POST, "/pictures/post"),
    pictures_postTag(ApiMethod.POST, "/pictures/tag"),
    pictures_update(ApiMethod.POST, "/pictures/update"),
    pictures_delete(ApiMethod.POST, "/pictures/delete"),

    users_list(ApiMethod.GET, "/users/list"),

    foods_search(ApiMethod.GET, "/foods/search"),
    foods_food(ApiMethod.GET, "/foods/food"),
    foods_put(ApiMethod.POST, "/foods/put"),

    ;

    public static final int POST = 1;
    public static final int GET = 0;
    

    public final int method;
    public final String path;

    private ApiMethod(int method, String path)
    {
        this.method = method;
        this.path = path;
    }

    @Override
    public int method() {
        return method;
    }
    @Override
    public String path() {
        return path ;
    }


}
