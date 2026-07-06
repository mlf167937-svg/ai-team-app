package com.example.aiteam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class MainActivity extends Activity {

    TextView chat;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        chat = new TextView(this);
        input = new EditText(this);
        Button send = new Button(this);

        send.setText("Kirim");

        layout.addView(chat);
        layout.addView(input);
        layout.addView(send);

        chat.setText("AI siap ngobrol...\n");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = input.getText().toString();

                chat.append("\nKamu: " + msg);

                String aiReply = fakeAI(msg);

                chat.append("\nAI: " + aiReply + "\n");

                input.setText("");
            }
        });

        setContentView(layout);
    }

    String fakeAI(String msg) {
        if (msg.contains("halo")) return "Halo juga";
        if (msg.contains("siapa")) return "Aku AI sederhana";
        return "Aku belum ngerti, tapi aku belajar dari kamu";
    }
}
