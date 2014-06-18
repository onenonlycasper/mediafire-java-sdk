import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.DeviceGetChangesResponse;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.SystemGetInfoResponse;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.*;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.RunnableApiGetRequest;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class DriverSessionTokenFarm {
    private static final String TAG = DriverSessionTokenFarm.class.getSimpleName();
    public static void main(String[] args) {
        String apiKey = "1ngvq4h5rn8om4at7u9884z9i3sbww44b923w5ee";
        String appId = "35";
        Configuration configuration = new Configuration();
        configuration.setApiKey(apiKey);
        configuration.setAppId(appId);
        configuration.setHttpConnectionTimeout(5000);
        configuration.setHttpReadTimeout(5000);
        configuration.setSessionTokensInBlockingQueueMinMax(1, 6);

        MediaFire mediaFire = MediaFire.newInstance(configuration);

        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put("email", "arkhivetest@test.com");
        credentials.put("password", "74107410");
        mediaFire.getApplicationCredentials().setUserCredentials(credentials);
        mediaFire.getApplicationCredentials().setCredentialsValid(true);
        mediaFire.startup();

        try {
            System.out.println(TAG + " SLEEPING 10 SECONDS");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Callback callback = new Callback();
        if (mediaFire == null) {
            System.out.println(TAG + " mediafire object is null");
        }

        if (mediaFire.apiCall() == null) {
            System.out.println(TAG + " mediafire.apiCall() is null");
        }

        if (mediaFire.apiCall().system == null) {
            System.out.println(TAG + " mediafire.apiCall().system is null");
        }

        System.out.println(TAG + " MAKING API CALL");
        Runnable runnable = mediaFire.apiCall().system.getInfo(callback, null, null);
        Thread thread = new Thread(runnable);
        thread.start();
        SystemGetInfoResponse response = mediaFire.apiCall().system.getInfo(null, null);
        System.out.println(response.getTermsOfService().getTerms());

        DeviceGetChangesResponse response2 = mediaFire.apiCall().device.getChanges(null, null);
    }

    public static class Callback implements ApiRequestRunnableCallback {

        @Override
        public void apiRequestProcessStarted() {
            System.out.println(TAG + " apiRequestProcessStarted");
        }

        @Override
        public void apiRequestProcessFinished(ApiRequestObject apiRequestObject) {
            System.out.println(TAG + " apiRequestProcessFinished");
            System.out.println(TAG + " http response code: " + apiRequestObject.getHttpResponseCode());
            System.out.println(TAG + " http response code: " + apiRequestObject.getHttpResponseString());
        }
    }
}
