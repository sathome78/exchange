package me.exrates.service;

import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;

import java.util.List;
import java.util.Locale;

public interface UserService {

    int getIdByEmail(String email);

    User findByEmail(String email);

    boolean create(User user, Locale locale);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String logIP(String email, String host);

    List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType);

    User verifyUserEmail(String token);

    void verifyUserEmail(String token, TokenType tokenType);

    List<UserRole> getAllRoles();

    User getUserById(int id);

    boolean createUserByAdmin(User user);

    boolean updateUserByAdmin(User user);

    boolean update(User user, boolean changePassword, boolean changeFinPassword, boolean resetPassword, Locale locale);

    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale);
}
