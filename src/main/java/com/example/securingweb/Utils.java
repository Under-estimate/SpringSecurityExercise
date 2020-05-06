package com.example.securingweb;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Utils {
    public static Map<String,MyUser> users;
    private static Map<Long,Captcha> captchaMem=null;
    public static final File data=new File("users.dat");
    public static InMemoryUserDetailsManager manager;
    public static final String captchaWords="ACEFGHJKLMNPQRTUVWXY34679";
    public static final Color[] captchaColors={new Color(0,0,0),new Color(0,0,100),new Color(100,0,0),new Color(0,100,0)};
    public static void loadUsers(){
        if(!data.exists()){
            users= Collections.synchronizedMap(new HashMap<>());
        }else {
            try {
                ObjectInputStream reader = new ObjectInputStream(new FileInputStream(data));
                users = (Map<String, MyUser>) reader.readObject();
                reader.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                users = Collections.synchronizedMap(new HashMap<>());
            }
        }
        ArrayList<UserDetails> userList=new ArrayList<>();
        Iterator<String> usernameIterator=users.keySet().iterator();
        String tempUsername;
        while(usernameIterator.hasNext()){
            tempUsername=usernameIterator.next();
            userList.add(User.builder().username(tempUsername).password(users.get(tempUsername).password).roles("USER").build());
        }
        manager=new InMemoryUserDetailsManager(userList);
    }
    public static void saveUsers(){
        try{
            ObjectOutputStream writer=new ObjectOutputStream(new FileOutputStream(data));
            writer.writeObject(users);
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void putUser(String username,String password,String question,String answer){
        users.put(username,new MyUser(username,new BCryptPasswordEncoder().encode(password),question,new BCryptPasswordEncoder().encode(answer)));
        manager.createUser(User.builder().username(username).password(new BCryptPasswordEncoder().encode(password)).roles("USER").build());
        saveUsers();
    }
    public static String encodeChineseForTip(String text){
        try {
            return (String)(new ScriptEngineManager().getEngineByName("JavaScript").eval("escape(\""+text+"\");"));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Captcha generateCaptcha(){
        if(captchaMem==null)
            captchaMem=Collections.synchronizedMap(new HashMap<>());
        else
            removeExpiredCaptcha();
        BufferedImage captchaImage=new BufferedImage(100,50,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g=(Graphics2D)captchaImage.getGraphics();
        g.setFont(new Font("Calibri",Font.PLAIN,20));
        g.setColor(Color.white);
        g.fillRect(0,0,100,50);
        String words=generateCaptchaWords();
        for(int i=0;i<10;i++){
            g.setColor(getRandomCaptchaColor());
            g.fillRect((int)(100*Math.random()),(int)(50*Math.random()),2,2);
        }
        for(int i=0;i<4;i++){
            g.setColor(getRandomCaptchaColor());
            g.drawLine((int)(100*Math.random()),(int)(50*Math.random()),(int)(100*Math.random()),(int)(50*Math.random()));
        }
        for(int i=0;i<words.length();i++){
            g.setColor(getRandomCaptchaColor());
            g.drawString(words.substring(i,i+1),(int)(12+i*25+(10*Math.random()-5)),(int)(25+(10*Math.random()-5)));
        }
        Captcha captcha=new Captcha();
        captcha.image=captchaImage;
        captcha.content=words;
        captcha.expireTime=System.currentTimeMillis()+1000*60*5;
        long id=new Random().nextLong();
        captcha.id=id;
        captchaMem.put(id,captcha);
        return captcha;
    }
    private static String generateCaptchaWords(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<4;i++){
            sb.append(captchaWords.charAt((int)(Math.random()*captchaWords.length())));
        }
        return sb.toString();
    }
    private static Color getRandomCaptchaColor(){
        return captchaColors[(int)(Math.random()*captchaColors.length)];
    }
    public static CaptchaVerifyState verifyCaptcha(String input,long id){
        if(!captchaMem.containsKey(id))
            return CaptchaVerifyState.Expired;
        if(captchaMem.get(id).content.equalsIgnoreCase(input)) {
            captchaMem.remove(id);
            return CaptchaVerifyState.Correct;
        } else {
            captchaMem.remove(id);
            return CaptchaVerifyState.Wrong;
        }
    }
    private static void removeExpiredCaptcha(){
        Iterator<Long> idIterator=captchaMem.keySet().iterator();
        ArrayList<Long> expiredId=new ArrayList<>();
        long tempId;
        while(idIterator.hasNext()){
            tempId=idIterator.next();
            if(captchaMem.get(tempId).expireTime<System.currentTimeMillis())
                expiredId.add(tempId);
        }
        idIterator=expiredId.iterator();
        while(idIterator.hasNext())
            captchaMem.remove(idIterator.next());
    }
    public enum CaptchaVerifyState{
        Expired,Wrong,Correct
    }
}
