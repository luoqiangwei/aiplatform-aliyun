package cn.oveay.aiplatform.service;

import cn.oveay.aiplatform.basebean.User;
import cn.oveay.aiplatform.dao.UserDao;
import cn.oveay.aiplatform.utils.encryption.SHA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author OVEA(Qiangwei Luo)
 * created on : 2020/5/20 21:03
 * 文件说明：
 */
@Service
public class LoginService {
    @Autowired
    private UserDao userDao;

    public User login(String phone, String password) {
        User user = userDao.findByPhone(phone);
        if (user.getPassword().equals(SHA.SHA512Encoding(password))) {
            return user;
        } else {
            return null;
        }
    }
}
