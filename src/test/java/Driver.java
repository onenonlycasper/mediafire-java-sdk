import com.mediafire.sdk.config.MFConfiguration;
import com.mediafire.sdk.config.MFCredentials;
import com.mediafire.sdk.token.MFTokenFarm;
import com.mediafire.sdk.util.MFGenericCallback;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Chris Najar on 7/16/2014.
 */
public class Driver {
    public static void main(String[] args) {
        Driver driver = new Driver();

        MFConfiguration.MFConfigurationBuilder mfConfigurationBuilder = new MFConfiguration.MFConfigurationBuilder("35", "1ngvq4h5rn8om4at7u9884z9i3sbww44b923w5ee");
        MFConfiguration mfConfiguration = mfConfigurationBuilder.build();

        Map<String, String> userCredentials = new LinkedHashMap<String, String>();
        userCredentials.put("email", "javasdktest@example.com");
        userCredentials.put("password", "74107410");
        MFCredentials mfCredentials =mfConfiguration.getMfCredentials();
        mfCredentials.setCredentials(userCredentials);


        MFTokenFarm mfTokenFarm = new MFTokenFarm(mfConfiguration);

        Callback callback = driver.new Callback();

        mfTokenFarm.startup(callback);
    }

    public class Callback implements MFGenericCallback<Void> {

        @Override
        public void jobStarted() {
            System.out.println("job started");
        }

        @Override
        public void jobFinished(Void aVoid) {
            System.out.println("job finished");
        }
    }
}
