import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api.responses.ApiResponse;
import com.arkhive.components.core.module_api_descriptor.interfaces.ApiRequestRunnableCallback;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/18/2014.
 */
public class DriverActionTokenFunctionalityDoNotDelete {
    private static final String TAG = DriverActionTokenFunctionalityDoNotDelete.class.getSimpleName();
    static volatile int errors = 0;
    static volatile int signatureErrors = 0;
    static volatile int sessionTokenErrors = 0;

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
        credentials.put("email", "javasdktest@example.com");
        credentials.put("password", "74107410");
        mediaFire.getApplicationCredentials().setUserCredentials(credentials);
        mediaFire.getApplicationCredentials().setCredentialsValid(true);
        mediaFire.startup();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(TAG + " MAKING API CALLS");
        for (int i = 0; i < 6; i++) {
            System.out.println("loop: " + i);
            Map<String, String> params = new LinkedHashMap<String, String>();
//            params.put("filename", "test_" + i + ".jpg");
//            params.put("hash", "d63c288c572865309fb4da37b4c9874181eb69459643203f4a8937603d25f529");
//            params.put("size", "1210118");
            params.put("filename", "test_" + i + ".jpg");
            params.put("hash", "f521fce6fff2aba6b29f7c60b32d00677cea19206057c04910c2e4cc4af12d22");
            params.put("size", "8917");
            new Thread(mediaFire.apiCall().upload.instantUpload(new GenericCallback(), params, null)).start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class GenericCallback implements ApiRequestRunnableCallback {
        @Override
        public void apiRequestProcessStarted() {
            System.out.println("apiRequestProcessStarted()");
        }
        @Override
        public void apiRequestProcessFinished(ApiResponse gsonResponse) {
            System.out.println("apiRequestProcessFinished()");
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
