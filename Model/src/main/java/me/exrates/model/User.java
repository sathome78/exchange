package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.serializer.LocalDateDeserializer;
import me.exrates.model.serializer.LocalDateSerializer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter@Setter
public class User implements Serializable {

    private int id;
    private String nickname;
    private String email;
    private String phone;
    @JsonProperty("status")
    private UserStatus userStatus = UserStatus.REGISTERED;
    private String password;
    private String finpassword;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate regdate;
    private String ipaddress;
    private String confirmPassword;
    private String confirmFinPassword;
    private boolean readRules;
    private UserRole role = UserRole.USER;
    private String parentEmail;
    private List<UserFile> userFiles = Collections.emptyList();
    private String kycStatus;
    private String country;
    private String firstName;
    private String lastName;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDay;
    private String publicId;
    private Boolean verificationRequired;
    private Boolean tradeRestriction;
    private Boolean tradesManuallyAllowed;


    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userStatus=" + userStatus +
                ", regdate=" + regdate +
                ", ipaddress='" + ipaddress + '\'' +
                ", readRules=" + readRules +
                ", role=" + role +
                ", parentEmail='" + parentEmail + '\'' +
                ", userFiles=" + userFiles +
                '}';
    }
}
