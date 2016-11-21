package com.learning.leap.beginningtobabble;

import com.learning.leap.beginningtobabble.settings.UserInfoFragment;

import java.lang.ref.WeakReference;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class DownloadFileRunnable implements Runnable {
    URL mURL;
    String mFileName;
    WeakReference<UserInfoFragment> mUserInfoFragment;
    public DownloadFileRunnable(URL url, String fileName,UserInfoFragment userInfoFragment){
        mURL = url;
        mFileName = fileName;
        mUserInfoFragment = new WeakReference<UserInfoFragment>(userInfoFragment);
    }
    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Boolean didDownload = true;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(mURL).build();
            okhttp3.Response response = client.newCall(request).execute();
            response.handshake();

//            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
//            S3Object object = s3Client.getObject(
//                    new GetObjectRequest(bucketName, key));
//            InputStream objectData = object.
//// Process the objectData stream.
//            objectData.close();
//            File file = new File(Environment.getExternalStorageDirectory(), mFileName);//create folders where writ
//            if (file.exists()){
//                file.delete();
//            }
          //  BufferedSink sink = Okio.buffer(Okio.sink(file));
           //sink.writeAll(objectData)
            //sink.close();
        }catch (Exception e){
            e.printStackTrace();
            didDownload = false;
        }
        final boolean finalDidDownload = didDownload;
        if (mUserInfoFragment.get() != null){
            mUserInfoFragment.get().getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }
}
