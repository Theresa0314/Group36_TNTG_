package com.mobdeve.group36.views.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.group36.Data.model.Chat;
import com.mobdeve.group36.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private static final int MSG_TYPE_LEFT_RECEIVED = 0;
    private static final int MSG_TYPE_RIGHT_RECEIVED = 1;
    private ArrayList<Chat> chatArrayList;
    private Context context;
    private String currentUser_sender;

    public MessageAdapter(ArrayList<Chat>chatArrayList, Context context, String currentUser_sender) {
        this.chatArrayList = chatArrayList;
        this.context = context;
        this.currentUser_sender = currentUser_sender;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_sent, parent, false);
            return new MessageHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_receive, parent, false);
            return new MessageHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Chat chats = chatArrayList.get(position);
        String message = chats.getMessage();
        String timeStamp = chats.getTimestamp();
        boolean isSeen = chats.getSeen();
        long intTimeStamp = Long.parseLong(timeStamp);
        String time_msg_received = timeStampConversionToTime(intTimeStamp);
        holder.tv_time.setText(time_msg_received);
        holder.tv_msg.setText(message);

        //click to show dialog box
        holder.rl_message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete message confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("DELETE");
                builder.setMessage("Are you sure you want to delete this message?");

                // delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //Create and show dialog box
                builder.create().show();
            }
        });

        if (position == chatArrayList.size() - 1) {
            if (isSeen) {
                holder.tv_seen.setVisibility(View.VISIBLE);
                String seen = "Seen";
                holder.tv_seen.setText(seen);
            } else {
                holder.tv_seen.setVisibility(View.VISIBLE);
                String delivered = "Delivered";
                holder.tv_seen.setText(delivered);
            }
        } else {
            holder.tv_seen.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int position) {
        //Logic:
        //Get timestamp of clicked message
        // compare clicked message with all messages in Chats
        //Same values == delete

        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp = chatArrayList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    //For user to only delete their own messages
                    if(ds.child("senderId").getValue().equals(myUID)) {
                        //Actual remove message from Chats
                        ds.getRef().removeValue();
                        Toast.makeText(context, "Message deleted.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(context, "Cannot delete this message.", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String timeStampConversionToTime(long timeStamp) {

        Date date = new Date(timeStamp);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat jdf = new SimpleDateFormat("hh:mm a");
        jdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return jdf.format(date);
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView tv_msg;
        TextView tv_time;
        TextView tv_seen;
        RelativeLayout rl_message_layout;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            tv_msg = itemView.findViewById(R.id.tv_chat_received);
            tv_time = itemView.findViewById(R.id.tv_chat_time_received);
            tv_seen = itemView.findViewById(R.id.tv_seen);
            rl_message_layout = itemView.findViewById(R.id.rl_message_layout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatArrayList.get(position).getReceiverId().equals(currentUser_sender)) {
            return MSG_TYPE_LEFT_RECEIVED;
        } else return MSG_TYPE_RIGHT_RECEIVED;
    }
}
