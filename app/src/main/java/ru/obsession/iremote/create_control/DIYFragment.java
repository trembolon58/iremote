package ru.obsession.iremote.create_control;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.ControlButton;
import ru.obsession.iremote.select_control.SelectControlActivity;
import ru.obsession.iremote.views.SimpleSwipeListener;
import ru.obsession.iremote.views.SwipeLayout;

public class DIYFragment extends Fragment implements View.OnClickListener {

    private ArrayList<ControlButton> buttons;
    public static final String TAG = "DIYFragment_tag";
    private Control control;

    public void buttonCreated(ControlButton controlButton) {
        buttons.add(controlButton);
    }

    public void buttonChanged(ControlButton controlButton) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttons = new ArrayList<>();
        control = (Control) getArguments().getSerializable(Control.KEY);
        buttons = DatabaseApi.getInstance(getActivity()).getButtons(control.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_diy_fragment);
        View root = inflater.inflate(R.layout.fragment_diy, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.listView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new DIYAdapter(getActivity()));
        ButtonFloat button = (ButtonFloat) root.findViewById(R.id.bAdd);
        button.setOnClickListener(this);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.diy, menu);
    }

    private void setResult() {
        DatabaseApi.getInstance(getActivity()).saveDIYRemote();
        Intent intent = new Intent();
        //TODO: CREATE CONFIG_FILE
        Control control = new Control("LG", "Noname");
        control.setType(Control.CUSTOM);
        intent.putExtra(SelectControlActivity.CONTROL_EXTRA, control);
        getActivity().setResult(SelectControlActivity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //TODO
                if (buttons.size() == 0) {
                    SnackBar snackbar = new SnackBar(getActivity(),
                            getString(R.string.question_not_add_any_button),
                            getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult();
                        }
                    });
                    snackbar.show();
                    return true;
                }
                setResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new ButtonFragment();
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    public class DIYAdapter extends RecyclerView.Adapter<DIYAdapter.ViewHolder> {

        private FragmentActivity activity;
        private HashMap<Integer, ViewHolder> views;

        public DIYAdapter(FragmentActivity context) {
            activity = context;
            views = new HashMap<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_button_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // добавляется без удаления, потому что лейауты используются повторно
            if (!views.containsKey(holder.tag)) {
                views.put(holder.tag, holder);
            }
            holder.setOnClickListener(position);
        }

        @Override
        public int getItemCount() {
            return buttons.size();
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            if (holder.swipeLayout != null) {
                holder.swipeLayout.close();
            }
            super.onViewDetachedFromWindow(holder);

        }

        protected class ViewHolder extends RecyclerView.ViewHolder {

            int tag;
            private SwipeLayout swipeLayout;
            private Button bDelete;
            private ImageView imageView;
            private TextView textView;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.name_text);
                imageView = (ImageView) v.findViewById(R.id.image_view);
                tag = (new Random()).nextInt();
                swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
                bDelete = (Button) v.findViewById(R.id.delete);
            }

            public void setOnClickListener(int position) {
                final ControlButton controlButton = buttons.get(position);
                imageView.setImageResource(controlButton.icon);
                textView.setText(controlButton.name);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new ButtonFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Control.KEY, control);
                        bundle.putParcelable(ControlButton.KEY, controlButton);
                        fragment.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction().
                                replace(R.id.container, fragment).addToBackStack(null).commit();
                    }
                });

                bDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttons.remove(getPosition());
                        DIYAdapter.this.notifyItemRemoved(getPosition());
                    }
                });

                swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                    }

                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        for (ViewHolder holder : DIYAdapter.this.views.values()) {
                            if (holder.tag != tag) {
                                holder.swipeLayout.close();
                            }
                        }
                    }
                });
            }
        }
    }
}
