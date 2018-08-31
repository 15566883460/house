package com.dlyong.house.biz.service;

import com.dlyong.house.biz.mapper.UserMapper;
import com.dlyong.house.common.model.User;
import com.dlyong.house.common.utils.BeanHelper;
import com.dlyong.house.common.utils.HashUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private MailService mailService;

    @Value("${file.prefix}")
    private String prefix;

    public List<User> getUsers() {
        return  userMapper.selectUsers();
    }

    /**
     * 1.插入数据库操作，非激活；密码加盐md5;保存头像到本地
     * 2.生成key,绑定email
     * 3.发送邮件给客户
     * @param account
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addAccount(User account) {
        // 密码加密
        account.setPasswd(HashUtils.encryPassword(account.getPasswd()));
        // 用户头像处理
        List<String> imgList = fileService.getImgPath(Lists.newArrayList(account.getAvatarFile()));
        if(!imgList.isEmpty()) {
            account.setAvator(imgList.get(0));
        }
        // 对象属性为空，默认插入空字符串
        BeanHelper.setDefaultProp(account,User.class);
        // 写入插入时间.
        BeanHelper.onInsert(account);
        // 未激活状态
        account.setEnable(0);
        userMapper.insert(account);
        mailService.registerNotify(account.getEmail());
        return true;
    }


    public Boolean enable(String key) {

        return mailService.enable(key);
    }

    /***s
     * 用户名密码验证
     * @param username
     * @param password
     * @return
     */
    public User auth(String username, String password) {

        User user = new User();
        user.setEmail(username);
        user.setPasswd(HashUtils.encryPassword(password));
        user.setEnable(1);
        List<User> list = getUserByQuery(user);
        if(!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private List<User> getUserByQuery(User user) {

        List<User> list = userMapper.selectUsersByQuery(user);
        list.forEach(u -> {
            u.setAvator(prefix+u.getAvator());
        } );
        return list;
    }


}
