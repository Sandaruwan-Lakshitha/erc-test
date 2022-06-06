package com.g7.ercsystem.rest.auth.controller;

import com.g7.ercsystem.utils.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*",maxAge = 3600)
public class TestController {

    @Autowired
    private MailSender sender;

    @GetMapping(value = "/email")
    public ResponseEntity<?> sendEmail(){
        try {
                sender.sendVerificationEmail("Sandaruawn Lakshitha","uimalka.96@gmail.com");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(value = "/test")
    public ResponseEntity<?> cookies(HttpServletRequest request){
        try {
            Cookie name = WebUtils.getCookie(request, "sample");
            return new ResponseEntity<>(name.getValue(),HttpStatus.ACCEPTED);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping(value = "")
    public ResponseEntity<?> test(){
        return new ResponseEntity<>("Hello from server",HttpStatus.ACCEPTED);
    }
    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(){
        return "admin";
    }
    @GetMapping(value = "/applicant")
    @PreAuthorize("hasRole('APPLICANT')")
    public String applicant(){
        return "applicant";
    }
    @GetMapping(value = "/reviewer")
    @PreAuthorize("hasRole('REVIEWER')")
    public String reviewer(){
        return "reviewer";
    }
    @GetMapping(value = "/secretary")
    @PreAuthorize("hasRole('SECRETARY')")
    public String secretary(){
        return "secretary";
    }
    @GetMapping(value = "/clerk")
    @PreAuthorize("hasRole('CLERK')")
    public String clerk(){
        return "clerk";
    }
}
