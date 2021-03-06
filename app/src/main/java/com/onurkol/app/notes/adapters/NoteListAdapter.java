package com.onurkol.app.notes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.onurkol.app.notes.R;
import com.onurkol.app.notes.activity.EditNoteActivity;
import com.onurkol.app.notes.data.NoteData;

import java.util.List;

import static com.onurkol.app.notes.activity.MainActivity.noteListViewWeak;
import static com.onurkol.app.notes.controller.NoteController.removeNote;
import static com.onurkol.app.notes.data.PreferenceData.APP_NOTES;
import static com.onurkol.app.notes.popups.PopupEditNewNote.showNewNoteDialog;
import static com.onurkol.app.notes.popups.PopupLockUnlockOpenNote.showLockUnlockOpenDialog;

public class NoteListAdapter extends ArrayAdapter<NoteData> {
    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final List<NoteData> notes;

    public NoteListAdapter(Context context, List<NoteData> notes){
        super(context,0, notes);
        this.context=context;
        this.notes=notes;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).hashCode();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            convertView=inflater.inflate(R.layout.list_notes, null);
            holder=new ViewHolder();
            holder.noteName=convertView.findViewById(R.id.noteNameTextView);
            holder.noteDeleteButton=convertView.findViewById(R.id.noteDeleteButton);
            holder.noteEditButton=convertView.findViewById(R.id.noteEditButton);
            holder.noteOpenButton=convertView.findViewById(R.id.noteOpenLayout);
            holder.noteLockStatus=convertView.findViewById(R.id.noteLockStatus);
            holder.noteLockUnlockButton=convertView.findViewById(R.id.noteLockUnlockButton);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        // Get Current Note Data
        final NoteData curNote=notes.get(position);

        // Set List Item Data
        holder.noteName.setText(curNote.getNoteName());
        // Card View Style
        int textColor,background;
        background=curNote.getNoteColor();
        // Check Text Color
        Resources res=context.getResources();
        if(background==res.getColor(R.color.cardColorWhite) ||
                background==res.getColor(R.color.cardColorYellow) ||
                background==res.getColor(R.color.cardColorLime) ||
                background==res.getColor(R.color.cardColorAmber))
            textColor=context.getResources().getColor(R.color.black);
        else
            textColor=context.getResources().getColor(R.color.white);
        holder.noteOpenButton.setCardBackgroundColor(background);
        holder.noteName.setTextColor(textColor);
        holder.noteDeleteButton.setColorFilter(textColor);
        holder.noteEditButton.setColorFilter(textColor);
        holder.noteLockStatus.setColorFilter(textColor);
        holder.noteLockUnlockButton.setColorFilter(textColor);

        // Check Note Lock Status
        // Show lock image and Button events.
        String notePass=curNote.getNotePassword();
        boolean lockMode;
        if(notePass==null || notePass.equals("null")) {
            holder.noteLockStatus.setVisibility(View.GONE);
            holder.noteLockUnlockButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_lock_24));
        }
        else {
            holder.noteLockStatus.setVisibility(View.VISIBLE);
            holder.noteLockUnlockButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_round_lock_open_24));
        }

        // Button Click Event
        holder.noteDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.sure_delete_note_text))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(context.getString(R.string.yes_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Update Data
                                removeNote(curNote.getNoteID(),curNote.getNoteName(),curNote.getNoteText(),curNote.getNotePassword(),curNote.getNoteColor());
                                // Refresh ListView
                                notes.remove(position);
                                noteListViewWeak.get().invalidateViews();
                                // Update Array
                                APP_NOTES=notes;
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no_text), null)
                        .show();
            }
        });

        holder.noteEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewNoteDialog(position);
            }
        });

        holder.noteOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Password Dialog.
                showLockUnlockOpenDialog(position,false);
            }
        });

        holder.noteLockUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLockUnlockOpenDialog(position,true);
            }
        });

        return convertView;
    }


    // View Holder
    private static class ViewHolder {
        TextView noteName;
        ImageButton noteDeleteButton,noteEditButton,noteLockUnlockButton;
        ImageView noteLockStatus;
        CardView noteOpenButton;
    }
}
