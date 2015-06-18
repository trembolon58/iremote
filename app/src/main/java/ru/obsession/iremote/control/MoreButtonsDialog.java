package ru.obsession.iremote.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.ControlButton;


public class MoreButtonsDialog extends DialogFragment {


    private ArrayList<ControlButton> buttons;
    private ControlFragment fragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        final View root = getActivity().getLayoutInflater().inflate(R.layout.grid_view, null);
        final GridView gridView = (GridView) root.findViewById(R.id.gridView);
        builder.setView(root);
        builder.setInverseBackgroundForced(true);
        fragment = (ControlFragment) getFragmentManager().findFragmentByTag(ControlFragment.CONTROL_FRAGMENT);
        Bundle bundle = getArguments();
        buttons = bundle.getParcelableArrayList(ControlFragment.BUTTONS_KEY);
        gridView.setAdapter(new Adapter(getActivity(), initItems()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) gridView.getAdapter().getItem(position);
                MoreButtonsDialog.this.fragment.onButtonClick(item.button,
                        MoreButtonsDialog.this, item.positionInList);
            }
        });
        final TextView myView = new TextView(getActivity());
        myView.setText(R.string.other_buttons);
        myView.setTextSize(20);
        builder.setCustomTitle(myView);
        return builder.create();
    }

    public ArrayList<Item> initItems(){
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < buttons.size(); ++i) {
            if (!buttons.get(i).isVisible()) {
                items.add(new Item(buttons.get(i), i));
            }
        }
        return items;
    }

    public class Item {
        public ControlButton button;
        public int positionInList;
        public Item(ControlButton button, int positionInList) {
            this.button = button;
            this.positionInList = positionInList;
        }
    }

    private class Adapter extends ArrayAdapter<Item> {

        private LayoutInflater lInflater;
        public Adapter(Context context, List<Item> objects) {
            super(context, R.layout.more_dialog_item, objects);
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            TextView textView;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = lInflater.inflate(R.layout.more_dialog_item, null);
                holder.textView = (TextView) v.findViewById(R.id.textView);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            Item item = getItem(position);
            holder.textView.setText(item.button.name);
            return v;
        }
    }
}
