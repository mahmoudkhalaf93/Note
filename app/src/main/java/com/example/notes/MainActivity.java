package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notes.offlineDatabase.NoteDatabase;
import com.example.notes.request.AddNoteRequest;
import com.example.notes.response.Note;
import com.example.notes.response.NotesResponse;
import com.example.notes.response.StateResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity1";
    static String token;
    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    ArrayList<Note> arraynote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPref.getInstance(this);
        if (SharedPref.getToken().isEmpty()) {
            //go to log in activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //if it's not first time to open app
            if (!SharedPref.getFirstTimeRun().isEmpty()) {
                if (isNetworkConnected()) {
                    //so copy the offline database state to online database state
                    SyncNotes();
                } else
                    Toast.makeText(this, "Please Connect", Toast.LENGTH_SHORT).show();
            }
            chooseOfforOnlineNote();
        }


    }

    private void SyncNotes() {
        OnlineMethod.getConnect().getNotes(SharedPref.getToken()).enqueue(new Callback<NotesResponse>() {
            @Override
            public void onResponse(Call<NotesResponse> call, Response<NotesResponse> response) {
                if (response.body().isState()) {

                    //copy added note from offline database to online database
                    for (Note n : (ArrayList<Note>) NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes()) {
                        if (!n.getUploadedState()) {
                            addNotesOnline(n.getTitle(), n.getBody());
                        }
                    }
                    //edit note that changed in offline database to change it in online database too
                    for (Note i : response.body().getNotes()) {
                        for (int j = 0; j <  NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().size(); j++) {
                            if (i.getId() ==  NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getId()) {
                                if (!(i.getTitle().equals( NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getTitle()) || i.getBody().equals( NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getBody()))) {
                                    editOnline( NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getTitle(), NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getBody(), i.getId());
                                }
                            }
                        }

                    }
                    //Delete the note from online database which deleted from offline database
                    for (Note i : response.body().getNotes()) {
                        boolean notFound = true;
                        for (int j = 0; j <  NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().size(); j++) {
                            //if the note in online founed in offlineDatabase then break and don't delete ( (notFound = false ) = found)
                            if (i.getId() ==  NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getId()) {
                                Log.i(TAG, "onResponse: "+i.getId()+" : "+ NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes().get(j).getId());
                                notFound = false;
                                break;
                            }
                        }
                        if (notFound)
                        {    deleteOnline(i.getId());
                             }
                    }

                }
            }

            @Override
            public void onFailure(Call<NotesResponse> call, Throwable t) {

            }
        });
    }

    //the method display current state offline notes but first time open app it's copy the online notes to the offline note
    //so this method choose online note will disply or offline note based on it's first time to open application or not first time
    public void chooseOfforOnlineNote() {
        //if first time to open app you will need to get the data from online database
        if (SharedPref.getFirstTimeRun().isEmpty()) {
            if (isNetworkConnected()) {
                OnlineMethod.getConnect().getNotes(SharedPref.getToken()).enqueue(new Callback<NotesResponse>() {
                    @Override
                    public void onResponse(Call<NotesResponse> call, Response<NotesResponse> response) {
                        for (Note n : response.body().getNotes()) {
                            n.setUploadedState(true);
                            NoteDatabase.getInstance(MainActivity.this).noteDao().addNote(n);
                        }
                        displayOfflineNotes((ArrayList<Note>) NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes());
                        SharedPref.setFirstTimeRun();
                    }

                    @Override
                    public void onFailure(Call<NotesResponse> call, Throwable t) {
                    }
                });
            } else
                Toast.makeText(this, "Please Connect Internet to Sync", Toast.LENGTH_SHORT).show();
        } else {
                displayOfflineNotes((ArrayList<Note>) NoteDatabase.getInstance(MainActivity.this).noteDao().getNotes());
        }
    }
    //the offline delete done in NotesAdapter
    //thie method of Sync
    private void deleteOnline(int id) {
        OnlineMethod.getConnect().deleteNotes(id, SharedPref.getToken()).enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {

                if (response.body().isState()) {

                } else {
                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }
    //the offline edit done in NotesAdapter
    //thie method of Sync
    private void editOnline(String titleBt, String noteBt, int id) {
        AddNoteRequest addNoteRequest = new AddNoteRequest();
        addNoteRequest.setTitle(titleBt);
        addNoteRequest.setBody(noteBt);
        OnlineMethod.getConnect().editNotes(SharedPref.getToken(), addNoteRequest, id).enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                if (response.body().isState()) {

                }
                //else
                // Toast.makeText(this, "there is error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }
    //add note to offline database
    private void addNoteOffline(String title, String body, Boolean stateOnline,int idOnline) {
        Note note = new Note();
        note.setTitle(title);
        note.setBody(body);
        note.setId(idOnline);
        note.setUploadedState(stateOnline);
        NoteDatabase.getInstance(this).noteDao().addNote(note);
        chooseOfforOnlineNote();
    }
    //add note to online database
    private void addNotesOnline(String title, String note) {
        AddNoteRequest addNoteRequest = new AddNoteRequest();
        addNoteRequest.setTitle(title);
        addNoteRequest.setBody(note);
        OnlineMethod.getConnect().addNotes(SharedPref.getToken(), addNoteRequest).enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                if (response.body().isState()) {
                    OnlineMethod.getConnect().getNotes(SharedPref.getToken()).enqueue(new Callback<NotesResponse>() {
                        @Override
                        public void onResponse(Call<NotesResponse> call, Response<NotesResponse> response) {
                         if(response.body().isState()){
                             addNoteOffline(title, note, true,response.body().getNotes().get(
                                     response.body().getNotes().size()-1
                             ).getId());
                         }

                        }

                        @Override
                        public void onFailure(Call<NotesResponse> call, Throwable t) {

                        }
                    });

                    Log.i(TAG, "onResponse: add note success  ");
                } else {

                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }

    private void displayOfflineNotes(ArrayList<Note> notes) {
        arraynote = notes;
        notesAdapter = new NotesAdapter(arraynote, MainActivity.this);
        recyclerView = findViewById(R.id.note_Rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(notesAdapter);

    }
//    private void displayOnlineNotes() {
//        OnlineMethod.getConnect().getNotes(SharedPref.getToken()).enqueue(new Callback<NotesResponse>() {
//            @Override
//            public void onResponse(Call<NotesResponse> call, Response<NotesResponse> response) {
//                if (response.body().isState()) {//int i=response.body().getNotes().size();
//                    //  Log.i(TAG, "onResponse: "+response.body().getNotes().get(i-1).getTitle());
//                    arraynote = response.body().getNotes();
//                    notesAdapter = new NotesAdapter(arraynote, MainActivity.this);
//                    recyclerView = findViewById(R.id.note_Rv);
//                    recyclerView.setHasFixedSize(true);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//                    recyclerView.setAdapter(notesAdapter);
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NotesResponse> call, Throwable t) {
//
//            }
//        });
//    }
    public void addNoteButton(View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View vv = inflater.inflate(R.layout.add_note, null);
        EditText titleBt = vv.findViewById(R.id.title_bt);
        EditText noteBt = vv.findViewById(R.id.note_bt);
        Button addToNoteBt = vv.findViewById(R.id.add_note_bt);
        final BottomSheetDialog bt = new BottomSheetDialog(this);
        bt.setContentView(vv);
        bt.show();
        addToNoteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(titleBt.getText().toString().isEmpty() || noteBt.getText().toString().isEmpty())) {
                    if (isNetworkConnected()) {
//                        addNoteOffline(titleBt.getText().toString(), noteBt.getText().toString(), true);
                        addNotesOnline(titleBt.getText().toString(), noteBt.getText().toString());
                        bt.dismiss();

                    } else {
                        addNoteOffline(titleBt.getText().toString(), noteBt.getText().toString(), false,-1);
                        bt.dismiss();
                    }

                } else
                    Toast.makeText(MainActivity.this, "some fields is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}