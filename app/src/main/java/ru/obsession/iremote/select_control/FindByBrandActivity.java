package ru.obsession.iremote.select_control;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import ru.obsession.iremote.R;
import ru.obsession.iremote.automatch.AutoMatchActivity;
import ru.obsession.iremote.automatch.SearchCompanyFragment;

public class FindByBrandActivity extends ActionBarActivity {

    public static final String KEY_TYPE = "type_key";
    public static final String KEY_COMPANY = "type_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatch_control);
        SearchCompanyFragment fragment = new SearchCompanyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AutoMatchActivity.KEY_TYPE, -1);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }
}
