import com.arkhive.components.test_session_manager_fixes.module_credentials.ApplicationCredentials;
import com.arkhive.components.test_session_manager_fixes.module_credentials.CredentialsException;
import com.arkhive.components.test_session_manager_fixes.module_http_processor.HttpPeriProcessor;
import com.arkhive.components.test_session_manager_fixes.module_session_token.SessionToken;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarm;
import com.arkhive.components.test_session_manager_fixes.module_token_farm.TokenFarmException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 6/15/2014.
 */
public class DriverSessionTokenFarm {
    public static void main(String[] args) {
        String apiKey = "1ngvq4h5rn8om4at7u9884z9i3sbww44b923w5ee";
        String appId = "35";
        ApplicationCredentials applicationCredentials = null;
        try {
            applicationCredentials = new ApplicationCredentials(appId, apiKey);
        } catch (CredentialsException e) {
            e.printStackTrace();
            return;
        }
        HttpPeriProcessor httpPeriProcessor = new HttpPeriProcessor(5000, 5000);

        TokenFarm tokenFarm = TokenFarm.getInstance();
        if (tokenFarm == null) {
            try {
                tokenFarm = TokenFarm.newInstance(applicationCredentials, httpPeriProcessor);
            } catch (TokenFarmException e) {
                e.printStackTrace();
            }
        }

        Map<String, String> userCredentials = new LinkedHashMap<String, String>();
        userCredentials.put("email", "arkhivetest@test.com");
        userCredentials.put("password", "74107410");
        try {
            applicationCredentials.setUserCredentials(userCredentials);
        } catch (CredentialsException e) {
            e.printStackTrace();
        }
        if (tokenFarm != null) {
            for (int i = 0; i < 6; i++) {
                tokenFarm.getNewSessionToken();
            }
        }
    }
}
