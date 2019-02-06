package me.exrates.service.ripple;

import me.exrates.service.MerchantService;
import me.exrates.service.WithdrawService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CasinoCoinWsServiceImplTest {


    @Mock
    private MerchantService merchantService;
    @Mock
    private WithdrawService withdrawService;

    private RippleService rippleService = spy(new RippleServiceImpl());

    private String wsUrl;
    private String address = "rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ";

    private RippleWsServiceImpl spyService = spy(new RippleWsServiceImpl(rippleService, merchantService, withdrawService));


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(spyService.getAddress()).thenReturn(address);
        doNothing().when(rippleService).onTransactionReceive(any(), any(), any());
    }

    @Test
    public void processTransactionPayment() throws IOException {
        doCallRealMethod().when(rippleService).onTransactionReceive(any(),any(),any());
        spyService.onMessage(jsonPartialPaymentWsNotify);
        verify(spyService, times(0)).getTransaction(any());
    }

    @Test
    public void processResponsePayment() throws IOException {
        spyService.onMessage(jsonPartialPaymentWsRequest);
        doAnswer((Answer) invocation -> {
            Object arg0 = invocation.getArguments()[0];
            Object arg1 = invocation.getArguments()[1];
            Object arg2 = invocation.getArguments()[2];
            assertEquals("F4051F9B2E6E15664CE1DC993F45050AA529CC36D18B738616B29456B5D4541F", arg0);
            assertEquals(830951366, arg1);
            assertEquals("21934", arg2);
            return null;
        }).when(rippleService).onTransactionReceive(any(), any(), any());
        verify(rippleService, times(1)).onTransactionReceive(any(), any(), any());
    }

    @Test
    public void processResponsePayment2() throws IOException {
        spyService.onMessage(usualTxWsResponse);
        doAnswer((Answer) invocation -> {
            Object arg0 = invocation.getArguments()[0];
            Object arg1 = invocation.getArguments()[1];
            Object arg2 = invocation.getArguments()[2];
            assertEquals("84A012F61FE0DEBC0E19A7C6880313FF9925D1373A870549FF0EC1319F75DB82", arg0);
            assertEquals(829161567, arg1);
            assertEquals("50000000000", arg2);
            return null;
        }).when(rippleService).onTransactionReceive(any(), any(), any());
        verify(rippleService, times(1)).onTransactionReceive(any(), any(), any());
    }

    @Test
    public void processTransactionUsualPayment() throws IOException {
        spyService.onMessage(usualPaymentNotify);
        doAnswer((Answer) invocation -> {
            Object arg0 = invocation.getArguments()[0];
            assertEquals("423150F00CE8C8BEA187A1CA07BDB18158F3AB159BFB2818C96288F4E9A11FD2", arg0);
            return null;
        }).when(spyService).getTransaction(any());
        verify(spyService, times(1)).getTransaction(any());
    }


    private String jsonPartialPaymentWsNotify = "{\"engine_result\":\"tesSUCCESS\",\"engine_result_code\":0,\n" +
            "\"engine_result_message\":\"The transaction was applied. Only final in a validated ledger.\",\"ledger_hash\":\n" +
            "\"1701E27733242837F5E8843AAD3856B32649B450C1664FCD0E813F3E1ABE158A\",\"ledger_index\":40432411,\n" +
            "\"meta\":{\"AffectedNodes\":[{\"ModifiedNode\":{\"FinalFields\":{\"Balance\":{\"currency\":\"USD\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\",\n" +
            "\"value\":\"9.08099209844183\"},\"Flags\":1114112,\"HighLimit\":{\"currency\":\"USD\",\"issuer\":\"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
            "\"value\":\"0\"},\"HighNode\":\"00000000000019E0\",\"LowLimit\":{\"currency\":\"USD\",\"issuer\":\"rpePPeRpC89vpCY3CDzhzMCs78nPoNnAKm\",\n" +
            "\"value\":\"100000000\"},\"LowNode\":\"0000000000000000\"},\"LedgerEntryType\":\"RippleState\",\n" +
            "\"LedgerIndex\":\"40EF33AF26075DD7675DB5975C59883C184A72DFD9408BA73D4E85BFBA1D8603\",\"PreviousFields\":\n" +
            "{\"Balance\":{\"currency\":\"USD\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\",\"value\":\"9.09099209844183\"}},\n" +
            "\"PreviousTxnID\":\"D51C1A883CB30A676759D29419A1DD157E8DA20B3B8DDE8BF8EFB60122147C4C\",\"PreviousTxnLgrSeq\":40431660}},\n" +
            "{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rpePPeRpC89vpCY3CDzhzMCs78nPoNnAKm\",\"Balance\":\"164924233\",\n" +
            "\"Domain\":\"636F646975732E7769657473652E7872706C2E746F6F6C73\",\"EmailHash\":\"833237B8665D2F4E00135E8DE646589F\",\"Flags\":0,\n" +
            "\"OwnerCount\":3,\"Sequence\":122},\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"655F5F859B58E83D5AAD10D8D1C8C3087FB93FDEE3A4635388FC08D3588DDAAE\",\n" +
            "\"PreviousFields\":{\"Balance\":\"164924245\",\"Sequence\":121},\"PreviousTxnID\":\"D51C1A883CB30A676759D29419A1DD157E8DA20B3B8DDE8BF8EFB60122147C4C\",\n" +
            "\"PreviousTxnLgrSeq\":40431660}},{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rGu3s6nDXqKNJTmcPZhd7nwqksbRJfghZ9\",\"Balance\":\"123850918\",\n" +
            "\"Flags\":0,\"OwnerCount\":8,\"Sequence\":1449},\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"9B4E89B5B04420D7A48D2717CA05A9A0E67439DEC1A34CEBA92E726B7059A498\",\n" +
            "\"PreviousFields\":{\"Balance\":\"123872852\"},\"PreviousTxnID\":\"E8D813638D2A8807FD6D09E844A10ED716665F07257842D578ED2822FCC89FB1\",\n" +
            "\"PreviousTxnLgrSeq\":40432396}},{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rGu3s6nDXqKNJTmcPZhd7nwqksbRJfghZ9\",\n" +
            "\"BookDirectory\":\"79C54A4EBD69AB2EADCE313042F36092BE432423CC6A4F784E102A4485232203\",\"BookNode\":\"0000000000000000\",\"Flags\":0,\"OwnerNode\":\"0000000000000000\",\n" +
            "\"Sequence\":1446,\"TakerGets\":\"225317\",\"TakerPays\":{\"currency\":\"USD\",\"issuer\":\"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
            "\"value\":\"0.1025209800798404\"}},\"LedgerEntryType\":\"Offer\",\"LedgerIndex\":\"9F5670743BCBDEE5D2A8F156DD3D73733B7C807EFD0E64F571AB9EE7794CB778\",\n" +
            "\"PreviousFields\":{\"TakerGets\":\"247251\",\"TakerPays\":{\"currency\":\"USD\",\"issuer\":\"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\"value\":\"0.11250102\"}},\n" +
            "\"PreviousTxnID\":\"A5867413972AED132D98B1B520C076720C14FDB5B47E33C00703A62FB40BFECA\",\"PreviousTxnLgrSeq\":40432390}},\n" +
            "{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\"Balance\":\"79789111524\",\"Flags\":0,\"OwnerCount\":0,\"Sequence\":244},\n" +
            "\"LedgerEntryType\":\"AccountRoot\",\"LedgerIndex\":\"A7D2D7E5683CA0204D3AD7D5287D686AC0F05DDE52532EE7FF24476F38E68768\",\"PreviousFields\":{\"Balance\":\"79789089590\"},\n" +
            "\"PreviousTxnID\":\"E653FEF60F520D391988A12103C4681538B1D348CF17CD5C7788906E0634BBCC\",\"PreviousTxnLgrSeq\":40415758}},\n" +
            "{\"ModifiedNode\":{\"FinalFields\":{\"Balance\":{\"currency\":\"USD\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\",\"value\":\"-56.14798809415435\"},\n" +
            "\"Flags\":2228224,\"HighLimit\":{\"currency\":\"USD\",\"issuer\":\"rGu3s6nDXqKNJTmcPZhd7nwqksbRJfghZ9\",\"value\":\"10000000000\"},\"HighNode\":\"0000000000000000\",\n" +
            "\"LowLimit\":{\"currency\":\"USD\",\"issuer\":\"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\"value\":\"0\"},\"LowNode\":\"00000000000019FD\"},\"LedgerEntryType\":\"RippleState\",\n" +
            "\"LedgerIndex\":\"ADB0F5EFEE25D7249BFD4C5149DF8F8CEE9B34FC0F9EA43C2D57F15BC73D35ED\",\"PreviousFields\":{\"Balance\":{\"currency\":\"USD\",\n" +
            "\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\",\"value\":\"-56.1380080542342\"}},\"PreviousTxnID\":\"884E0ED8405F1130090E8971572A66B0ABB14631E0343C47B4F47ADC03BA062F\",\n" +
            "\"PreviousTxnLgrSeq\":40432380}}],\"DeliveredAmount\":\"21934\",\"TransactionIndex\":5,\"TransactionResult\":\"tesSUCCESS\"},\"status\":\"closed\",\n" +
            "\"transaction\":{\"Account\":\"rpePPeRpC89vpCY3CDzhzMCs78nPoNnAKm\",\"Amount\":\"1000000000000\",\"Destination\":\"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\n" +
            "\"DestinationTag\":830951366,\"Fee\":\"12\",\"Flags\":2147614720,\"SendMax\":{\"currency\":\"USD\",\"issuer\":\"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\"value\":\"0.01\"},\n" +
            "\"Sequence\":121,\"SigningPubKey\":\"034E85AC7EF74B45AF0054F4A5B078DA3A9B9E34B2701ECD452B7FC045FB53A718\",\"TransactionType\":\"Payment\",\n" +
            "\"TxnSignature\":\"30450221008E4562AC2E4DC6FCEEB605FF0533AC6F10EB4E62928D9E45348A374077AA3D1102202B5736CA566EC0AC42D3984DE513F1E645155B651700D146E22D80E77CF544DE\",\n" +
            "\"date\":586221712,\"hash\":\"F4051F9B2E6E15664CE1DC993F45050AA529CC36D18B738616B29456B5D4541F\"},\"type\":\"transaction\",\"validated\":true}\n";

   private String jsonPartialPaymentWsRequest = "{\n" +
           "  \"id\": \"get transaction\",\n" +
           "  \"status\": \"success\",\n" +
           "  \"type\": \"response\",\n" +
           "  \"result\": {\n" +
           "    \"Account\": \"rpePPeRpC89vpCY3CDzhzMCs78nPoNnAKm\",\n" +
           "    \"Amount\": \"1000000000000\",\n" +
           "    \"Destination\": \"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\n" +
           "    \"DestinationTag\": 830951366,\n" +
           "    \"Fee\": \"12\",\n" +
           "    \"Flags\": 2147614720,\n" +
           "    \"SendMax\": {\n" +
           "      \"currency\": \"USD\",\n" +
           "      \"issuer\": \"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
           "      \"value\": \"0.01\"\n" +
           "    },\n" +
           "    \"Sequence\": 121,\n" +
           "    \"SigningPubKey\": \"034E85AC7EF74B45AF0054F4A5B078DA3A9B9E34B2701ECD452B7FC045FB53A718\",\n" +
           "    \"TransactionType\": \"Payment\",\n" +
           "    \"TxnSignature\": \"30450221008E4562AC2E4DC6FCEEB605FF0533AC6F10EB4E62928D9E45348A374077AA3D1102202B5736CA566EC0AC42D3984DE513F1E645155B651700D146E22D80E77CF544DE\",\n" +
           "    \"date\": 586221712,\n" +
           "    \"hash\": \"F4051F9B2E6E15664CE1DC993F45050AA529CC36D18B738616B29456B5D4541F\",\n" +
           "    \"inLedger\": 40432411,\n" +
           "    \"ledger_index\": 40432411,\n" +
           "    \"meta\": {\n" +
           "      \"AffectedNodes\": [\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Balance\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rrrrrrrrrrrrrrrrrrrrBZbvji\",\n" +
           "                \"value\": \"9.08099209844183\"\n" +
           "              },\n" +
           "              \"Flags\": 1114112,\n" +
           "              \"HighLimit\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
           "                \"value\": \"0\"\n" +
           "              },\n" +
           "              \"HighNode\": \"00000000000019E0\",\n" +
           "              \"LowLimit\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rpePPeRpC89vpCY3CDzhzMCs78nPoNnAKm\",\n" +
           "                \"value\": \"100000000\"\n" +
           "              },\n" +
           "              \"LowNode\": \"0000000000000000\"\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"RippleState\",\n" +
           "            \"LedgerIndex\": \"40EF33AF26075DD7675DB5975C59883C184A72DFD9408BA73D4E85BFBA1D8603\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rrrrrrrrrrrrrrrrrrrrBZbvji\",\n" +
           "                \"value\": \"9.09099209844183\"\n" +
           "              }\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"D51C1A883CB30A676759D29419A1DD157E8DA20B3B8DDE8BF8EFB60122147C4C\",\n" +
           "            \"PreviousTxnLgrSeq\": 40431660\n" +
           "          }\n" +
           "        },\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Account\": \"rpePPeRpC89vpCY3CDzhzMCs78nPoNnAKm\",\n" +
           "              \"Balance\": \"164924233\",\n" +
           "              \"Domain\": \"636F646975732E7769657473652E7872706C2E746F6F6C73\",\n" +
           "              \"EmailHash\": \"833237B8665D2F4E00135E8DE646589F\",\n" +
           "              \"Flags\": 0,\n" +
           "              \"OwnerCount\": 3,\n" +
           "              \"Sequence\": 122\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"AccountRoot\",\n" +
           "            \"LedgerIndex\": \"655F5F859B58E83D5AAD10D8D1C8C3087FB93FDEE3A4635388FC08D3588DDAAE\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": \"164924245\",\n" +
           "              \"Sequence\": 121\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"D51C1A883CB30A676759D29419A1DD157E8DA20B3B8DDE8BF8EFB60122147C4C\",\n" +
           "            \"PreviousTxnLgrSeq\": 40431660\n" +
           "          }\n" +
           "        },\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Account\": \"rGu3s6nDXqKNJTmcPZhd7nwqksbRJfghZ9\",\n" +
           "              \"Balance\": \"123850918\",\n" +
           "              \"Flags\": 0,\n" +
           "              \"OwnerCount\": 8,\n" +
           "              \"Sequence\": 1449\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"AccountRoot\",\n" +
           "            \"LedgerIndex\": \"9B4E89B5B04420D7A48D2717CA05A9A0E67439DEC1A34CEBA92E726B7059A498\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": \"123872852\"\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"E8D813638D2A8807FD6D09E844A10ED716665F07257842D578ED2822FCC89FB1\",\n" +
           "            \"PreviousTxnLgrSeq\": 40432396\n" +
           "          }\n" +
           "        },\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Account\": \"rGu3s6nDXqKNJTmcPZhd7nwqksbRJfghZ9\",\n" +
           "              \"BookDirectory\": \"79C54A4EBD69AB2EADCE313042F36092BE432423CC6A4F784E102A4485232203\",\n" +
           "              \"BookNode\": \"0000000000000000\",\n" +
           "              \"Flags\": 0,\n" +
           "              \"OwnerNode\": \"0000000000000000\",\n" +
           "              \"Sequence\": 1446,\n" +
           "              \"TakerGets\": \"225317\",\n" +
           "              \"TakerPays\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
           "                \"value\": \"0.1025209800798404\"\n" +
           "              }\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"Offer\",\n" +
           "            \"LedgerIndex\": \"9F5670743BCBDEE5D2A8F156DD3D73733B7C807EFD0E64F571AB9EE7794CB778\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"TakerGets\": \"247251\",\n" +
           "              \"TakerPays\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
           "                \"value\": \"0.11250102\"\n" +
           "              }\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"A5867413972AED132D98B1B520C076720C14FDB5B47E33C00703A62FB40BFECA\",\n" +
           "            \"PreviousTxnLgrSeq\": 40432390\n" +
           "          }\n" +
           "        },\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Account\": \"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\n" +
           "              \"Balance\": \"79789111524\",\n" +
           "              \"Flags\": 0,\n" +
           "              \"OwnerCount\": 0,\n" +
           "              \"Sequence\": 244\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"AccountRoot\",\n" +
           "            \"LedgerIndex\": \"A7D2D7E5683CA0204D3AD7D5287D686AC0F05DDE52532EE7FF24476F38E68768\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": \"79789089590\"\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"E653FEF60F520D391988A12103C4681538B1D348CF17CD5C7788906E0634BBCC\",\n" +
           "            \"PreviousTxnLgrSeq\": 40415758\n" +
           "          }\n" +
           "        },\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Balance\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rrrrrrrrrrrrrrrrrrrrBZbvji\",\n" +
           "                \"value\": \"-56.14798809415435\"\n" +
           "              },\n" +
           "              \"Flags\": 2228224,\n" +
           "              \"HighLimit\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rGu3s6nDXqKNJTmcPZhd7nwqksbRJfghZ9\",\n" +
           "                \"value\": \"10000000000\"\n" +
           "              },\n" +
           "              \"HighNode\": \"0000000000000000\",\n" +
           "              \"LowLimit\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rhub8VRN55s94qWKDv6jmDy1pUykJzF3wq\",\n" +
           "                \"value\": \"0\"\n" +
           "              },\n" +
           "              \"LowNode\": \"00000000000019FD\"\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"RippleState\",\n" +
           "            \"LedgerIndex\": \"ADB0F5EFEE25D7249BFD4C5149DF8F8CEE9B34FC0F9EA43C2D57F15BC73D35ED\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": {\n" +
           "                \"currency\": \"USD\",\n" +
           "                \"issuer\": \"rrrrrrrrrrrrrrrrrrrrBZbvji\",\n" +
           "                \"value\": \"-56.1380080542342\"\n" +
           "              }\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"884E0ED8405F1130090E8971572A66B0ABB14631E0343C47B4F47ADC03BA062F\",\n" +
           "            \"PreviousTxnLgrSeq\": 40432380\n" +
           "          }\n" +
           "        }\n" +
           "      ],\n" +
           "      \"DeliveredAmount\": \"21934\",\n" +
           "      \"TransactionIndex\": 5,\n" +
           "      \"TransactionResult\": \"tesSUCCESS\",\n" +
           "      \"delivered_amount\": \"21934\"\n" +
           "    },\n" +
           "    \"validated\": true\n" +
           "  }\n" +
           "}";

   private String usualTxWsResponse = "{\n" +
           "  \"id\": \"get transaction\",\n" +
           "  \"status\": \"success\",\n" +
           "  \"type\": \"response\",\n" +
           "  \"result\": {\n" +
           "    \"Account\": \"r43Vj4WKDH8AyCBBRy6nxYY9YUgNAYthFQ\",\n" +
           "    \"Amount\": \"50000000000\",\n" +
           "    \"Destination\": \"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\n" +
           "    \"DestinationTag\": 829161567,\n" +
           "    \"Fee\": \"12\",\n" +
           "    \"Flags\": 2147483648,\n" +
           "    \"LastLedgerSequence\": 40402284,\n" +
           "    \"Sequence\": 29,\n" +
           "    \"SigningPubKey\": \"037B9FB8CADF5F4553B8B05F17B8E0B19F4ACF62A0C675AEC21B92AB29E2D33F9D\",\n" +
           "    \"SourceTag\": 0,\n" +
           "    \"TransactionType\": \"Payment\",\n" +
           "    \"TxnSignature\": \"30450221009E31B0D6DDF29A2399A07D40560FA8ED48CCE74898990604E2B9FFC6A832506402207BE3F547EC6496639451DE4A54A2F1E73917DA02BFA39512C84C649AAA69BA2C\",\n" +
           "    \"date\": 586111081,\n" +
           "    \"hash\": \"84A012F61FE0DEBC0E19A7C6880313FF9925D1373A870549FF0EC1319F75DB82\",\n" +
           "    \"inLedger\": 40402257,\n" +
           "    \"ledger_index\": 40402257,\n" +
           "    \"meta\": {\n" +
           "      \"AffectedNodes\": [\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Account\": \"r43Vj4WKDH8AyCBBRy6nxYY9YUgNAYthFQ\",\n" +
           "              \"Balance\": \"49563859652\",\n" +
           "              \"Flags\": 0,\n" +
           "              \"OwnerCount\": 0,\n" +
           "              \"Sequence\": 30\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"AccountRoot\",\n" +
           "            \"LedgerIndex\": \"24547F313DCF89A3BFC8B41347795854EFEE2F0D51B5DD54249AE572FB99AD4A\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": \"99563859664\",\n" +
           "              \"Sequence\": 29\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"014C0C0220580F600447E4FECE9BC60704CDF0F2C7E70E13CF8241870A2342D8\",\n" +
           "            \"PreviousTxnLgrSeq\": 40389544\n" +
           "          }\n" +
           "        },\n" +
           "        {\n" +
           "          \"ModifiedNode\": {\n" +
           "            \"FinalFields\": {\n" +
           "              \"Account\": \"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\n" +
           "              \"Balance\": \"79919145964\",\n" +
           "              \"Flags\": 0,\n" +
           "              \"OwnerCount\": 0,\n" +
           "              \"Sequence\": 242\n" +
           "            },\n" +
           "            \"LedgerEntryType\": \"AccountRoot\",\n" +
           "            \"LedgerIndex\": \"A7D2D7E5683CA0204D3AD7D5287D686AC0F05DDE52532EE7FF24476F38E68768\",\n" +
           "            \"PreviousFields\": {\n" +
           "              \"Balance\": \"29919145964\"\n" +
           "            },\n" +
           "            \"PreviousTxnID\": \"0A70DA76227EC912D996D0B680716E619C5D40CA38D067106D4F7446191431E2\",\n" +
           "            \"PreviousTxnLgrSeq\": 40397221\n" +
           "          }\n" +
           "        }\n" +
           "      ],\n" +
           "      \"TransactionIndex\": 0,\n" +
           "      \"TransactionResult\": \"tesSUCCESS\",\n" +
           "      \"delivered_amount\": \"50000000000\"\n" +
           "    },\n" +
           "    \"validated\": true\n" +
           "  }\n" +
           "}";

   private String usualPaymentNotify = "{\"engine_result\":\"tesSUCCESS\",\"engine_result_code\":0,\n" +
           "\"engine_result_message\":\"The transaction was applied. Only final in a validated ledger.\",\n" +
           "\"ledger_hash\":\"0918F2CA0D1F07F6446CE3C2192EB103DE5F9BC76683D0F242397EC1725B4210\",\"ledger_index\":40449549,\n" +
           "\"meta\":{\"AffectedNodes\":[{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"r4eEbLKZGbVSBHnSUBZW8i5XaMjGLdqT4a\",\n" +
           "\"Balance\":\"668950571299\",\"Flags\":0,\"OwnerCount\":0,\"Sequence\":79116},\"LedgerEntryType\":\"AccountRoot\",\n" +
           "\"LedgerIndex\":\"784E22E7FE21F5DF2A5D4DC8D3F226619EB1A994E17549E8399F40E8687482CF\",\"PreviousFields\":{\"Balance\":\"668975562299\",\n" +
           "\"Sequence\":79115},\"PreviousTxnID\":\"96162875C0E777BF3397E21B3001D89CBF180F06161A8B7F063C3511C7F643FB\",\n" +
           "\"PreviousTxnLgrSeq\":40449200}},{\"ModifiedNode\":{\"FinalFields\":{\"Account\":\"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\n" +
           "\"Balance\":\"79791530962\",\"Flags\":0,\"OwnerCount\":0,\"Sequence\":245},\"LedgerEntryType\":\"AccountRoot\",\n" +
           "\"LedgerIndex\":\"A7D2D7E5683CA0204D3AD7D5287D686AC0F05DDE52532EE7FF24476F38E68768\",\"PreviousFields\":{\"Balance\":\"79767039962\"},\n" +
           "\"PreviousTxnID\":\"14FF332BD6588DE89C0F801BAE7FC31CF9B4B54C04D5F620A0DE2823BA9292A6\",\"PreviousTxnLgrSeq\":40436420}}],\"TransactionIndex\":12,\n" +
           "\"TransactionResult\":\"tesSUCCESS\"},\"status\":\"closed\",\"transaction\":{\"Account\":\"r4eEbLKZGbVSBHnSUBZW8i5XaMjGLdqT4a\",\n" +
           "\"Amount\":\"24491000\",\"Destination\":\"rpTxdXAm6AcW8S6ewpGeccKaMXcppz7kVZ\",\"DestinationTag\":556121535,\n" +
           "\"Fee\":\"500000\",\"Flags\":2147483648,\"Sequence\":79115,\"SigningPubKey\":\"038CF47114672A12B269AEE015BF7A8438609B994B0640E4B28B2F56E93D948B15\",\n" +
           "\"TransactionType\":\"Payment\",\n" +
           "\"TxnSignature\":\"3045022100F5406A57CEC396343E5057DDFA81997E97D9621B8508BAEA9A9E929F821326330220028072ECFC0D522095CA\",\n" +
           "\"date\":586285622,\"hash\":\"423150F00CE8C8BEA187A1CA07BDB18158F3AB159BFB2818C96288F4E9A11FD2\"},\"type\":\"transaction\",\"validated\":true}";

}