package com.dlyong.house.biz.service;

import com.dlyong.house.biz.mapper.UserMapper;
import com.dlyong.house.common.model.User;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 缓存key-emil的关系
     */
    private final Cache<String,String> registerCache = CacheBuilder.newBuilder().maximumSize(100)
            // 缓存有效时间
            .expireAfterAccess(15, TimeUnit.MINUTES).removalListener(
                    new RemovalListener<String, String>() {
                        // 过期进行操作
                        @Override
                        public void onRemoval(RemovalNotification<String, String> removalNotification) {
                            userMapper.delete(removalNotification.getValue());
                        }
                    }).build();

    @Value("${domain.name}")
    private String domainName;


    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;



    /**
     * 1.缓存key-emil的关系
     * 2.借助spring-mail发送邮件
     * 3.借助异步框架进行异步操作
     * @param email
     */
    @Async
    public void registerNotify(String email) {

        String randomKey = RandomStringUtils.randomAlphabetic(10);
        registerCache.put(randomKey,email);
        String url = "http://"+domainName+"/accounts/verify?key="+randomKey;
        sendMail("房产平台激活邮件",url,email);

    }

    /**
     * 发送邮件
     * @param 房产平台激活邮件
     * @param url
     * @param email
     */
    public void sendMail(String 房产平台激活邮件, String url, String email) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(email);
        mailMessage.setText(url);
        mailSender.send(mailMessage);
    }

    /**
     * 激活账号
     * @param key
     * @return
     */
    public Boolean enable(String key) {
        String email = registerCache.getIfPresent(key);
        if(StringUtils.isBlank(email)) {
            return  false;
        }
        User user = new User();
        user.setEmail(email);
        user.setEnable(1);
        userMapper.update(user);
        // 设置过期
        registerCache.getIfPresent(key);
        return true;
    }
}
