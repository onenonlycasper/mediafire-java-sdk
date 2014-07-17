import com.mediafire.sdk.*;

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
    }

    public void systemGetInfo() {
        System.out.println("\n\nSYSTEM/GET_INFO");
        MFHttpClient mfHttpClient = new MFHttpClient(null);
        MFRequest request = new MFRequest(MFHost.LIVE_HTTP, MFApi.URI_SYSTEM_GET_INFO);
        MFResponse response = mfHttpClient.sendRequest(request);
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
        MFRequest request = new MFRequest(MFHost.LIVE_HTTP, MFApi.URI_USER_GET_SESSION_TOKEN, requestParameters);
        MFResponse response = mfHttpClient.sendRequest(request);
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
