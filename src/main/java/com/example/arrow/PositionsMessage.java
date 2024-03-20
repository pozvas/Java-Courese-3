package com.example.arrow;

public class PositionsMessage {
    public Action act;
    public Positions data;

    public PositionsMessage(Action act, Positions data) {
        this.act = act;
        this.data = data;
    }

    public Action getAct() {
        return act;
    }

    public Positions getData() {
        return data;
    }

    public void setAct(Action act) {
        this.act = act;
    }

    public void setData(Positions data) {
        this.data = data;
    }
}

