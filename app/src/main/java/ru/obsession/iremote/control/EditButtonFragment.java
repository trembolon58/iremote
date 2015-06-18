package ru.obsession.iremote.control;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.widgets.SnackBar;

import ru.obsession.iremote.MainActivity;
import ru.obsession.iremote.R;
import ru.obsession.iremote.create_control.ButtonFragment;
import ru.obsession.iremote.create_control.DIYFragment;
import ru.obsession.iremote.models.ControlButton;

public class EditButtonFragment extends ButtonFragment {

    protected void setTitle() {
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        toolbar.setTitle(R.string.title_edit_button_fragment);
        toolbar.setSubtitle("");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            //TODO
            ControlFragment fragment = (ControlFragment) getFragmentManager().findFragmentByTag(DIYFragment.TAG);
            if (testButton.getVisibility() != View.GONE) {
                if (editName.getText().toString().length() == 0) {
                    editName.setError("Введите название кнопки");
                }
                if (controlButton == null) {
                    controlButton = new ControlButton(control, "some_name");
                    findInfo();
                //    fragment.buttonCreated(controlButton);
                } else {
                    findInfo();
                //    fragment.buttonChanged(controlButton);
                }
                hideKeyboard();
                getFragmentManager().popBackStack();
            } else {
                SnackBar snackbar = new SnackBar(getActivity(), "пожалуйста запишите сигнал");
                snackbar.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
