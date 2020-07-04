package com.example.securingweb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

@RestController
public class AutoUpdate {
    @GetMapping("/update")
    public String getNewestVersion(@RequestParam(name="app") String app,
                                   HttpServletRequest request){
        File f=new File(app);
        if(!f.isDirectory())return "Error: No such app.";
        File[] list=f.listFiles();
        if(list==null||list.length<=0)return "Error: No version provided.";
        f=list[0];
        GregorianCalendar gc=new GregorianCalendar();
        String time=(gc.get(Calendar.MONTH)+1)+"."+gc.get(Calendar.DAY_OF_MONTH)+" "+gc.get(Calendar.HOUR_OF_DAY)+":"+gc.get(Calendar.MINUTE)+":"+gc.get(Calendar.SECOND);
        System.out.println("["+time+"]["+request.getHeader("X-Forwarded-For")+"]Inquire:"+app);

        return f.getName();
    }
    @GetMapping(value = "/download",produces="application/octet-stream")
    public byte[] getDownload(@RequestParam(name="app") String app,
                              HttpServletRequest request,
                              HttpServletResponse response){
        File f=new File(app);
        if(!f.isDirectory())return null;
        File[] list=f.listFiles();
        if(list==null||list.length<=0)return null;
        f=list[0];
        try {
            FileInputStream fis = new FileInputStream(f);
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] buf=new byte[1024];
            int len;
            while((len=fis.read(buf))!=-1)
                bos.write(buf,0,len);
            fis.close();
            GregorianCalendar gc=new GregorianCalendar();
            String time=(gc.get(Calendar.MONTH)+1)+"."+gc.get(Calendar.DAY_OF_MONTH)+" "+gc.get(Calendar.HOUR_OF_DAY)+":"+gc.get(Calendar.MINUTE)+":"+gc.get(Calendar.SECOND);
            System.out.println("["+time+"]["+request.getHeader("X-Forwarded-For")+"]Download:"+app);
            response.addHeader("Content-Disposition", "inline;filename=\""+f.getName()+"\"");
            return bos.toByteArray();
        }catch (Exception e){
            return e.toString().getBytes();
        }
    }
}
