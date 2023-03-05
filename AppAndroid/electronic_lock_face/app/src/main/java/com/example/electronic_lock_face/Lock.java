package com.example.electronic_lock_face;

public class Lock {
    private String id, nickname, unicode, stat, stat_lock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getStat_lock() {
        return stat_lock;
    }

    public void setStat_lock(String stat_lock) {
        this.stat_lock = stat_lock;
    }
}
