package com.example.securingweb;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;


@RestController
public class BlogManagement {
    public static final String articleTemplate="{\"num\":%d,\"title\":\"%s\",\"time\":\"%s\",\"view\":%d,\"likes\":%d,\"dislikes\":%d}";
    public static final String articleDetailTemplate="{\"title\":\"%s\",\"time\":\"%s\",\"view\":%d,\"likes\":%d,\"dislikes\":%d,\"content\":\"%s\"}";
    public static final String userTemplate="{\"gender\":\"%s\",\"desc\":\"%s\",\"birthday\":\"%s\"}";
    public static final String userBasicTemplate="{\"username\":\"%s\",\"articles\":%d}";

    public static final String err_user_not_exist="{\"state\":\"error\",\"message\":\"指定的用户不存在。\"}";
    public static final String err_article_not_exist="{\"state\":\"error\",\"message\":\"指定的文章不存在。\"}";
    public static final String err_already_liked="{\"state\":\"error\",\"message\":\"你已经赞过了。\"}";
    public static final String err_already_disliked="{\"state\":\"error\",\"message\":\"你已经踩过了。\"}";
    public static final String err_permission_denied="{\"state\":\"error\",\"message\":\"你没有权限。\"}";
    @PostMapping("/getlist")
    public String getArticleList(@RequestParam(name="author") String author){
        if(!Utils.users.containsKey(author))
            return err_user_not_exist;
        MyUser user=Utils.users.get(author);
        StringBuilder sb=new StringBuilder();
        sb.append("{\"state\":\"success\",\"articles\":[");
        for(Article a:user.articles)
            sb.append(String.format(articleTemplate,user.articles.indexOf(a), a.title,a.getTime(),a.views,a.likes.size(),a.dislikes.size())).append(',');
        if(sb.charAt(sb.length()-1)==',')sb.deleteCharAt(sb.length()-1);
        sb.append("]}");
        return sb.toString();
    }
    @PostMapping("/getuser")
    public String getUser(@RequestParam(name="username") String username){
        if(!Utils.users.containsKey(username))
            return err_user_not_exist;
        MyUser user=Utils.users.get(username);
        return "{\"state\":\"success\",\"user\":" +
                String.format(userTemplate, user.info.gender == MyUser.Gender.MALE ? "男" :
                        user.info.gender == MyUser.Gender.FEMALE ? "女" : "未指定", user.info.description, user.info.birthday) +
                "}";
    }

    @GetMapping("/getavatar")
    public byte[] getAvatar(@RequestParam(name="username") String username){
        if(!Utils.users.containsKey(username))
            return "指定的用户不存在".getBytes();
        MyUser user=Utils.users.get(username);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            ImageIO.write(user.info.avatar.bi, "png", bos);
        }catch (Exception e){
            e.printStackTrace();
            return "Internal Server error".getBytes();
        }
        return bos.toByteArray();
    }
    @PostMapping("/getarticle")
    public String getArticle(@RequestParam(name="username") String username,
                             @RequestParam(name="article") int num){
        if(!Utils.users.containsKey(username))
            return err_user_not_exist;
        MyUser user=Utils.users.get(username);
        if(num<0||num>=user.articles.size())
            return err_article_not_exist;
        Article a=user.articles.get(num);
        return "{\"state\":\"success\",\"article\":"+String.format(articleDetailTemplate,a.title,a.getTime(),a.views,a.likes.size(),a.dislikes.size(),a.content)+"}";
    }
    @PostMapping("/getcurrentusername")
    public String getCurrentUsername(){
        MyUser target= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        return target.username;
    }
    @PostMapping("/like")
    public String addLike(@RequestParam(name="target") String target,
                          @RequestParam(name="article") int article){
        MyUser user= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!Utils.users.containsKey(target))
            return err_user_not_exist;
        if(article<0||article>=Utils.users.get(target).articles.size())
            return err_article_not_exist;
        Article a=Utils.users.get(target).articles.get(article);
        if(a.likes.contains(user.username))
            return err_already_liked;
        a.dislikes.remove(user.username);
        a.likes.add(user.username);
        Utils.saveUsers();
        return "{\"state\":\"success\"}";
    }
    @PostMapping("/dislike")
    public String addDislike(@RequestParam(name="target") String target,
                          @RequestParam(name="article") int article){
        MyUser user= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!Utils.users.containsKey(target))
            return err_user_not_exist;
        if(article<0||article>=Utils.users.get(target).articles.size())
            return err_article_not_exist;
        Article a=Utils.users.get(target).articles.get(article);
        if(a.dislikes.contains(user.username))
            return err_already_disliked;
        a.likes.remove(user.username);
        a.dislikes.add(user.username);
        Utils.saveUsers();
        return "{\"state\":\"success\"}";
    }
    @PostMapping("/deletearticle")
    public String deleteArticle(@RequestParam(name="target")String target,
                                @RequestParam(name="article") int article){
        MyUser current = Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!(current.username.equals(target)||current.username.equals("admin")))
            return err_permission_denied;
        if(!Utils.users.containsKey(target))
            return err_user_not_exist;
        if(article<0||article>=Utils.users.get(target).articles.size())
            return err_article_not_exist;
        Utils.users.get(target).articles.remove(article);
        Utils.saveUsers();
        return "{\"state\":\"success\"}";
    }
    @PostMapping("/addview")
    public String addView(@RequestParam(name="author")String author,
                          @RequestParam(name="article")int article){
        MyUser user= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!Utils.users.containsKey(author))
            return err_user_not_exist;
        if(article<0||article>=Utils.users.get(author).articles.size())
            return err_article_not_exist;
        Article a=Utils.users.get(author).articles.get(article);
        if(!user.username.equals(author))
            a.views++;
        Utils.saveUsers();
        return "{\"state\":\"success\"}";
    }
    @PostMapping("/getalluser")
    public String getAllUser(){
        MyUser user= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!user.username.equals("admin"))
            return err_permission_denied;
        StringBuilder sb=new StringBuilder();
        sb.append("{\"state\":\"success\",\"users\":[");
        for(String name:Utils.users.keySet()){
            MyUser u=Utils.users.get(name);
            sb.append(String.format(userBasicTemplate,u.username,u.articles.size())).append(',');
        }
        if(sb.charAt(sb.length()-1)==',')
            sb.deleteCharAt(sb.length()-1);
        sb.append("]}");
        return sb.toString();
    }
    @PostMapping("/deleteuser")
    public String deleteUser(@RequestParam(name="username")String username){
        MyUser user= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!user.username.equals("admin"))
            return err_permission_denied;
        if(!Utils.users.containsKey(username))
            return err_user_not_exist;
        Utils.removeUser(username);
        Utils.saveUsers();
        return "{\"state\":\"success\"}";
    }
}
