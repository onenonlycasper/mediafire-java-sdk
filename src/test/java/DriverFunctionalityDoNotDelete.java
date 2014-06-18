import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.*;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class DriverFunctionalityDoNotDelete {
    private static final String TAG = DriverFunctionalityDoNotDelete.class.getSimpleName();
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

        System.out.println(TAG + " MAKING API CALLS");
        SystemGetInfoResponse call1 = mediaFire.apiCall().system.getInfo(null, null);
        DeviceGetStatusResponse call2 = mediaFire.apiCall().device.getStatus(null, null);
        FolderGetContentsResponse call3 = mediaFire.apiCall().folder.getContents(null, null);
        FolderGetInfoResponse call4 = mediaFire.apiCall().folder.getInfo(null, null);
        Callback userGetInfoCallback = new Callback();

        Runnable userGetInfoRunnable = mediaFire.apiCall().user.getInfo(userGetInfoCallback, null, null);
        Thread thread = new Thread(userGetInfoRunnable);
        thread.start();
    }

    public static class Callback implements ApiRequestRunnableCallback<UserGetInfoResponse> {
        @Override
        public void apiRequestProcessStarted() {
            System.out.println(TAG + " apiRequestProcessStarted");
        }

        @Override
        public void apiRequestProcessFinished(UserGetInfoResponse apiResponse) {
            System.out.println(TAG + " apiRequestProcessFinished");
            UserGetInfoResponse response = (UserGetInfoResponse) apiResponse;
            System.out.println(response.getUserInfo().getDisplayName());
            System.out.println(response.getUserInfo().getEmail());
        }

    }
}
