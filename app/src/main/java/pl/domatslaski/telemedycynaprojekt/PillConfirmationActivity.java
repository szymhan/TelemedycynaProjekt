package pl.domatslaski.telemedycynaprojekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class PillConfirmationActivity extends AppCompatActivity {

    Button mButton;
    LinearLayout mLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_confirmation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Intent intent=getIntent();
        int id=intent.getIntExtra("PRZEGRODKA_NUMBER",0);
        int hourInterval = intent.getIntExtra("HOUR_INTERVAL",12);
        mLinearLayout=findViewById(R.id.pill_confirmation_backgroud_layout);
        switch (id)
        {
            case 1:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.yellowbackgroud,null));
                break;
            case 2:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.redbackground,null));
                break;
            case 3:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.bluebackground,null));
                break;
            case 4:
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.greenbackground,null));
                break;
            default:
                break;
        }
        mButton=findViewById(R.id.i_took_a_pill_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                finish();
            }
        });
    }
}
