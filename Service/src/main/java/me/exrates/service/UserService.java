package me.exrates.service;

import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.Comment;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.IpLogDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.UsersInfoDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminIpLogsFilterData;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.UnRegisteredUserDeleteException;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface UserService {

    int getIdByEmail(String email);

    int getIdByNickname(String nickname);

    boolean setNickname(String newNickName, String userEmail);

    boolean hasNickname(String userEmail);

    User findByEmail(String email);

    User findByNickname(String nickname);

    void createUserFile(int userId, List<Path> paths);

    void setUserAvatar(int userId, Path path);

    void deleteUserFile(int docId);

    List<UserFile> findUserDoc(int userId);

    List<String> getLocalesList();

    boolean create(User user, Locale locale, String source);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    boolean userExistByEmail(String email);

    List<TemporalToken> getTokenByUserAndType(User user, TokenType tokenType);

    int verifyUserEmail(String token);

    void logIP(Integer userId, String ip, UserEventEnum eventEnum, String url);

    List<UserRole> getAllRoles();

    User getUserById(int id);

    boolean createUserByAdmin(User user);

    boolean updateUserByAdmin(UpdateUserDto user);

    @Transactional(rollbackFor = Exception.class)
    boolean updateUserSettings(UpdateUserDto user);

    boolean update(UpdateUserDto user, boolean resetPassword, Locale locale);

    boolean update(UpdateUserDto user, Locale locale);

    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale);

    List<TemporalToken> getAllTokens();

    boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException;

    @Transactional(rollbackFor = Exception.class)
    void sendEmailWithToken(User user, TokenType tokenType, String tokenLink, String emailSubject, String emailText, Locale locale, String newPass, String... params);

    void sendUnfamiliarIpNotificationEmail(User user, String emailSubject, String emailText, Locale locale);

    boolean createTemporalToken(TemporalToken token);

    User getCommonReferralRoot();

    void checkFinPassword(String enteredFinPassword, User storedUser, Locale locale);

    void updateCommonReferralRoot(int userId);

    int setCallbackURL(int userId, CallbackURL callbackURL) throws CallBackUrlAlreadyExistException;

    int updateCallbackURL(int userId, CallbackURL callbackURL);

    /**
     * Returns preferred locale for user stored in DB
     *
     * @param userId
     * @return string mnemonic of locale
     */
    String getPreferedLang(int userId);

    String getPreferedLangByEmail(String email);

    /**
     * Stores preferred locale for user in DB
     *
     * @param userId
     * @param locale
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean setPreferedLang(int userId, Locale locale);

    /**
     * Stores IP-address in DB for user. Data is stored in table USER_IP
     *
     * @param email is email the user as his identifier
     * @param ip    is IP-address
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean insertIp(String email, String ip);

    /**
     * Returns IP-address state for user
     *
     * @param email is email for search the user
     * @param ip    is IP-address for check
     * @return one of the values the enum UserIpState: CONFIRMED or NOT_CONFIRMED
     */
    UserIpDto getUserIpState(String email, String ip);

    /**
     * Saves in DB last date for IP? when user auth successfully
     *
     * @param userId is ID the user
     * @param ip     is ip-address from which user auth
     * @return "true" if data saved successfully, or "false" if none
     */
    boolean setLastRegistrationDate(int userId, String ip);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);

    boolean removeTemporaryPassword(Long id);

    String getAvatarPath(Integer userId);

    Locale getUserLocaleForMobile(String email);

    Collection<Comment> getUserComments(int id, String authenticatedAdminEmail);

    boolean addUserComment(UserCommentTopicEnum topic, String newComment, String email, boolean sendMessage);

    void editUserComment(int commentId, String newComment, String email, boolean sendMessage, String authenticatedAdminEmail);

    boolean deleteUserComment(int id);

    List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId, Set<String> allowedAuthorities, Locale locale);

    List<AdminAuthorityOption> getActiveAuthorityOptionsForUser(Integer userId);

    void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId, String currentUserEmail);

    UserRole getUserRoleFromSecurityContext();

    void setCurrencyPermissionsByUserId(List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList);

    InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(Integer userId, Integer currencyId, InvoiceOperationDirection invoiceOperationDirection);

    String getEmailById(Integer id);

    UserRole getUserRoleFromDB(String email);

    UserRole getUserRoleFromDB(Integer userId);

    String updatePinForUserForEvent(String userEmail, NotificationMessageEventEnum event);

    boolean checkPin(String email, String pin, NotificationMessageEventEnum event);

    boolean isLogin2faUsed(String email);

    String getUserEmailFromSecurityContext();

    TemporalToken getTemporalTokenByValue(String token);

    User getUserByTemporalToken(String token);

    boolean checkPassword(int userId, String password);

    List<Integer> getUserFavouriteCurrencyPairs(String email);

    boolean manageUserFavouriteCurrencyPair(String email, int currencyPairId, boolean delete);

    boolean deleteTempTokenByValue(String value);

    void updateGaTag(String gaCookie, String username);

    int updateReferenceIdAndStatus(String referenceId, EventStatus status);

    String getEmailByReferenceId(String referenceId);

    String findEmailById(int id);

    void blockUserByRequest(int userId);

    UsersInfoDto getUsersInfoFromCache(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    UsersInfoDto getUsersInfoFromDatabase(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    TemporalToken verifyUserEmailForForgetPassword(String token);

    String getUserKycStatusByEmail(String email);

    boolean updateVerificationStatus(String email, String status);

    boolean updateKycStatus(String status);

    String getKycReferenceByEmail(String email);

    boolean addPolicyToUser(String email, String policy);

    boolean updateUserRole(int userId, UserRole userRole);

    boolean existPolicyByUserIdAndPolicy(int id, String name);

    String getEmailByPubId(String pubId);

    String getPubIdByEmail(String email);

    DataTable<List<IpLogDto>> getIpAdressesTable(AdminIpLogsFilterData adminOrderFilterData, DataTableParams dataTableParams);

    boolean updateCountryCode(String countryCode);

    void deleteTemporalTokenByUserIdAndTokenType(int userId, TokenType tokenType);

    boolean subscribeToMailingByPublicId(String publicId, boolean subscribe);

    boolean subscribeToMailingByEmail(String email, boolean subscribe);

    void deleteUserPin(String email, NotificationMessageEventEnum login);
}
