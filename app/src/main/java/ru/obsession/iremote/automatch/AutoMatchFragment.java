package ru.obsession.iremote.automatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;
import com.nineoldandroids.animation.Animator;

import org.json.JSONArray;

import java.util.ArrayList;

import ru.obsession.iremote.ProgressDialog;
import ru.obsession.iremote.R;
import ru.obsession.iremote.ServerApi;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.select_control.FindByBrandActivity;
import ru.obsession.iremote.select_control.SelectControlActivity;

public class AutoMatchFragment extends Fragment implements Animator.AnimatorListener, View.OnClickListener {

    private ButtonRectangle bResponse;
    private ButtonRectangle bNotResponse;
    private RelativeLayout relativeLayout;
    private boolean visible;
    private int numResponse;
    private String companyName;
    private ProgressDialog dlg;
    private ButtonFloat b1;
    private ButtonFloat b2;
    private ButtonFloat b3;
    private ButtonFloat b4;
    private ButtonFloat b5;
    private ButtonFloat b6;
    private TextView textCount;
    private int position;
    private ArrayList<Control> controls;
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!visible) {
                try {
                    Control control = controls.get(position);
                    control.sendSignal((String)v.getTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AutoMatchFragment.this.setVisibility(View.VISIBLE);
            }
        }
    };
    private Response.Listener<String> responseListenerList = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                final JSONArray array = new JSONArray(response);
                controls = new ArrayList<>();
                String s;
                for (int i = 0; i < array.length(); ++i) {
                    s = array.getString(i);
                    Control control = new Control(companyName, s);
                    controls.add(control);
                }
                setCountText();
                onClick(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Response.Listener<String> responseListenerItem = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                closeProgressDialog();
                Context context = AutoMatchFragment.this.getActivity();
                Control control = controls.get(position);
                control.saveControlToDisk(context, response);
                control.parseFile(context);
                String[] comands = control.getCommands();
                b1.setTag(comands[0]);
                b2.setTag(comands[1]);
                b3.setTag(comands[2]);
                b4.setTag(comands[3]);
                b5.setTag(comands[4]);
                b6.setTag(comands[5]);
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
                SnackBar snackbar = new SnackBar(AutoMatchFragment.this.getActivity(),
                        getString(R.string.error_query), getString(R.string.yes),
                        AutoMatchFragment.this);
                snackbar.setDismissTimer(100 * 1000);
                snackbar.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private void setCountText(){
        textCount.setText((position + 1) + "/" + controls.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        companyName = bundle.getString(FindByBrandActivity.KEY_COMPANY);
        View root = inflater.inflate(R.layout.fragment_test_button, container, false);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_automatch_fragment);
        relativeLayout = (RelativeLayout) root.findViewById(R.id.buttonContainer);
        bResponse = (ButtonRectangle) root.findViewById(R.id.response);
        bNotResponse = (ButtonRectangle) root.findViewById(R.id.notResponse);
        textCount = (TextView) root.findViewById(R.id.textCount);
        b1 = (ButtonFloat) root.findViewById(R.id.controlButton1);
        b2 = (ButtonFloat) root.findViewById(R.id.controlButton2);
        b3 = (ButtonFloat) root.findViewById(R.id.controlButton3);
        b4 = (ButtonFloat) root.findViewById(R.id.controlButton4);
        b5 = (ButtonFloat) root.findViewById(R.id.controlButton5);
        b6 = (ButtonFloat) root.findViewById(R.id.controlButton6);
        b1.setOnClickListener(buttonClickListener);
        b2.setOnClickListener(buttonClickListener);
        b3.setOnClickListener(buttonClickListener);
        b4.setOnClickListener(buttonClickListener);
        b5.setOnClickListener(buttonClickListener);
        b6.setOnClickListener(buttonClickListener);
        YoYo.with(Techniques.SlideOutDown).duration(0).delay(0).playOn(bResponse);
        YoYo.with(Techniques.SlideOutDown).duration(0).delay(0).playOn(bNotResponse);
        onClick(null);
        bResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible = false;
                numResponse++;
                AutoMatchFragment.this.changeButton();
            }
        });
        bNotResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible = false;
                numResponse = 0;
                AutoMatchFragment.this.changeButton();
            }
        });
        return root;
    }

    private void changeButton() {

        if (numResponse == 1) {
            Intent intent = new Intent();
            intent.putExtra(SelectControlActivity.CONTROL_EXTRA, controls.get(position));
            getActivity().setResult(SelectControlActivity.RESULT_OK, intent);
            getActivity().finish();
            return;
        } else {
            ++position;
            if (position == controls.size()){
                Toast.makeText(getActivity(), R.string.not_finded_device, Toast.LENGTH_LONG).show();
                getActivity().finish();
                return;
            }
            setCountText();
            onClick(null);
        }
        setVisibility(View.GONE);
        YoYo.with(Techniques.ZoomOutLeft).duration(200).delay(0).withListener(this).playOn(relativeLayout);
        YoYo.with(Techniques.ZoomInRight).duration(200).delay(200).playOn(relativeLayout);
    }

    private void setVisibility(int visibility) {
        if (visibility == View.GONE && bResponse.getVisibility() != visibility) {
            YoYo.with(Techniques.SlideOutDown).duration(200).delay(0).playOn(bResponse);
            YoYo.with(Techniques.SlideOutDown).duration(200).delay(0).playOn(bNotResponse);
        } else if (visibility == View.VISIBLE && bResponse.getVisibility() != visibility){
            YoYo.with(Techniques.SlideInUp).duration(200).delay(0).playOn(bResponse);
            YoYo.with(Techniques.SlideInUp).duration(200).delay(0).playOn(bNotResponse);
        }
    }


    private void closeProgressDialog()
    {
        if (dlg != null) {
            dlg.dismiss();
        }
    }
    private void showProgressDialog()
    {
        if (getFragmentManager().findFragmentByTag("ProgressDialog") == null) {
            if (dlg == null) {
                dlg = new ProgressDialog();
            }
            dlg.show(getFragmentManager(), "ProgressDialog");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
       /* Random random = new Random();
        int i = Math.abs(random.nextInt() % ControlButton.BUTTON_TYPES_ICONS.length);
        button.getIcon().setImageResource(ControlButton.BUTTON_TYPES_ICONS[i]);*/
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onClick(View v) {
        showProgressDialog();
        ServerApi api = ServerApi.getInstance(getActivity());
        if (controls == null || controls.size() == 0) {
            api.getDevicesByBrand(companyName, responseListenerList, errorListener);
        } else {
            api.getDevice(companyName, controls.get(position).getName(),responseListenerItem, errorListener);
        }
    }
}
