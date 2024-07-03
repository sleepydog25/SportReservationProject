package com.example.demo.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import io.springboot.captcha.utils.CaptchaUtil;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaController {
	
	@GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CaptchaUtil.out(request, response);
    }

}
