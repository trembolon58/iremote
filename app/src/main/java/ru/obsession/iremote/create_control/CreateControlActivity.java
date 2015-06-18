package ru.obsession.iremote.create_control;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import ru.obsession.iremote.R;

public class CreateControlActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_control);
        StartDIYFragment fragment = new StartDIYFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }
}
