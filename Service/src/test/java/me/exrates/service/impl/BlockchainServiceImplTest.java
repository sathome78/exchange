package me.exrates.service.impl;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
//@RunWith(MockitoJUnitRunner.class)
public class BlockchainServiceImplTest  {
/*
    @Mock
    private TransactionService transactionService;

    @Mock
    private PendingPaymentDao pendingPaymentDao;

    @Mock
    private BTCTransactionDao btcTransactionDao;

    @Mock
    private AlgorithmService algorithmService;

    @Mock
    private BlockchainSDKWrapper blockchainSDKWrapper;

    @InjectMocks
    private BlockchainService blockchainService = new BlockchainServiceImpl();

    private Transaction transaction;

    private PendingPayment pendingPayment;

    private BTCTransaction btcTransaction;

    private Map<String, String> pretendedPayment;

    private String callback;

    private static Map<String,String> fakes;

    private static final CreditsOperation DUMMY_CREDITS_OPERATION = null;
    private static final Transaction DUMMY_TRANSACTION = null;
    private static final BigDecimal DUMMY_AMOUNT = null;
    private static final String TRANSACTION_HASH = "0xBBB";
    private static final String RECEIVING_ADDRESS = "0x10FF";
    private static final int decimalPlaces = 8;
    private static final int ID = 1;


    @BeforeClass
    public static void initFake() {
        fakes = new HashMap<String, String>() {
            {
                put("xPub", "xPub");
                put("apiCode", "apiCode");
                put("callbackUrl", "callbackUrl");
                put("secret", "secret");
            }
        };
    }

    @Before
    public void setup() throws APIException, IOException {
        pretendedPayment = new HashMap<String, String>() {
            {
                put("value", "200000000");
                put("address", "addr");
                put("secret", fakes.get("secret"));
                put("confirmations", "4");
                put("transaction_hash", "hash");
            }
        };

        pendingPayment = new PendingPayment();
        pendingPayment.setAddress(pretendedPayment.get("address"));
        pendingPayment.setInvoiceId(ID);
        pendingPayment.setTransactionHash(pretendedPayment.get("secret"));

        btcTransaction = new BTCTransaction();
        btcTransaction.setAmount(ONE.add(ONE).setScale(decimalPlaces, ROUND_HALF_UP));
        btcTransaction.setTransactionId(ID);
        btcTransaction.setHash(pretendedPayment.get("transaction_hash"));

        final Wallet userWallet = new Wallet();
        userWallet.setName("fake");
        userWallet.setId(ID);
        userWallet.setActiveBalance(ONE);
        userWallet.setCurrencyId(ID);

        final Merchant merchant = new Merchant();
        merchant.setId(ID);
        merchant.setDescription("fake");
        merchant.setName("fake");

        final Commission commission = new Commission();
        commission.setDateOfChange(valueOf(LocalDate.now()));
        commission.setId(ID);
        commission.setOperationType(INPUT);
        commission.setValue(ONE);

        transaction = new Transaction();
        transaction.setId(ID);
        transaction.setAmount(ONE);
        transaction.setCommissionAmount(ONE);
        transaction.setCommission(commission);
        transaction.setOperationType(INPUT);
        transaction.setMerchant(merchant);
        transaction.setDatetime(LocalDateTime.now());
        transaction.setConfirmation(1);

        doWithFields(
                BlockchainServiceImpl.class,
                field -> {
                    field.setAccessible(true);
                    field.set(blockchainService,fakes.get(field.getName()));
                },
                field -> fakes.containsKey(field.getName()));

        final String seq = "1:".concat(fakes.get("secret"));
        callback = UriComponentsBuilder
                .fromUriString(fakes.get("callbackUrl"))
                .queryParam("invoice_id", transaction.getId())
                .queryParam("secret", TRANSACTION_HASH)
                .build(true)
                .toString();
        given(algorithmService.sha256(seq)).willReturn(TRANSACTION_HASH);
        given(algorithmService.sha256(argThat(not(seq)))).willReturn("bad");
        given(transactionService.findById(ID)).willReturn(transaction);
        given(btcTransactionDao.create(btcTransaction)).willReturn(btcTransaction);
        given(transactionService.createTransactionRequest(any())).willReturn(transaction);
        given(blockchainSDKWrapper.receive(fakes.get("xPub"), callback, fakes.get("apiCode")))
                .willReturn(new ReceiveResponse(ID, RECEIVING_ADDRESS, fakes.get("callbackUrl")));
        given(blockchainSDKWrapper.receive(eq(fakes.get("xPub")), argThat(not(callback)), eq(fakes.get("apiCode"))))
                .willThrow(new IOException());
        given(pendingPaymentDao.findByInvoiceId(ID)).willReturn(Optional.of(pendingPayment));
        given(pendingPaymentDao.findByInvoiceId(0)).willThrow(new MerchantInternalException());
    }

    @Test
    public void computeTransactionHashOnValidTransaction() {
        final Method method = findMethod(BlockchainServiceImpl.class, "computeTransactionHash", Transaction.class);
        method.setAccessible(true);
        assertEquals(invokeMethod(method, blockchainService, transaction),TRANSACTION_HASH);
        verify(algorithmService,times(1)).sha256(anyString());
    }

    @Test
    public void computeTransactionHashOnInvalidTransaction() {
        final Method method = findMethod(BlockchainServiceImpl.class, "computeTransactionHash", Transaction.class);
        method.setAccessible(true);
        transaction.setId(10);
        assertNotEquals(invokeMethod(method, blockchainService, transaction),TRANSACTION_HASH);
        verify(algorithmService,times(1)).sha256(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void computeTransactionHashOnNullTransaction() {
        final Method method = findMethod(BlockchainServiceImpl.class, "computeTransactionHash", Transaction.class);
        method.setAccessible(true);
        invokeMethod(method, blockchainService, (Object) null);
    }

    @Test
    public void shouldCorresponds() {
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), Optional.empty());
    }

    @Test
    public void notCorrespondsWithNullAmount() {
        pretendedPayment.remove("value");
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), of("Amount is invalid"));
    }

    @Test
    public void notCorrespondsWithNullAddress() {
        pretendedPayment.remove("address");
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), of("Address is not correct"));
    }

    @Test
    public void notCorrespondsWithIncorrectAddress() {
        pretendedPayment.put("address", "different");
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), of("Address is not correct"));
    }

    @Test
    public void notCorrespondsWithNullSecret() {
        pretendedPayment.remove("secret");
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), of("Secret is invalid"));
    }

    @Test
    public void notCorrespondsWithInvalidSecret() {
        pretendedPayment.put("secret","invalid");
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), of("Secret is invalid"));
    }

    @Test
    public void notCorrespondsWithNullTransactionHash() {
        pretendedPayment.remove("transaction_hash");
        assertEquals(blockchainService.notCorresponds(pretendedPayment, pendingPayment), of("Transaction hash missing"));
    }

    @Test
    public void approveBlockchainTransactionShouldReturnWaitingForConfirmations() {
        pretendedPayment.put("confirmations", "0");
        assertEquals(blockchainService.approveBlockchainTransaction(pendingPayment, pretendedPayment),
                "Waiting for confirmations");
    }

    @Test
    public void approveBlockchainTransactionShouldReturnIncorrectAmountInCaseOfExistingConfirmations() {
        pretendedPayment.put("value", "1");
        assertEquals(blockchainService.approveBlockchainTransaction(pendingPayment, pretendedPayment),
                "Incorrect amount! Amount cannot change since it confirmed at least once");
        final InOrder inOrder = inOrder(transactionService);
        inOrder.verify(transactionService).findById(ID);
    }

    @Test
    public void approveBlockchainTransactionShouldReturnWaitingForConfirmationsAndRecalculateAmount() {
        transaction.setConfirmation(0);
        pretendedPayment.put("value", "1");
        pretendedPayment.put("confirmations", "1");
        assertEquals(blockchainService.approveBlockchainTransaction(pendingPayment, pretendedPayment),
                "Waiting for confirmations");
        final InOrder inOrder = inOrder(transactionService);
        inOrder.verify(transactionService).findById(ID);
    }

    @Test
    public void approveBlockchainTransactionShouldReturnOk() {
        assertEquals(blockchainService.approveBlockchainTransaction(pendingPayment, pretendedPayment), "*ok*");
        InOrder inOrder = inOrder(transactionService, pendingPaymentDao, btcTransactionDao);
        inOrder.verify(transactionService).findById(pendingPayment.getInvoiceId());
        inOrder.verify(pendingPaymentDao).delete(pendingPayment.getInvoiceId());
        inOrder.verify(transactionService).provideTransaction(transaction);
        inOrder.verify(btcTransactionDao).create(btcTransaction);
    }

    @Test(expected = RejectedPaymentInvoice.class)
    public void createPaymentInvoiceShouldThrowRejectedPaymentInvoice() {
        transaction.setId(0);
        blockchainService.createPaymentInvoice(DUMMY_CREDITS_OPERATION);
    }

    @Test
    public void createPaymentInvoiceShouldReturnPayment() throws APIException, IOException {
        pendingPayment.setAddress(RECEIVING_ADDRESS);
        pendingPayment.setTransactionHash(TRANSACTION_HASH);
        assertEquals(blockchainService.createPaymentInvoice(DUMMY_CREDITS_OPERATION), pendingPayment);
        InOrder inOrder = inOrder(transactionService, blockchainSDKWrapper, pendingPaymentDao);
        inOrder.verify(transactionService).createTransactionRequest(DUMMY_CREDITS_OPERATION);
        inOrder.verify(blockchainSDKWrapper).receive(fakes.get("xPub"), callback, fakes.get("apiCode"));
        inOrder.verify(pendingPaymentDao).create(pendingPayment);
    }

    @Test(expected = MerchantInternalException.class)
    public void findByInvoiceIdShouldThrowMerchantInternalException() {
        pendingPaymentDao.findByInvoiceId(0);
    }

    @Test
    public void findByInvoiceIdShouldReturnPendingPayment() {
        assertEquals(pendingPaymentDao.findByInvoiceId(ID),Optional.of(pendingPayment));
    }*/
}
