package cn.oveay.aiplatform.service;

import cn.oveay.aiplatform.basebean.User;
import cn.oveay.aiplatform.dao.UserDao;
import cn.oveay.aiplatform.utils.autoid.Nanoflake;
import cn.oveay.aiplatform.utils.encryption.SHA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RegisterService {
    @Autowired
    private UserDao userDao;

    public User register(User user) {
        user.setPassword(SHA.SHA512Encoding(user.getPassword()));
        user.setNickname(user.getPhone());
        user.setIsAdmin(true);
        user.setIsEffective(true);
        user.setId(Nanoflake.getNanoflake());
        user.setAvatar("default.jpg");
        user.setCreateDate(new Date());
        userDao.insert(user);
        return user;
    }

    public boolean check(User user) {
        return userDao.findByPhone(user.getPhone()) == null;
    }
}
