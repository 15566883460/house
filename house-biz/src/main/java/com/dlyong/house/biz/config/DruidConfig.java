package com.dlyong.house.biz.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DruidConfig {

    // 将配置文件与class文件进行绑定
    @ConfigurationProperties(prefix="spring.druid")
    @Bean(initMethod = "init",destroyMethod = "close")
    public DruidDataSource dataSource() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return dataSource;

    }
    // 慢日志
    @Bean
    public Filter statFilter(){

        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(5000);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    // 监控
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {

        return new ServletRegistrationBean(new StatViewServlet(),"/druid/*");


    }
}
