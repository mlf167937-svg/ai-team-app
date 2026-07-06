package com.example.aiteam;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    TextView output;
    EditText input;

    CheckBox gpt, claude, gemini, groq, deepseek;

    // ISI API KEY KAMU DI SINI
    String GPT_KEY = "";
    String CLAUDE_KEY = "";
    String GEMINI_KEY = "";
    String GROQ_KEY = "";
    String DEEPSEEK_KEY = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.output);
        input = findViewById(R.id.input);

        gpt = findViewById(R.id.gpt);
        claude = findViewById(R.id.claude);
        gemini = findViewById(R.id.gemini);
        groq = findViewById(R.id.groq);
        deepseek = findViewById(R.id.deepseek);

        findViewById(R.id.send).setOnClickListener(v -> run());
    }

    void run() {

        String prompt = input.getText().toString();

        output.append("\nUSER:\n" + prompt + "\n\n");

        new Thread(() -> {

            try {

                String context = prompt;

                List<String[]> team = new ArrayList<>();

                if (gpt.isChecked()) team.add(new String[]{"GPT","openai"});
                if (claude.isChecked()) team.add(new String[]{"Claude","claude"});
                if (gemini.isChecked()) team.add(new String[]{"Gemini","gemini"});
                if (groq.isChecked()) team.add(new String[]{"Groq","groq"});
                if (deepseek.isChecked()) team.add(new String[]{"DeepSeek","deepseek"});

                for (String[] ai : team) {

                    String name = ai[0];
                    String type = ai[1];

                    String res = call(type, context);

                    context = clean(res);

                    String finalText = "[" + name + "]\n" + context + "\n\n";

                    runOnUiThread(() -> output.append(finalText));
                }

            } catch (Exception e) {
                runOnUiThread(() -> output.append("ERROR: " + e.getMessage()));
            }

        }).start();
    }

    String call(String type, String prompt) throws Exception {

        switch (type) {

            case "openai":
                return postOpenAI(GPT_KEY, prompt);

            case "claude":
                return postClaude(CLAUDE_KEY, prompt);

            case "gemini":
                return postGemini(GEMINI_KEY, prompt);

            case "groq":
                return postGroq(GROQ_KEY, prompt);

            case "deepseek":
                return postDeepseek(DEEPSEEK_KEY, prompt);
        }

        return "";
    }

    String postOpenAI(String key, String prompt) throws Exception {
        return post("https://api.openai.com/v1/chat/completions",
                "Authorization",
                "Bearer " + key,
                "{\"model\":\"gpt-4o-mini\",\"messages\":[{\"role\":\"user\",\"content\":\""+prompt+"\"}]}"
        );
    }

    String postGroq(String key, String prompt) throws Exception {
        return post("https://api.groq.com/openai/v1/chat/completions",
                "Authorization",
                "Bearer " + key,
                "{\"model\":\"llama3-70b-8192\",\"messages\":[{\"role\":\"user\",\"content\":\""+prompt+"\"}]}"
        );
    }

    String postDeepseek(String key, String prompt) throws Exception {
        return post("https://api.deepseek.com/chat/completions",
                "Authorization",
                "Bearer " + key,
                "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"user\",\"content\":\""+prompt+"\"}]}"
        );
    }

    String postClaude(String key, String prompt) throws Exception {

        URL url = new URL("https://api.anthropic.com/v1/messages");
        HttpURLConnection c = (HttpURLConnection) url.openConnection();

        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestProperty("x-api-key", key);
        c.setRequestProperty("anthropic-version", "2023-06-01");
        c.setDoOutput(true);

        String body =
                "{\"model\":\"claude-3-haiku-20240307\",\"max_tokens\":500,"
                        + "\"messages\":[{\"role\":\"user\",\"content\":\""+prompt+"\"}]}";

        OutputStream os = c.getOutputStream();
        os.write(body.getBytes());
        os.close();

        return read(c);
    }

    String postGemini(String key, String prompt) throws Exception {

        URL url = new URL(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + key
        );

        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "application/json");
        c.setDoOutput(true);

        String body =
                "{\"contents\":[{\"parts\":[{\"text\":\""+prompt+"\"}]}]}";

        OutputStream os = c.getOutputStream();
        os.write(body.getBytes());
        os.close();

        return read(c);
    }

    String post(String urlStr, String header, String value, String body) throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();

        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestProperty(header, value);
        c.setDoOutput(true);

        OutputStream os = c.getOutputStream();
        os.write(body.getBytes());
        os.close();

        return read(c);
    }

    String read(HttpURLConnection c) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));

        String line, out = "";
        while ((line = br.readLine()) != null) out += line;

        br.close();
        return out;
    }

    String clean(String s) {

        int i = s.indexOf("content");
        if (i == -1) return s;

        int a = s.indexOf(":", i) + 2;
        int b = s.indexOf("\"", a);

        if (b == -1) return s;

        return s.substring(a, b);
    }
}
