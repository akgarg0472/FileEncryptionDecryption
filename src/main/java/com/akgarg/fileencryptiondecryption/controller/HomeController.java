package com.akgarg.fileencryptiondecryption.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@CrossOrigin(origins = "*")
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }


    @RequestMapping(value = "/enc", method = RequestMethod.GET)
    public String enc() {
        return "enc";
    }


    @RequestMapping(value = "dec", method = RequestMethod.GET)
    public String dec() {
        return "dec";
    }
}
