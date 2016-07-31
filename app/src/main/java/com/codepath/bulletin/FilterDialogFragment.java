package com.codepath.bulletin;

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

import com.codepath.bulletin.models.Filter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Shows a DialogFragment with Filter feature
 */
public class FilterDialogFragment extends DialogFragment implements OnClickListener {
    EditText mEditTextDueDate;

    Button mButtonSave;
    Spinner mSpinnerSortBy;
    CheckBox cbArts;
    CheckBox cbFashionStyle;
    CheckBox cbSports;

    String mSpinnerText;

    String mBeginDateText;


    Calendar mCalendar;
    DatePickerDialog.OnDateSetListener mDateListener;


    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
    private String beginDate;
    private String sortBy;
    private boolean newsdeskArts;
    private boolean newsdeskFashionStyle;
    private boolean newsdeskSports;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.dialog_filter, null);

        setupViews(root);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_filter, null);

        setupViews(layout);

        //set all listeners
        setBeginDateListener();
        setSortByListener();
        setNewsDeskListener();
        mButtonSave = (Button) layout.findViewById(R.id.btnSave);
        mButtonSave.setOnClickListener(this);


        return layout;
    }

    private void setupViews(View layout) {
        //get list of shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        beginDate = settings.getString(SearchActivity.BEGIN_DATE_STR, "missing");
        sortBy = settings.getString(SearchActivity.SORT_BY_STR, "missing");
        newsdeskArts = settings.getBoolean(SearchActivity.NEWSDESK_ARTS_STR, false);
        newsdeskFashionStyle = settings.getBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, false);
        newsdeskSports = settings.getBoolean(SearchActivity.NEWSDESK_SPORTS_STR, false);

        System.out.println("DEBUGGY: " + beginDate + " " + sortBy + newsdeskArts + " " + newsdeskFashionStyle + " " + newsdeskSports);

        //get references to views
        mEditTextDueDate = (EditText) layout.findViewById(R.id.etBeginDate);
        cbArts = (CheckBox) layout.findViewById(R.id.cbArts);
        cbFashionStyle = (CheckBox) layout.findViewById(R.id.cbFashionStyle);
        cbSports = (CheckBox) layout.findViewById(R.id.cbSports);
        mSpinnerSortBy = (Spinner) layout.findViewById(R.id.spinnerSort);

        //populate views with values from shared preferences
        mEditTextDueDate.setText(beginDate);
        cbArts.setChecked(newsdeskArts);
        cbFashionStyle.setChecked(newsdeskFashionStyle);
        cbSports.setChecked(newsdeskSports);

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
     * Sets up listener for News desk checkbox options
     */
    private void setNewsDeskListener() {
        cbArts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        cbFashionStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        cbSports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        mEditTextDueDate.setOnClickListener(new OnClickListener() {
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
                        mEditTextDueDate.setText(mBeginDateText);

                        Filter.getInstance().setBeginDate(mBeginDateText);


                    }

                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), mDateListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                //disable all past dates
//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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
     * Interface used as a listener for MainActivity when fragment is dismissed.
     */
    public interface SetFilterDialogListener {
        void onFinishSetFilterDialog();
    }
}

