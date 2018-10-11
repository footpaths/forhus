package luan.pc.husnew;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends Activity {
    private ScreenOnOffReceiver screenOnOffReceiver = null;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
       /* ComponentName componentName2 = new ComponentName(this,
                MainActivity.class);
        this.getPackageManager().setComponentEnabledSetting(
                componentName2,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);*/
        Intent backgroundService = new Intent(getApplicationContext(), ScreenOnOffBackgroundService.class);
        startService(backgroundService);

        Log.d("SCREEN_TOGGLE_TAG", "Activity onCreate");

        mAuth.signInWithEmailAndPassword("thanhluan@gmail.com", "123456789")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SCREEN_TOGGLE_TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SCREEN_TOGGLE_TAG", "signInWithEmail:failure", task.getException());

                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SCREEN_TOGGLE_TAG", "Activity onDestroy");
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

    }

}
