package ru.obsession.iremote.rooms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ru.obsession.iremote.R;
import ru.obsession.iremote.database.DatabaseApi;
import ru.obsession.iremote.models.Room;
import ru.obsession.iremote.views.SimpleSwipeListener;
import ru.obsession.iremote.views.SwipeLayout;

public class AdapterRooms extends RecyclerView.Adapter<AdapterRooms.ViewHolder> {

    private ArrayList<Room> roomsList;
    private HashMap<Integer, ViewHolder> views;
    private FragmentManager fm;

    public AdapterRooms(Context context, FragmentManager fm) {
        views = new HashMap<>();
        this.fm = fm;
        this.roomsList = DatabaseApi.getInstance(context).getRooms();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rooms_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!views.containsKey(holder.tag)) {
            views.put(holder.tag, holder);
        }
        holder.setOnClickListener(views, this, roomsList.get(position), fm);
    }


    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.swipeLayout.close();
    //    views.remove(holder.tag);
    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private SwipeLayout swipeLayout;
        int tag;
        private Button bDelete;
        private TextView textView;
        private ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            tag = (new Random()).nextInt();
            bDelete = (Button) v.findViewById(R.id.delete);
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.image_view);
        }
        public void setOnClickListener(final HashMap<Integer, ViewHolder> viewes,
                                       final AdapterRooms adapter, final Room room,
                                       final FragmentManager fm)
        {
            textView.setText(room.name);
            imageView.setImageResource(room.iconRes);
            bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseApi.getInstance(itemView.getContext()).removeRoom(room);
                    adapter.roomsList.remove(getPosition());
                    adapter.notifyItemRemoved(getPosition());
                }
            });
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new EditRoomFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EditRoomFragment.ROOM_PARAM, room);
                    fragment.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
                }
            });
            swipeLayout.addSwipeListener(new SimpleSwipeListener()
            {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                    for (ViewHolder holder : viewes.values()) {
                        if (holder.tag != tag) {
                            holder.swipeLayout.close();
                        }
                    }
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {
                    for (ViewHolder holder : viewes.values()) {
                        if (holder.tag != tag) {
                            holder.swipeLayout.close();
                        }
                    }
                }
            });
        }
    }
}
