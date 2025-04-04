package com.quickmedicalcare.backend.changeInfo;

import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;

import java.io.IOException;

public interface SuperTokensResetPasswordInterface {
    String rid = SuperTokensInterface.rid;
    String api_key = SuperTokensInterface.api_key;
    String cdi_version = SuperTokensInterface.cdi_version;
    String url_prefix = SuperTokensInterface.url_prefix;
    String getPasswordToken(String userId, String email) throws IOException;
    Boolean resetPassword(String token, String newPassword) throws IOException;
    Boolean resetEmail(String email, String userId, String password) throws IOException;
}
