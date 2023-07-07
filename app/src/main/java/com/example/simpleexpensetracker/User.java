package com.example.simpleexpensetracker;

import java.io.Serializable;

public class User implements Serializable {

    String nama, email, telp, user_id;

    public User(String nama, String email, String telp, String user_id){
            this.nama = nama;
            this.email = email;
            this.telp = telp;
            this.user_id = user_id;
    }

    public  String getNama(){
        return nama;
    }
    public String getEmail(){
        return email;
    }
    public  String getTelp(){
        return telp;
    }
    public  String getUserId(){
        return user_id;
    }




}
