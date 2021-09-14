package com.mobdeve.group36.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.group36.Data.model.User;
import com.mobdeve.group36.Model.DatabaseModel;
import com.mobdeve.group36.R;
import com.mobdeve.group36.views.adapters.UserAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class UserFragment extends Fragment {
    private Context context;
    private DatabaseModel databaseViewModel;
    private ArrayList<User> mUSer;
    private String currentUserId;
    private RecyclerView recyclerView;
    private UserAdapter userFragmentAdapter;
    EditText et_search;

    public UserFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_fragment, container, false);
        init(view);
        fetchingAllUserNAme();
        return view;
    }



    private void fetchingAllUserNAme() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                User users = dataSnapshot.getValue(User.class);
                assert users != null;
                currentUserId = users.getId();
            }
        });

        databaseViewModel.fetchUserByNameAll();
        databaseViewModel.fetchUserNames.observe(getViewLifecycleOwner(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (et_search.getText().toString().equals("")) {
                    mUSer.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        assert user != null;
                        if (!(user.getEmailId() == null)
                        ) {
                            if (!currentUserId.equals(user.getId())) {
                                mUSer.add(user);

                            }
                        }
                        userFragmentAdapter = new UserAdapter(mUSer, context, false);
                        recyclerView.setAdapter(userFragmentAdapter);

                    }

                }
            }
        });
    }

    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(DatabaseModel.class);

        recyclerView = view.findViewById(R.id.user_list_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mUSer = new ArrayList<>();
        et_search = view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_search.getText().toString().startsWith(" "))
                    et_search.setText("");
            }
        });

    }

    private void searchUsers(String searchText) {

        if (!(searchText.isEmpty() && searchText.equals(""))) {
            databaseViewModel.fetchSearchedUser(searchText);
            databaseViewModel.fetchSearchUser.observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    mUSer.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User users = snapshot.getValue(User.class);
                        assert users != null;
                        if (!users.getId().equals(currentUserId)) {
                            mUSer.add(users);
                        }

                    }
                    userFragmentAdapter = new UserAdapter(mUSer, context, false);
                    recyclerView.setAdapter(userFragmentAdapter);

                }
            });
        }else {
            fetchingAllUserNAme();
        }
    }
}
