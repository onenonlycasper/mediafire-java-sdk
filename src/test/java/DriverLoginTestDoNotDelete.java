import com.arkhive.components.core.Configuration;
import com.arkhive.components.core.MediaFire;
import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;
import com.arkhive.components.core.module_token_farm.interfaces.GetNewSessionTokenCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/24/2014.
 */
public class DriverLoginTestDoNotDelete {
    private static final String TAG = DriverLoginTestDoNotDelete.class.getSimpleName();
    public static void main(String[] args) {
        String apiKey = "1ngvq4h5rn8om4at7u9884z9i3sbww44b923w5ee";
        String appId = "35";
        Configuration configuration = new Configuration();
        configuration.setApiKey(apiKey);
        configuration.setAppId(appId);
        configuration.setHttpConnectionTimeout(5000);
        configuration.setHttpReadTimeout(5000);
        configuration.setSessionTokensInBlockingQueueMinMax(1, 6);

        final MediaFire mediaFire = MediaFire.newInstance(configuration);

        Map<String, String> credentials = new HashMap<String, String>();
        credentials.put("email", "javasdktest@example.com");
        credentials.put("password", "74107410");
        mediaFire.getApplicationCredentials().setUserCredentials(credentials);

        mediaFire.tryLogin(new GetNewSessionTokenCallback() {
            @Override
            public void receiveNewSessionToken(ApiRequestObject apiRequestObject) {
                if (!apiRequestObject.getApiResponse().hasError()) {
                    mediaFire.getApplicationCredentials().setCredentialsValid(true);
                    mediaFire.startup();
                } else {
                    System.out.println(TAG + "error: " + apiRequestObject.getApiResponse().getError());
                    System.out.println(TAG + "message: " + apiRequestObject.getApiResponse().getMessage());
                    System.out.println(TAG + "result: " + apiRequestObject.getApiResponse().getResult());
                    System.out.println(TAG + "action: " + apiRequestObject.getApiResponse().getAction());
                }
            }
        });
    }
}
