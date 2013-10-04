package com.jobmineplus.mobile.debug;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jobmineplus.mobile.R;
import com.jobmineplus.mobile.activities.jbmnpls.JobSearch;

public class DebugJobSearch extends JobSearch {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Job Search (Debug)");
    }

    @Override
    protected boolean verifyLogin() {
        if (DebugHomeActivity.debugLocalhost) {
            return true;
        }
        return super.verifyLogin();
    }

    protected boolean isReallyOnline() {
        if (DebugHomeActivity.debugLocalhost) {
            return isOnline() && isNetworkConnected();
        } else {
            return super.isReallyOnline();
        }
    }

    //====================
    //  Localhost Menu
    //====================
    @Override
    protected int getMenuId() {
        return R.menu.debug_main_menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuitem_localhost) {
            DebugHomeActivity.debugLocalhost = !DebugHomeActivity.debugLocalhost;
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem localhostBtn = menu.findItem(R.id.menuitem_localhost);
        localhostBtn.setTitle(getString(DebugHomeActivity.debugLocalhost
                ? R.string.menuitem_online : R.string.menuitem_localhost));
        return super.onPrepareOptionsMenu(menu);
    }
}