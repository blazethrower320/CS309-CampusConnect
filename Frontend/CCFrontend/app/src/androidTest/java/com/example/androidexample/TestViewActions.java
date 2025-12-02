package com.example.androidexample;

import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.PickerActions.setTime;

public class TestViewActions {

    public static ViewAction setDate(int year, int month, int day) {
        return setDate(year, month, day);
    }

    public static ViewAction setTime(int hour, int minute) {
        return setTime(hour, minute);
    }
}