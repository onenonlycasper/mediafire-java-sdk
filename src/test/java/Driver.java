import com.mediafire.sdk.http.MFApi;
import com.mediafire.sdk.http.MFHost;
import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.config.MFCredentials;
import com.mediafire.sdk.http.MFHttpClient;
import com.mediafire.sdk.http.MFHttpRequest;
import com.mediafire.sdk.http.MFHttpResponse;
import com.mediafire.sdk.token.MFTokenFarm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class Driver {
    public static void main(String[] args) {
        Driver driver = new Driver();
        driver.systemGetInfo();
        driver.userGetSessionToken();
        MFConfiguration.MFConfigurationBuilder mfConfigurationBuilder = new MFConfiguration.MFConfigurationBuilder("35", "1ngvq4h5rn8om4at7u9884z9i3sbww44b923w5ee");
        MFConfiguration mfConfiguration = mfConfigurationBuilder.build();
        Map<String, String> userCredentials = new LinkedHashMap<String, String>();
        userCredentials.put("email", "javasdktest@example.com");
        userCredentials.put("password", "74107410");
        MFCredentials mfCredentials =mfConfiguration.getMfCredentials();
        mfCredentials.setCredentials(userCredentials);
        MFTokenFarm mfTokenFarm = new MFTokenFarm(mfConfiguration);
        mfTokenFarm.getNewSessionToken();
    }

    public void systemGetInfo() {
        System.out.println("\n\nSYSTEM/GET_INFO");
        MFHttpClient mfHttpClient = new MFHttpClient(null);
        MFHttpRequest request = new MFHttpRequest(MFHost.LIVE_HTTP, MFApi.SYSTEM_GET_INFO);
        MFHttpResponse response = mfHttpClient.sendRequest(request);
        System.out.println("status: " + response.getStatus());
        System.out.println("headers ");
        for (String key : response.getHeaders().keySet()) {
            System.out.println(key + " - " + response.getHeaders().get(key).toString());
        }
        System.out.println("body - " + response.getResponseString());
        System.out.println("body bytes");
        for (byte b : response.getBodyBytes()) {
            System.out.print(b);
        }
    }

    public void userGetSessionToken() {
        System.out.println("\n\nUSER/GET_SESSION_TOKEN");
        MFHttpClient mfHttpClient = new MFHttpClient(null);
        Map<String, String> requestParameters = new LinkedHashMap<String, String>();
        requestParameters.put("email", "javasdktest@example.com");
        requestParameters.put("password", "74107410");
        requestParameters.put("application_id", "35");
        requestParameters.put("signature", "30abbbd4a3f8827d1a6408f1f2ee20d5edcc4799");
        requestParameters.put("token_version", "2");
        MFHttpRequest request = new MFHttpRequest(MFHost.LIVE_HTTP, MFApi.USER_GET_SESSION_TOKEN, requestParameters);
        MFHttpResponse response = mfHttpClient.sendRequest(request);
        System.out.println("status: " + response.getStatus());
        System.out.println("headers ");
        for (String key : response.getHeaders().keySet()) {
            System.out.println(key + " - " + response.getHeaders().get(key).toString());
        }
        System.out.println("body - " + response.getResponseString());
        System.out.println("body bytes");
        for (byte b : response.getBodyBytes()) {
            System.out.print(b);
        }
    }
}
