package com.buluoxing.famous.index;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buluoxing.famous.ConfirmActivity;
import com.buluoxing.famous.ImageListActivity;
import com.buluoxing.famous.MoneyPackageOpendActivity;
import com.buluoxing.famous.MyApplication;
import com.buluoxing.famous.R;
import com.buluoxing.famous.fileload.FileApi;
import com.buluoxing.famous.fileload.FileCallback;
import com.buluoxing.famous.user.AddBeansActivity;
import com.buluoxing.famous.user.AddMoneyActivity;
import com.buluoxing.famous.user.BeansToMoneyActivity;
import com.buluoxing.famous.user.CompleteMissionActivity;
import com.buluoxing.famous.user.FollowActivity;
import com.buluoxing.famous.user.GetMoneyActivity;
import com.buluoxing.famous.user.GoOnlineService;
import com.buluoxing.famous.user.InviteFriendsActivity;
import com.buluoxing.famous.user.KolManageActivity;
import com.buluoxing.famous.user.KolSetupActivity;
import com.buluoxing.famous.user.MyBeansActivity;
import com.buluoxing.famous.user.MyMissionActivity;
import com.buluoxing.famous.user.MyMoneyActivity;
import com.buluoxing.famous.user.SysMissionActivity;
import com.buluoxing.famous.user.SystemMessageActivity;
import com.buluoxing.famous.user.UserSetUpActivity;
import com.buluoxing.famous.user.UserSettingActivity;
import com.buluoxing.famous.wxapi.WXEntryActivity;
import com.detail.UserInfo;
import com.diy.HDialogBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.util.Common;
import com.util.Config;
import com.util.DataManager;
import com.util.Http;
import com.util.T;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {


	private DisplayImageOptions options;
	private SwipeRefreshLayout swipeRefreshLayout;
	private UMShareAPI mShareAPI;
	private String bindingWx;
	private MyApplication App = new MyApplication();

	private String integral = "0" ;// 用户红豆数量

//	private TextView txtProgress;
//	private HDialogBuilder hDialogBuilder;

	public UserFragment() {
		// Required empty public constructor
	}

	public View view = null;


	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_user, container, false);

			view.findViewById(R.id.user_set_up).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent userSetUpIntent = new Intent(getContext(), UserSetUpActivity.class);
//					Intent userSetUpIntent = new Intent(getContext(), UserSettingActivity.class);
//					T.showShort(getActivity().getApplicationContext(),"热修复 Patch");
					startActivity(userSetUpIntent);
				}
			});
			view.findViewById(R.id.mission_complete).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent missionCompleteIntent = new Intent(getContext(), CompleteMissionActivity.class);
					startActivity(missionCompleteIntent);
				}
			});

			view.findViewById(R.id.my_beans).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent myBeansCompleteIntent = new Intent(getContext(), MyBeansActivity.class);
					startActivity(myBeansCompleteIntent);
				}
			});

			view.findViewById(R.id.my_money).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent myMoneyIntent = new Intent(getContext(), MyMoneyActivity.class);
					startActivity(myMoneyIntent);
				}
			});
			view.findViewById(R.id.go_my_mission).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getContext(), MyMissionActivity.class);
					startActivity(intent);
				}
			});

			view.findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Common.userSign(getContext());
					((TextView)view.findViewById(R.id.sign_in)).setText("今日已签到");
				}
			});


			swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
			//设置刷新时动画的颜色，可以设置4个
			swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					loadData();
				}
			});


			view.findViewById(R.id.mange_kol).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), KolManageActivity.class);
					startActivity(intent);
				}
			});

			view.findViewById(R.id.my_follow).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), FollowActivity.class);
					intent.putExtra("type","follow");
					startActivity(intent);
				}
			});
			view.findViewById(R.id.my_follower).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), FollowActivity.class);
					intent.putExtra("type","follower");
					startActivity(intent);
				}
			});
			view.findViewById(R.id.invite_friends).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), InviteFriendsActivity.class);
					startActivity(intent);
				}
			});
			view.findViewById(R.id.sys_message).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), SystemMessageActivity.class);
					startActivity(intent);
				}
			});
			view.findViewById(R.id.sys_mission).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), SysMissionActivity.class);
					startActivity(intent);
				}
			});
			view.findViewById(R.id.bind_wx).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if(bindingWx.equals("0")) {
						mShareAPI = UMShareAPI.get(getActivity());
						mShareAPI.doOauthVerify(getActivity(), SHARE_MEDIA.WEIXIN, umAuthListener);
						WXEntryActivity.setAuthListener(new WXEntryActivity.WxAuthCallbackListener() {
							@Override
							public void success(WXEntryActivity.WxAuthResult result) {
								String openid = result.openid;
								String unionid = result.unionid;

								HashMap<String,String> params = new HashMap<>();
								params.put("action","userBinding");
								params.put("user_id",Common.getUserId(getContext()));
								params.put("open_id", openid);
								params.put("union_id", unionid);

								final Common.LoadingHandler handler = Common.loading(getActivity(),"正在绑定");
								Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
									@Override
									public void run(String result) {

									}

									@Override
									public void run(JSONObject result) {
										handler.close();
										try {
											Toast.makeText(getActivity(),result.getString("message"),Toast.LENGTH_SHORT).show();
											loadData();
										} catch (JSONException e) {
											e.printStackTrace();
											Toast.makeText(getActivity(),"绑定失败",Toast.LENGTH_SHORT).show();
										}
									}

									@Override
									public void run(JSONArray result) {

									}
								});
							}

							@Override
							public void failed(int code) {
								Toast.makeText(getActivity(),"登录失败",Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						Intent intent = new Intent(getActivity(), ConfirmActivity.class);
						intent.putExtra("message","你确认解除微信绑定吗?");
						startActivityForResult(intent, 100);
					}
				}
			});
			view.findViewById(R.id.beans_for_money).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), BeansToMoneyActivity.class);
					intent.putExtra("integral",integral);
					startActivity(intent);
				}
			});
			view.findViewById(R.id.add_beans).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), AddBeansActivity.class);
					startActivity(intent);
				}
			});
			view.findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
					App.initSysConfig();
					startActivity(intent);
				}
			});
			view.findViewById(R.id.get_money).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), GetMoneyActivity.class);
					App.initSysConfig();
					startActivity(intent);
				}
			});

		}
		view.findViewById(R.id.OnlineService).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(), GoOnlineService.class);
				startActivity(intent);
			}
		});
		// 检测更新
//		view.findViewById(R.id.check_up_data).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				checkUpDate(getContext());
//				// 显示 Dialog
//				//showLoadingDialog();
//			}
//		});
		return view;
	}

	public void onActivityResult(int requestCode,int resultCode,Intent data) {
		if(requestCode==100) {
			if(resultCode==1) {
				HashMap<String,String> params = new HashMap<String, String>();
				params.put("action","userDelBinding");
				params.put("user_id", Common.getUserId(getActivity()));
				final Common.LoadingHandler handler = Common.loading(getActivity(),"解除绑定...");
				Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpPostSuccessWithJsonResultInterface() {
					@Override
					public void run(String result) {

					}

					@Override
					public void run(JSONObject result) {
						handler.close();
						bindingWx = "0";
						Toast.makeText(getActivity(),"解除绑定成功!",Toast.LENGTH_SHORT).show();
						loadData();
					}

					@Override
					public void run(JSONArray result) {

					}
				});
			}
		}
	}

	/** auth callback interface**/
	private UMAuthListener umAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
			Log.i("auth", data.toString());

		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			Toast.makeText( getContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			Toast.makeText( getContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
		}
	};
	public void onResume() {
		super.onResume();
		loadData();
		//PlatformConfig.setWeixin(Config.WxAppIdBind, Config.WxAppSecretBind);
	}

	private void loadData() {
		String userId = Common.getUserId(getContext());
		HashMap<String, String> params = new HashMap<>();
		params.put("action", "userRefreshInfo");
		params.put("user_id", userId);
		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/Members/index"), params, new Http.HttpSuccessInterface() {
			@Override
			public void run(String result) {
				Log.i("user", result);
				swipeRefreshLayout.setRefreshing(false);
				ImageLoader imageLoader = ImageLoader.getInstance();
				final ImageView userIcon = (ImageView) view.findViewById(R.id.profile_image);
				try {
					JSONObject resultObject = new JSONObject(result);
					final JSONObject userInfo = resultObject.getJSONObject("result");
					String photo = userInfo.getString("photo");
					imageLoader.loadImage(photo, Config.options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							userIcon.setImageBitmap(loadedImage);
						}
					});

					userIcon.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String imageUrlList[] = new String[0];
							try {
								imageUrlList = new String[]{userInfo.getString("photo")};
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Intent intent = new Intent(getActivity(), ImageListActivity.class);
							intent.putExtra("image_list", imageUrlList);
							intent.putExtra("current_index", 0);
							startActivity(intent);
						}
					});

					integral = userInfo.getString("integral");
					((TextView)view.findViewById(R.id.username)).setText(userInfo.getString("nickname"));
					((TextView)view.findViewById(R.id.city)).setText(userInfo.getString("city"));
					((TextView)view.findViewById(R.id.area)).setText(userInfo.getString("domian_name"));
					((TextView)view.findViewById(R.id.followed_count)).setText(userInfo.getString("followed_num"));
					((TextView)view.findViewById(R.id.follow_count)).setText(userInfo.getString("follow_num"));
					((TextView)view.findViewById(R.id.red_bits_count)).setText( integral );
					((TextView)view.findViewById(R.id.money_count)).setText(userInfo.getString("balance"));
					((TextView)view.findViewById(R.id.kol_level)).setText(String.format("Lv.%s", userInfo.getString("grade_id")));

					bindingWx = userInfo.getString("is_binding");

//					if(bindingWx.equals("1")) {
//						view.findViewById(R.id.bind_wx).setVisibility(View.GONE);
//						((TextView)view.findViewById(R.id.bind_status)).setText("已成功绑定微信");
//					} else {
//						view.findViewById(R.id.bind_wx).setVisibility(View.VISIBLE);
//						((TextView)view.findViewById(R.id.bind_status)).setText("绑定微信");
//					}



					if(userInfo.getString("type").equals(Config.UserTypeKol) || userInfo.getString("type").equals(Config.UserTypeSingleKol)) {
						view.findViewById(R.id.mange_kol).setVisibility(View.VISIBLE);
						view.findViewById(R.id.request_being_kol).setVisibility(View.GONE);


						if(userInfo.getString("type").equals(Config.UserTypeSingleKol)) {
							view.findViewById(R.id.single_kol).setVisibility(View.VISIBLE);
							view.findViewById(R.id.kol).setVisibility(View.GONE);
						} else {
							view.findViewById(R.id.kol).setVisibility(View.VISIBLE);
							view.findViewById(R.id.single_kol).setVisibility(View.GONE);
						}

					} else {
						view.findViewById(R.id.mange_kol).setVisibility(View.GONE);
						view.findViewById(R.id.request_being_kol).setVisibility(View.VISIBLE);

						view.findViewById(R.id.single_kol).setVisibility(View.GONE);
						view.findViewById(R.id.kol).setVisibility(View.GONE);
					}


					final UserInfo userDetail = new UserInfo(userInfo);


					view.findViewById(R.id.request_being_kol).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(userDetail.is_kol==UserInfo.REQUEST_KOL_NO || userDetail.is_kol==UserInfo.REQUEST_KOL_FAILED) {
								if (userDetail.has_publish) {
									Intent intent = new Intent(getActivity(), KolSetupActivity.class);
									intent.putExtra("request_kol", "1");
									startActivity(intent);
								} else {
									Toast.makeText(getActivity(), "还未发布过任务，请发布后再申请", Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(getActivity(), "正在申请中,请耐心等待通知...", Toast.LENGTH_SHORT).show();
							}
						}
					});
					//userDetail.is_kol = 3;
					if(userDetail.is_kol==UserInfo.REQUEST_KOL_ING) {
						view.findViewById(R.id.request_being_kol).setBackgroundResource(R.drawable.radius_rect_disable);
						((TextView)view.findViewById(R.id.request_being_kol)).setText("正在申请中,请耐心等待通知");
					} else if(userDetail.is_kol==UserInfo.REQUEST_KOL_FAILED) {
						view.findViewById(R.id.request_being_kol).setBackgroundResource(R.drawable.radius_login);
						((TextView)view.findViewById(R.id.request_being_kol)).setText("申请失败，重新申请网红");
					} else {
						view.findViewById(R.id.request_being_kol).setBackgroundResource(R.drawable.radius_login);
					}

					if(userDetail.is_report) {
						((TextView)view.findViewById(R.id.sign_in)).setText("今日已签到");
					}

					MyApplication application = (MyApplication) getActivity().getApplication();
					application.userInfo = userDetail;

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});


		HashMap<String, String> params2 = new HashMap<>();
		params2.put("action", "sysMessage");
		params2.put("user_id", userId);

		Http.postApiWithToken(Http.buildBaseApiUrl("/Home/System/index"), params2, new Http.HttpPostSuccessWithJsonResultInterface() {
			@Override
			public void run(String result) {

			}

			@Override
			public void run(JSONObject result) {
				try {
					Log.e("UserFra", "run: " + result.toString());
					//  result 为空  判断
					String status = result.getString("status");
					if ("106".equals(status)){
						// 没有 系统消息
					}else if ("100".equals(status)){
						JSONArray newsList = result.getJSONArray("result");
						view.findViewById(R.id.new_news).setVisibility(View.GONE);
						for (int i=0;i<newsList.length();i++) {
							if(newsList.getJSONObject(i).getInt("is_show")==0) {
								view.findViewById(R.id.new_news).setVisibility(View.VISIBLE);
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run(JSONArray result) {

			}
		});
	}
//	//  版本更新
//
//
//
//	private void checkUpDate(Context context){
//
//		int code = Integer.parseInt(Config.Version_ID);
//		if (isUpDate(context,code)){
//			//更新
//			String baseUrl = Config.Version_URL;
//			String fileName = "buluoxing.apk";
//			String fileStoreDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File
//					.separator + "buluoxing";
//			String fileStoreName = fileName;
//			showLoadingDialog();
//
//			FileApi.getInstance(baseUrl)
//					.loadFileByName(fileName, new FileCallback(fileStoreDir, fileStoreName) {
//						@Override
//						public void onSuccess(File file) {
//							super.onSuccess(file);
//							hDialogBuilder.dismiss();
//							openFile(file);
//						}
//
//						@Override
//						public void progress(long progress, long total) {
//							updateProgress(progress, total);
//						}
//
//						@Override
//						public void onFailure(Call<ResponseBody> call, Throwable t) {
//							hDialogBuilder.dismiss();
//							call.cancel();
//						}
//					});
//
//		}else {
//
//			final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
//			pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//			pDialog.setTitleText("当前版本为最新版本,不需要更新")
//			.setConfirmText("确定")
//			.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//				@Override
//				public void onClick(SweetAlertDialog sweetAlertDialog) {
//					sweetAlertDialog.dismiss();
//				}
//			}).show();
//
//
//
//
//		}
//
//	}
//
//	// 安装 APK
//	private void openFile(File file) {
//		// TODO Auto-generated method stub
//		Log.e("OpenFile", file.getName());
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
//		intent.setDataAndType(Uri.fromFile(file),
//				"application/vnd.android.package-archive");
//		startActivity(intent);
//	}
//
//	/**
//	 * 更新下载进度
//	 *
//	 * @param progress
//	 * @param total
//	 */
//	private void updateProgress(long progress, long total) {
//		txtProgress.setText(String.format("正在下载：(%s/%s)",
//				DataManager.getFormatSize(progress),
//				DataManager.getFormatSize(total)));
//	}
//
//	/**
//	 * 显示下载对话框
//	 */
//	private void showLoadingDialog() {
//		txtProgress = (TextView) View.inflate(getContext(), R.layout
//				.layout_hd_dialog_custom_tv, null);
//		//txtProgress.setText("确认下载？");
//		hDialogBuilder = new HDialogBuilder(getContext());
//		hDialogBuilder.setCustomView(txtProgress)
//				.title("下载")
//				.nBtnText("取消")
//				//.pBtnText("确认")
////				.pBtnClickListener(new View.OnClickListener() {
////					@Override
////					public void onClick(View v) {
////						checkUpDate(getContext());
////					}
////				})
//				.nBtnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						hDialogBuilder.dismiss();
//						FileApi.cancelLoading();
//					}
//				})
//				.outsideCancelable(false)
//				.show();
//	}
//
//	private boolean isUpDate(Context context ,int code){
//		String versionName = "";
//		int versioncode = 0;
//		try {
//			// ---get the package info---
//			PackageManager pm = context.getPackageManager();
//			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//			versionName = pi.versionName;
//			versioncode = pi.versionCode;
//			Log.e("VersionInfo", "versionName = " + versionName);
//			Log.e("VersionInfo", "versioncode = " + versioncode);
//			if ( versioncode < code ){
//				// 更新
//				return true;
//			}
//		} catch (Exception e) {
//			Log.e("VersionInfo", "Exception", e);
//			return false;
//		}
//
//		// 其他情况 一律 不更新
//		return false;
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if ( App != null ){
			App = null;
		}
	}
}
