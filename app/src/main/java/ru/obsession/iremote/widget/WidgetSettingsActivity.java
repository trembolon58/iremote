package ru.obsession.iremote.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.obsession.iremote.IRemote;
import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;

public class WidgetSettingsActivity extends ActionBarActivity {

    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new Adapter(this));
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
            }
        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private ArrayList<Control> controls;
        private FragmentActivity activity;

        public Adapter(FragmentActivity context) {
            activity = context;
            controls = DatabaseApi.getInstance(context).getAllControls();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.controls_list_tem, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setOnClickListener(activity, controls.get(position));
        }

        @Override
        public int getItemCount() {
            return controls.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            private TextView company;
            private TextView model;
            public ImageView imageView;
            public ViewHolder(View v) {
                super(v);
                company = (TextView) v.findViewById(R.id.textCompany);
                model = (TextView) v.findViewById(R.id.textModel);
                imageView = (ImageView) v.findViewById(R.id.imageIcon);
            }

            public void setOnClickListener(final FragmentActivity activity, final Control control)
            {
                company.setText(control.getCompany());
                model.setText(control.getName());
                imageView.setBackgroundColor(control.getColor(activity.getResources()));
                imageView.setImageResource(control.getIcon());
                itemView.findViewById(R.id.itemContainer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                            Intent resultValue = new Intent();
                            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                            // Записываем идентефикатор пульта
                            SharedPreferences sp = getSharedPreferences(IRemote.WIDGET_PREFERENCES, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt(getPackageName() + appWidgetId, control.getId());
                            editor.commit();
                            setResult(RESULT_OK, resultValue);
                        }
                        finish();
                    }
                });
            }
        }
    }
}
