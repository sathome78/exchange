package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.model.Commission;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.ExOrder;
import me.exrates.model.ReferralLevel;
import me.exrates.model.ReferralTransaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.RefFilterData;
import me.exrates.model.dto.ReferralInfoDto;
import me.exrates.model.dto.ReferralProfitDto;
import me.exrates.model.dto.RefsListContainer;
import me.exrates.model.dto.onlineTableDto.MyReferralDetailedDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefActionType;
import me.exrates.model.enums.ReferralTransactionStatusEnum;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CommissionService;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.NotificationService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.util.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Objects.isNull;
import static me.exrates.model.enums.OperationType.REFERRAL;
import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

@Log4j2
@Service
@PropertySource("classpath:/referral.properties")
public class ReferralServiceImpl implements ReferralService {


    private static final int decimalPlaces = 9;
    @Autowired
    private ReferralLevelDao referralLevelDao;
    @Autowired
    private ReferralUserGraphDao referralUserGraphDao;
    @Autowired
    private ReferralTransactionDao referralTransactionDao;
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyWalletService companyWalletService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    private MessageSource messageSource;

    private Commission commission;
    /**
     * Maximum amount of percents
     */
    private final BigDecimal HUNDREDTH = valueOf(100L);
    /**
     * URL following format  - xxx/register?ref=
     * where xxx is replaced by the domain name depending on the maven profile
     */
    private
    @Value("${referral.url}")
    String referralUrl;

    @PostConstruct
    public void init(){
        this.commission = commissionService.getDefaultCommission(REFERRAL);
    }

    /**
     * Generates referral reference following format : [3 random digits] Sponsor UserId [3 random digits]
     *
     * @param userEmail Sponsor email
     * @return String contains referral reference
     */
    @Override
    public String generateReferral(final String userEmail) {
        final int userId = userService.getIdByEmail(userEmail);
        int prefix = new Random().nextInt(999 - 100 + 1) + 100;
        int suffix = new Random().nextInt(999 - 100 + 1) + 100;
        return referralUrl + prefix + userId + suffix;
    }

    /**
     * Consuming referral reference generated by {@link ReferralService#generateReferral(String) generateReferral}
     * and extracting Sponsor {@link User#id} UserId
     *
     * @param ref Referral reference
     * @return Optional which contains Sponsor UserId if ref valid or an Optional.empty()
     */
    @Override
    public Optional<Integer> reduceReferralRef(final String ref) {
        final String id = ref.substring(3).substring(0, ref.length() - 6);
        if (id.matches("[0-9]+")) {
            return Optional.of(Integer.valueOf(id));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void processReferral(final ExOrder exOrder, final BigDecimal commissionAmount, Currency currency, int userId) {
        final List<ReferralLevel> levels = referralLevelDao.findAll();
        CompanyWallet cWallet = companyWalletService.findByCurrency(currency);
        Integer parent = null;
        for (ReferralLevel level : levels) {
            if (Objects.isNull(parent)) {
                parent = referralUserGraphDao.getParent(userId);
            } else {
                parent = referralUserGraphDao.getParent(parent);
            }
            if (Objects.nonNull(parent) && !level.getPercent().equals(ZERO)) {
                final ReferralTransaction referralTransaction = new ReferralTransaction();
                referralTransaction.setExOrder(exOrder);
                referralTransaction.setReferralLevel(level);
                referralTransaction.setUserId(parent);
                referralTransaction.setInitiatorId(userId);
                int walletId = walletService.getWalletId(parent, currency.getId()); // Mutable variable
                if (walletId == 0) { // Wallet is absent, creating new wallet
                    final Wallet wallet = new Wallet();
                    wallet.setActiveBalance(ZERO);
                    wallet.setCurrencyId(currency.getId());
                    wallet.setUser(userService.getUserById(parent));
                    wallet.setReservedBalance(ZERO);
                    walletId = walletService.createNewWallet(wallet); // Changing mutable variable state
                }
                final ReferralTransaction createdRefTransaction = referralTransactionDao.create(referralTransaction);
                final BigDecimal amount = doAction(commissionAmount, level.getPercent(), ActionType.MULTIPLY_PERCENT);
                final WalletOperationData wod = new WalletOperationData();
                wod.setCommissionAmount(this.commission.getValue());
                wod.setCommission(this.commission);
                wod.setAmount(amount);
                wod.setWalletId(walletId);
                wod.setBalanceType(ACTIVE);
                wod.setOperationType(OperationType.INPUT);
                wod.setSourceType(TransactionSourceType.REFERRAL);
                wod.setSourceId(createdRefTransaction.getId());
                walletService.walletBalanceChange(wod);
                companyWalletService.withdrawReservedBalance(cWallet, amount);
                notificationService.createLocalizedNotification(parent, NotificationEvent.IN_OUT,
                        "referral.title", "referral.message",
                        new Object[]{BigDecimalProcessing.formatNonePoint(amount, false), currency.getName()});
            } else {
                break;
            }
        }
    }


    @Override
    public List<ReferralTransaction> findAll(final int userId) {
        return referralTransactionDao.findAll(userId);
    }

    @Override
    public List<ReferralLevel> findAllReferralLevels() {
        return referralLevelDao.findAll();
    }

    @Override
    public String getParentEmail(final int childId) {
        final Integer parent = referralUserGraphDao.getParent(childId);
        final User user = userService.getUserById(parent);
        if (!isNull(user)) {
            return user.getEmail();
        }
        return null;
    }
    
    @Override
    public Integer getReferralParentId(int childId) {
        return referralUserGraphDao.getParent(childId);
    }

    /**
     * The number of referral levels is hardcoded in database (table REFERRAL_LEVEL)
     * Creates new level with specified percent. Old level is not removed, but not used anymore.
     * Stored for already committed transactions at that level
     *
     * @param level      modified level
     * @param oldLevelId old level id
     * @param percent    the desired percentage (the total amount of percents on all levels should not exceed 100 )
     *                   either throws IllegalStateException
     * @return id of the newly created level
     */
    @Override
    public int updateReferralLevel(final int level, final int oldLevelId, final BigDecimal percent) {
        final BigDecimal oldLevelPercent = referralLevelDao.findById(oldLevelId).getPercent();
        if (referralLevelDao.getTotalLevelsPercent().subtract(oldLevelPercent).add(percent).compareTo(HUNDREDTH) > 0) {
            throw new IllegalStateException("The total amount of percents at all levels should not exceed 100%");
        }
        if (percent.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Percent should be positive");
        }
        final ReferralLevel referralLevel = new ReferralLevel();
        referralLevel.setLevel(level);
        referralLevel.setPercent(percent);
        return referralLevelDao.create(referralLevel);
    }

    /**
     * Associates registered user with its sponsor
     *
     * @param childUserId  child UserId
     * @param parentUserId parent UserId
     */
    @Override
    public void bindChildAndParent(final int childUserId, final int parentUserId) {
        referralUserGraphDao.create(childUserId, parentUserId);
    }

    @Override
    public List<MyReferralDetailedDto> findAllMyReferral(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale) {
        List<MyReferralDetailedDto> result = referralTransactionDao.findAllMyRefferal(email, offset, limit, locale);
        result.forEach(p -> {
            p.setStatus(messageSource.getMessage("message.ref." + p.getStatus(), null, locale));
        });
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyReferralDetailedDto>() {{
                add(new MyReferralDetailedDto(false));
            }};
        }
        return result;
    }

    @Override
    public List<MyReferralDetailedDto> findAllMyReferral(String email, Integer offset, Integer limit, Locale locale) {
        return referralTransactionDao.findAllMyRefferal(email, offset, limit, locale);
    }
    
    @Override
    public List<Integer> getChildrenForParentAndBlock(Integer parentId) {
        return referralUserGraphDao.getChildrenForParentAndBlock(parentId);
    }
    
    @Override
    @Transactional
    public void updateReferralParentForChildren(User user) {
        Integer userReferralParentId = getReferralParentId(user.getId());
        if (userReferralParentId == null) {
            userReferralParentId = userService.getCommonReferralRoot().getId();
        }
        log.debug(String.format("Changing ref parent from %s to %s", user.getId(), userReferralParentId));
        referralUserGraphDao.changeReferralParent(user.getId(), userReferralParentId);
    }

    @Override
    public RefsListContainer getRefsContainerForReq(String action, Integer userId, int profitUserId,
                                                    int onPage, int page, RefFilterData refFilterData) {
        int refLevel = 1;
        RefsListContainer container;
        RefActionType refActionType = RefActionType.convert(action);
        switch (refActionType) {
            case init:{
                container = this
                        .getUsersFirstLevelAndCountProfitForUser(profitUserId, profitUserId, onPage, page, refFilterData);
                container.setReferralProfitDtos(this.getAllUserRefProfit(null, profitUserId,  refFilterData));
                break;
            }
            case search:{
                if (!StringUtils.isEmpty(refFilterData.getEmail())) {
                    userId = userService.getIdByEmail(refFilterData.getEmail());
                    refLevel = this.getUserReferralLevelForChild(userId, profitUserId);
                    if (refLevel == -1) {
                        return new RefsListContainer(Collections.emptyList());
                    }
                    container = this.getUsersRefToAnotherUser(userId, profitUserId, refLevel, refFilterData);
                    container.setReferralProfitDtos(this.getAllUserRefProfit(userId, profitUserId, refFilterData));
                } else {
                    container = this
                            .getUsersFirstLevelAndCountProfitForUser(profitUserId, profitUserId, onPage, page, refFilterData);
                    container.setReferralProfitDtos(this.getAllUserRefProfit(null, profitUserId, refFilterData));
                }
                break;
            }
            case toggle:{
                refLevel = this.getUserReferralLevelForChild(userId, profitUserId);
                if (refLevel >= 7 || refLevel < 0) {
                    return new RefsListContainer(Collections.emptyList());
                }
                container = this
                        .getUsersFirstLevelAndCountProfitForUser(userId, profitUserId, onPage, page, refFilterData);

                break;
            }
            default:return new RefsListContainer(Collections.emptyList());
        }
        container.setCurrentLevel(refLevel);
        return container;
    }

    private RefsListContainer getUsersFirstLevelAndCountProfitForUser(int userId, int profitForId, int onPage, int pageNumber, RefFilterData refFilterData) {
        int offset = (pageNumber - 1) * onPage;
        List<ReferralInfoDto> dtoList = referralUserGraphDao.getInfoAboutFirstLevRefs(userId, profitForId, onPage, offset, refFilterData);
        setDetailedAmountToDtos(dtoList, profitForId, refFilterData);
        int totalSize = referralUserGraphDao.getInfoAboutFirstLevRefsTotalSize(userId);
        log.warn("list size {}", dtoList.size());
        return new RefsListContainer(dtoList, onPage, pageNumber, totalSize);
    }

    private RefsListContainer getUsersRefToAnotherUser(int userId, int profitUser, int level, RefFilterData refFilterData) {
        List<ReferralInfoDto> dtoList = Arrays.asList(referralUserGraphDao.getInfoAboutUserRef(userId, profitUser, refFilterData));
        setDetailedAmountToDtos(dtoList, profitUser, refFilterData);
        return new RefsListContainer(dtoList, level);
    }

    private void setDetailedAmountToDtos(List<ReferralInfoDto> list, int profitUser, RefFilterData refFilterData) {
        list.stream().filter(p -> p.getRefProfitFromUser() > 0)
                .forEach(l -> l.setReferralProfitDtoList(referralUserGraphDao.detailedCountRefsTransactions(l.getRefId(), profitUser, refFilterData)));
    }


    private int getUserReferralLevelForChild(Integer childUserId, Integer parentUserId) {
        int i = 1;
        int level = -1;
        if (childUserId == null || childUserId.equals(0) || parentUserId == null){
            return level;
        }
        if (childUserId.equals(parentUserId)) {
            return 0;
        }
        Integer parentId = referralUserGraphDao.getParent(childUserId);
        while (parentId != null && i <= 7) {
            if (parentId.equals(parentUserId)) {
                level = i;
                break;
            }
            parentId = referralUserGraphDao.getParent(parentId);
            i++;
        }
        return level;
    }

    private List<ReferralProfitDto> getAllUserRefProfit(Integer userId, Integer profitUserId, RefFilterData filterData) {
        return referralUserGraphDao.detailedCountRefsTransactions(userId, profitUserId, filterData);
    }

    @Override
    public List<String> getRefsListForDownload(int profitUser, RefFilterData filterData) {
        List<String> resultList = prepareCsv();
        int level = 1;
        List<ReferralInfoDto> nextLevelList;
        if (StringUtils.isEmpty(filterData.getEmail())) {
            nextLevelList = referralUserGraphDao
                    .getInfoAboutFirstLevRefs(profitUser, profitUser, -1, -1, filterData);
          } else {
            final int userId = userService.getIdByEmail(filterData.getEmail());
            level = this.getUserReferralLevelForChild(userId, profitUser);
            if (level == -1) {
                return resultList;
            }
            nextLevelList = Arrays.asList(referralUserGraphDao.getInfoAboutUserRef(userId, profitUser, filterData));
        }
        return convertTrListToString(nextLevelList, level, resultList, profitUser, filterData);
    }

    private List<String> convertTrListToString(List<ReferralInfoDto> info, int level,
                                               List<String> resultList, int profitForId,
                                               RefFilterData filterData) {
        if (level > 7) return resultList;
        setDetailedAmountToDtos(info, profitForId, filterData);
        info.forEach(i -> {
            StringBuilder sb = new StringBuilder();
            sb.append(i.getEmail())
                    .append(";")
                    .append(i.getRefProfitFromUser() == 0 ? 0 : refProfitString(i.getReferralProfitDtoList()))
                    .append(";")
                    .append(i.getFirstRefLevelCount())
                    .append(";")
                    .append(level);
            resultList.add(sb.toString());
            resultList.add("\n");
            if (i.getFirstRefLevelCount() > 0) {
                int newUserId = userService.getIdByEmail(i.getEmail());
                List<ReferralInfoDto> nextLevelList = referralUserGraphDao
                        .getInfoAboutFirstLevRefs(newUserId, profitForId, -1, -1, filterData);
            convertTrListToString(nextLevelList, level+1, resultList, profitForId, filterData);
            }
        });
        return resultList;
    }

    private String refProfitString(List<ReferralProfitDto> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(i -> {
            sb.append(i.getAmount());
            sb.append(i.getCurrencyName());
            sb.append(", ");
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    private String getCSVTransactionsHeader() {
        return "Email;Amount;Referrals count;level";
    }

    private List<String> prepareCsv(){
        List<String> transactionsResult = new ArrayList<>();
        transactionsResult.add(getCSVTransactionsHeader());
        transactionsResult.add("\n");
        return transactionsResult;
    }

    @Override
    @Transactional
    public void setRefTransactionStatus(ReferralTransactionStatusEnum status, int refTransactionId) {
        referralTransactionDao.setRefTransactionStatus(status, refTransactionId);
    }
}
