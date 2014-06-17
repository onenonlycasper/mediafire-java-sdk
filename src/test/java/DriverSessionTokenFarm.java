import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.*;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.interfaces.ApiRequestRunnableCallback;
import com.arkhive.components.test_session_manager_fixes.module_api_descriptor.requests.ApiRequestRunnable;
import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.exceptions.TokenFarmException;

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
        ApplicationCredentials applicationCredentials = new ApplicationCredentials(appId, apiKey);

        Map<String, String> userCredentials = new LinkedHashMap<String, String>();
        userCredentials.put("email", "arkhivetest@test.com");
        userCredentials.put("password", "74107410");
        applicationCredentials.setUserCredentials(userCredentials);

        HttpPeriProcessor httpPeriProcessor = new HttpPeriProcessor(5000, 5000);

        TokenFarm tokenFarm = new TokenFarm(applicationCredentials, httpPeriProcessor);

        MyGoodRunnable goodRunnable = new MyGoodRunnable(tokenFarm, httpPeriProcessor);
        MyBadRunnable badRunnable = new MyBadRunnable(tokenFarm, httpPeriProcessor);
        Thread goodThread = new Thread(goodRunnable);
        Thread badThread = new Thread(badRunnable);
        goodThread.start();
        badThread.start();

    }

    public static class MyGoodRunnable implements Runnable {
        private final TokenFarm tokenFarm;
        private final HttpPeriProcessor httpPeriProcessor;
        public MyGoodRunnable(TokenFarm tokenFarm, HttpPeriProcessor httpPeriProcessor) {
            this.tokenFarm = tokenFarm;
            this.httpPeriProcessor = httpPeriProcessor;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                TheCallbackKing callback = new TheCallbackKing();
                ApiRequestObject apiRequestObject = new ApiRequestObject("http://www.mediafire.com", "/api/folder/get_info.php");
                LinkedHashMap<String, String> optionalParameters = new LinkedHashMap<String, String>();
                optionalParameters.put("response_format", "json");
                apiRequestObject.setOptionalParameters(optionalParameters);
                ApiRequestRunnable apiRequestRunnable = new ApiRequestRunnable(callback, new ApiRequestHttpPreProcessor(), new ApiRequestHttpPostProcessor(), tokenFarm, httpPeriProcessor, apiRequestObject);
                Thread thread = new Thread(apiRequestRunnable);
                thread.start();
            }
        }
    }

    public static class MyBadRunnable implements Runnable {

        private final TokenFarm tokenFarm;
        private final HttpPeriProcessor httpPeriProcessor;

        public MyBadRunnable(TokenFarm tokenFarm, HttpPeriProcessor httpPeriProcessor) {
            this.tokenFarm = tokenFarm;
            this.httpPeriProcessor = httpPeriProcessor;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                TheCallbackKing callback = new TheCallbackKing();
                ApiRequestObject apiRequestObject = new ApiRequestObject("http://www.mediafire.com", "/api/folder/get_info.php");
                LinkedHashMap<String, String> optionalParameters = new LinkedHashMap<String, String>();
                optionalParameters.put("response_format", "json");
                apiRequestObject.setOptionalParameters(optionalParameters);
                LinkedHashMap<String, String> requiredParameters = new LinkedHashMap<String, String>();
                optionalParameters.put("folder_key", "asdfasdfasdfasdfasdfasdf");
                apiRequestObject.setRequiredParameters(requiredParameters);
                ApiRequestRunnable apiRequestRunnable = new ApiRequestRunnable(callback, new ApiRequestHttpPreProcessor(), new ApiRequestHttpPostProcessor(), tokenFarm, httpPeriProcessor, apiRequestObject);
                Thread thread = new Thread(apiRequestRunnable);
                thread.start();
            }
        }
    }

    public static class TheCallbackKing implements ApiRequestRunnableCallback {
        private final String TAG = TheCallbackKing.class.getSimpleName();
        @Override
        public void apiRequestProcessStarted() {
            System.out.println(TAG + " apiRequestProcessStarted()");
        }

        @Override
        public void apiRequestProcessFinished(ApiRequestObject apiRequestObject) {
            System.out.println(TAG + " apiRequestProcessFinished()");
        }
    }
}
