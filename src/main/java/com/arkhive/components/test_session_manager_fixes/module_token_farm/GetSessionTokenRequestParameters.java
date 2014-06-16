package com.arkhive.components.test_session_manager_fixes.module_token_farm;

import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/16/2014.
 */
public class GetSessionTokenRequestParameters {
    private static String OPTIONAL_PARAMETER_TOKEN_VERSION = "token_version";
    private static String OPTIONAL_PARAMETER_EKEY = "ekey";
    private static String OPTIONAL_PARAMETER_RESPONSE_FORMAT = "response_format";
    private static String REQUIRED_PARAMETER_APPLICATION_ID = "application_id";
    private static String REQUIRED_PARAMETER_SIGNATURE = "signature";

    public static Map<String, String> constructOptionalParameters() {
        Map<String, String> optionalParameters = new LinkedHashMap<String, String>();
        optionalParameters.put(OPTIONAL_PARAMETER_TOKEN_VERSION, "2");
        optionalParameters.put(OPTIONAL_PARAMETER_RESPONSE_FORMAT, "json");
        return optionalParameters;
    }

    public static Map<String, String> constructRequiredParameters(ApplicationCredentials applicationCredentials) throws CredentialsException {
        Map<String, String> requiredParameters = new LinkedHashMap<String, String>();
        requiredParameters.putAll(applicationCredentials.getCredentials());
        requiredParameters.put(REQUIRED_PARAMETER_APPLICATION_ID, applicationCredentials.getAppId());
        requiredParameters.put(REQUIRED_PARAMETER_SIGNATURE, calculateSignature(applicationCredentials));
        return requiredParameters;
    }

    private static String calculateSignature(ApplicationCredentials applicationCredentials) throws CredentialsException {
        Map<String, String> credentialsMap = applicationCredentials.getCredentials();
        String appId = applicationCredentials.getAppId();
        String apiKey = applicationCredentials.getApiKey();

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : credentialsMap.keySet()) {
            stringBuilder.append(credentialsMap.get(key));
        }
        stringBuilder.append(appId);
        stringBuilder.append(apiKey);

        String preHashString = stringBuilder.toString();

        String signature = calculateSignatureForString(preHashString);

        return signature;
    }

    private static String calculateSignatureForString(String hashTarget) {
        String signature;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            md.update(hashTarget.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            signature = hashTarget;
        }
        return signature;
    }
}
