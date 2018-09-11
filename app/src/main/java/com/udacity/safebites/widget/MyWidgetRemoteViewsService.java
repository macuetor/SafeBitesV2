package com.udacity.safebites.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MyWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}