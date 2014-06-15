import com.arkhive.components.test_session_manager_fixes.layer_http.HttpLayer;
import com.arkhive.components.test_session_manager_fixes.layer_http.HttpPreProcessor;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiPostRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;

import java.util.HashMap;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class DriverHttpPostRequest {
    public static void main(String[] args) {
        System.out.println();
        System.out.println("doPostRequestSystemGetInfo");
        doPostRequestSystemGetInfo();
    }

    private static void doPostRequestSystemGetInfo() {
        ApiPostRequestObject apiPostRequestObject = new ApiPostRequestObject();
        String uri = "/api/system/get_info.php";
        apiPostRequestObject.setUri(uri);

        String domain = "http://www.mediafire.com";
        apiPostRequestObject.setDomain(domain);

        HashMap<String, String> optionalParameters = new HashMap<String, String>();
        optionalParameters.put("response_format", "json");
        optionalParameters.put("quick_key", "wl88kcc0k0xvj");
        apiPostRequestObject.setOptionalParameters(optionalParameters);

        HashMap<String, String> requiredParameters = new HashMap<String, String>();
        apiPostRequestObject.setRequiredParameters(requiredParameters);

        SessionToken sessionToken = SessionToken.newInstance("a test session token");
        sessionToken.setTokenString("123456798795646541321657987651321654984651");
        sessionToken.setTokenSignature("0123456789012345678901234567890123456789");
        apiPostRequestObject.setToken(sessionToken);
        HttpPreProcessor httpGetPreProcessor = new HttpPreProcessor(apiPostRequestObject);

        httpGetPreProcessor.processApiRequestObject();

        HttpLayer httpLayer = new HttpLayer();
        apiPostRequestObject = httpLayer.sendPostRequest(apiPostRequestObject);

        printResults(apiPostRequestObject);
    }

    private static void printResults(ApiRequestObject apiPostRequestObject) {
        System.out.println(apiPostRequestObject.getDomain());
        System.out.println(apiPostRequestObject.getUri());
        if (apiPostRequestObject.getApiResponse() != null) {
            System.out.println(apiPostRequestObject.getApiResponse());
        } else {
            System.out.println("no api response generated");
        }
        System.out.println("original domain: " + apiPostRequestObject.getDomain());
        System.out.println("original uri: " + apiPostRequestObject.getUri());
        System.out.println("original url: " + apiPostRequestObject.getConstructedUrl());
        System.out.println("response code: " + apiPostRequestObject.getHttpResponseCode());
        System.out.println("response string: " + apiPostRequestObject.getHttpResponseString());
        if (apiPostRequestObject.getExceptionsDuringRequest() != null) {
            System.out.println("exceptions during request...");
            for (Exception exception : apiPostRequestObject.getExceptionsDuringRequest()) {
                System.out.println("exception: " + exception);
            }
        } else {
            System.out.println("no exceptions during request");
        }
        if (apiPostRequestObject.getRequiredParameters() != null) {
            System.out.println("required parameters passed during request...");
            HashMap<String, String> params = apiPostRequestObject.getRequiredParameters();
            for (String key : params.keySet()) {
                System.out.println("key=" + key + ", value=" + params.get(key));
            }
        } else {
            System.out.println("no required parameters during request");
        }

        if (apiPostRequestObject.getOptionalParameters() != null) {
            System.out.println("optional parameters passed during request...");
            HashMap<String, String> params = apiPostRequestObject.getOptionalParameters();
            for (String key : params.keySet()) {
                System.out.println("key=" + key + ", value=" + params.get(key));
            }
        } else {
            System.out.println("no optional parameters during request");
        }

        if (apiPostRequestObject.getToken() != null) {
            System.out.println("session/action token: " + apiPostRequestObject.getToken().getTokenString());
        } else {
            System.out.println("no session/action token passed during request");
        }

        if (apiPostRequestObject.getToken() != null) {
            System.out.println("signature: " + apiPostRequestObject.getToken().getTokenSignature());
        } else {
            System.out.println("no signature passed during request");
        }
    }
}
