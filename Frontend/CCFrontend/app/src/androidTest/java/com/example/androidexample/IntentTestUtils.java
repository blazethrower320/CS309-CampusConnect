package com.example.androidexample;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IntentTestUtils {
    public static <T> void launchActivity(Class<T> activityClass) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationProvider.getApplicationContext().startActivity(intent);
    }

    public static Matcher<View> hasExactChildCount(final int expectedCount) {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof RecyclerView)) return false;
                RecyclerView rv = (RecyclerView) view;
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter == null) return false;

                return adapter.getItemCount() == expectedCount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should have exactly " + expectedCount + " items");
            }
        };
    }
}