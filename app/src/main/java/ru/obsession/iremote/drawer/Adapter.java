package ru.obsession.iremote.drawer;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloatSmall;

import java.util.List;

import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Control;
import ru.obsession.iremote.models.Room;
import ru.obsession.iremote.rooms.EditRoomFragment;
import ru.obsession.iremote.rooms.RoomsActivity;
import ru.obsession.iremote.views.OnDrawerClickListener;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Control> controls;
    private final int HEADER = -6;
    private ViewHolder lastSelectedItem;
    private OnDrawerClickListener listener;
    private Room room;
    private FragmentActivity activity;

    public Adapter(FragmentActivity context, Room room, OnDrawerClickListener listener) {
        activity = context;
        this.listener = listener;
        this.room = room;
        controls = DatabaseApi.getInstance(context).getControls(room);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        View itemView;
        if (viewType == HEADER) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_menu_header, parent, false);
            holder = new ViewHolderHeader(itemView, listener);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_item, parent, false);
            holder = new ViewHolder(itemView, listener);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnClickListener(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0) {
            return super.getItemViewType(position);
        } else {
            return HEADER;
        }
    }

    @Override
    public int getItemCount() {
        return controls.size() + 1;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected OnDrawerClickListener listener;
        protected Control control;
        protected ImageView imageView;
        private TextView company;
        private TextView model;

        public ViewHolder(View v, OnDrawerClickListener listener) {
            super(v);
            this.listener = listener;
            company = (TextView) v.findViewById(R.id.textCompany);
            model = (TextView) v.findViewById(R.id.textModel);
            imageView = (ImageView) v.findViewById(R.id.imageIcon);
        }

        public void setOnClickListener(final int position) {
            control = controls.get(position - 1);
            company.setText(control.getCompany());
            model.setText(control.getName());
            imageView.setImageResource(control.getIcon());
            imageView.setBackgroundColor(control.getColor(imageView.getResources()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastSelectedItem != null) {
                        lastSelectedItem.itemView.setSelected(false);
                    }
                    lastSelectedItem = ViewHolder.this;
                    listener.OnClick(control);
                    itemView.setSelected(true);
                }
            });
        }
    }

    private class ViewHolderHeader extends ViewHolder{

        private ButtonFloatSmall bEdit;
        private ButtonFlat bRooms;
        public ViewHolderHeader(View v, OnDrawerClickListener listener) {
            super(v, listener);
            bEdit = (ButtonFloatSmall) v.findViewById(R.id.bEdit);
            bRooms = (ButtonFlat) v.findViewById(R.id.bRooms);
            TextView textView = (TextView) v.findViewById(R.id.textView);
            ImageView background = (ImageView) v.findViewById(R.id.roomIcon);
            textView.setText(room.name);
            background.setImageResource(room.iconRes);
        }

        @Override
        public void setOnClickListener(int position) {
            bEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, RoomsActivity.class);
                    intent.putExtra(RoomsActivity.EDIT_ROOM, true);
                    intent.putExtra(EditRoomFragment.ROOM_PARAM, room);
                    activity.startActivityForResult(intent, 1);
                }
            });
            bRooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, RoomsActivity.class);
                    activity.startActivityForResult(intent, 1);
                }
            });
        }
    }
}
