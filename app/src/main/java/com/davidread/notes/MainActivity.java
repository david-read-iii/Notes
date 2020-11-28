package com.davidread.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The main activity is the first activity that greets the user. It contains a list view of notes
 * that are sourced from an array list. The user may add notes to the list by pressing the add note
 * action bar button and they will be sent to the detail activity to specify the attributes of the
 * note. Once they return to this activity, it will perform the appropriate functions to add the
 * note to the array list. A similar process applies to modification operations the user may take
 * when clicking on a note on the list view. An array adapter adapts the array list notes to the list
 * view. In between app executions, the array list is stored to persistent storage using shared
 * preferences.
 *
 * Shared preference code is adapted from one of Coding in Flow's tutorials, which can be found at
 * https://codinginflow.com/tutorials/android/save-arraylist-to-sharedpreferences-with-gson.
 */
public class MainActivity extends AppCompatActivity {

    // Request and result code constants.
    public static final int REQUEST_ADD_NOTE = 0;
    public static final int REQUEST_MODIFY_NOTE = 1;
    public static final int RESULT_SAVE_NOTE = 0;
    public static final int RESULT_DELETE_NOTE = 1;
    public static final int RESULT_DISCARD_NOTE = 2;

    // Intent extra constants.
    public static final String INTENT_EXTRA_KEY = "KEY";
    public static final String INTENT_EXTRA_TITLE = "TITLE";
    public static final String INTENT_EXTRA_DESCRIPTION = "DESCRIPTION";

    // Shared preference constants.
    public static final String SHARED_PREFERENCES_FILE_NAME = "SHARED_PREFERENCES";
    public static final String SHARED_PREFERENCES_STRING_JSON = "JSON";

    // List view and helper objects.
    private ArrayList<Note> notes;
    private NotesAdapter notesAdapter;
    private ListView listView;

    // Shared preference and helper objects.
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private String json;
    private Type type;

    /**
     * First function called when the activity executes. It sets up the list view and specifies what to
     * do in response to clicks.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Attempt to get array list from shared preferences. If shared preferences had no array
         * list, initialize as empty array list. */
        notes = getNotesFromSharedPreferences();

        if (notes == null)
            notes = new ArrayList<>();

        // Initialize adapter for list view.
        notesAdapter = new NotesAdapter(this, notes);

        // Initialize list view.
        listView = findViewById(R.id.list_view_notes);
        listView.setAdapter(notesAdapter);

        /* Specify list view clicks to start the detail activity with request code 1. Pass
         * attributes of the clicked note as intent extras to the activity. */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Note selectedNote = (Note) notesAdapter.getItem(i);

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(INTENT_EXTRA_KEY, selectedNote.getKey());
                intent.putExtra(INTENT_EXTRA_TITLE, selectedNote.getTitle());
                intent.putExtra(INTENT_EXTRA_DESCRIPTION, selectedNote.getDescription());

                startActivityForResult(intent, REQUEST_MODIFY_NOTE);
            }
        });
    }

    /**
     * Is called when the activity leaves the foreground. It calls saveNotesToSharedPreferences(),
     * to save the current state of the array list to shared preferences.
     */
    @Override
    protected void onPause() {
        saveNotesToSharedPreferences();
        super.onPause();
    }

    /**
     * Is called when an activity finishes and goes back to this one. In this case, this is the
     * detail activity. A request code, result code, and intent are returned from the detail
     * activity. The request code corresponds to the type of action the user was doing when they
     * initially launched the detail activity. This is either adding a note or modifying a note.
     * The result code corresponds to the type of action the user accomplished when they finished
     * the detail activity. This is either saving a note, deleting a note, or discarding a note.
     * Given the request and result code, this function takes the appropriate steps to update
     * the array list.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* If user was saving the note, add new note to array list. If they were modifying an
         * existing note, delete the old one before adding the new one. */
        if (resultCode == RESULT_SAVE_NOTE) {
            if (requestCode == REQUEST_MODIFY_NOTE)
                deleteNote(data.getLongExtra(INTENT_EXTRA_KEY, -1));

            addNote(data.getStringExtra(INTENT_EXTRA_TITLE), data.getStringExtra(INTENT_EXTRA_DESCRIPTION));
            notesAdapter.notifyDataSetChanged();
        }

        // If the user was deleting an existing note, then delete it from the array list.
        else if (resultCode == RESULT_DELETE_NOTE && requestCode == REQUEST_MODIFY_NOTE) {
            deleteNote(data.getLongExtra(INTENT_EXTRA_KEY, -1));
            notesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This function defines what menu file specifies the action bar buttons.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This function specifies what to do in response to action bar clicks.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // If add note action is pressed, start the detail activity with request code 0.
        if (id == R.id.action_add_note) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            startActivityForResult(intent, REQUEST_ADD_NOTE);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This function adds a note with the specified title and description to the array list.
     */
    public void addNote(String title, String description) {
        notes.add(new Note(
                System.currentTimeMillis(),
                title,
                description
        ));
    }

    /**
     * This function deletes a note with the specified key from the array list.
     */
    public void deleteNote(long key) {
        Iterator<Note> iterator = notes.iterator();
        while(iterator.hasNext()) {
            Note note = iterator.next();
            if (note.getKey() == key) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * This function returns the saved array list from shared preferences. If no array list is
     * saved, it returns null.
     */
    public ArrayList<Note> getNotesFromSharedPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        gson = new Gson();
        json = sharedPreferences.getString(SHARED_PREFERENCES_STRING_JSON, null);
        type = new TypeToken<ArrayList<Note>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * This function saves the array list to shared preferences.
     */
    public void saveNotesToSharedPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        json = gson.toJson(notes);
        editor.putString(SHARED_PREFERENCES_STRING_JSON, json);
        editor.apply();
    }
}