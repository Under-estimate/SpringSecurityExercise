package com.example.securingweb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Article implements Serializable {
    public String content;
    public String title;
    public long time=System.currentTimeMillis();
    public ArrayList<String> likes=new ArrayList<>();
    public ArrayList<String> dislikes=new ArrayList<>();
    public int views=0;
    public String getTime(){
        GregorianCalendar gc=new GregorianCalendar();
        gc.setTimeInMillis(time);
        return gc.get(Calendar.YEAR)+"-"+(gc.get(Calendar.MONTH)+1)+"-"+gc.get(Calendar.DAY_OF_MONTH);
    }
}
