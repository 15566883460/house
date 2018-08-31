package com.dlyong.house.web.controller;

import com.dlyong.house.common.model.User;
import com.dlyong.house.common.result.ResultMsg;
import org.apache.commons.lang3.StringUtils;

public class UserHelper {


    public static ResultMsg validate(User account) {
        if(StringUtils.isBlank(account.getEmail())) {
            ResultMsg.errorMsg("Email 有误");
        }
        if(StringUtils.isBlank(account.getPhone())) {
            ResultMsg.errorMsg("Phone 有误");
        }
        if( StringUtils.isBlank(account.getPasswd()) ||StringUtils.isBlank(account.getConfirmpasswd())||
                !account.getConfirmpasswd().equals(account.getPasswd())) {
            ResultMsg.errorMsg("Password 有误");
        }
        if(account.getPasswd().length()<6) {
            ResultMsg.errorMsg("密码大于6位");
        }
        return ResultMsg.successMsg("");
    }

}
