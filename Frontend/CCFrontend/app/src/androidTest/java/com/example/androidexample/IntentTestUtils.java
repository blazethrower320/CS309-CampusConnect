package com.example.androidexample;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

public class IntentTestUtils {
    public static <T> void launchActivity(Class<T> activityClass) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);
    }
}