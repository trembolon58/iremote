package ru.obsession.iremote.views;

import android.content.Context;
import android.util.AttributeSet;

import com.gc.materialdesign.views.ButtonFloat;

import ru.obsession.iremote.R;

public class TrashControl extends ButtonFloat implements Draggable {

    private boolean editable;

    public TrashControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setDraggable(boolean draggable) {
        editable = draggable;
        setTrash();
    }

    @Override
    public void toggleDraggable() {
        editable = !editable;
        setTrash();
    }

    @Override
    public boolean isDrawed() {
        return false;
    }

    private void setTrash() {
        if (editable) {
            this.getIcon().setImageResource(R.drawable.trash);
        } else {
            this.getIcon().setImageResource(R.drawable.other);
        }
    }
}
