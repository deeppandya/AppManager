package com.deeppandya.appmanager.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.deeppandya.appmanager.managers.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by deeppandya on 2017-07-27.
 */

public class FirebaseUtil {

    public static void saveUserFeedbackToFirebase(float rating,String feedback) {

        Map<String,Object> userFeedback=new HashMap<>();
        userFeedback.put("rating",rating);
        userFeedback.put("feedback",feedback);

        FirebaseManager.getFirebaseDatabase().getReference().child("user_rating_feedback").child(FirebaseManager.getFirebaseAuth().getCurrentUser().getUid()).setValue(userFeedback);
    }

    public static void anonymousUserLogin() {
        FirebaseManager.getFirebaseAuth().signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }

    public static void saveBugReportToFirebase(String databaseName,String title, String desc, String email, String deviceInfo) {

        if(!TextUtils.isEmpty(databaseName)) {
            Map<String, Object> userFeedback = new HashMap<>();
            userFeedback.put("title", title);
            userFeedback.put("desc", desc);
            userFeedback.put("email", email);
            userFeedback.put("deviceInfo", deviceInfo);

            FirebaseManager.getFirebaseDatabase().getReference().child(databaseName).child(FirebaseManager.getFirebaseAuth().getCurrentUser().getUid()).setValue(userFeedback);
        }
    }
}
