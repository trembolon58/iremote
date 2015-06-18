package ru.obsession.iremote.rooms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import ru.obsession.iremote.R;

public class AdapterRoomImages extends RecyclerView.Adapter<AdapterRoomImages.ViewHolder>{

    ImageSwitcher switcher;
    int[] imagesPrev;
    int[] images;
    OnAdapterImageChanged listener;

    public AdapterRoomImages(int[]images, int[] imagesPrev, ImageSwitcher switcher, OnAdapterImageChanged listener) {
        this.imagesPrev = imagesPrev;
        this.images = images;
        this.switcher = switcher;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.img_preview, parent, false), switcher, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnClickListener(position);
    }

    @Override
    public int getItemCount() {
        return imagesPrev.length;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private ImageSwitcher switcher;
        OnAdapterImageChanged listener;

        public ViewHolder(View v, ImageSwitcher switcher, OnAdapterImageChanged listener) {
            super(v);
            this.switcher = switcher;
            this.listener = listener;
        }

        public void setOnClickListener(final int position) {
            ((ImageView) itemView).setImageResource(imagesPrev[position]);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switcher.setImageResource(images[position]);
                    listener.OnChanged(position);
                }
            });

        }
    }
}
