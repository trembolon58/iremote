package ru.obsession.iremote.select_control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.astuetz.PagerSlidingTabStrip;

import ru.obsession.iremote.R;
import ru.obsession.iremote.automatch.AutoMatchActivity;
import ru.obsession.iremote.create_control.CreateControlActivity;


public class SelectControlActivity extends ActionBarActivity {

    public static final String CONTROL_EXTRA = "control_extra";
    private SearchDevicesPagerAdapter adapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // для второй версии
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.primary));

        setContentView(R.layout.activity_select_controll);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new SearchDevicesPagerAdapter(getSupportFragmentManager(), getResources());
        mViewPager.setAdapter(adapter);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL), Typeface.NORMAL);
        tabs.setShouldExpand(true);
        tabs.setUnderlineColor(getResources().getColor(R.color.primary_light));
        tabs.setIndicatorColor(getResources().getColor(R.color.primary_light));
        tabs.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_controll, menu);
        MenuItem menuItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                if (mViewPager.getCurrentItem() == 0) {
                    adapter.getFirst().querySubmitted(query);
                } else {
                    adapter.getSecond().querySubmitted(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (mViewPager.getCurrentItem() == 0) {
                    adapter.getFirst().querySubmitted(query);
                } else {
                    adapter.getSecond().querySubmitted(query);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            setResult(SelectControlActivity.RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_diy:
                startActivity(CreateControlActivity.class);
                return true;
            case R.id.action_auto_match:
                startActivity(AutoMatchActivity.class);
                return true;
            case R.id.action_brand:
                startActivity(FindByBrandActivity.class);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void startActivity(Class c) {
       startActivityForResult(new Intent(this, c), 1);
    }
}

