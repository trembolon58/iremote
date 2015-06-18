package ru.obsession.iremote.automatch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import ru.obsession.iremote.R;

public class AutoMatchActivity extends ActionBarActivity {

    public static final String KEY_TYPE = "type_key";
    public static final String KEY_COMPANY = "type_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatch_control);
        GroupFragment fragment = new GroupFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }
}
