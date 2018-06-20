package com.example.eltimmy.oneway2;

/**
 * Created by eltimmy on 2/21/2018.
 */

public class UserInfo {
    public String Name ;
    public  String phone;

    public UserInfo(){
    }

    public UserInfo(String name, String phone) {
        Name = name;
        this.phone = phone;
    }
    public String  getname()
    {
        return Name;
    }
}
