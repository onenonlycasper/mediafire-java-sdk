package com.arkhive.components.core.module_errors;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;

/**
 * Created by Chris Najar on 7/2/2014.
 */
public interface ErrorTracker {
    public void trackApiError(String source, ApiRequestObject object);
    public void trackError(String source, int classId, int id, String shortDescription, String fullDescription);
    public void trackError(String source, Exception e);
}
