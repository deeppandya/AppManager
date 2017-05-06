package com.deeppandya.appmanager.listeners;

import com.deeppandya.appmanager.model.AppModel;

import java.util.List;

/**
 * Created by deeppandya on 2017-05-06.
 */

public interface GetAppsListener {
    void beforeGetApps();
    void afterGetApps(List<AppModel> appModels);
    void onError();
}
