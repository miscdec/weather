package com.opweather.base;

/**
 * Created by lyh on 3/13.
 */

public interface IBasePresenter<V extends IBaseView> {


    void attachView(V mRootView);

    void detachView();

}
