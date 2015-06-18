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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;

import ru.obsession.iremote.R;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;

public class ButtonFragment extends Fragment {

    protected Control control;
    protected ControlButton controlButton;
    protected EditText editName;
    protected Spinner spinner;
    protected ButtonRectangle testButton;
    protected ButtonRectangle recordButton;
    protected TextView textTutorial;
    protected boolean notNeedChangeText;

    private String[] buttonsNames;

    protected void setTitle() {
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_button_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        setTitle();
        buttonsNames = getResources().getStringArray(R.array.buttonNsmes);
        View root = inflater.inflate(R.layout.fragment_button, container, false);
        testButton = (ButtonRectangle) root.findViewById(R.id.testButton);
        recordButton = (ButtonRectangle) root.findViewById(R.id.recordButton);
        editName = (EditText) root.findViewById(R.id.editName);
        spinner = (Spinner) root.findViewById(R.id.spinner);
        Adapter adapter = new Adapter(getActivity());
        spinner.setAdapter(adapter);
        if (bundle != null) {
            control = (Control) bundle.getSerializable(Control.KEY);
            controlButton = bundle.getParcelable(ControlButton.KEY);
            if (controlButton != null) {
                notNeedChangeText = true;
                spinner.setSelection(controlButton.type);
                editName.setText(controlButton.name);
                testButton.setVisibility(View.VISIBLE);
            }
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testButton.setVisibility(View.VISIBLE);
                textTutorial.setText(R.string.create_button_test);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!notNeedChangeText) {
                    editName.setText(buttonsNames[position]);
                }
                notNeedChangeText = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textTutorial = (TextView) root.findViewById(R.id.textTutorial);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.done, menu);
    }

    protected void findInfo() {
        controlButton.name = editName.getText().toString();
        controlButton.type = spinner.getSelectedItemPosition();
        controlButton.icon = ControlButton.BUTTON_TYPES_ICONS[controlButton.type];
    }

    protected void hideKeyboard() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            DIYFragment fragment = (DIYFragment) getFragmentManager().findFragmentByTag(DIYFragment.TAG);
            if (testButton.getVisibility() != View.GONE) {
                if (editName.getText().toString().length() == 0) {
                    editName.setError(getString(R.string.enter_button_name));
                }
                if (controlButton == null) {
                    controlButton = new ControlButton(control, "some_name");
                    findInfo();
                    fragment.buttonCreated(controlButton);
                } else {
                    findInfo();
                    fragment.buttonChanged(controlButton);
                }
                hideKeyboard();
                getFragmentManager().popBackStack();
            } else {
                SnackBar snackbar = new SnackBar(getActivity(), getString(R.string.record_signal));
                snackbar.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class Adapter extends ArrayAdapter<Integer> {

        private LayoutInflater lInflater;

        public Adapter(Context context) {
            super(context, R.layout.dropdown_type_item, ControlButton.BUTTON_TYPES_ICONS);
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                v = lInflater.inflate(R.layout.dropdown_button_item, null);
                holder.imageView = (ImageView) v.findViewById(R.id.icon);
                holder.textView = (TextView) v.findViewById(R.id.text);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.imageView.setImageResource(getItem(position));
            holder.textView.setText(buttonsNames[position]);
            return v;
        }
    }
}
