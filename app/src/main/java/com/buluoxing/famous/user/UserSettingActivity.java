package com.buluoxing.famous.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.buluoxing.famous.LoginActivity;
import com.buluoxing.famous.MyActivity;
import com.buluoxing.famous.MyApplication;
import com.buluoxing.famous.R;
import com.detail.UserInfo;
import com.diy.MyRadioGroup;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.util.Common;
import com.util.Config;
import com.util.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

//  个人中心 - 用户设置 - 1.01版
public class UserSettingActivity extends MyActivity {



    private TextView birthdayView ; // 生日

    private String sex =  "1";
    private int y;
    private int m;
    private int d;
    private String dateString;
    private Bitmap photo;
    private Common.LoadingHandler uploadImageHandler = null;
    private UserInfo userDetail;

    private AlertDialog dialog;

    @Override
    protected int getLayout() {
        return R.layout.activity_user_set_up;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View backView = findViewById(R.id.back);
        if(backView!=null ) {
            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordIntent = new Intent(UserSettingActivity.this, PasswordActivity.class);
                startActivity(passwordIntent);
                finish();
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication application = (MyApplication) getApplication();
                application.logout();
                Intent intent = new Intent(UserSettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //  头像
        findViewById(R.id.edit_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingActivity.this);
                dialog = builder.create();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                View viewDialog = View.inflate(UserSettingActivity.this, R.layout.dialog_bottom_layout, null);
                dialog.setView(viewDialog, 0, 0, 0, 0);

                LinearLayout linearLayout = (LinearLayout) viewDialog.findViewById(R.id.layout_dialog);
                int childCount = linearLayout.getChildCount();
                dialogListener listener = new dialogListener();
                for (int i = 0; i < childCount; i++) {
                    View view = linearLayout.getChildAt(i);
                    if (view instanceof Button) {
                        view.setOnClickListener(listener);
                    }
                }

                dialog.show();
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.personal_info_bottom_dialog);
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.8f;
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                lp.width = (int) (display.getWidth()); //设置宽度
                window.setAttributes(lp);





//                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingActivity.this);
//                builder.setItems(new String[]{"拍照", "从手机相册里选择"}, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) // 拍照
//                        {
//                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(camera, 101);
//                        } else if (which == 1) // 从手机相册选择
//                        {
//                            Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(picture, 102);
//                        }
//                    }
//                });
//                Dialog dialog = builder.create();
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.show();
            }
        });

        ((MyRadioGroup)findViewById(R.id.sex_radio)).addRadio("男");
        ((MyRadioGroup)findViewById(R.id.sex_radio)).addRadio("女");


        ((MyRadioGroup)findViewById(R.id.sex_radio)).setOnCheckStatusChangedListener(new MyRadioGroup.OnCheckStatusChangedListener() {
            @Override
            public void check(MyRadioGroup cb, int position) {
                sex = (position + 1) + "";
            }
        });



        birthdayView = (TextView) findViewById(R.id.select_birthday);
        birthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取系统当前时间
//                GregorianCalendar now = new GregorianCalendar();
//                Date date = now.getGregorianChange();
//                long time = date.getTime();

                // YMD : 按照  年-月-日 的格式显示
                showControlTime(birthdayView,false,100, "YMD",TimePickerView.Type.YEAR_MONTH_DAY);

            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = userDetail.getSaveParams();
                params.put("action", "modifyUserInfo");
                params.put("user_id", Common.getUserId(UserSettingActivity.this));
                params.put("sex", sex);
                params.put("birthday", birthdayView.getText().toString());
                params.put("nickname", ((TextView) findViewById(R.id.nickname)).getText().toString());
                String nickname = ((TextView) findViewById(R.id.nickname)).getText().toString().trim();
                int length = nickname.length();
                if (length == 0) {
                    Toast.makeText(UserSettingActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                    return;
                } else if (length >= 20) {
                    Toast.makeText(UserSettingActivity.this, "昵称最多20字节", Toast.LENGTH_SHORT).show();
                    return;
                }

                final SpotsDialog dialog = new SpotsDialog(UserSettingActivity.this, "正在保存");
                Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
                    @Override
                    public void run(String result) {

                    }

                    @Override
                    public void run(JSONObject result) {
                        dialog.dismiss();

                        try {
                            if (result.getString("status").equals(Config.HttpSuccessCode)) {

                                Toast.makeText(UserSettingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserSettingActivity.this, result.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("save", result.toString());
                    }

                    @Override
                    public void run(JSONArray result) {

                    }
                });


            }
        });
    }

    // 调用系统相机
    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 101);
    }

    // 调用系统相册
    private void openPhotos() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/jpeg");

        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 102);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadData() {
        String userId = Common.getUserId(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "userRefreshInfo");
        params.put("user_id", userId);

//        final Common.LoadingHandler handler = Common.loading(this,"加载中...");
        final SpotsDialog dialog = new SpotsDialog(UserSettingActivity.this, "加载中");
        Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
            @Override
            public void run(String result) {
                dialog.dismiss();
                Log.i("user", result);
                ImageLoader imageLoader = ImageLoader.getInstance();
                final ImageView userIcon = (ImageView) findViewById(R.id.user_icon);

                try {
                    JSONObject resultObject = new JSONObject(result);
                    JSONObject userInfo = resultObject.getJSONObject("result");


                    userDetail  = new UserInfo(userInfo);
                    String photo = userInfo.getString("photo");

                    imageLoader.loadImage(photo, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            userIcon.setImageBitmap(loadedImage);
                        }
                    });

                    ((TextView) findViewById(R.id.nickname)).setText(userInfo.getString("nickname"));
                    birthdayView.setText(userInfo.getString("birthday"));

                    int sexIndex = userInfo.getInt("sex") - 1;
                    sexIndex = Math.max(0, sexIndex);

                    ((MyRadioGroup) findViewById(R.id.sex_radio)).setRadioCheckByIndex(sexIndex);


                    dateString = userInfo.getString("birthday") ;

                    SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd",
                            Locale.CHINA);
                    try {
                        Date date = sdr.parse(dateString);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        y = calendar.get(Calendar.YEAR);
                        m = calendar.get(Calendar.MONTH);
                        d = calendar.get(Calendar.DAY_OF_MONTH);
                        birthdayView.setText(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Handler handler = new Handler() {
        public  void handleMessage(Message msg) {
            super.handleMessage(msg);

            FileInfo fileInfo = (FileInfo)msg.obj;

            Bitmap bitMini = fileInfo.bitmap;
            ImageView imageView = (ImageView) findViewById(R.id.user_icon);
                                        /* 将Bitmap设定到ImageView */
            imageView.setImageBitmap(bitMini);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitMini.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("action","userUploadImg");
            params.put("user_id", Common.getUserId(UserSettingActivity.this));
            params.put("token", Common.signAction("userUploadImg", Common.getUserId(UserSettingActivity.this)));
            params.put("img", new ByteArrayInputStream(byteArray), fileInfo.filename+".png");

            client.post(Http.buildBaseApiUrl("/Home/Members/index"),params,new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray

                    if(uploadImageHandler!=null) {
                        uploadImageHandler.close();
                    }
                    Log.i("upload",response.toString());
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray timeline) {
                    // Pull out the first event on the public timeline

                }
            });
        }
    };


    public class FileInfo {
        public String filename = "";
        public Bitmap bitmap;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data==null) {
            return;
        }

        if (requestCode == 102 || requestCode==101) {
            final Uri uri = data.getData();

            uploadImageHandler = Common.loading(UserSettingActivity.this,"上传中...");
            if(uri == null){
                //use bundle to get data
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    photo = (Bitmap) bundle.get("data"); //get bitmap
                } else {
                    Toast.makeText(getApplicationContext(), "err****", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {

            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentResolver cr = UserSettingActivity.this.getContentResolver();
                    try {
                        Bitmap bitmap = null;
                        if(uri!=null) {
                            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        } else {
                            bitmap =photo;
                        }
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        float bili = (float)width/height;

                        if(width>200) {
                            width = 200;
                            height =(int) ((float)width/bili);
                        }
                        Bitmap bitMini = ThumbnailUtils.extractThumbnail(bitmap, width, height);

                        FileInfo fileInfo = new FileInfo();
                        fileInfo.filename = "avatar";
                        fileInfo.bitmap = bitMini;

                        Message msg = handler.obtainMessage();
                        msg.obj = fileInfo;
                        msg.sendToTarget();

                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(),e);
                    }
                }
            }).start();
        } else if(requestCode==101) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    class dialogListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_openCamera:
                    dialog.dismiss();
                    openCamera();
                    break;
                case R.id.id_openPhtots:
                    dialog.dismiss();
                    openPhotos();
                    break;
                case R.id.id_hide:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
