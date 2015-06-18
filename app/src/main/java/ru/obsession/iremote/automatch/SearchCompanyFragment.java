package ru.obsession.iremote.automatch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gc.materialdesign.widgets.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.obsession.iremote.ProgressDialog;
import ru.obsession.iremote.R;
import ru.obsession.iremote.ServerApi;
import ru.obsession.iremote.select_control.DevicesFragment;
import ru.obsession.iremote.select_control.FindByBrandActivity;
import ru.obsession.iremote.views.FastSearchListView;

public class SearchCompanyFragment extends Fragment implements View.OnClickListener,  AdapterView.OnItemClickListener{

    private FastSearchListView listView;
    private ProgressDialog dlg;
    private static ArrayList<Item> allItems;
    private ServerApi api;
    private int type;
    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                closeProgressDialog();
                final JSONArray array = new JSONArray(response);
                listView.setAdapter(new Adapter(getActivity(), array));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Response.ErrorListener errorListener;

    public SearchCompanyFragment() {
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    closeProgressDialog();
                    SnackBar snackbar = new SnackBar(SearchCompanyFragment.this.getActivity(),
                            SearchCompanyFragment.this.getString(R.string.error_query), SearchCompanyFragment.this.getString(R.string.yes),
                            SearchCompanyFragment.this);
                    snackbar.setDismissTimer(100 * 1000);
                    snackbar.show();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        };
    }

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
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_search_company);
        View root = inflater.inflate(R.layout.fragment_select_company, container, false);
        listView = (FastSearchListView) root.findViewById(R.id.listView);
        listView.setFastScrollEnabled(true);
        listView.setOnItemClickListener(this);
        if (type == 0) {
            Bundle bundle = getArguments();
            type = bundle.getInt(AutoMatchActivity.KEY_TYPE);
        }
        api = ServerApi.getInstance(getActivity());
        onClick(null);
        return root;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideKeyboard();
        Item item = ((Adapter) listView.getAdapter()).getItem(position);
        if (item.type != Item.SECTION) {
            Bundle bundle = getArguments();
            Fragment fragment;
            if (type > 0) {
                fragment = new AutoMatchFragment();
            } else {
                fragment = new DevicesFragment();
            }
            bundle.putString(FindByBrandActivity.KEY_COMPANY, item.text);
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction().
                    replace(R.id.container, fragment).addToBackStack(null).commit();
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Adapter adapter = (Adapter) listView.getAdapter();
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (allItems == null) {
            showProgressDialog();
            api.getBrands(responseListener, errorListener);
        } else {
            listView.setAdapter(new Adapter(getActivity(), allItems));
        }
    }

    public static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;

        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override
        public String toString() {
            return text.toLowerCase();
        }

    }

    public class Adapter extends ArrayAdapter<Item>
            implements SectionIndexer, FastSearchListView.PinnedSectionListAdapter {

        private Item[] sectionsItems;
        private Context context;
        private Filter filter;

        public Adapter(Context ctx, ArrayList<Item> items) {
            super(ctx, android.R.layout.simple_list_item_1);
            ArrayList<Item> sectionsList = new ArrayList<>();
            context = ctx;
            String sections = "";
            int sectionPosition = 0, listPosition = 0;
            for (Item var : items) {
                if (var.type == Item.SECTION) {
                    continue;
                }
                if (sections.indexOf(var.text.charAt(0)) == -1) {
                    Item section = new Item(Item.SECTION, String.valueOf(var.text.charAt(0)));
                    section.listPosition = listPosition++;
                    section.sectionPosition = sectionPosition++;
                    sections += var.text.charAt(0);
                    sectionsList.add(section);
                    add(section);
                }
                Item item = new Item(Item.ITEM, var.text);
                item.listPosition = listPosition++;
                item.sectionPosition = sectionPosition - 1;
                add(item);
            }

            char[] buff = sections.toCharArray();
            Arrays.sort(buff);
            this.context = ctx;
            sectionsItems = new Item[sectionsList.size()];
            int i = 0;
            for (Item item : sectionsList) {
                sectionsItems[i++] = item;
            }
        }

        public Adapter(Context ctx, JSONArray companies) throws JSONException {
            super(ctx, android.R.layout.simple_list_item_1);
            allItems = new ArrayList<>();
            ArrayList<Item> sectionsList = new ArrayList<>();
            String sections = "";
            int sectionPosition = 0, listPosition = 0;
            for (int i = 0; i < companies.length(); ++i) {
                String s = companies.getString(i);
                if (sections.indexOf(s.charAt(0)) == -1) {
                    Item section = new Item(Item.SECTION, String.valueOf(s.charAt(0)));
                    section.listPosition = listPosition++;
                    section.sectionPosition = sectionPosition++;
                    sections += s.charAt(0);
                    sectionsList.add(section);
                    add(section);
                }
                Item item = new Item(Item.ITEM, s);
                item.listPosition = listPosition++;
                item.sectionPosition = sectionPosition - 1;
                add(item);
                allItems.add(item);
            }

            char[] buff = sections.toCharArray();
            Arrays.sort(buff);
            this.context = ctx;
            sectionsItems = new Item[sectionsList.size()];
            int i = 0;
            for (Item item : sectionsList) {
                sectionsItems[i++] = item;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }

            TextView text = (TextView) v.findViewById(android.R.id.text1);
            text.setText(getItem(position).text);
            if (getItem(position).type == Item.SECTION) {
                text.setBackgroundColor(getResources().getColor(R.color.primary));
            } else {
                text.setBackgroundResource(R.drawable.list_selector);
            }
            return v;

        }

        @Override
        public int getPositionForSection(int section) {
            if (section >= sectionsItems.length) {
                return sectionsItems.length - 1;
            }
            return sectionsItems[section].listPosition;
        }

        @Override
        public int getSectionForPosition(int position) {
            if (position >= getCount()) {
                return getCount() - 1;
            }
            return getItem(position).sectionPosition;
        }

        @Override
        public Item[] getSections() {
            return sectionsItems;
        }

        @Override
        public Filter getFilter() {
            if (filter == null)
                filter = new AppFilter(allItems);
            return filter;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == Item.SECTION;
        }
    }

    public class AppFilter extends Filter {

        private ArrayList<Item> sourceObjects;

        public AppFilter(List<Item> objects) {
            sourceObjects = new ArrayList<>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq.length() > 0) {
                ArrayList<Item> filter = new ArrayList<>();

                for (Item object : sourceObjects) {
                    // the filtering itself:
                    if (object.toString().toLowerCase().contains(filterSeq)) {
                        filter.add(object);
                    }
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      Filter.FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<Item> filtered = (ArrayList<Item>) results.values;
            listView.setAdapter(new Adapter(getActivity(), filtered));
        }
    }
}
