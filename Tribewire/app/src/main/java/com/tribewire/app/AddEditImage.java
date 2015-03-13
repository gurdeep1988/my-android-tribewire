package com.tribewire.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONObject;

import com.app.eyesay.custommenu.CustomMenu;
import com.app.eyesay.custommenu.CustomMenu.OnMenuItemSelectedListener;
import com.eyesayapp.Utils.ActivityStatus;
import com.eyesayapp.Utils.CommonFunctions;
import com.eyesayapp.Utils.Constants;
import com.eyesayapp.Utils.TribeWireUtil;
import com.eyesayapp.Utils.UploadFiles;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AddEditImage extends Fragment implements OnClickListener {

	private ImageView img_profile;
	private Button btnBack;
	String uploadFilePath = "";
	private final int ACTIVITY_SELECT_IMAGE = 211;
	private final int ACTIVITY_CAPTURE_IMAGE = 212;

	public static final String IMAGE_DATA = "image_data";
	public static final String IMAGE_URI = "image_uri";
	public static final String VIDEO_DATA = "video_data";
	public static final String AUDIO_DATA = "audio_data";
	public static final String LOCAL_PROFILE_PIC = "profile_pic";
	Uri imageUri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_addedit_image, null);

		btnBack = (Button) view.findViewById(R.id.btn_back);
		img_profile = (ImageView) view.findViewById(R.id.img_profile);
		btnBack.setOnClickListener(this);
		img_profile.setOnClickListener(this);
		setDefaultImage();
		return view;

	}

	@Override
	public void onResume() {
		ActivityStatus.setActivity(getActivity());
		super.onResume();
	}
	@Override
	public void onPause() {
		ActivityStatus.setActivity(null);
		super.onPause();
	}
	
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// setContentView(R.layout.activity_addedit_image);
	//
	// super.onCreate(savedInstanceState);
	// }

	private void setDefaultImage() {
		try {
			String path = CommonFunctions.getPref(getActivity(),
					LOCAL_PROFILE_PIC, "");
			if (path != null && path.length() > 0) {
				File image = new File(path);
				if (image.exists()) {
					//Bitmap bitmap = getBitmap(Uri.fromFile(image));
                    img_profile.setImageURI(Uri.parse(image.toString()));
//					img_profile.setImageBitmap(TribeWireUtil.rotateBitmap(
//							image.toString(), bitmap));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		if (v == btnBack) {
			// finish();
		} else if (v == img_profile) {
			showMenu();
		}

	}

	Uri selectedImage = null;
	Bitmap profile_bm = null;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTIVITY_CAPTURE_IMAGE
				&& resultCode == Activity.RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {

				Uri selectedImage = imageUri;
				try {

					getActivity().getContentResolver().notifyChange(
							selectedImage, null);
					Log.e("",
							"-------------------------------------------------------------"
									+ selectedImage);

					uploadFilePath = selectedImage.toString();

					if (uploadFilePath.startsWith("file://")) {
						uploadFilePath = uploadFilePath.replace("file://", "");
						Bitmap bitmap = getBitmap(selectedImage);
						profile_bm = TribeWireUtil.rotateBitmapZero(
								selectedImage.toString(), bitmap);
						// Bitmap bitmap =
						// BitmapFactory.decodeFile(uploadFilePath);
						// Log.e("", "============== The Bitmap is "+bitmap);
						// profile_bm =
						// TribeWireUtil.rotateBitmap(selectedImage.toString(),
						// profile_bm);
						Log.e("",
								"After-------------------------------------------------------------"
										+ uploadFilePath);

					}

					// handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(2);
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		}

		else if (requestCode == ACTIVITY_SELECT_IMAGE
				&& resultCode == Activity.RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {

				try {
					selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getActivity().getContentResolver().query(
							selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					Log.e("", "File Path is " + filePath);
					uploadFilePath = filePath;
					cursor.close();
					try {
						profile_bm = getBitmap(selectedImage);
						profile_bm = TribeWireUtil.rotateBitmap(filePath,
								profile_bm);
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();

						boolean isImageWritten = profile_bm.compress(
								Bitmap.CompressFormat.JPEG, 100, bos);
						if (isImageWritten) {

						}

						byte bitmapImage[] = bos.toByteArray();
						File file1 = getTempFile(getActivity());
						FileOutputStream fos = new FileOutputStream(file1);
						uploadFilePath = file1.toString();
						Log.e("", "The Compressed bitmap File path is "
								+ uploadFilePath);
						fos.write(bitmapImage);
						fos.close();

						handler.sendEmptyMessage(2);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showMenu() {
		try {
			HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
			hashMap.put(0, "Take Photo");
			hashMap.put(1, "Use Existing Photo");

			hashMap.put(3, "Cancel");
			CustomMenu.show(getActivity(), hashMap,
					new OnMenuItemSelectedListener() {

						@Override
						public void MenuItemSelectedEvent(Integer selection) {
							if (selection == 0) {

								takePhoto();
							} else if (selection == 1) {
								chooseExisting();
							} else if (selection == 2) {

							} else if (selection == 3) {
								CustomMenu.hide();
							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Takes Photo Using Default Camera
	private void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		File photo = getTempFile(getActivity());
		photo.delete();
		photo = getTempFile(getActivity());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		imageUri = Uri.fromFile(photo);
		startActivityForResult(intent, ACTIVITY_CAPTURE_IMAGE);

	}

	// choose Existing Image and Video from Library
	private void chooseExisting() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
	}

	public static File getTempFile(Context context) {

		File path1 = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path1.exists()) {
			path1.mkdir();
			Log.e("", "Directory Created Again");
		}

		return new File(path1, System.currentTimeMillis() + "eyesay.jpeg");
	}

	private String TAG = this.getClass().getSimpleName();

	private Bitmap getBitmap(Uri uri) {

		// Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			final int IMAGE_MAX_SIZE = 700000; // 1.2MP
			in = getActivity().getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth
					+ ", orig-height: " + o.outHeight);

			Bitmap b = null;
			in = getActivity().getContentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				Log.d(TAG, "1th scale operation dimenions - width: " + width
						+ ",height: " + height);

				double y = Math.sqrt(IMAGE_MAX_SIZE
						/ (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
						(int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}

			in.close();
			Log.e("", "");
			return b;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	ProgressDialog pd;

	private void startUploading() {
		try {
			if (uploadFilePath != null && uploadFilePath.length() > 0) {
				if (CommonFunctions.isInternetConnected(getActivity())) {
					pd = ProgressDialog.show(getActivity(),
							"Updating profile pic",
							"Please wait while change your profile pic");
					pd.setCancelable(true);
					new UploadProfilePic().execute();
				} else {
					CommonFunctions.showAlert(getActivity(),
							"Please check your internect connectivity",
							"Network Error");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				try {
					if (imageUri != null) {

						img_profile.setImageURI(imageUri);
						startUploading();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 2) {
				try {
					if (profile_bm != null) {

						img_profile.setImageBitmap(profile_bm);
						startUploading();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else if (msg.what == 3) {
				try {
					if (pd != null) {
						pd.dismiss();
					}
					Toast.makeText(getActivity(),
							"Profile picture uploaded sucessfully",
							Toast.LENGTH_LONG).show();
					try {
						CommonFunctions.setPref(getActivity(),
								LOCAL_PROFILE_PIC, "" + uploadFilePath);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (msg.what == 4) {
				try {
					if (pd != null) {
						pd.dismiss();
					}
					Toast.makeText(getActivity(),
							"Error while uploading your profile picture",
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	};

	class UploadProfilePic extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			uploadProfilePic();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	private void uploadProfilePic() {
		String Response1 = "";

		try {
			UploadFiles files = new UploadFiles();
			files.setProfilePic(true);
			Response1 = files.SendRecord(uploadFilePath, getActivity());
			files.setProfilePic(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String file_url;
		String Response;
		if (Response1 != null && Response1.length() > 0) {

			try {
				// JSONObject jsonObject = new JSONObject(
				// Response1);

				JSONObject jsonObject1 = new JSONObject(Response1);
				JSONObject jsonObject = jsonObject1.getJSONObject("response");
				String error = jsonObject.getString("error");
				if (error.equalsIgnoreCase("false")) {

					handler.sendEmptyMessage(3);

				} else {
					handler.sendEmptyMessage(4);
					Log.e("",
							"============Error while uploading Video to server==========");
					Response = "";

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}
