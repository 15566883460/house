package com.dlyong.house.biz.mapper;

import com.dlyong.house.common.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> selectUsers();

    int insert(User account);

    int delete(String value);

    int update(User user);

    List<User> selectUsersByQuery(User user);
}
