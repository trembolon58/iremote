package ru.obsession.iremote.select_control;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gc.materialdesign.widgets.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ru.obsession.iremote.ProgressDialog;
import ru.obsession.iremote.R;
import ru.obsession.iremote.ServerApi;
import ru.obsession.iremote.models.Control;

public class DevicesFragment extends Fragment implements View.OnClickListener {

    private ServerApi api;
    private String companyName;
    private ProgressDialog dlg;
    private ArrayList<Control> controls;
    private RecyclerView recyclerView;
    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                closeProgressDialog();
                final JSONArray array = new JSONArray(response);
                controls = new ArrayList<>();
                String s;
                for (int i = 0; i < array.length(); ++i) {
                    s = array.getString(i);
                    Control control = new Control(companyName, s);
                    controls.add(control);
                }
                recyclerView.setAdapter(new Adapter(getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                closeProgressDialog();
                SnackBar snackbar = new SnackBar(DevicesFragment.this.getActivity(),
                        DevicesFragment.this.getString(R.string.error_query), DevicesFragment.this.getString(R.string.yes),
                        DevicesFragment.this);
                snackbar.setDismissTimer(100 * 1000);
                snackbar.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };


    private void closeProgressDialog()
    {
        if (dlg != null) {
            dlg.dismiss();
        }
    }
    private void showProgressDialog()
    {
        if (dlg == null) {
            dlg = new ProgressDialog();
        }
        dlg.show(getFragmentManager(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        api = ServerApi.getInstance(getActivity());
        Bundle bundle = getArguments();
        companyName = bundle.getString(FindByBrandActivity.KEY_COMPANY);

        onClick(null);
        return recyclerView;
    }

    @Override
    public void onClick(View v) {
        showProgressDialog();
        api.getDevicesByBrand(companyName, responseListener, errorListener);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private FragmentActivity activity;

        public Adapter(FragmentActivity context) {
            activity = context;
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

            public ImageView imageView;
            private TextView company;
            private TextView model;

            public ViewHolder(View v) {
                super(v);
                company = (TextView) v.findViewById(R.id.textCompany);
                model = (TextView) v.findViewById(R.id.textModel);
                imageView = (ImageView) v.findViewById(R.id.imageIcon);
            }

            public void setOnClickListener(final FragmentActivity activity, final Control control) {
                company.setText(control.getCompany());
                model.setText(control.getName());
                imageView.setBackgroundColor(control.getColor(activity.getResources()));
                imageView.setImageResource(control.getIcon());
                itemView.findViewById(R.id.itemContainer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api.getDevice(companyName, control.getName(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    control.saveControlToDisk(activity, response);
                                    Intent intent = new Intent();
                                    intent.putExtra(SelectControlActivity.CONTROL_EXTRA, control);
                                    activity.setResult(SelectControlActivity.RESULT_OK, intent);
                                    activity.finish();
                                } catch (IOException e) {
                                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(activity, R.string.error_query, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
