package ru.obsession.iremote.models;

import java.io.Serializable;

import ru.obsession.iremote.R;

public class Room implements Serializable{

    public int iconRes = R.drawable.room;
    public String name = "no_name";
    private int id;

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public void setId(long id) {
        this.id = (int) id;
    }
}
