package cn.oveay.aiplatform.basebean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class User {
    private String id;
    private String nickname;
    private String phone;
    private String password;
    private String gender;
    private String avatar;
    private Date createDate;
    private String realName;
    private String idCard;
    private Boolean isAdmin;
    private Boolean isEffective;
}
