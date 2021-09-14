package com.mobdeve.group36.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mobdeve.group36.Model.DatabaseModel;
import com.mobdeve.group36.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class ProfileUpdate extends BottomSheetDialogFragment {
    Context context;
    Boolean isUsername;

    EditText et_user_input_bottom_sheet_fragment;
    TextView btnSave;
    TextView btnCancel;
    DatabaseModel databaseViewModel;
    String username;
    String bio;

    public ProfileUpdate(Context context, Boolean isUsername) {
        this.context = context;
        this.isUsername = isUsername;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_save_profile, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        view.findViewById(R.id.et_user_input_bottom_sheet_fragment).requestFocus();
        init(view);

        return view;
    }

    private void updateUsernameAndBio() {
        if(isUsername){
            username = et_user_input_bottom_sheet_fragment.getText().toString().trim();
            databaseViewModel.addUsernameInDatabase("username",username);
        }else{
            bio = et_user_input_bottom_sheet_fragment.getText().toString().trim();
            databaseViewModel.addBioInDatabase("bio", bio);
        }
        dismiss();

    }

    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(DatabaseModel.class);

        et_user_input_bottom_sheet_fragment = view.findViewById(R.id.et_user_input_bottom_sheet_fragment);
        btnSave = view.findViewById(R.id.btn_save_bottom_sheet);
        btnCancel = view.findViewById(R.id.btn_cancel_bottom_sheet);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsernameAndBio();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }


}
