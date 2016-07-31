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

    String  mDueDateText;


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

//        System.out.println("DEBUGGY: " + mFilter.getBeginDate());
//        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("Filters", 0);
//
////
//         beginDate = settings.getString(SearchActivity.BEGIN_DATE_STR, "missing");
//               System.out.println("DEBUGGY: " + beginDate );
//
//         sortBy = settings.getString(SearchActivity.SORT_BY_STR, "missing");
//         newsdeskArts = settings.getBoolean(SearchActivity.NEWSDESK_ARTS_STR, false);
//         newsdeskFashionStyle = settings.getBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, false);
//        newsdeskSports = settings.getBoolean(SearchActivity.NEWSDESK_SPORTS_STR, false);
//
//        System.out.println("DEBUGGY task printing test: " + " sort by " + sortBy + " " +newsdeskArts );


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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        beginDate = settings.getString(SearchActivity.BEGIN_DATE_STR, "missing");

        sortBy = settings.getString(SearchActivity.SORT_BY_STR, "missing");
        newsdeskArts = settings.getBoolean(SearchActivity.NEWSDESK_ARTS_STR, false);
        newsdeskFashionStyle = settings.getBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, false);
        newsdeskSports = settings.getBoolean(SearchActivity.NEWSDESK_SPORTS_STR, false);

        System.out.println("DEBUGGY: " + beginDate + " " + sortBy + newsdeskArts + " " + newsdeskFashionStyle + " " + newsdeskSports);

        mEditTextDueDate = (EditText) layout
                .findViewById(R.id.etBeginDate);

        mEditTextDueDate.setText(beginDate);


        cbArts = (CheckBox) layout.findViewById(R.id.cbArts);
        cbFashionStyle = (CheckBox) layout.findViewById(R.id.cbFashionStyle);
        cbSports = (CheckBox) layout.findViewById(R.id.cbSports);

        cbArts.setChecked(newsdeskArts);
        cbFashionStyle.setChecked(newsdeskFashionStyle);
        cbSports.setChecked(newsdeskSports);

        //get spinner
        mSpinnerSortBy = (Spinner) layout
                .findViewById(R.id.spinnerSort);


        //set spinner selection from values from SharedPreferences
        if (sortBy == null){
            mSpinnerSortBy.setSelection(0);
        }
        else{
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
        cbArts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    newsdeskArts = true;
                    System.out.println("DEBUGGY: arts checked" );

                }
                else{
                    newsdeskArts = false;
                }
                Filter.getInstance().setNewsdeskArts(newsdeskArts);

            }
        });

        cbFashionStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    newsdeskFashionStyle = true;
                    System.out.println("DEBUGGY: fash checked" );

                }
                else{
                    newsdeskFashionStyle = false;
                }
                Filter.getInstance().setNewsdeskFashionStyle(newsdeskFashionStyle);

            }
        });

        cbSports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    newsdeskSports = true;
                    System.out.println("DEBUGGY: sports checked" );

                }
                else{
                    newsdeskSports = false;
                }
                Filter.getInstance().setNewsdeskSports(newsdeskSports);

            }
        });
    }

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
     * Takes care of setting listeners for all UI components
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
                        String myFormat = "EEE, MMMM dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        mDueDateText = sdf.format(mCalendar.getTime());
                        System.out.println("task printing date set: " + mDueDateText.toString());
                        mEditTextDueDate.setText(mDueDateText);

                        Filter.getInstance().setBeginDate(mDueDateText);

//                        savedBeginDate = mDueDateText;
//                        mSettings.edit().putString(SearchActivity.BEGIN_DATE_STR, mDueDateText);


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
                saveTask();
                break;
//            case R.id.cbArts:
//                artsChecked();
//                break;
//            case R.id.cbFashionStyle:
//                newsdeskFashionStyle = true;
//                break;
//            case R.id.cbSports:
//                newsdeskSports = true;
//                break;

        }
    }


    /**
     * Saves updated task to database and dismisses fragment to go back to MainActivity
     */
    public void saveTask() {


        //String taskText = mEditTextTaskText.getText().toString();

        //modify current task's values
//        mCurrentTask.taskText = taskText;
//        mCurrentTask.priority = mPriority;
//        mCurrentTask.dueDate = mDueDateText;
//        mCurrentTask.completed = false;
//        mCurrentTask.classification = mClassificationText;
//
//
//        // Save the task object to the table
//        mCurrentTask.save();

//        mSettings = getActivity().getSharedPreferences("Settings", 0);

//        SharedPreferences.Editor e = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
//        e.putString(SearchActivity.BEGIN_DATE_STR, beginDate);
//        e.commit();

//        mSettings.edit().putString(SearchActivity.BEGIN_DATE_STR, beginDate);
//        editor.putString(SearchActivity.SORT_BY_STR, sortBy);
//        editor.putBoolean(SearchActivity.NEWSDESK_ARTS_STR, newsdeskArts);
//        editor.putBoolean(SearchActivity.NEWSDESK_FASHION_STYLE_STR, newsdeskFashionStyle);
//        editor.putBoolean(SearchActivity.NEWSDESK_SPORTS_STR, newsdeskSports);
//        editor.commit();
//        editor.apply();

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

