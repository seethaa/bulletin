package com.codepath.bulletin.activities;

/**
 * Created by seetha on 6/27/16.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.codepath.bulletin.R;
import com.codepath.bulletin.models.Filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Shows a DialogFragment with Filter feature. Allows user to edit begin date, sort by, and select
 * news desk options. Filter ptions are saved and persist through sessions on save.
 */
public class FilterDialogFragment extends DialogFragment implements OnClickListener {

    /* Layout components */
    @BindView(R.id.etBeginDate) EditText mEditTextBeginDate;
    @BindView(R.id.btnSave) Button mButtonSave;
    @BindView(R.id.spinnerSort) Spinner mSpinnerSortBy;
    @BindView(R.id.cbArts) CheckBox mCheckBoxArts;
    @BindView(R.id.cbFashionStyle) CheckBox mCheckBoxFashionStyle;
    @BindView(R.id.cbSports) CheckBox mCheckBoxSports;

    private String mSpinnerText;
    private String mBeginDateText;
    private Calendar mCalendar;
    private DatePickerDialog.OnDateSetListener mDateListener;

    /* SharedPreferences fields */
    private String beginDate;
    private String sortBy;
    private boolean newsdeskArts;
    private boolean newsdeskFashionStyle;
    private boolean newsdeskSports;

    private Unbinder unbinder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.dialog_filter, null);

        unbinder = ButterKnife.bind(this, root);

        setupViews(root);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_filter, null);
        unbinder = ButterKnife.bind(this, layout);

        //set up the views
        setupViews(layout);

        //set all listeners
        setBeginDateListener();
        setSortByListener();
        setNewsDeskListener();
//        mButtonSave = (Button) layout.findViewById(R.id.btnSave);
        mButtonSave.setOnClickListener(this);

        return layout;
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Sets up all views related to Filter Dialog Fragment
     *
     * @param layout current view layout
     */
    private void setupViews(View layout) {
        //get list of shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        beginDate = settings.getString(SearchActivity.BEGIN_DATE_STR, "missing");
        sortBy = settings.getString(SearchActivity.SORT_BY_STR, "missing");
        newsdeskArts = settings.getBoolean(SearchActivity.NEWSDESK_ARTS_STR, false);
        newsdeskFashionStyle = settings.getBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, false);
        newsdeskSports = settings.getBoolean(SearchActivity.NEWSDESK_SPORTS_STR, false);

        //debugging purposes
//        System.out.println("DEBUGGY: " + beginDate + " " + sortBy + " " + newsdeskArts + " " + newsdeskFashionStyle + " " + newsdeskSports);

        //populate views with values from shared preferences
        mEditTextBeginDate.setText(beginDate);
        mCheckBoxArts.setChecked(newsdeskArts);
        mCheckBoxFashionStyle.setChecked(newsdeskFashionStyle);
        mCheckBoxSports.setChecked(newsdeskSports);

        //set spinner selection
        if (sortBy == null) {
            mSpinnerSortBy.setSelection(0);
        } else {
            if (sortBy.equals("Oldest")) {
                mSpinnerSortBy.setSelection(0);
            } else if (sortBy.equals("Newest")) {
                mSpinnerSortBy.setSelection(1);
            }
        }
    }

    /**
     * Sets up listener for News desk checkbox options. Currently we only have Arts, Fashion/Style, and Sports
     */
    private void setNewsDeskListener() {
        mCheckBoxArts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newsdeskArts = true;
                } else {
                    newsdeskArts = false;
                }
                Filter.getInstance().setNewsdeskArts(newsdeskArts);

            }
        });

        mCheckBoxFashionStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newsdeskFashionStyle = true;
                } else {
                    newsdeskFashionStyle = false;
                }
                Filter.getInstance().setNewsdeskFashionStyle(newsdeskFashionStyle);
            }
        });

        mCheckBoxSports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newsdeskSports = true;
                } else {
                    newsdeskSports = false;
                }
                Filter.getInstance().setNewsdeskSports(newsdeskSports);

            }
        });
    }

    /**
     * Sets up listener for Sort By Spinner
     */
    private void setSortByListener() {

        //spinner change listener
        mSpinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mSpinnerText = mSpinnerSortBy.getSelectedItem().toString();
                Filter.getInstance().setSortBy(mSpinnerText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    /**
     * Sets up listener for Date field
     */
    private void setBeginDateListener() {
        mEditTextBeginDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mCalendar = Calendar.getInstance();

                mDateListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        //update date in edittext
                        String myFormat = "MM-dd-yy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        mBeginDateText = sdf.format(mCalendar.getTime());
                        mEditTextBeginDate.setText(mBeginDateText);

                        Filter.getInstance().setBeginDate(mBeginDateText);


                    }

                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), mDateListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                //disable all future dates
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();


            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                saveFilter();
                break;

        }
    }

    /**
     * Dismisses fragment to go back to SearchActivity
     */
    public void saveFilter() {

        //dismiss current fragment
        SetFilterDialogListener activity = (SetFilterDialogListener) getActivity();
        activity.onFinishSetFilterDialog();
        this.dismiss();
    }

    /**
     * Interface used as a listener for SearchActivity when fragment is dismissed.
     */
    public interface SetFilterDialogListener {
        void onFinishSetFilterDialog();
    }
}

