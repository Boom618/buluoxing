package com.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.buluoxing.famous.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


public class FileUpload {
	private Context context;
	private ImageView imageHolder;
	private Common.LoadingHandler loadingHandler;
	private int maxWidth = 400;
	private boolean xequey = false;
	private String fileKey = "img";
	private OnUploadStatusChangedListener listener;
	private HashMap<String,String> paramsExtend;
	public FileUpload(Context context) {
		this.context = context;
	}
	public void setImageShowHolder(ImageView holder) {
		imageHolder = holder;
	}
	public boolean checkIsScreenShot = false;

	public class FileInfo {
		public String filename = "";
		public Bitmap bitmap;
		public byte[] data;
	}
	public Handler handler = new Handler() {
		public  void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.obj == null){
				loadingHandler.close();
				Toast.makeText(context,"请上传正确的任务截图！！",Toast.LENGTH_SHORT).show();
				return;
			}
			FileInfo fileInfo = (FileInfo)msg.obj;
			final Bitmap bitMini = fileInfo.bitmap;
			if(imageHolder!=null) {
				imageHolder.setImageBitmap(bitMini);
			}
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();


			params.put("action","uploadImg");
			params.put("user_id", Common.getUserId(context));
			params.put("token", Common.signAction("userUploadImg", Common.getUserId(context)));


			for (String key : paramsExtend.keySet()) {
				params.put(key,paramsExtend.get(key));
			}


			params.put(fileKey, new ByteArrayInputStream(fileInfo.data), fileInfo.filename + ".png");
			Log.i("u",params.toString());
			client.post(Http.buildBaseApiUrl("/Home/System/index"), params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
					// If the response is JSONObject instead of expected JSONArray
					Log.i("upload", response.toString());
					if (imageHolder != null) {
						imageHolder.setImageBitmap(bitMini);
					}
					loadingHandler.close();

					listener.success(response);
				}

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray timeline) {
					// Pull out the first event on the public timeline
				}

				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					Toast.makeText(context,"图片上传失败，请重新上传！",Toast.LENGTH_SHORT).show();
					// 关闭 加载 Dialog
					loadingHandler.close();
				}

			});
		}
	};

	public interface OnUploadStatusChangedListener {
		public void success(JSONObject result);
	}
	public void setMaxWidth(int width) {
		maxWidth = width;
	}
	public void setFileKey(String fileKey) {
		fileKey = fileKey;
	}
	public void setOnUploadStatusChangedListener(OnUploadStatusChangedListener listener) {
		this.listener = listener;
	}
	public void uploadByResult(Intent data,HashMap<String,String> params) {
		this.paramsExtend = params;
		loadingHandler = Common.loading(context,"上传中...");
		final Uri uri = data.getData();
		Log.i("path", uri.toString());
		String path = uri.getPath();
		final String fileName = uri.getPath().substring(path.lastIndexOf("/") + 1, path.length());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ContentResolver cr = context.getContentResolver();
					Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();

					Log.i("size",String.format("%d:%d",width,height));

					if(checkIsScreenShot) {
						if(width < 500 || width>1600) {
							Message msg = handler.obtainMessage();
							msg.sendToTarget();
							return;
						}
					}

					float bili = (float)width/height;


					Log.i("fuck",maxWidth+"");
					if(width>maxWidth) {
						width = maxWidth;

						height =(int) ((float)width/bili);
					}



					Bitmap bitMini = ThumbnailUtils.extractThumbnail(bitmap, width, height);

					FileInfo fileInfo = new FileInfo();
					fileInfo.filename = fileName;
					fileInfo.bitmap = bitMini;


					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitMini.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();

					fileInfo.data = byteArray;

					Message msg = handler.obtainMessage();
					msg.obj = fileInfo;
					msg.sendToTarget();

				} catch (FileNotFoundException e) {
					Log.e("Exception", e.getMessage(), e);
					Message msg = handler.obtainMessage();
					msg.sendToTarget();
				} catch (Exception e) {
					Message msg = handler.obtainMessage();
					msg.sendToTarget();
				}
			}
		}).start();
	}
}
