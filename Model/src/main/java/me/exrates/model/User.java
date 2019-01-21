package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private String nickname;
    private String email;
    private String phone;
    private UserStatus status = UserStatus.REGISTERED;
    private UserStatus userStatus = UserStatus.REGISTERED;
    private String password;
    private String finpassword;
    private Date regdate;
    private String ip;
    private String confirmPassword;
    private String confirmFinPassword;
    private boolean readRules;
    private UserRole role = UserRole.USER;
    private String parentEmail;
    private List<UserFile> userFiles = Collections.emptyList();
}