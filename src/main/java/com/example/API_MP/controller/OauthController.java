package com.example.API_MP.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.API_MP.service.OauthService;


@RestController
@RequestMapping("/oauth")
public class OauthController{
    OauthService oauthService;

    public OauthController(OauthService os){
        this.oauthService = os;
    }

    @GetMapping("/init")
    public String init(@RequestParam String param) {
        return oauthService.UrlAutorizacion();
    }
    @GetMapping("/callback")
    public String callback(@RequestParam String param) {
        return "Llego al callback";
    }
    
}