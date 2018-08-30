package com.dlyong.house.web.controller;

import com.dlyong.house.biz.service.UserService;
import com.dlyong.house.common.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HelloController {

    @Autowired
    private UserService userService;

    @RequestMapping("/hello")
    public String hello(ModelMap modelMap) {
        List<User> users = userService.getUsers();
        User user = users.get(0);
        modelMap.put("user",user);
        return "hello";
    }

    @RequestMapping("/index")
    public String index() {
        return "homepage/index";
    }
}
