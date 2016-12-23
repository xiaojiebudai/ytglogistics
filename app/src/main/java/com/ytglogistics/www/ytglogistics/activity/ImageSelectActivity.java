package com.ytglogistics.www.ytglogistics.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.widget.Toast;

import com.ytglogistics.www.ytglogistics.utils.AndtoidRomUtil;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.FileUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.ZLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 图片选择界面
 *
 * @author xl
 * @date:2016-8-2下午3:18:31
 * @description 单图选择(调用系统相机,相册和裁剪)
 */
public class ImageSelectActivity extends Activity {

	public static final String KEY_MODE = "mode";
	/** 拍照 */
	public static final int MODE_TAKE_PICTURE = 0;
	/** 相册 */
	public static final int MODE_PHOTO_ALBUM = 1;
	/** 执行裁剪 */
	public static final String KEY_DO_CROP = "do_crop";
	/** 是否是身份证 */
	public static final String KEY_ISIDCARD = "isIdCard";

	private static final int REQUEST_CODE_CAMERA = 10;
	private static final int REQUEST_CODE_CROP_PICTURE = 11;
	private static final int REQUEST_CODE_PHOTO_ALBUM = 12;
	private int mMode;
	private boolean isCrop;
	private boolean isIdCard;

	private File mTmpFile;
	/** 裁剪图的输出 */
	private File mCropFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMode = getIntent().getIntExtra(KEY_MODE, MODE_TAKE_PICTURE);
		isCrop = getIntent().getBooleanExtra(KEY_DO_CROP, false);
		isIdCard = getIntent().getBooleanExtra(KEY_ISIDCARD, false);
		switch (mMode) {
			case MODE_TAKE_PICTURE:// 拍照
//			if (android.os.Build.VERSION.SDK_INT>=23){
//				if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//						!= PackageManager.PERMISSION_GRANTED) {
//					//申请WRITE_EXTERNAL_STORAGE权限
//					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}
//						,1);
//					break;
//				}
//			}
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (cameraIntent.resolveActivity(getPackageManager()) != null) {
					mTmpFile = FileUtils.createTmpFile(this);
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(mTmpFile));
					startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
				} else {
					WWToast.showShort("没有找到系统相机!");
				}
				break;
			case MODE_PHOTO_ALBUM:

				selectPicFromLocal();
				break;

			default:
				break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ZLog.showPost(resultCode + "-----");
		switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				if (resultCode == Activity.RESULT_OK) {
					if (mTmpFile != null) {
						// Intent.ACTION_MEDIA_MOUNTED:媒体库进行全扫描;
						// Intent.ACTION_MEDIA_SCANNER_SCAN_FILE扫描某个文件
						sendBroadcast(new Intent(
								Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
								Uri.fromFile(mTmpFile)));// 通知系统媒体库更新,不然下次进来没有最新的拍照图片
						if (isCrop) {// 裁剪
							if(isIdCard){
								cropImageUri(Uri.fromFile(mTmpFile), 400, 400,
										REQUEST_CODE_CROP_PICTURE);
							}else{
								cropImageUri(Uri.fromFile(mTmpFile), 400, 400,
										REQUEST_CODE_CROP_PICTURE);
							}

						} else {// 直接返回
							Intent intent = new Intent();
							intent.putExtra(Consts.KEY_DATA,
									mTmpFile.getAbsolutePath());
							setResult(RESULT_OK, intent);
							finish();
						}

					}
				} else {
					if (mTmpFile != null && mTmpFile.exists()) {
						mTmpFile.delete();
					}
					finish();
				}
				break;

			case REQUEST_CODE_CROP_PICTURE:

				if (resultCode == RESULT_OK) {
					Intent intent = new Intent();
					intent.putExtra(Consts.KEY_DATA, mCropFile.getAbsolutePath());
					setResult(RESULT_OK, intent);
				} else {
					if (mCropFile != null && mCropFile.exists()) {
						mCropFile.delete();
					}
				}
				finish();
				break;
			case REQUEST_CODE_PHOTO_ALBUM:
				if (resultCode == RESULT_OK) {
					if (data != null) {

						Uri selectImageUri = data.getData();
						// 拿到相册图片后进入裁剪
						if (selectImageUri != null) {
							String BasePath = Environment
									.getExternalStorageDirectory().toString()
									+ "/"
									+ getPackageName() + "/";
							String CropPicPath = BasePath
									+ System.currentTimeMillis() + ".jpg";
							// 将选取的图片uri转化为path
							String selectImagePath = uri2path(selectImageUri);
							// 复制一份到新的路径
							copyFile(selectImagePath, CropPicPath);
							// 将截取的图片路径转化为uri
							Uri tempUri = Uri.fromFile(new File(CropPicPath));
							// 带入截图
							if (selectImageUri != null) {
								if (!TextUtils.isEmpty(selectImagePath)) {
									if (isCrop) {
										if(isIdCard){
											cropImageUri(tempUri, 600, 400,
													REQUEST_CODE_CROP_PICTURE);
										}else{
											cropImageUri(tempUri, 400, 400,
													REQUEST_CODE_CROP_PICTURE);
										}

									} else {
										Intent intent = new Intent();
										intent.putExtra(Consts.KEY_DATA,
												selectImagePath);
										setResult(RESULT_OK, intent);
										finish();
									}

								}
							} else {
								Toast.makeText(getApplicationContext(),"没有选择文件",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				} else {
					finish();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 复制图片
	 *
	 * @param oldPath
	 *            旧的图片路径
	 * @param newPath
	 *            新的图片路径
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				File file = new File(newPath);
				if (file.exists()) {
					file.delete();
				} else {
					new File(file.getParent()).mkdirs();
					file.createNewFile();
				}
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String uri2path(Uri uri) {
		String path = "";
		path = getImageAbsolutePath(this, uri);
		return path;
	}

	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 *
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
									   String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * 系统图库
	 *
	 * @author xl
	 * @date:2016-8-2下午3:55:15
	 * @description
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.setType("image/*");
		}
		startActivityForResult(intent, REQUEST_CODE_PHOTO_ALBUM);
	}

	/**
	 * 调起裁剪
	 *
	 * @author xl
	 * @date:2016-7-13下午3:44:21
	 * @description
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		mCropFile = FileUtils.createTmpFile(this);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");// 可以选择图片类型，如果是*表明所有类型的图片
		intent.putExtra("crop", "true");
		// 宽高比
		if (AndtoidRomUtil.isEMUI()) {// 华为特殊处理 不然会显示圆

			if (isIdCard) {
				intent.putExtra("aspectX", 3);
				intent.putExtra("aspectY", 2);
			} else {
				intent.putExtra("aspectX", 9998);
				intent.putExtra("aspectY", 9999);
			}

		} else {
			if (isIdCard) {
				intent.putExtra("aspectX", 3);
				intent.putExtra("aspectY", 2);
			} else {
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
			}

		}
		// 裁剪宽高
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCropFile));// 裁剪图片路径
		// 是否将数据保留在Bitmap中返回
		intent.putExtra("return-data", false);
		// 设置输出的格式
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 解决小米手机上获取图片路径为null的情况
	 *
	 * @param intent
	 * @return
	 */
	public Uri geturi(android.content.Intent intent) {
		Uri uri = intent.getData();
		String type = intent.getType();
		if (uri.getScheme().equals("file") && (type.contains("image/"))) {
			String path = uri.getEncodedPath();
			if (path != null) {
				path = Uri.decode(path);
				ContentResolver cr = this.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(Images.ImageColumns.DATA).append("=")
						.append("'" + path + "'").append(")");
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { Images.ImageColumns._ID },
						buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					index = cur.getColumnIndex(Images.ImageColumns._ID);
					// set _id value
					index = cur.getInt(index);
				}
				if (index == 0) {
					// do nothing
				} else {
					Uri uri_temp = Uri
							.parse("content://media/external/images/media/"
									+ index);
					if (uri_temp != null) {
						uri = uri_temp;
					}
				}
			}
		}
		return uri;
	}
}
