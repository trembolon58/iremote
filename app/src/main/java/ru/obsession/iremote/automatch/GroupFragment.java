package ru.obsession.iremote.automatch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Control;

public class GroupFragment  extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_group_fragment);
        View root = inflater.inflate(R.layout.grid_view, container, false);
        final Integer[] remoteTypes = Control.GetRemoteTypes();
        GridView gridView = (GridView) root.findViewById(R.id.gridView);
        gridView.setAdapter(new Adapter(getActivity(), remoteTypes));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new SearchCompanyFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(AutoMatchActivity.KEY_TYPE, remoteTypes[position]);
                fragment.setArguments(bundle);
                GroupFragment.this.getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.container, fragment).commit();
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    private class Adapter extends ArrayAdapter<Integer> {


        private LayoutInflater lInflater;

        public Adapter(Context context, Integer[] objects) {
            super(context, R.layout.type_item, objects);
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            ImageView imageView;
            TextView textView;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = lInflater.inflate(R.layout.type_item, null);
                holder.imageView = (ImageView) v.findViewById(R.id.icon);
                holder.textView = (TextView) v.findViewById(R.id.textView);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            int item = getItem(position);
            holder.imageView.setImageResource(item);
            holder.textView.setText(Control.GetTitleForType(position));
            return v;
        }
    }
}
