package com.arkhive.components.core.module_token_farm.interfaces;

import com.arkhive.components.core.module_api_descriptor.ApiRequestObject;

/**
 * Created by  on 6/16/2014.
 */
public interface SessionTokenDistributor {
    public void borrowSessionToken(ApiRequestObject apiRequestObject);
    public void returnSessionToken(ApiRequestObject apiRequestObject);
}
