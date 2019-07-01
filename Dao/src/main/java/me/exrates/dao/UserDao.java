package me.exrates.dao;

import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.Comment;
import me.exrates.model.PagingData;
import me.exrates.model.Policy;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.IpLogDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserBalancesDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserIpReportDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.UserShortDto;
import me.exrates.model.dto.UsersInfoDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminIpLogsFilterData;
import me.exrates.model.dto.ieo.IeoUserStatus;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.PolicyEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public interface UserDao {

    int getIdByNickname(String nickname);

    boolean setNickname(String newNickName, String userEmail);

    boolean create(User user);

    void createUserDoc(int userId, List<Path> paths);

    void setUserAvatar(int userId, String path);

    List<UserFile> findUserDoc(int userId);

    void deleteUserDoc(int docId);

    List<UserRole> getAllRoles();

    List<User> getUsersByRoles(List<UserRole> listRoles);

    UserRole getUserRoleById(Integer id);

    List<String> getUserRoleAndAuthorities(String email);

    List<AdminAuthorityOption> getAuthorityOptionsForUser(Integer userId);

    boolean createAdminAuthoritiesForUser(Integer userId, UserRole role);

    boolean hasAdminAuthorities(Integer userId);

    void updateAdminAuthorities(List<AdminAuthorityOption> options, Integer userId);

    boolean removeUserAuthorities(Integer userId);


    User findByEmail(String email);

    PagingData<List<User>> getUsersByRolesPaginated(List<UserRole> roles, int offset, int limit,
                                                    String orderColumnName, String orderDirection,
                                                    String searchValue);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String getIP(int userId);

    boolean setIP(int id, String ip);

    int getIdByEmail(String email);


    boolean addIpToLog(Integer userId, String ip, UserEventEnum eventEnum, String url);

    PagingData<List<IpLogDto>> getIpLogPage(AdminIpLogsFilterData adminOrderFilterData,
                                            DataTableParams dataTableParams);

    boolean update(UpdateUserDto user);

    Optional<String> findKycReferenceByEmail(String email);

    UserShortDto findShortByEmail(String email);

    boolean updateVerificationStatus(String email, String result);

    User findByNickname(String nickname);

    List<User> getAllUsers();

    User getUserById(int id);

    User getCommonReferralRoot();

    void updateCommonReferralRoot(int userId);

    UserRole getUserRoles(String email);

    boolean createTemporalToken(TemporalToken token);

    TemporalToken verifyToken(String token);

    boolean deleteTemporalToken(TemporalToken token);

    boolean deleteTemporalToken(String tempToken);

    /**
     * Delete all tokens for user with concrete TokenType.
     * Uses in "Send again" in registration.
     *
     * @param token (TemporalToken)
     * @return boolean (false/true)
     */
    boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token);

    List<TemporalToken> getTokenByUserAndType(int userId, TokenType tokenType);

    boolean updateUserStatus(User user);

    List<TemporalToken> getAllTokens();

    boolean delete(User user);

    String getPreferredLang(int userId);

    boolean setPreferredLang(int userId, Locale locale);

    String getPreferredLangByEmail(String email);

    boolean insertIp(String email, String ip);

    UserIpDto getUserIpState(String email, String ip);

    boolean setIpStateConfirmed(int userId, String ip);

    boolean setLastRegistrationDate(int userId, String ip);

    Long saveTemporaryPassword(Integer userId, String password, Integer tokenId);

    boolean deleteTemporaryPassword(Long id);

    List<UserSessionInfoDto> getUserSessionInfo(Set<String> emails);

    Collection<Comment> getUserComments(int id);

    Optional<Comment> getCommentById(int id);

    boolean addUserComment(Comment comment);

    void editUserComment(int id, String newComment, boolean sendMessage);

    boolean deleteUserComment(int id);

    void setCurrencyPermissionsByUserId(Integer userId, List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList);

    InvoiceOperationPermission getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(Integer userId, Integer currencyId, InvoiceOperationDirection invoiceOperationDirection);

    String getEmailById(Integer id);

    String getEmailByPubId(String pubId);

    String getPubIdByEmail(String email);

    UserRole getUserRoleByEmail(String email);

    void savePollAsDoneByUser(String email);

    boolean checkPollIsDoneByUser(String email);

    String getPinByEmailAndEvent(String email, NotificationMessageEventEnum event);

    void updatePinByUserEmail(String userEmail, String pin, NotificationMessageEventEnum event);

    UsersInfoDto getUsersInfo(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    List<UserBalancesDto> getUserBalances(List<UserRole> userRoles);

    User getUserByTemporalToken(String token);

    String getPassword(int userId);

    Integer updateGaTag(String gatag, String userName);

    boolean userExistByEmail(String email);

    String getAvatarPath(Integer userId);

    List<Integer> findFavouriteCurrencyPairsById(int userId);

    boolean manageUserFavouriteCurrencyPair(int userId, int currencyPairId, boolean delete);

    boolean updateReferenceIdAndStatus(String referenceId, EventStatus status, String userEmail);

    String getReferenceIdByUserEmail(String userEmail);

    String getEmailByReferenceId(String referenceId);

    TemporaryPasswordDto getTemporaryPasswordById(Long id);

    boolean updateUserPasswordFromTemporary(Long tempPassId);

    boolean tempDeleteUserWallets(int userId);

    boolean tempDeleteUser(int id);

    Integer retrieveNicknameSearchLimit();

    List<String> findNicknamesByPart(String part, Integer limit);

    boolean updateLast2faNotifyDate(String email);

    List<UserIpReportDto> getUserIpReportByRoleList(List<Integer> userRoleList);

    Integer getNewRegisteredUserNumber(LocalDateTime startTime, LocalDateTime endTime);

    long countUserEntrance(String email);

    Integer getUserIdByGaTag(String gaTag);

    String getKycStatusByEmail(String email);

    boolean updatePrivacyDataAndKycReferenceIdByEmail(String email, String refernceUID, String country,
                                                      String firstName, String lastName, Date birthDay);

    Optional<User> findByKycReferenceId(String referenceId);

    String findKycReferenceByUserEmail(String email);

    List<Policy> getAllPoliciesByUserId(String id);

    boolean existPolicyByUserIdAndPolicy(int id, String policyName);

    boolean updateUserPolicyByEmail(String email, PolicyEnum policyEnum);

    IeoUserStatus findIeoUserStatusByEmail(String email);

    boolean updateUserRole(int userId, UserRole userRole);

    String getUserPublicId(String userEmail);

    boolean updateCountryCode(String countryCode, String userEmail);
}
