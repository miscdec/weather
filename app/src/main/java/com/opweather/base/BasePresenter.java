package com.opweather.base;

/**
 * Created by lyh on 3/14.
 */

public class BasePresenter<T extends IBaseView> implements IBasePresenter<T> {

    public T rootView;

    public BasePresenter(T rootView) {
        this.rootView = rootView;
    }

    @Override
    public void attachView(T mRootView) {
        rootView = mRootView;
    }

    @Override
    public void detachView() {

    }
}
