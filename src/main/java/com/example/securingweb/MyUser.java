package com.example.securingweb;

import java.io.Serializable;

public class MyUser implements Serializable{
    public String password;
    public String username;
    public String question;
    public String answer;
    public MyUser(String username,String password,String question,String answer){
        this.answer=answer;
        this.username=username;
        this.password=password;
        this.question=question;
    }
}
