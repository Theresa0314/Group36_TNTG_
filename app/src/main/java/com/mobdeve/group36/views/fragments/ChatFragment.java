package com.mobdeve.group36.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.group36.Data.model.ChatList;
import com.mobdeve.group36.Data.model.User;
import com.mobdeve.group36.Model.DatabaseModel;
import com.mobdeve.group36.Model.LoginModel;
import com.mobdeve.group36.R;
import com.mobdeve.group36.views.adapters.UserAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private Context context;
    private UserAdapter userAdapter;
    private ArrayList<User> mUsers;
    private String currentUserId;
    private ArrayList<ChatList> userList;  //list of all other users with chat record
    private DatabaseModel databaseViewModel;
    private LoginModel logInViewModel;
    private RecyclerView recyclerView_chat_fragment;
    RelativeLayout relative_layout_chat_fragment;

    public ChatFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        init(view);
        fetchAllChat();



        return view;
    }


    private void fetchAllChat() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                User users = dataSnapshot.getValue(User.class);
                assert users != null;
                currentUserId = users.getId();
            }
        });

        databaseViewModel.getChaListUserDataSnapshot(currentUserId);
        databaseViewModel.getChaListUserDataSnapshot.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatList chatList = dataSnapshot1.getValue(ChatList.class);
                    userList.add(chatList);
                }

                chatLists();
            }
        });


    }

    private void chatLists() {
        databaseViewModel.fetchUserByNameAll();
        databaseViewModel.fetchUserNames.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User users = dataSnapshot1.getValue(User.class);
                    for (ChatList chatList : userList) {
                        assert users != null;
                        if (users.getId().equals(chatList.getId())) {
                            if(!mUsers.contains(users))
                                mUsers.add(users);
                        }
                    }
                }
                if(mUsers.size()<1){
                    relative_layout_chat_fragment.setVisibility(View.VISIBLE);
                }else {
                    relative_layout_chat_fragment.setVisibility(View.GONE);
                }

                userAdapter = new UserAdapter(mUsers, context, true);
                recyclerView_chat_fragment.setAdapter(userAdapter);
            }
        });
    }


    private void updateToken(String token) {
        logInViewModel.updateToken(token);
    }


    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(DatabaseModel.class);

        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(LoginModel.class);

        relative_layout_chat_fragment = view.findViewById(R.id.relative_layout_chat_fragment);
        recyclerView_chat_fragment = view.findViewById(R.id.recycler_view_chat_fragment);
        recyclerView_chat_fragment.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_chat_fragment.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView_chat_fragment.addItemDecoration(dividerItemDecoration);
        mUsers = new ArrayList<>();
        userList = new ArrayList<>();

    }
}
