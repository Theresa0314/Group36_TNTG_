package com.mobdeve.group36.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mobdeve.group36.Data.firebase.FirebaseSignUpInstance;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class SigninModel extends ViewModel {

    private FirebaseSignUpInstance signUpInstance;
    public LiveData<Task> signInUser;
    public  LiveData<FirebaseUser> userFirebaseSession;

    public SigninModel() {
        signUpInstance = new FirebaseSignUpInstance();
    }

    public void userSignIn(String userNameSignIn, String emailSignIn, String passwordSignIn) {
        signInUser = signUpInstance.signInUser(userNameSignIn, emailSignIn, passwordSignIn);
    }


    public void getUserFirebaseSession(){
        userFirebaseSession = signUpInstance.firebaseUsers;
    }


}
