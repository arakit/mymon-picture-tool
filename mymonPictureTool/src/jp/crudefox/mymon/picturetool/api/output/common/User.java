package jp.crudefox.mymon.picturetool.api.output.common;


/**
 * Created by chikara on 2014/09/17.
 */
public class User {

    public Long id;
    public String name;
    public String full_name;


    public User parseDummy(long id){
        return parseDummy(id, "aaaa", "あああああ");
    }
    public User parseDummy(long id, String user_name, String full_name){
        this.id = id;
        this.name = user_name;
        this.full_name = full_name;
        return this;
    }



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", full_name='" + full_name + '\'' +
                '}';
    }
}
