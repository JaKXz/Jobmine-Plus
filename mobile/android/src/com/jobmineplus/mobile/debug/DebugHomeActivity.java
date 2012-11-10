package com.jobmineplus.mobile.debug;

import android.view.View;
import android.widget.Button;

import com.jobmineplus.mobile.activities.HomeActivity;
import com.jobmineplus.mobile.database.JobDataSource;
import com.jobmineplus.mobile.widgets.Job;

public class DebugHomeActivity extends HomeActivity implements Debuggable{
    @Override
    public void onClick(View arg0) {
        String name = ((Button) arg0).getText().toString();
        goToActivity(".debug.activities.Debug" + name);
    }
}
