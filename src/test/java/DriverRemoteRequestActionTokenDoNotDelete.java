import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.MediaFire;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/24/2014.
 */
public class DriverRemoteRequestActionTokenDoNotDelete {
    private static final String TAG = DriverRemoteRequestActionTokenDoNotDelete.class.getSimpleName();
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
        System.out.println(TAG + " startup");
        mediaFire.startup();

        System.out.println(TAG + " requesting some tokens");
        System.out.println(TAG + " requested image action token: " + mediaFire.apiCall().requestImageActionToken());
        System.out.println(TAG + " requested upload action token: " + mediaFire.apiCall().requestUploadActionToken());
    }
}
