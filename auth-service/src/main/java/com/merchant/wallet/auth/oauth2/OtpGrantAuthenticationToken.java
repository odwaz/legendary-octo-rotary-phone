package com.merchant.wallet.auth.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;

public class OtpGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    public static final AuthorizationGrantType OTP_GRANT_TYPE = new AuthorizationGrantType("otp");

    private final String phoneNumber;
    private final String otpCode;

    public OtpGrantAuthenticationToken(String phoneNumber, String otpCode, Authentication clientPrincipal, Map<String, Object> additionalParameters) {
        super(OTP_GRANT_TYPE, clientPrincipal, additionalParameters);
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOtpCode() {
        return otpCode;
    }
}