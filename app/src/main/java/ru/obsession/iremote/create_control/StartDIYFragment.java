package ru.obsession.iremote.create_control;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.views.DelayAutoCompleteTextView;

public class StartDIYFragment extends Fragment {

    private DelayAutoCompleteTextView autoCompany;
    private EditText autoModel;
    private Spinner spinnerType;
    private Control control;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        control = (Control) getActivity().getIntent().getSerializableExtra(Control.KEY);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_start_diy_agment);
        View view = inflater.inflate(R.layout.fragment_start_diy, container, false);
        spinnerType = (Spinner) view.findViewById(R.id.spinnerType);
        AdapterRemoteTypes adapter = AdapterRemoteTypes.getInstance(getActivity());
        spinnerType.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.dropdown_type_item);
        autoCompany = (DelayAutoCompleteTextView) view.findViewById(R.id.autoCompany);
        autoModel = (EditText) view.findViewById(R.id.autoModel);

        if (control != null) {
            spinnerType.setSelection(control.getType());
            autoCompany.setText(control.getCompany());
            autoModel.setText(control.getName());
        }

        setAdapters(view, autoCompany, R.id.progressCompany, R.array.company_names);

        return view;
    }

    private void setAdapters(View root, final DelayAutoCompleteTextView v, int progressId, int arrayId) {
        v.setThreshold(1);
        v.setAdapter(new AutoCompleteAdapter(getActivity(), arrayId));
        v.setLoadingIndicator((ProgressBar) root.findViewById(progressId));
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                v.setText((String) parent.getItemAtPosition(position));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.start, menu);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void collectData() {
        boolean isError = false;

        if (autoCompany.getText().length() == 0) {
            autoCompany.setError(getString(R.string.input_feeld));
            isError = true;
        }
        if (autoModel.getText().length() == 0) {
            autoModel.setError(getString(R.string.input_feeld));
            isError = true;
        }
        if (isError) {
            return;
        }
        Fragment fragment = new DIYFragment();
        Bundle bundle = new Bundle();
        if (control == null) {
        //TODO create_control
        control = new Control(spinnerType.getSelectedItemPosition(), "");
        } else {
            control.setType(spinnerType.getSelectedItemPosition());
        }
        control.setCompany(autoCompany.getText().toString());
        control.setName(autoModel.getText().toString());
        bundle.putSerializable(Control.KEY, control);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.container, fragment, DIYFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start:
                hideKeyboard();
                collectData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class AutoCompleteAdapter extends BaseAdapter implements Filterable {

        private static final int MAX_RESULTS = 10;

        private final Context mContext;
        private List<String> mResults;
        private int arrayId;

        public AutoCompleteAdapter(Context context, int arrayId) {
            mContext = context;
            mResults = new ArrayList<>();
            this.arrayId = arrayId;
        }

        @Override
        public int getCount() {
            return mResults.size();
        }

        @Override
        public String getItem(int position) {
            return mResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.dropdown_item, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.textView)).setText(getItem(position));

            return convertView;
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<String> books = findNames(mContext, constraint.toString());
                        filterResults.values = books;
                        filterResults.count = books.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        mResults = (List<String>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }

        /**
         * Нужно переопределить для каждого поля
         */
        private ArrayList<String> findNames(Context context, String pattern) {
            ArrayList<String> list = new ArrayList<>();
            String[] strings = context.getResources().getStringArray(arrayId);
            for (String s : strings) {
                if (s.startsWith(pattern)) {
                    list.add(s);
                    if (list.size() > MAX_RESULTS) {
                        break;
                    }
                }
            }
            return list;
        }
    }

    private static class AdapterRemoteTypes extends ArrayAdapter<Integer> {

        private LayoutInflater lInflater;

        private AdapterRemoteTypes(Context context, Integer[] objects) {
            super(context, R.layout.dropdown_type_item, objects);
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public static AdapterRemoteTypes getInstance(Context context) {
            return new AdapterRemoteTypes(context, Control.GetRemoteTypes());
        }

        private class ViewHolder {
            ImageView imageView;
            TextView textView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView);
        }

        @Override
        public View getDropDownView(int position, View v, ViewGroup parent) {
            return getCustomView(position, v);
        }

        public View getCustomView(int position, View v) {
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = lInflater.inflate(R.layout.dropdown_type_item, null);
                holder.imageView = (ImageView) v.findViewById(R.id.icon);
                holder.textView = (TextView) v.findViewById(R.id.text);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            int item = getItem(position);
            holder.imageView.setImageResource(item);
            holder.imageView.setBackgroundResource(Control.GetColorForType(position));
            holder.textView.setText(Control.GetTitleForType(position));
            return v;
        }
    }
}
