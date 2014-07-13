package com.arkhive.components.core.module_errors;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;

/**
 * Created by  on 7/2/2014.
 */
public interface ErrorTracker {
    // debug
    public void d(String src, String msg);
    public void d(String src, String msg, Throwable tr);
    // error
    public void e(String src, String msg);
    public void e(String src, Exception e);
    public void e(String src, String msg, Throwable tr);
    public void e(String src, String shortDescription, String fullDescription, int classId, int id);
    // info
    public void i(String src, String msg);
    public void i(String src, String msg, Throwable tr);
    // verbose
    public void v(String src, String msg);
    public void v(String src, String msg, Throwable tr);
    // warnings
    public void w(String src, String msg);
    public void w(String src, String msg, Throwable tr);
    public void w(String src, Exception e);
    // api error
    public void apiError(String src, ApiRequestObject object);

}
