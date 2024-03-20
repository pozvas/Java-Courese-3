package com.example.arrow;

public class Message {
    public Action act;
    public String data;

    public Message(Action act, String data) {
        this.act = act;
        this.data = data;
    }

    public Action getAct() {
        return act;
    }

    public String getData() {
        return data;
    }

    public void setAct(Action act) {
        this.act = act;
    }

    public void setData(String data) {
        this.data = data;
    }
}
