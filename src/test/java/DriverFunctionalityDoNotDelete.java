import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api.responses.*;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class DriverFunctionalityDoNotDelete {
    static int errors = 0;
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
        for (int i = 0; i < 5; i++) {
            Map<String, String> params = new LinkedHashMap<String, String>(); params.put("revision", "500");
            Runnable deviceGetChangesRunnable = mediaFire.apiCall().device.getChanges(new DeviceGetChangesCallback(), params, null);
            Runnable systemGetInfoRunnable = mediaFire.apiCall().system.getInfo(new SystemGetInfoCallback(), null, null);
            Runnable deviceGetStatusRunnable = mediaFire.apiCall().device.getStatus(new DeviceGetStatusCallback(), null, null);
            Runnable folderGetContentsRunnable = mediaFire.apiCall().folder.getContents(new FolderGetContentsCallback(), null, null);
            Runnable folderGetInfoRunnable = mediaFire.apiCall().folder.getInfo(new FolderGetInfoCallback(), null, null);
            Runnable userGetInfoRunnable = mediaFire.apiCall().user.getInfo(new UserGetInfoCallback(), null, null);
            Thread thread1 = new Thread(folderGetInfoRunnable);
            Thread thread2 = new Thread(userGetInfoRunnable);
            Thread thread3 = new Thread(folderGetContentsRunnable);
            Thread thread4 = new Thread(deviceGetStatusRunnable);
            Thread thread5 = new Thread(systemGetInfoRunnable);
            Thread thread6 = new Thread(deviceGetChangesRunnable);

            thread1.start();
            thread2.start();
            thread3.start();
            thread4.start();
            thread5.start();
            thread6.start();
        }
    }

    public static class DeviceGetChangesCallback implements ApiRequestRunnableCallback<DeviceGetChangesResponse> {
        @Override
        public void apiRequestProcessStarted() { }
        @Override
        public void apiRequestProcessFinished(DeviceGetChangesResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                System.out.println("ERROR FOUND: " + gsonResponse.getError() + ", " + gsonResponse.getMessage());
                errors++;
            }
        }
    }
    public static class SystemGetInfoCallback implements ApiRequestRunnableCallback<SystemGetInfoResponse> {
        @Override
        public void apiRequestProcessStarted() { }
        @Override
        public void apiRequestProcessFinished(SystemGetInfoResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                System.out.println("ERROR FOUND: " + gsonResponse.getError() + ", " + gsonResponse.getMessage());
                errors++;
            }
        }
    }

    public static class DeviceGetStatusCallback implements ApiRequestRunnableCallback<DeviceGetStatusResponse> {
        @Override
        public void apiRequestProcessStarted() { }
        @Override
        public void apiRequestProcessFinished(DeviceGetStatusResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                System.out.println("ERROR FOUND: " + gsonResponse.getError() + ", " + gsonResponse.getMessage());
                errors++;
            }
        }
    }

    public static class FolderGetContentsCallback implements ApiRequestRunnableCallback<FolderGetContentsResponse> {
        @Override
        public void apiRequestProcessStarted() {}
        @Override
        public void apiRequestProcessFinished(FolderGetContentsResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                System.out.println("ERROR FOUND: " + gsonResponse.getError() + ", " + gsonResponse.getMessage());
                errors++;
            }
        }
    }
    public static class UserGetInfoCallback implements ApiRequestRunnableCallback<UserGetInfoResponse> {
        @Override
        public void apiRequestProcessStarted() { }
        @Override
        public void apiRequestProcessFinished(UserGetInfoResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                System.out.println("ERROR FOUND: " + gsonResponse.getError() + ", " + gsonResponse.getMessage());
                errors++;
            }
        }
    }

    public static class FolderGetInfoCallback implements ApiRequestRunnableCallback<FolderGetInfoResponse> {
        @Override
        public void apiRequestProcessStarted() {}
        @Override
        public void apiRequestProcessFinished(FolderGetInfoResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                System.out.println("ERROR FOUND: " + gsonResponse.getError() + ", " + gsonResponse.getMessage());
                errors++;
            }
        }
    }
}
