package com.example.notes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.network.NotesApis;
import com.example.notes.offlineDatabase.NoteDatabase;
import com.example.notes.request.AddNoteRequest;
import com.example.notes.response.Note;
import com.example.notes.response.StateResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private static final String TAG = "NotesAdapterii";
    private ArrayList<Note> listdata;
    private Context mContext;

    public NotesAdapter(ArrayList<Note> listdata, Context con) {
        this.listdata = listdata;
        this.mContext = con;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Note myListData = listdata.get(position);
        holder.title.setText(myListData.getTitle());
        holder.body.setText(myListData.getBody());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    deleteOnline(myListData, position);
                    deleteOfflineDatabase(myListData, position);
                } else {
                    deleteOfflineDatabase(myListData, position);
                }
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View vv = inflater.inflate(R.layout.add_note, null);
                EditText titleBt = vv.findViewById(R.id.title_bt);
                EditText noteBt = vv.findViewById(R.id.note_bt);
                titleBt.setText(myListData.getTitle());
                noteBt.setText(myListData.getBody());
                Button editNoteBt = vv.findViewById(R.id.add_note_bt);
                editNoteBt.setText("edit note");
                final BottomSheetDialog bt = new BottomSheetDialog(mContext);
                bt.setContentView(vv);
                bt.show();
                editNoteBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!(titleBt.getText().toString().isEmpty() || noteBt.getText().toString().isEmpty())) {
                            AddNoteRequest addNoteRequest = new AddNoteRequest();
                            addNoteRequest.setTitle(titleBt.getText().toString());
                            addNoteRequest.setBody(noteBt.getText().toString());
                            String title=titleBt.getText().toString(),body=noteBt.getText().toString();
                            if (isNetworkConnected()) {
                                editOnline(title, body, myListData, position, addNoteRequest, bt);
                                editOffline(myListData,title,body,position,bt);
                            } else {
                                editOffline(myListData,title,body,position,bt);
                            }


                        } else
                            Toast.makeText(mContext, "some fields is empty", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, body;
        Button delete, edit;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.note_title);
            this.body = itemView.findViewById(R.id.note_body);
            this.delete = itemView.findViewById(R.id.delete);
            this.edit = itemView.findViewById(R.id.edit);
        }
    }

    private void deleteOnline(Note myListData, int position) {
        OnlineMethod.getConnect().deleteNotes(myListData.getId(), SharedPref.getToken()).enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                Log.i(TAG, "onResponse: " + myListData.getId() + " . " + SharedPref.getToken());
                //   Toast.makeText(mContext, ""+myListData.getId()+" . "+SharedPref.getToken(), Toast.LENGTH_SHORT).show();
                if (response.body().isState()) {
                    Toast.makeText(mContext, "note deleted", Toast.LENGTH_SHORT).show();
                    listdata.remove(position);
                    // recycler.removeViewAt(position);
                    //  notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listdata.size());
                } else {
                    Toast.makeText(mContext, "there is problem" + response.body().isState(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }

    private void deleteOfflineDatabase(Note notePara, int position) {
        Note note = notePara;
        NoteDatabase.getInstance(mContext).noteDao().deleteNote(note);
        listdata.remove(position);
        notifyItemRangeChanged(position, listdata.size());
    }

    private void editOnline(String tilteBt, String bodyBt, Note myListData, int position, AddNoteRequest addNoteRequest, BottomSheetDialog bt) {
        OnlineMethod.getConnect().editNotes(SharedPref.getToken(), addNoteRequest, myListData.getId()).enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                if (response.body().isState()) {
                    bt.dismiss();
                    listdata.get(position).setTitle(tilteBt);//"titleBt.getText().toString()"
                    listdata.get(position).setBody(bodyBt);//noteBt.getText().toString()
                    notifyItemRangeChanged(position, listdata.size());
                } else
                    Toast.makeText(mContext, "there is error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }
private void editOffline(Note note,String tilteBt, String bodyBt,int position,BottomSheetDialog bt){
        NoteDatabase.getInstance(mContext).noteDao().updateNote(note);
    bt.dismiss();
    listdata.get(position).setTitle(tilteBt);//"titleBt.getText().toString()"
    listdata.get(position).setBody(bodyBt);//noteBt.getText().toString()
    notifyItemRangeChanged(position, listdata.size());

}


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}

