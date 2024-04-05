package com.tech.haven.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/gzip")
public class GZIPController {

    @GetMapping(path = "/")
    public String gzipDemo() {
        String str = "";
        for (int i = 1; i <= 100000; i++) {
            str += "welcome : " + i;
        }
        return str;
    }

}
