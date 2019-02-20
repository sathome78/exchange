package me.exrates.controller.openAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import me.exrates.model.HmacSignature;
import me.exrates.security.filter.OpenApiAuthenticationFilter;
import me.exrates.security.service.OpenApiAuthService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.userOperation.UserOperationService;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.Collection;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public abstract class OpenApiCommonTest {


    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    UserOperationService userOperationService;
    @Autowired
    OpenApiAuthService openApiAuthService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    WalletService walletService;

    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(openApiAuthService.getUserByPublicKey(anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(getTestUserDetails());
        when(userService.getUserEmailFromSecurityContext()).thenReturn("test@test.me");
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(userService.getPreferedLang(anyInt())).thenReturn("en");
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(true);
    }

    protected RequestBuilder getOpenApiRequestBuilder(URI uri, HttpMethod method, HttpHeaders httpHeaders, String content, String contentType) {
        HttpHeaders headers = createHeaders(method.toString(), uri.getPath());
        if (httpHeaders != null) {
            headers.putAll(httpHeaders);
        }
        if (method.equals(HttpMethod.GET)) {
           return MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType);
        } else if (method.equals(HttpMethod.POST)) {
            return MockMvcRequestBuilders.post(uri).headers(headers).content(content).contentType(contentType);
        }
        throw new UnsupportedOperationException(String.format("Method: %s not supported", method.toString()));
    }

    private HttpHeaders createHeaders(String endPoint, String method) {
        Long timestamp = System.currentTimeMillis();
        HmacSignature signature = new HmacSignature.Builder()
                .algorithm("HmacSHA256")
                .delimiter("|")
                .apiSecret("PRIVATE_KEY")
                .endpoint(endPoint)
                .requestMethod(method)
                .timestamp(timestamp)
                .publicKey("PUBLIC_KEY")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.put(OpenApiAuthenticationFilter.HEADER_PUBLIC_KEY, ImmutableList.of("PUBLIC_KEY"));
        headers.put(OpenApiAuthenticationFilter.HEADER_SIGNATURE, ImmutableList.of(signature.getSignatureHexString()));
        headers.put(OpenApiAuthenticationFilter.HEADER_TIMESTAMP, ImmutableList.of(Long.toString(timestamp)));
        return headers;
    }


    private UserDetails getTestUserDetails() {
        Collection<GrantedAuthority> tokenPermissions = Sets.newHashSet();
        return new User("name", "password", true, false, false,
                false, tokenPermissions);
    }
}
