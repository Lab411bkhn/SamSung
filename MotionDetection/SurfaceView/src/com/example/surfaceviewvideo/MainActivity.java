package com.example.surfaceviewvideo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		TextureView.SurfaceTextureListener, OnPreparedListener {
	MediaPlayer player, player1;
	String videoSrc = "rtsp://192.168.0.122/onvif1";
//    String videoSrc = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";
	static String URL = "";
	static boolean stream = false;
	static boolean detect = false;
	
	static Context ctx;
	static String MAIL = "";

	static Surface s;
	TextureView tview;
	ImageView imgView;
	String mailAddress = "hantanabka@gmail.com";
	VideoCapture videoCapture;
	SurfaceHolder mSurfaceHolder;
	SurfaceView mSurfaceView;
	Paint paint = new Paint();
	private Bitmap bmp = Bitmap.createBitmap(640, 480, Bitmap.Config.RGB_565);
	private Canvas canvas = new Canvas(bmp);
	private BaseLoaderCallback mOpenCVCallBack;
	private Mat mFlow;
	private Mat matRGB;
	private Mat flowImage;
	
	private Mat mRgb;
	private Mat matOpFlowThis, matOpFlowPrev, hsvImage, bgrImage, maskImage;
	private MatOfPoint MOPcorners;
	private MatOfPoint2f mMOP2fptsPrev, mMOP2fptsSafe, mMOP2fptsThis;
	private List<org.opencv.core.Point> cornersPrev;
	private List<org.opencv.core.Point> cornersThis;
	private MatOfByte mMOBStatus;
	private List<Byte> byteStatus;
	private int x, y;
	private org.opencv.core.Point pt, pt2;
	private Scalar colorRed = new Scalar(255, 0, 0);
	private MatOfFloat mMOFerr;
	private int maxCorners = 300;

	double qualityLevel = 0.01;
	double minDistance = 10;
	int blockSize = 3;
	boolean useHarrisDetector = false;
	double k = 0.04;
	int vmin = 65, vmax = 256, smin = 55;
	int count = 0;

	private enum STATUS {
		STATIC, TRACKING, DYNAMIC
	};

	STATUS status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceHolder = mSurfaceView.getHolder();

		tview = (TextureView) findViewById(R.id.surface);
		tview.setSurfaceTextureListener(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		player1 = new MediaPlayer();
		// player1 = MediaPlayer.create(getApplicationContext(), R.raw.warning);
		// player1.start();

		paint.setColor(0xff00ffff);
		paint.setTextSize(24);

		ctx = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mOpenCVCallBack = new BaseLoaderCallback(this) {
			@Override
			public void onManagerConnected(int status) {
				switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					Log.i("LOAD", "OpenCV loaded successfully");

				}
					break;

				default: {
					super.onManagerConnected(status);
				}
					break;
				}
			}
		};
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this,
				mOpenCVCallBack)) {
			System.out.println("Failed to INIT \n OpenCV Failure");
		} else {
			System.out.println("OpenCV INIT Succes");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int id = item.getItemId();
		switch (id) {
		case R.id.action_stream:
			openStream();
			break;
		case R.id.action_send:
			alertEmail();
			break;
		case R.id.action_cap:
			detectMotion();
			break;
		case R.id.action_stop:
			stop();
			break;
		}
		return true;
	}

	public void detectMotion() {
		detect = true;
	}

	public void stop() {
		
	}

	public void alertEmail() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.mail_dialog);
		final EditText edit_mail = (EditText) dialog
				.findViewById(R.id.edit_mail);
		Button send = (Button) dialog.findViewById(R.id.btn_send);
		Button cancel = (Button) dialog.findViewById(R.id.bt_cancel);

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							GmailSender sender = new GmailSender(
									"manhterry93@gmail.com", "manhterry007");

							sender.sendMail("Alert Motion Detection",
									"xxxxxxxxxxxx", "manhterry93@gmail.com",
									mailAddress);

						} catch (Exception e) {
							Log.e("SendMail", e.getMessage(), e);
						}
					}
				}).start();
				dialog.dismiss();
			}

		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void openStream() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog);
		final EditText edit_url = (EditText) dialog.findViewById(R.id.edit_url);
		Button go = (Button) dialog.findViewById(R.id.btn_go);
		Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// URL = edit_url.getText().toString();
				URL = videoSrc;
				stream = true;
				dialog.dismiss();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stream = false;
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub

				if (stream) {
					try {
						player = new MediaPlayer();
						player.setSurface(s);
						player.setAudioStreamType(AudioManager.STREAM_MUSIC);
						player.setDataSource(videoSrc);
						player.prepare();
						player.setOnPreparedListener(new OnPreparedListener() {

							@Override
							public void onPrepared(MediaPlayer mp) {
								// TODO Auto-generated method stub
								Toast.makeText(ctx, "Prepared!",
										Toast.LENGTH_SHORT).show();
								Thread thread = new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										player.start();
									}
								});
								thread.start();

							}
						});

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		dialog.show();
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		s = new Surface(surface);
		Log.e("Test", "Available");

		mRgb = new Mat();
		mMOP2fptsPrev = new MatOfPoint2f();
		mMOP2fptsSafe = new MatOfPoint2f();
		mMOP2fptsThis = new MatOfPoint2f();
		matOpFlowThis = new Mat();
		matOpFlowThis = new Mat();
		matOpFlowPrev = new Mat();
		MOPcorners = new MatOfPoint();
		mMOBStatus = new MatOfByte();
		mMOFerr = new MatOfFloat();
		hsvImage = new Mat();
		bgrImage = new Mat();
		maskImage = new Mat();
		mFlow =  new Mat();
		matRGB = new Mat();
		flowImage = new Mat(height, width, CvType.CV_8U);

	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.e("Test", "SizeChanged");

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		Log.e("Test", "Destroyed");
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// TODO Auto-generated method stub
		// Log.d("Test", "Updated");
		if (detect) {
			tview.getBitmap(bmp);
			Bitmap result = opticalFlow(bmp);
			Canvas c = mSurfaceHolder.lockCanvas();
			if (canvas == null) {
				Log.e("Test", "Cannot draw onto the canvas as it's null");
			} else {
				//
				c.drawBitmap(result, 0, 0, null);
				mSurfaceHolder.unlockCanvasAndPost(c);
			}

		}
	}

	public Bitmap opticalFlow(Bitmap bitmap) {
		Bitmap result = Bitmap.createBitmap(640, 480, Bitmap.Config.RGB_565);
		// Convert Bitmap to Mat
		Utils.bitmapToMat(bitmap, mRgb);
		if (mMOP2fptsPrev.rows() == 0) {

			// get this mat
			Imgproc.cvtColor(mRgb, matOpFlowThis, Imgproc.COLOR_RGB2GRAY);

			// copy that to prev mat
			matOpFlowThis.copyTo(matOpFlowPrev);

			// get prev corners
			Imgproc.goodFeaturesToTrack(matOpFlowPrev, MOPcorners, maxCorners,
					qualityLevel, minDistance);
			// Imgproc.goodFeaturesToTrack(matOpFlowPrev, MOPcorners,
			// maxCorners, qualityLevel, minDistance, maskImage, blockSize,
			// useHarrisDetector, k);
			mMOP2fptsPrev.fromArray(MOPcorners.toArray());

			// get safe copy of this corners
			mMOP2fptsPrev.copyTo(mMOP2fptsSafe);
		} else {
			// we've been through before so
			// this mat is valid. Copy it to prev mat
			matOpFlowThis.copyTo(matOpFlowPrev);

			// get this mat
			Imgproc.cvtColor(mRgb, matOpFlowThis, Imgproc.COLOR_RGB2GRAY);

			// get the corners for this mat
			Imgproc.goodFeaturesToTrack(matOpFlowThis, MOPcorners, maxCorners,
					qualityLevel, minDistance);
			// Imgproc.goodFeaturesToTrack(matOpFlowPrev, MOPcorners,
			// maxCorners, qualityLevel, minDistance, maskImage, blockSize,
			// useHarrisDetector, k);
			mMOP2fptsThis.fromArray(MOPcorners.toArray());

			// retrieve the corners from the prev mat
			// (saves calculating them again)
			mMOP2fptsSafe.copyTo(mMOP2fptsPrev);

			// and save this corners for next time through

			mMOP2fptsThis.copyTo(mMOP2fptsSafe);
		}

		Video.calcOpticalFlowPyrLK(matOpFlowPrev, matOpFlowThis, mMOP2fptsPrev,
				mMOP2fptsThis, mMOBStatus, mMOFerr);

		cornersPrev = mMOP2fptsPrev.toList();
		cornersThis = mMOP2fptsThis.toList();
		byteStatus = mMOBStatus.toList();

		y = byteStatus.size() - 1;
		ArrayList<Point> listPoint = new ArrayList<Point>();
		for (x = 0; x < y; x++) {
			if (byteStatus.get(x) == 1) {
				pt = cornersThis.get(x);
				pt2 = cornersPrev.get(x);
				listPoint.add(new Point(Math.abs(pt.x - pt2.x), Math.abs(pt.y
						- pt2.y)));

				Core.circle(mRgb, pt, 5, colorRed, 3 - 1);

				Core.line(mRgb, pt, pt2, colorRed, 3);
			}
		}
		warning(listPoint);
		Utils.matToBitmap(mRgb, result);
		return result;
	}

	public void warning(ArrayList<Point> listPoint) {
		int dem = 0;
		for (int i = 0; i < listPoint.size(); i++) {
			if (listPoint.get(i).x >= 5 || listPoint.get(i).y >= 5) {
				dem++;
			}
		}
		if (dem >= 20) {
			// warning = true;
			status = STATUS.DYNAMIC;
			count = 0;
			Log.e("DYNAMIC", dem + "");
		} else {
			count++;
			if (count <= 2) {
				status = STATUS.TRACKING;
				Log.e("TRACKING", count + "");
			} else {
				status = STATUS.STATIC;
				Log.e("STATIC", count + "");
				if (count > 10)
					count = 3;
			}
		}
		switch (status) {
		case STATIC:
			if (player1.isPlaying()) {
				player1.stop();
				Toast.makeText(getApplicationContext(),"Stop",Toast.LENGTH_SHORT).show();
			}
			break;
		case TRACKING:
			if (player1.isPlaying()) {
				player1.stop();
				Toast.makeText(getApplicationContext(),"Stop",Toast.LENGTH_SHORT).show();
			}
			break;
		case DYNAMIC:
			if (!player1.isPlaying()) {
				player1.reset();
				player1 = MediaPlayer.create(getApplicationContext(),
						R.raw.warning);
				player1.start();
				Toast.makeText(getApplicationContext(),"isPlaying",Toast.LENGTH_SHORT).show();
			}
			 break;
		default:
			break;
		}
		
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub

	}
}
