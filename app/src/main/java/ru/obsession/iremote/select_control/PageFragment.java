package ru.obsession.iremote.select_control;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gc.materialdesign.widgets.SnackBar;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ru.obsession.iremote.ProgressDialog;
import ru.obsession.iremote.R;
import ru.obsession.iremote.ServerApi;
import ru.obsession.iremote.models.Control;


public class PageFragment extends Fragment {

    public static final String POSITION_KEY = "position_key";
    private RecyclerView recyclerView;
    private ProgressDialog dlg;

    public void querySubmitted(String query) {
        if (recyclerView.getAdapter() != null) {
            ((Adapter) recyclerView.getAdapter()).setQuery(query);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments().getInt(POSITION_KEY) == 0) {
            recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new Adapter(getActivity()));
            return recyclerView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void closeProgressDialog() {
        if (dlg != null) {
            dlg.dismiss();
        }
    }

    private void showProgressDialog() {
        if (dlg == null) {
            dlg = new ProgressDialog();
        }
        dlg.show(getFragmentManager(), null);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements View.OnClickListener {

        private ArrayList<Control> controls;
        private FragmentActivity activity;
        private ServerApi api;
        private String searchPattern = "";
        private boolean hasNotCompletedResponse;
        private Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    hasNotCompletedResponse = false;
                    closeProgressDialog();
                    if (response.equals("null")){
                        Toast.makeText(activity, R.string.nothing_founded, Toast.LENGTH_LONG).show();
                        return;
                    }

                    final JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); ++i) {
                        JSONArray obj = new JSONArray(array.getString(i));
                        Control control = new Control(obj.getString(0), obj.getString(1));
                        controls.add(control);
                    }
                    Adapter.this.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                }
            }
        };

        private Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    closeProgressDialog();
                    hasNotCompletedResponse = false;
                    SnackBar snackbar = new SnackBar(activity,
                            activity.getString(R.string.error_query), activity.getString(R.string.yes),
                            Adapter.this);
                    snackbar.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        public Adapter(FragmentActivity context) {
            activity = context;
            api = ServerApi.getInstance(context);
            controls = new ArrayList<>();
            downloadMore();
        }

        public void setQuery(String query) {
            searchPattern = query;
            controls.clear();
            notifyDataSetChanged();
            downloadMore();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.controls_list_tem, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setOnClickListener(activity, controls.get(position));
            if (controls.size() - position < 30 && !hasNotCompletedResponse) {
                downloadMore();
            }
        }

        public void downloadMore() {
            hasNotCompletedResponse = true;
            if (controls.size() == 0) {
                showProgressDialog();
            }
            api.fingDivece(controls.size() / 100, searchPattern, listener, errorListener);
        }

        @Override
        public int getItemCount() {
            return controls.size();
        }

        @Override
        public void onClick(View v) {
            downloadMore();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView imageView;
            private TextView company;
            private TextView model;
            private Control control;
            private Response.Listener<String> listenerDevice = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    closeProgressDialog();
                    hasNotCompletedResponse = false;
                    try {
                        control.saveControlToDisk(activity, response);
                        Intent intent = new Intent();
                        intent.putExtra(SelectControlActivity.CONTROL_EXTRA, control);
                        activity.setResult(SelectControlActivity.RESULT_OK, intent);
                        activity.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            };
            private Response.ErrorListener errorListenerDevice = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    closeProgressDialog();
                    hasNotCompletedResponse = false;
                    SnackBar snackbar = new SnackBar(activity,
                            activity.getString(R.string.error_query), activity.getString(R.string.yes),
                            ViewHolder.this);
                    snackbar.show();
                }
            };

            public ViewHolder(View v) {
                super(v);
                company = (TextView) v.findViewById(R.id.textCompany);
                model = (TextView) v.findViewById(R.id.textModel);
                imageView = (ImageView) v.findViewById(R.id.imageIcon);
            }

            public void setOnClickListener(final FragmentActivity activity, final Control cont) {
                this.control = cont;
                company.setText(control.getCompany());
                model.setText(control.getName());
                imageView.setBackgroundColor(control.getColor(activity.getResources()));
                imageView.setImageResource(control.getIcon());
                itemView.findViewById(R.id.itemContainer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager inputManager =
                                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        api.getDevice(control.getCompany(), control.getName(), listenerDevice, errorListenerDevice);
                    }
                });
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
