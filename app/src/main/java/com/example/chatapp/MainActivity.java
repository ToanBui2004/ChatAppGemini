package com.example.chatapp;

import static com.example.chatapp.Message.SENT_BY_BOT;
import static com.example.chatapp.Message.SENT_BY_ME;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView_Chat;
    EditText message_ET;
    ImageButton imgae_Send;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    private Toast currentToast;
    private ApiClient apiClient;


    // userID là id của người dùng, thay SENT_BY_ME bằng id của người dùng, mặc định là SENT_BY_ME
    // ví dụ userID= "user 123";
    private String userID = SENT_BY_ME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();

        apiClient = new ApiClient();

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView_Chat.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView_Chat.setLayoutManager(llm);

        setHistoryList();

        sendMessage();


    }

    void init()
    {
        recyclerView_Chat = findViewById(R.id.recyclerView_Chat);
        message_ET = findViewById(R.id.message_ET);
        imgae_Send = findViewById(R.id.imgae_Send);
    }
    @SuppressLint("NotifyDataSetChanged")
    void addMessage(String message, String sent_by){
        runOnUiThread(() -> {
            messageList.add(new Message(message,sent_by));
            messageAdapter.notifyDataSetChanged();
            recyclerView_Chat.smoothScrollToPosition(messageAdapter.getItemCount()-1);
        });
    }
    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();  // Hủy Toast cũ trước khi hiển thị mới
        }
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
    private void sendMessage()
    {

        imgae_Send.setOnClickListener(v -> {
            String question = message_ET.getText().toString().trim();
            if (question.equals(""))
                showToast("Không được bỏ trống tin nhắn khi gửi");
            else {
                addMessage(question, SENT_BY_ME);
                message_ET.setText("");
                apiClient.sendMessage(userID, question , new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> addMessage(response, SENT_BY_BOT));
                    }
                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> addMessage(error,SENT_BY_BOT));
                    }
                });
            }
        });
    }
    private void setHistoryList() {
        apiClient.getChatHistory(userID,  new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject historyObject = jsonArray.getJSONObject(i);
                        JSONArray historyChat = historyObject.getJSONArray("history_chat");


                        for (int j = 0; j < historyChat.length(); j++) {
                            JSONObject messageObject = historyChat.getJSONObject(j);
                            String role = messageObject.getString("role");
                            String text = messageObject.getString("text");

                            if (role.equals("user"))
                                runOnUiThread(() -> addMessage(text,SENT_BY_ME));
                            else
                                runOnUiThread(() -> addMessage(text,SENT_BY_BOT));
                        }
                    }

                } catch (JSONException e) {
                    showToast("Lỗi parse JSON: " + e.getMessage());
                }
            }
            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> addMessage(error,SENT_BY_BOT));
            }
        });
    }

}