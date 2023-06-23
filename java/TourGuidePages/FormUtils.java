package com.example.tourbud5.TourGuidePages;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.tourbud5.R;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FormUtils {

    public static TimePickerDialog initializeTimePicker(Context context, TextView textView, Date data){
        //callback for time field
        //initialize time picker
        final Calendar t = Calendar.getInstance();
        int mHour = t.get(Calendar.HOUR_OF_DAY);
        int mMinute = t.get(Calendar.MINUTE);


        // initialize Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        textView.setText(hourOfDay + ":" + minute);
                        data.setHours(hourOfDay);
                        data.setMinutes(minute);
                    }
                }, mHour, mMinute, false);

        return timePickerDialog;
    }

    public  static DatePickerDialog initializeDatePicker(Context context, TextView textView, Date data){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        //initialize date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        data.setYear(year-1900);
                        data.setMonth(monthOfYear);
                        data.setDate(dayOfMonth);



                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        return  datePickerDialog;
    }

    public static void createFormChip(String [] tags, ArrayList<String> selectedTags, ChipGroup chipGroup, Context context, Resources res){

        for (String tag : tags) {
            com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(context);
            chip.setText(tag);

            if(selectedTags.contains(tag)){
                chip.setChipBackgroundColorResource(R.color.chip_dark);

            }else{
                chip.setChipBackgroundColorResource(R.color.chip_light);

            }
            chip.setTextColor(res.getColor(R.color.white));

            chip.setOnClickListener((param -> {
                if(selectedTags.contains(tag)){
                    chip.setChipBackgroundColorResource(R.color.chip_light);
                    selectedTags.remove(tag);

                }else{
                    chip.setChipBackgroundColorResource(R.color.chip_dark);
                    selectedTags.add(tag);

                }


            }));
            chipGroup.addView(chip);

        }

    }
}
