package com.davidread.notes;

/**
 * Model class that represents a note with a unique key, title, and description.
 */
public class Note {

    private long key;
    private String title;
    private String description;

    public Note(long key, String title, String description) {
        this.key = key;
        this.title = title;
        this.description = description;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}