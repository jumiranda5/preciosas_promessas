package com.alf.preciosaspromessas.utils;

public class Verse {

    int _id;
    String verse;

    public Verse() {}

    // constructor
    public Verse(int id, String verse){
        this._id = id;
        this.verse = verse;
    }

    // Getters
    public int getId() { return _id; }
    public String getVerse() { return verse; }

    // Setters
    public void setId(int id) { this._id = id; }
    public void setVerse(String verse) { this.verse = verse; }

}
