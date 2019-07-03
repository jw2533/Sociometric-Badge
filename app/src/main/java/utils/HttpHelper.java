package utils;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.channels.FileChannel;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {
    public static class UploadTaskCompleteToken {
        private boolean isCompleted;
        public boolean getIsCompleted() {
            return isCompleted;
        }

    }

    private static final String UPLOAD_URL = "http://10.56.174.164:5000/files/upload";
    private static final OkHttpClient httpClient = new OkHttpClient();
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static UploadTaskCompleteToken postFiles(Map<String, File> csvFiles) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, File> entry : csvFiles.entrySet()) {
            File csvFile = entry.getValue();
            String fileKey = entry.getKey();
            try {
                byte[] fileContent = Files.readAllBytes(csvFile.toPath());
                RequestBody csvFileBody = RequestBody.create(MediaType.get("application/octet-stream"), fileContent);
                bodyBuilder.addFormDataPart(fileKey, csvFile.getName(), csvFileBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MultipartBody body = bodyBuilder.build();
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(body)
                .build();
        UploadTaskCompleteToken token = new UploadTaskCompleteToken();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CSV_UPLOAD", "upload error", e);
                token.isCompleted = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("CSV_UPLOAD", "upload success");
                token.isCompleted = true;
            }
        });
        return token;
    }
}
