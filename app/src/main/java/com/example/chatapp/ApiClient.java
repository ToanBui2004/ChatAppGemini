package com.example.chatapp;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.1.154:8000"; // điền IP vào của máy bạn vào
    private final OkHttpClient client;
    public ApiClient() {
        client = new OkHttpClient();
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public void sendMessage(String userId, String message, ApiCallback callback) {
        String json = "{\"user_id\": \"" + userId + "\", \"message\": \"" + message + "\"}";
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "/chat")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Lỗi: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        // code để chuyển sang JSON
                        JSONObject jsonObject = new JSONObject(responseData);

                        String reply = jsonObject.getString("reply");

                        // 2 Thằng time hiện tại không dùng nhưng để dự phòng
                        String timeUtc = jsonObject.getString("time_utc");
                        String timeVn = jsonObject.getString("time_vn");

                        callback.onSuccess(reply);
                    } catch (JSONException e) {
                        callback.onFailure("Lỗi parse JSON: " + e.getMessage());
                    }
                } else {
                    String errorData = response.body().string();
                    callback.onFailure("Lỗi từ server: " + errorData + " (Mã: " + response.code() + ")");
                }
            }
        });
    }
    public void getChatHistory(String userId, ApiCallback callback) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            callback.onFailure("Lỗi tạo JSON request lịch sử: " + e.getMessage());
            return;
        }
        String jsonBody = jsonObject.toString();

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/chat/history")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Lỗi mạng khi lấy lịch sử: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        callback.onFailure("Phản hồi lịch sử trống từ server (Mã: " + response.code() + ")");
                        return;
                    }
                    String responseData = responseBody.string();

                    if (response.isSuccessful()) {
                        callback.onSuccess(responseData);
                    } else {
                        callback.onFailure("Lỗi lấy lịch sử từ server: " + responseData + " (Mã: " + response.code() + ")");
                    }
                } catch (IOException e) {
                    callback.onFailure("Lỗi đọc phản hồi lịch sử: " + e.getMessage());
                }
            }
        });
    }
    }

