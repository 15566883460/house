package com.dlyong.house.web.controller;

import com.dlyong.house.biz.service.UserService;
import com.dlyong.house.common.constants.CommonConstants;
import com.dlyong.house.common.model.User;
import com.dlyong.house.common.result.ResultMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册提交：1.注册验证2.发送邮件3.验证失败重定向到注册页面
     * 注册页获取：根据account对象为依据判断是否时注册页获取请求
     * @param account
     * @param modelMap
     * @return
     */
    @RequestMapping("accounts/register")
    public String accountsRegister(User account, ModelMap modelMap) {

        if(account == null || StringUtils.isBlank(account.getName())) {
            return "user/accounts/register";
        }
        // 用户验证
        ResultMsg resultMsg = UserHelper.validate(account);
        if(resultMsg.isSuccess() && userService.addAccount(account)) {
            modelMap.put("email",account.getEmail());
            return "/user/accounts/registerSubmit";

        }else {
            return "redirect:/accounts/register?"+resultMsg.asUrlParams();
        }
    }

    /**
     * 激活账户
     * @param key
     * @return
     */
    @RequestMapping("accounts/verify")
    public String verify (String key) {

        Boolean result = userService.enable(key);
        if(result) {
            return "redirect:/index?"+ResultMsg.successMsg("激活成功");
        }else {
            return "redirect:/accounts/register?"+ResultMsg.errorMsg("激活失败，请确认链接是否过期");
        }

    }

    // -----------------------------------sign in--------------------------
    @RequestMapping("/accounts/signin")
    public String signin (HttpServletRequest request,HttpServletResponse response) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String target = request.getParameter("target");
        if(StringUtils.isBlank(username)&& StringUtils.isBlank(password)){
            request.setAttribute("target",target);
            return "/user/accounts/signin";

        }
        User user =userService.auth(username,password);
        if (user == null) {
            return "redirect:/account/signin?"+"target="+target+"&username"+username+"&"+ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
        }else{
            HttpSession session = request.getSession(true);
            session.setAttribute(CommonConstants.USER_ATTRIBUTE,user);
            session.setAttribute(CommonConstants.PLAIN_USER_ATTRIBUTE,user);
            return StringUtils.isNoneBlank(target)?"redirect:"+target:"redirect::/index";
        }
    }

}
