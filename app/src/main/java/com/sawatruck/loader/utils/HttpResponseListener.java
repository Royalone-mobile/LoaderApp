package com.sawatruck.loader.utils;

/**
 * Created by SKY on 9/19/2017.
 */

public interface HttpResponseListener {
    public void OnSuccess(Object response);
    public void OnFailure(Object error);
}
