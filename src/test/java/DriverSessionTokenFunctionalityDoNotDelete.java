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
public class DriverSessionTokenFunctionalityDoNotDelete {
    static volatile int errors = 0;
    static volatile int signatureErrors = 0;
    static volatile int sessionTokenErrors = 0;

    private static final String TAG = DriverSessionTokenFunctionalityDoNotDelete.class.getSimpleName();
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

        Map<String, String> params = new LinkedHashMap<String, String>(); params.put("revision", "500");
        new LinkedHashMap<String, String>().put("revision","500");
        System.out.println(TAG + " MAKING API CALLS");
        for (int i = 0; i < 50; i++) {
            new Thread(mediaFire.apiCall().folder.getInfo(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().user.getInfo(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().folder.getContents(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().device.getStatus(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().system.getInfo(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().contact.fetchContacts(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().folder.getRevision(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().user.getAvatar(new GenericCallback(), null, null)).start();
            new Thread(mediaFire.apiCall().device.getChanges(new GenericCallback(), params, null)).start();
            new Thread(mediaFire.apiCall().folder.search(new GenericCallback(), null, null)).start();
        }
    }

    public static class GenericCallback implements ApiRequestRunnableCallback{
        @Override
        public void apiRequestProcessStarted() { }
        @Override
        public void apiRequestProcessFinished(ApiResponse gsonResponse) {
            if (gsonResponse.hasError()) {
                errors++;
            }

            if (gsonResponse.getError() == 105) {
                sessionTokenErrors++;
            }

            if (gsonResponse.getError() == 127) {
                signatureErrors++;
            }
            System.out.println("Total Errors:         " + errors);
            System.out.println("Session Token Errors: " + sessionTokenErrors);
            System.out.println("Signature Errors:     " + signatureErrors);
        }
    }
}
