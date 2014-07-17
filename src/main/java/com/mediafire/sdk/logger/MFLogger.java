package com.mediafire.sdk.logger;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;

/**
 * Created by  on 7/2/2014.
 */
public interface MFLogger {
    public void logMessage(String src, String msg);
    public void logException(String src, Exception e);
    public void logApiError(String src, ApiRequestObject object);

}
