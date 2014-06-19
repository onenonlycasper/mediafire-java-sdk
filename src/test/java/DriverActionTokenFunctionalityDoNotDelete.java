import com.arkhive.components.test_session_manager_fixes.Configuration;
import com.arkhive.components.test_session_manager_fixes.MediaFire;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.ApiRequestObject;

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
        credentials.put("email", "arkhivetest@test.com");
        credentials.put("password", "74107410");
        mediaFire.getApplicationCredentials().setUserCredentials(credentials);
        mediaFire.getApplicationCredentials().setCredentialsValid(true);
        mediaFire.startup();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
