package com.jobmineplus.mobile.activities.jbmnpls;

import org.jsoup.nodes.Document;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.maps.MapView;
import com.jobmineplus.mobile.R;
import com.jobmineplus.mobile.activities.tab.DescriptionTabMapActivity;
import com.jobmineplus.mobile.exceptions.JbmnplsParsingException;
import com.jobmineplus.mobile.widgets.Job;

public class Description extends JbmnplsTabActivityBase{
	
	private static class LISTS {
		public static String DESCRIPTION = "description";
		public static String DETAILS = "details";
		public static String MAP = "map";
	}
	
	protected Job job;
	
	//===============
	// 	Ui Objects 
	//===============
	TextView employer;
	TextView title;
	TextView openings;
	TextView location;
	TextView levels;
	TextView grades;
	TextView warning;
	TextView openDate;
	TextView closedDate;
	TextView hiringSupport;
	TextView worktermSupport;
	TextView disciplines;
	TextView description;
	
	ScrollView descriptionLayout;
	ScrollView detailsLayout;
	
	FrameLayout container;
	
	MapView map;
	
	//====================
	// 	Override Methods
	//====================
	
	@Override
	protected String setUp(Bundle savedInstanceState) throws JbmnplsParsingException{
		setContentView(R.layout.job_description);
		int id = Integer.parseInt(getIntent().getStringExtra("jobId"));
		if (id == 0) {
			throw new JbmnplsParsingException("Did not receive an id going here.");
		}
		job = app.getJob(id);
		if (job == null) {
			throw new JbmnplsParsingException("This id does not have a job object");
		}
		return "";		//Can be anything because we override onRequestData()
	}
	
	@Override
	protected void defineUI(Bundle savedInstanceState) {
		super.defineUI(savedInstanceState);
		container = (FrameLayout) findViewById(android.R.id.tabcontent);
		
		// Description Tab
		employer = 			(TextView) findViewById(R.id.employer);
		title = 			(TextView) findViewById(R.id.title);
		location = 			(TextView) findViewById(R.id.location);
		openings = 			(TextView) findViewById(R.id.openings);
		grades = 			(TextView) findViewById(R.id.grades);
		warning = 			(TextView) findViewById(R.id.warning);
		description = 		(TextView) findViewById(R.id.description);
		descriptionLayout = (ScrollView) findViewById(R.id.description_layout);
		
		// Details Tab
		levels = 			(TextView) findViewById(R.id.levels);
		openDate = 			(TextView) findViewById(R.id.open_date);
		closedDate = 		(TextView) findViewById(R.id.last_day);
		hiringSupport = 	(TextView) findViewById(R.id.hiring_support);
		worktermSupport = 	(TextView) findViewById(R.id.work_term);
		disciplines = 		(TextView) findViewById(R.id.discplines);
		detailsLayout = 	(ScrollView) findViewById(R.id.details_layout);

		// Make tabs
		createTab(LISTS.DESCRIPTION, "Description");
		createTab(LISTS.DETAILS, "Details");
		createTab(LISTS.MAP, "Map", DescriptionTabMapActivity.class, false);
	}

	@Override
	public View onTabSwitched(String tag) {
		if (tag == LISTS.DESCRIPTION) {
			return descriptionLayout;
		} else if (tag == LISTS.DETAILS) {
			return detailsLayout;
		} else {
			return null;
		}
	}
	
	@Override
	protected String onRequestData(String[] args) {
		container.setVisibility(View.INVISIBLE);
		return job.grabDescriptionData();
	}
	
	@Override
	protected Object parseWebpage(Document doc) {
		// Not needed because it is all done in onRequestData
		return null;
	}	
	
	@Override
	protected void onRequestComplete(Object arg) {
		fillInDescription();
		container.setVisibility(View.VISIBLE);
	}
	
	//=====================
	// 	Protected Methods
	//=====================
	protected void fillInDescription() {
		// Description Tab
		employer.setText(job.getEmployer());
		title.setText(job.getTitle());
		location.setText(job.getLocation());
		int opennings = job.getNumberOfOpenings();
		openings.setText("Opennings: " + (opennings == 0 ? "0" : Integer.toString(opennings)));
		grades.setText(job.areGradesRequired() ? "Required" : "[none]");
		warning.setText(job.getDescriptionWarning());
		description.setText(job.getDescription());
		
		// Details Tab
		openDate.setText(DISPLAY_DATE_FORMAT.format(job.getOpenDateToApply()));
		closedDate.setText(DISPLAY_DATE_FORMAT.format(job.getLastDateToApply()));
		hiringSupport.setText(job.getHiringSupportName());
		worktermSupport.setText(job.getWorkSupportName());
		levels.setText(arrayJoin(job.getLevels(), ", "));
		disciplines.setText(arrayJoin(job.getDisciplines(), ", "));
	}
	
	private String arrayJoin(Object[] array, String delimiter) {
		String returnStr = "";
		int i = 1;
		int size = array.length;
		if (size != 0) {
			returnStr = array[0].toString();
			for (; i < size; i++) {
				returnStr += delimiter + array[i];
			}
		}
		return returnStr;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}
