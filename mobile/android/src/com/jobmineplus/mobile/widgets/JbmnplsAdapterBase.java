package com.jobmineplus.mobile.widgets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;
import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jobmineplus.mobile.R;

public abstract class JbmnplsAdapterBase extends ViewAdapterBase<Job> {
    private View[] currentElements;
    private View currentLayout;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    public static enum HIGHLIGHTING{
        GREAT, NORMAL, BAD, WORSE
    }

    public JbmnplsAdapterBase(Activity a, int widgetResourceLayout,
            int[] viewResourceIdListInWidget, ArrayList<Job> list) {
        super(a, widgetResourceLayout, viewResourceIdListInWidget, list);
    }

    protected abstract HIGHLIGHTING setJobWidgetValues(int position, Job item, View[] elements, View layout);

    public int getJobPosition(Job job) {
        return getJobPosition(job.getId());
    }

    public int getJobPosition(int id) {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public boolean removeByJobId(int id) {
        int i = getJobPosition(id);
        if (i == -1) {
            return false;
        }
        return remove(i);
    }

    public boolean remove(int i) {
        if (i < getCount()) {
            getList().remove(i);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    /*
     * Helper functions to set the widget text
     */
    protected void hide(int index) {
        Assert.assertNotNull("Can only call this in setJobWidgetValues.", currentElements);
        currentElements[index].setVisibility(View.GONE);
    }

    /*
     * Date methods
     */
    protected void setDate(int index, Date date) {
        setDate(index, date, (String)null, (SimpleDateFormat)null);
    }

    protected void setDate(int index, Date date, SimpleDateFormat format) {
        setDate(index, date, (String)null, format);
    }

    protected void setDate(int index, Date date, String prefix) {
        setDate(index, date, prefix, (SimpleDateFormat)null);
    }

    protected void setDate(int index, Date date, String prefix, SimpleDateFormat format) {
        Assert.assertNotNull("Can only call this in setJobWidgetValues.", currentElements);
        if (date == null) {
            hide(index);
            return;
        }
        if (format == null) {
            format = DATE_FORMAT;
        }
        String dateStr = format.format(date);
        if (prefix != null && prefix != "") {
            setText(index, prefix.trim() + " " + dateStr);
        } else {
            setText(index, dateStr);
        }
    }

    protected void setDate(int index, Date from, Date to) {
        setDate(index, from, to, null, null);
    }

    protected void setDate(int index, Date from, Date to, String prefix) {
        setDate(index, from, to, prefix, null);
    }

    protected void setDate(int index, Date from, Date to, SimpleDateFormat format) {
        setDate(index, from, to, null, null);
    }

    protected void setDate(int index, Date from, Date to, String prefix, SimpleDateFormat format) {
        Assert.assertNotNull("Can only call this in setJobWidgetValues.", currentElements);
        if (from == null || to == null) {
            hide(index);
            return;
        }
        if (format == null) {
            format = DATE_FORMAT;
        }
        String dateStr = format.format(from) + " - " + format.format(to);
        if (prefix != null && prefix != "") {
            setText(index, prefix.trim() + " " + dateStr);
        } else {
            setText(index, dateStr);
        }
    }

    protected void setText(int index, String text) {
        setText(index, text, false);
    }
    protected void setText(int index, String text, boolean uppercase) {
        Assert.assertNotNull("Can only call this in setJobWidgetValues.", currentElements);
        TextView element = (TextView) currentElements[index];
        if (text == null || TextUtils.isEmpty(text)) {
            hide(index);
            return;
        }
        if (uppercase) {
            text = text.toUpperCase(Locale.getDefault());
        }
        element.setText(Html.fromHtml(text));
    }

    protected void setText(int index, String text, String prefix) {
        setText(index, text, prefix, false);
    }

    protected void setText(int index, String text, String prefix, boolean uppercase) {
        if (text == null || TextUtils.isEmpty(text)) {
            hide(index);
            return;
        }
        setText(index, prefix + " " + text, uppercase);
    }

    protected void setText(int index1, int index2, String text) {
        setText(index1, index2, text, false);
    }
    protected void setText(int index1, int index2, String text, boolean uppercase) {
        Assert.assertNotNull("Can only call this in setJobWidgetValues.", currentElements);
        TextView element1 = (TextView) currentElements[index1];
        TextView element2 = (TextView) currentElements[index2];
        if (text == null || TextUtils.isEmpty(text)) {
            hide(index1);
            hide(index2);
            return;
        }
        if (uppercase) {
            text = text.toUpperCase(Locale.getDefault());
        }

        // Split the text into 2 elements, if there is no 2nd word, then hide it
        String[] textSplit = text.split(" ");
        element1.setText(Html.fromHtml(textSplit[0]));
        if (textSplit.length > 1) {
            element2.setText(Html.fromHtml(textSplit[1]));
        } else {
            hide(index2);
        }
    }

    private void setBackgroundColorFromResource(int resourceId) {
        int color = getActivity().getResources().getColor(resourceId);
        currentLayout.setBackgroundColor(color);
    }

    protected <T> boolean isOneOf(T value, T... list) {
        for (T item: list) {
            if (value.equals(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void setWidgetValues(int position, Job item, View[] elements, View layout) {
        currentLayout = layout;
        currentElements = elements;

        if (item != null) {
            HIGHLIGHTING highlight = setJobWidgetValues(position, item, currentElements, layout);
            switch(highlight) {
            case GREAT:
                setBackgroundColorFromResource(R.color.highlight_green);
                break;
            case BAD:
                setBackgroundColorFromResource(R.color.highlight_red);
                break;
            case WORSE:
                setBackgroundColorFromResource(R.color.highlight_grey);
                break;
            default:
                setBackgroundColorFromResource(android.R.color.transparent);
                break;
            }
        }
    }

    //============================
    //  Resource Formatting class
    //============================
    public static class Formatter {
        private Formatter() {
        }

        public static void hide(View view) {
            view.setVisibility(View.GONE);
        }
        public static void setText(TextView view, String text) {
            setText(view, text, false);
        }
        public static void setText(TextView view, String text, boolean doUppercase) {
            if (text != null && !TextUtils.isEmpty(text)) {
                text = Html.fromHtml(text).toString();
                if (doUppercase) {
                    text = text.toUpperCase(Locale.getDefault());
                }
                view.setText(text);
            } else {
                hide(view);
            }
        }
        public static void setText(TextView view1, TextView view2, String text) {
            setText(view1, view2, text, false);
        }
        public static void setText(TextView view1, TextView view2, String text, boolean doUppercase) {
            if (TextUtils.isEmpty(text)) {
                hide(view1);
                hide(view2);
            } else {
                text = Html.fromHtml(text).toString();
                if (doUppercase) {
                    text = text.toUpperCase(Locale.getDefault());
                }
                String[] split = text.split(" ");
                view1.setText(split[0]);
                // If there is more text, then add it to the next field, if not hide it
                if (split.length > 1) {
                    view2.setText(split[1]);
                } else {
                    hide(view2);
                }
            }
        }
        public static void setDate(TextView view, Date date) {
            setDate(view, date, (String)null, (SimpleDateFormat)null);
        }
        public static void setDate(TextView view, Date date, SimpleDateFormat format) {
            setDate(view, date, (String)null, format);
        }
        public static void setDate(TextView view, Date date, String prefix) {
            setDate(view, date, prefix, null);
        }
        public static void setDate(TextView view, Date date, String prefix, SimpleDateFormat format) {
            String text = null;
            if (date != null) {
                if (format == null) {
                    format = DATE_FORMAT;
                }
                if (prefix != null && !TextUtils.isEmpty(prefix)) {
                    text = prefix.trim() + " " + format.format(date);
                } else {
                    text = format.format(date);
                }
                setText(view, text, false);
            } else {
                hide(view);
            }
        }
        public static void setDate(TextView view, Date from, Date to) {
            setDate(view, from, to, null, null);
        }
        public static void setDate(TextView view, Date from, Date to, SimpleDateFormat format) {
            setDate(view, from, to, null, format);
        }
        public static void setDate(TextView view, Date from, Date to, String prefix) {
            setDate(view, from, to, prefix, null);
        }
        public static void setDate(TextView view, Date from, Date to, String prefix, SimpleDateFormat format) {
            String text = null;
            if (from != null && to != null) {
                if (format == null) {
                    format = DATE_FORMAT;
                }
                String dateStr = format.format(from) + " - " + format.format(to);
                if (prefix != null && !TextUtils.isEmpty(prefix)) {
                    text = prefix.trim() + " " + dateStr;
                } else {
                    text = dateStr;
                }
                setText(view, text, false);
            } else {
                hide(view);
            }
        }
    }
}
