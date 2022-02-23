package com.fruits.service.controller;

import com.fruits.service.annotations.Permission;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/service")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);


    @GetMapping("/hello")
    @Permission("fruits:banana:edit")
    public String hello() throws IOException, InterruptedException {

        String msg = "${java:vm}";
        log.info("hello,{}",msg);
        log.info("Try${date:YYYY-MM-dd}");


//        System.out.println("执行漏洞代码");
//        String[] commands = {"open", "/System/Applications/Calculator.app"};
//        Process pc = Runtime.getRuntime().exec(commands);
//        pc.waitFor();
//        System.out.println("完成执行漏洞代码");

        return "hello world!";
    }


    @PostMapping("/upload")
    public void upload(HttpServletRequest request, HttpServletResponse response) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");

        System.out.println(file.getSize());



    }

}
