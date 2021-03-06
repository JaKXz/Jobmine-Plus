package com.jobmineplus.mobile.activities.jbmnpls;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.gms.ads.AdView;
import com.jobmineplus.mobile.R;
import com.jobmineplus.mobile.exceptions.JbmnplsParsingException;
import com.jobmineplus.mobile.widgets.Job;
import com.jobmineplus.mobile.widgets.TabItemFragment;

public class Description extends JbmnplsPageActivityBase implements OnTouchListener {

    private static class TABS {
        public static String DESCRIPTION = " Description ";
        public static String DETAILS = "Details";
    }

    private Intent in;

    protected Job job;
    protected AdView adview;

    // ====================
    // Override Methods
    // ====================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.tabs_ads);
        super.onCreate(savedInstanceState);
        createTab(TABS.DESCRIPTION, JobDescription.newInstance());
        createTab(TABS.DETAILS, JobDetails.newInstance());
        setEmptyText(getString(R.string.description_no_data));
        adview = ((AdView)findViewById(R.id.adView));

        in = getIntent();
        int id = Integer.parseInt(in.getStringExtra("jobId"));
        if (id == 0) {
            throw new JbmnplsParsingException(
                    "Did not receive an id going here.");
        }
        job = jobDataSource.getJob(id);
        if (job == null) {
            throw new JbmnplsParsingException(
                    "This id does not have a job object");
        }
    }

    @Override
    public String getPageName() {
        return Description.class.getName();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setActivityResult(job.hasDescriptionData());
    }

    @Override
    public String getUrl() {
        return null;
    }

    private void setActivityResult(boolean success) {
        int result = success ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
        if (getParent() == null) {
            setResult(result, in);
        } else {
            getParent().setResult(result, in);
        }
    }

    @Override
    protected void requestData() throws RuntimeException {
        // If Job has description then load from data, if not then get it
        if (job.hasDescriptionData()) {
            fillInDescription();
        } else {
            ActionBar bar = getSupportActionBar();
            bar.setTitle(getString(R.string.description_getting_data));
            super.requestData();
        }
    }

    @Override
    protected String onRequestData(String[] args) throws IOException {
        String descriptionData = job.grabDescriptionData(client);
        if (descriptionData != null) {
            jobDataSource.addJob(job);      // updates with the description data
        }
        setActivityResult(true);
        return descriptionData;
    }

    @Override
    protected void parseWebpage(String html) {
        // Not needed because it is all done in onRequestData
    }

    @Override
    protected void onRequestComplete(boolean pullData) {
        fillInDescription();
    }

    @Override
    protected long doOffine() {
        // Not needed because we get the job from the database already
        return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Touch once and remove it
        v.setOnTouchListener(null);
        ((RelativeLayout)findViewById(R.id.grouplayout)).removeView(adview);;
        return false;
    }

    // =====================
    // Protected Methods
    // =====================
    protected void fillInDescription() {
        ActionBar bar = getSupportActionBar();
        String employer = job.getEmployerFullName() == "" ? job.getEmployerFullName() : job.getEmployer();
        bar.setSubtitle(Html.fromHtml(job.getTitle()));
        bar.setTitle(Html.fromHtml(employer));
        if (!job.hasDescriptionData()) {
            setIsEmpty(true);
        } else {
            ((TabItemFragment)getFragment(0)).invokeSetValues();
            ((TabItemFragment)getFragment(1)).invokeSetValues();
        }
    }

    public static final class JobDescription extends TabItemFragment {
        private LinearLayout layout;

        public static JobDescription newInstance() {
            return new JobDescription();
        }

        public JobDescription() {
            init(R.layout.job_description_content, new int[]{
                    R.id.description_layout,
                    R.id.warning,
                    R.id.scrollview
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((Description)getActivity()).createTab(TABS.DESCRIPTION, this);
        }

        public void appendText(String text) {
            Activity a = getActivity();
            if (!TextUtils.isEmpty(text)) {
                Character firstChar = text.charAt(0);
                Character lastChar = text.charAt(text.length() - 1);
                if (firstChar == '*' && lastChar == '*' || lastChar == ':') {
                    text = "<b>" + text;
                } else if (firstChar == '*' || firstChar == '-' && text.charAt(1) != '-') {
                    appendListItem(text.substring(1).trim());
                    return;
                } else if (isTitle(text)) {
                    text = "<b>" + text;
                }
            }
            TextView t = (TextView)a.getLayoutInflater().inflate(R.layout.template_description_text, null);
            setText(t, text);
            t.setLinkTextColor(getResources().getColor(R.color.details_link_text));
            Linkify.addLinks(t, Linkify.ALL);
            layout.addView(t);
        }

        public void appendDivider() {
            Activity a = getActivity();
            View ruler = new View(a);
            ruler.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 1);
            params.setMargins(0, 10, 0, 10);
            layout.addView(ruler, params);
        }

        public void appendListItem(String text) {
            Activity a = getActivity();
            LinearLayout l = (LinearLayout)a.getLayoutInflater().inflate(R.layout.template_list_item, null);
            TextView t = (TextView)l.findViewWithTag("text");
            setText(t, text);
            layout.addView(l);
        }

        private void setText(TextView v, String text) {
            text = text.replace("\\&quot;", "&quot;").replace("\\&#039;", "&#039;");
            v.setText(Html.fromHtml(text));
        }

        private boolean isTitle(String text) {
            // Count number of spaces
            int start = 0;
            int num = 0;
            while (start < text.length() && start != -1) {
                start = text.indexOf(' ', start + 1);
                if (start != -1) {
                    num++;
                }
            }
            return num < 10 && !text.contains(".") && !text.contains(":") && !text.contains(",");
        }

        @Override
        public void setValues(View[] views) {
            layout = (LinearLayout)views[0];
            Job job = ((Description)getActivity()).job;

            if (!job.hasDescriptionData()) {
                return;
            }

            // Show the warning if it exists
            String warning = job.getDescriptionWarning();
            if (!warning.equals("")) {
                setText(((TextView)views[1]), warning);
                appendDivider();
            } else {
                views[1].setVisibility(View.GONE);
            }

            // Parse the description
            String[] lines = job.getDescription().split("\n");

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.matches("-++") || line.matches("_++")) {
                    appendDivider();

                    // If next line is an empty space, skip it
                    if (i + 1 < lines.length &&TextUtils.isEmpty(lines[i+1].trim())) {
                        i++;
                    }
                } else {
                    appendText(line);
                }
            }

            // Set the scrollview event
            views[2].setOnTouchListener(((Description)getActivity()));
        }
    }

    public static final class JobDetails extends TabItemFragment {
        public static JobDetails newInstance() {
            return new JobDetails();
        }

        public JobDetails() {
            init(R.layout.job_description_details, new int[]{
                    R.id.grades,
                    R.id.openings,
                    R.id.location,
                    R.id.date_range,
                    R.id.work_term,
                    R.id.hiring_support,
                    R.id.discplines,
                    R.id.levels,
                    R.id.details_layout
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((Description)getActivity()).createTab(TABS.DETAILS, this);
        }

        @Override
        public void setValues(View[] views) {
            Job job = ((Description)getActivity()).job;

            if (!job.hasDescriptionData()) {
                return;
            }

            // Grades
            if (!job.areGradesRequired()) {
                views[0].setVisibility(View.GONE);
            }

            // Opennings
            int opennings = job.getNumberOfOpenings();
            ((TextView)views[1]).setText((opennings == 0 ? "No" : opennings) + " Openning"
                    + (opennings == 1 ? "" : "s"));

            // Location
            ((TextView)views[2]).setText(job.getLocation());

            // Dates
            String openingDate = DISPLAY_DATE_FORMAT.format(job.getOpenDateToApply());
            String lastDate = DISPLAY_DATE_FORMAT.format(job.getLastDateToApply());

            if (openingDate.equals(lastDate)) {
                ((TextView)views[3]).setText(getString(R.string.description_no_dates));
            } else {
                ((TextView)views[3]).setText(openingDate + " - " + lastDate);
            }

            // Work Support
            ((TextView)views[4]).setText(job.getWorkSupportName());

            // Hiring Support
            ((TextView)views[5]).setText(job.getHiringSupportName());

            // Disciplines
            ((TextView)views[6]).setText(job.getDisciplinesAsString("\n"));

            // Levels
            ((TextView)views[7]).setText(job.getLevelsAsString("\n"));

            // Set the scrollview event
            views[8].setOnTouchListener(((Description)getActivity()));
        }
    }
}
