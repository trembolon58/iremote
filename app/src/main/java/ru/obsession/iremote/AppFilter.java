package ru.obsession.iremote;

import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class AppFilter<T> extends Filter {

    private ArrayList<T> sourceObjects;
    private ArrayAdapter<T> adapter;
    public AppFilter(List<T> objects, ArrayAdapter<T> adapter) {
        sourceObjects = new ArrayList<T>();
        this.adapter = adapter;
        synchronized (this) {
            sourceObjects.addAll(objects);
        }
    }

    @Override
    protected FilterResults performFiltering(CharSequence chars) {
        String filterSeq = chars.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if (filterSeq.length() > 0) {
            ArrayList<T> filter = new ArrayList<T>();

            for (T object : sourceObjects) {
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
        ArrayList<T> filtered = (ArrayList<T>) results.values;
        adapter.clear();
        for (int i = 0; i < filtered.size(); i++) {
            adapter.add(filtered.get(i));
        }
        adapter.notifyDataSetChanged();
    }
}