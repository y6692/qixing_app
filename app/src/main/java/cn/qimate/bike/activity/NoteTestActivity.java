package cn.qimate.bike.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.tuo.customview.VerificationCodeView;

import cn.qimate.bike.R;


public class NoteTestActivity extends AppCompatActivity {
    private LinearLayout content;
    private VerificationCodeView icv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_test);

        content = (LinearLayout) findViewById(R.id.content);
        icv = (VerificationCodeView) findViewById(R.id.icv);


        final VerificationCodeView codeView = new VerificationCodeView(this);


        content.addView(codeView);

        icv.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.i("icv_input", icv.getInputContent());
            }

            @Override
            public void deleteContent() {
                Log.i("icv_delete", icv.getInputContent());
            }
        });


        codeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                codeView.setEtNumber(5);
            }
        }, 5000);



        codeView.setInputCompleteListener(new VerificationCodeView.InputCompleteListener() {
            @Override
            public void inputComplete() {
                Log.i("icv_input", codeView.getInputContent());
            }

            @Override
            public void deleteContent() {
                Log.i("icv_delete", codeView.getInputContent());
            }
        });


    }

    public void onClick(View view) {
        icv.clearInputContent();
    }
}
