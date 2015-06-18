package ru.obsession.iremote.rooms;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFloat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.Room;
import ru.obsession.iremote.select_control.SelectControlActivity;
import ru.obsession.iremote.views.SimpleSwipeListener;
import ru.obsession.iremote.views.SwipeLayout;

public class AdapterControls extends RecyclerView.Adapter<AdapterControls.ViewHolder>{

    public static final int TITLE = -1;
    public ArrayList<Control> controls;
    private HashMap<Integer, ViewHolder> views;
    private View.OnClickListener listener;
    private Room room;
    private FragmentActivity activity;

    public AdapterControls(FragmentActivity context, Room room, View.OnClickListener listener) {
        views = new HashMap<>();
        this.listener = listener;
        this.room = room;
        this.controls = DatabaseApi.getInstance(context).getControls(room);
        activity = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != TITLE) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.control_delete_item, parent, false));
        } else {
            return new ViewTitle(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_title, parent, false), room);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // добавляется без удаления, потому что лейауты используются повторно
        if (!views.containsKey(holder.tag) && position != 0) {
            views.put(holder.tag, holder);
        }
        if (position == 0) {
            holder.setOnClickListener(position);
        } else {
            holder.setOnClickListener(position - 1);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        if (holder.swipeLayout != null) {
            holder.swipeLayout.close();
        }
        super.onViewDetachedFromWindow(holder);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return controls.size() + 1;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        int tag;
        private SwipeLayout swipeLayout;
        private Button bDelete;
        private TextView company;
        private TextView model;
        private ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            tag = (new Random()).nextInt();
            company = (TextView) v.findViewById(R.id.textCompany);
            model = (TextView) v.findViewById(R.id.textModel);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe);
            bDelete = (Button) v.findViewById(R.id.delete);
            imageView = (ImageView) v.findViewById(R.id.imageIcon);
        }

        public void setOnClickListener(final int position) {
            final Control control = controls.get(position);
            company.setText(control.getCompany());
            model.setText(control.getName());
            imageView.setImageResource(control.getIcon());
            imageView.setBackgroundColor(control.getColor(imageView.getResources()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(RoomsActivity.SELECTED_ROOM, room);
                    intent.putExtra(SelectControlActivity.CONTROL_EXTRA, control);
                    activity.setResult(RoomsActivity.RESULT_OK, intent);
                    activity.finish();
                }
            });
            bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // минус один, топому что первый элемент заголовок
                    AdapterControls.this.controls.remove(getPosition() - 1);
                    DatabaseApi.getInstance(itemView.getContext()).removeControl(control);
                    AdapterControls.this.notifyItemRemoved(getPosition());
                }
            });
            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {
                    for (ViewHolder holder : views.values()) {
                        if (holder.tag != tag) {
                            holder.swipeLayout.close();
                        }
                    }
                }
            });
        }
    }

    protected class ViewTitle extends ViewHolder {

        ButtonFloat buttonFloat;

        public ViewTitle(View v, Room room) {
            super(v);
            buttonFloat = (ButtonFloat) v.findViewById(R.id.bEdit);
            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText(room.name);
            ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
            imageView.setImageResource(room.iconRes);
        }

        @Override
        public void setOnClickListener(int position) {
            buttonFloat.setOnClickListener(listener);
        }
    }
}