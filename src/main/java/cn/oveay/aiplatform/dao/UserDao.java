package cn.oveay.aiplatform.dao;

import cn.oveay.aiplatform.basebean.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserDao {
    @Insert("insert into user values(#{id}, #{nickname}, #{phone}, #{password}, #{gender}, #{avatar}, #{createDate}, #{realName}, #{idCard}, #{isAdmin}, #{isEffective})")
    void insert(User user);

    @Update("update user set nickname=#{nickname},password=#{password},gender=#{gender},avatar=#{avatar},realName=#{realName},idCard=#{idCard} where id=#{id}")
    void update(User user);

    @Delete("update user set isEffective=0 where id=#{id}")
    void delete(String id);

    @Select("select * from user")
    List<User> findAll();

    @Select("select * from user where isEffective=1")
    List<User> findAllEff();

    @Select("select * from user where id=#{id}")
    User findById(String id);

    @Select("select * from user where phone=#{phone}")
    User findByPhone(String phone);

    @Update("update user set isEffective=#{state} where id=#{id}")
    void updateState(String id, boolean state);
}
