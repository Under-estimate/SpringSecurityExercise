package com.example.securingweb;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class MyUser implements Serializable{
    public String password;
    public String username;
    public String question;
    public String answer;
    public UserInfo info;
    public ArrayList<Article> articles;
    public MyUser(String username,String password,String question,String answer){
        this.answer=answer;
        this.username=username;
        this.password=password;
        this.question=question;
        info=new UserInfo();
        articles=new ArrayList<>();
    }
    static class UserInfo implements Serializable{
        public Gender gender=Gender.UNSPECIFIED;
        public String description="";
        public SerializableImage avatar;
        public String birthday;
        public UserInfo(){
            BufferedImage init=new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
            Graphics g=init.getGraphics();
            g.setColor(Color.black);
            g.fillRect(0,0,100,100);
            avatar=new SerializableImage(init);
        }
    }
    enum Gender{
        MALE,FEMALE,UNSPECIFIED
    }
}
