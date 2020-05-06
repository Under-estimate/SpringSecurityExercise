package com.example.securingweb;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Controller
public class AccountUtil {

    @PostMapping("/regentry")
    public String registerHandler(@RequestParam(name="username") String username,
                                  @RequestParam(name="password") String password,
                                  @RequestParam(name="confirm") String confirm,
                                  @RequestParam(name="question") String question,
                                  @RequestParam(name="answer") String answer,
                                  @RequestParam(name="id") long captchaId,
                                  @RequestParam(name="captcha") String captcha){
        if(username.length()<1)
            return "redirect:/register?nullname";
        if(Utils.users.containsKey(username))
            return "redirect:/register?duplicate";
        if(!password.equals(confirm))
            return "redirect:/register?confirm";
        if(password.length()<8)
            return "redirect:/register?length";
        if(question.length()<1)
            return "redirect:/register?question";
        if(answer.length()<1)
            return "redirect:/register?answer";
        Utils.CaptchaVerifyState state=Utils.verifyCaptcha(captcha,captchaId);
        if(state==Utils.CaptchaVerifyState.Expired)
            return "redirect:/register?expired";
        if(state==Utils.CaptchaVerifyState.Wrong)
            return "redirect:/register?captcha";
        Utils.putUser(username,password,question,answer);
        return "redirect:/redirect?target=login&tip="+ Utils.encodeChineseForTip("注册成功，正在跳转至登陆界面...");
    }
    @PostMapping("/modpass")
    public String modifyPassword(@RequestParam(name="original") String original,
                                 @RequestParam(name="password") String password){
        MyUser target= Utils.users.get(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!new BCryptPasswordEncoder().matches(original,target.password))
            return "redirect:/change?wrongpass";
        if(original.equals(password))
            return "redirect:/change?equal";
        if(password.length()<8)
            return "redirect:/change?length";
        Utils.manager.changePassword(target.password,new BCryptPasswordEncoder().encode(password));
        target.password=new BCryptPasswordEncoder().encode(password);
        Utils.users.put(target.username,target);
        Utils.saveUsers();
        return "redirect:/redirect?target=secured&tip="+Utils.encodeChineseForTip("修改密码成功，正在跳转");
    }
    @PostMapping("/getquest")
    public String getQuestion(@RequestParam(name="username") String username){
        if(!Utils.users.containsKey(username))
            return "redirect:/find?inexistent";
        MyUser target=Utils.users.get(username);
        return "redirect:/find2?username="+Utils.encodeChineseForTip(target.username)+"&question="+Utils.encodeChineseForTip(target.question);
    }
    @PostMapping("/resetpass")
    public String resetPass(@RequestParam(name="username") String username,
                            @RequestParam(name="answer") String answer,
                            @RequestParam(name="password") String password,
                            @RequestParam(name="confirm") String confirm){
        if(!Utils.users.containsKey(username))
            return "redirect:/redirect?target=find&tip="+Utils.encodeChineseForTip("参数错误，请重试。正在跳转...");
        MyUser target=Utils.users.get(username);
        if(!new BCryptPasswordEncoder().matches(answer,target.answer))
            return "redirect:/find2?wrong&username="+Utils.encodeChineseForTip(target.username)+"&question="+Utils.encodeChineseForTip(target.question);
        if(!password.equals(confirm))
            return "redirect:/find2?confirm&username="+Utils.encodeChineseForTip(target.username)+"&question="+Utils.encodeChineseForTip(target.question);
        if(password.length()<8)
            return "redirect:/find2?length&username="+Utils.encodeChineseForTip(target.username)+"&question="+Utils.encodeChineseForTip(target.question);
        if(new BCryptPasswordEncoder().matches(password,target.password))
            return "redirect:/find2?equal&username="+Utils.encodeChineseForTip(target.username)+"&question="+Utils.encodeChineseForTip(target.question);
        Utils.manager.updatePassword(Utils.manager.loadUserByUsername(username),new BCryptPasswordEncoder().encode(password));
        target.password=new BCryptPasswordEncoder().encode(password);
        Utils.users.put(target.username,target);
        Utils.saveUsers();
        return "redirect:/redirect?target=login&tip="+Utils.encodeChineseForTip("重置密码成功，正在跳转至登陆界面...");
    }
}