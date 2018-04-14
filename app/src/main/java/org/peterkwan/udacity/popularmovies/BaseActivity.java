package org.peterkwan.udacity.popularmovies;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected final FragmentManager fragmentManager;

    public BaseActivity() {
        fragmentManager = getSupportFragmentManager();
    }

    protected void replaceFragment(int containerId, Fragment newFragment, @NonNull String fragmentTag) {
        fragmentManager.beginTransaction()
                .replace(containerId, newFragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit();
    }

}
