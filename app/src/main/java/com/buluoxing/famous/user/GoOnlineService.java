package com.buluoxing.famous.user;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.R;
// 个人中心 - 在线客服
public class GoOnlineService extends MyActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_go_online_service;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        findViewById(R.id.goOnlineServiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                ClipData myClip;
                myClip = ClipData.newPlainText("text", "tribal-star");
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(GoOnlineService.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
