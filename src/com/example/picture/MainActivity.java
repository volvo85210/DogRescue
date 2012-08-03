package com.example.picture;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import android.app.Activity;  
import android.content.Context;
import android.content.Intent;  
import android.database.Cursor;  
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;  
import android.os.Bundle;  
import android.os.Environment;  
import android.provider.MediaStore;  
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.AutoCompleteTextView;
import android.widget.Button;  
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;


public class MainActivity extends Activity  implements OnClickListener {    
	  private static final int RESULT_CAPTURE_IMAGE = 1;// 照相的requestCode    
	  private static final int REQUEST_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode    
	  private LocationManager lms;
      private String bestProvider = LocationManager.GPS_PROVIDER;
      private boolean getService = false;
      public String latitude;//緯度
      public String longitude;//經度
	         private String strImgPath = "";// 照片文件绝对路径    
	         private String strVideoPath = "";// 视频文件的绝对路径    
	         Canvas canvas;
	         Button buttonShot;
	         RelativeLayout introLayout;
	         RelativeLayout photo;
	         RelativeLayout textphoto;
	         RelativeLayout text;
	         AutoCompleteTextView autoCompleteTextView1;
	         AutoCompleteTextView autoCompleteTextView2;
	         AutoCompleteTextView autoCompleteTextView3;
	       //  Button buttonVideo;   
	         String path;
	         @Override    
	         protected void onCreate(Bundle savedInstanceState) {    
	                 super.onCreate(savedInstanceState);    
	                 this.setContentView(R.layout.activity_main);    
	                 buttonShot = (Button)findViewById(R.id.ButtonShot);  
	                 buttonShot.setOnClickListener(this);  
	               //  buttonVideo = (Button)findViewById(R.id.ButtonVideo);  
	               //  buttonVideo.setOnClickListener(this);
	                 introLayout = (RelativeLayout) findViewById(R.id.introLayout);
	                  photo = (RelativeLayout) findViewById(R.id.photo);
	                  textphoto=(RelativeLayout)findViewById(R.id.textphoto);
	                  text=(RelativeLayout)findViewById(R.id.text);
	                 introLayout.setVisibility(View.GONE);
	                 textphoto.setVisibility(View.GONE);
	                 text.setVisibility(View.GONE);
	                
	         }    
	         
	        private void locationServiceInitial() {
				
	        	lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
	        	Criteria criteria = new Criteria();	//資訊提供者選取標準
	    		bestProvider = lms.getBestProvider(criteria, true);	//選擇精準度最高的提供者
	    		Location location = lms.getLastKnownLocation(bestProvider);
	    		getLocation(location);
			}
	        
			private void getLocation(Location location) {
				if(location != null) {
				
		 
					Double lo = location.getLongitude();	//取得經度
					Double la = location.getLatitude();	//取得緯度
					latitude=String.valueOf(la);
					longitude=String.valueOf(lo);
					Log.e("經度經度經度經度經度經度", String.valueOf(longitude));
					Log.e("緯度緯度緯度緯度緯度緯度", String.valueOf(latitude));
				}
				else {
					Log.e("!!!!!!!!!!!!!!", "無法定位座標");
					//Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
				}
			}
			
			

			@Override    
	         protected void onActivityResult(int requestCode, int resultCode, Intent data) {    
	                 super.onActivityResult(requestCode, resultCode, data);    
	                 switch (requestCode) {    
	                case RESULT_CAPTURE_IMAGE://拍照    
	                         if (resultCode == RESULT_OK) {    
	                                 Toast.makeText(this, strImgPath, Toast.LENGTH_SHORT).show();    
	                                 Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	                                  ImageView ivTest = (ImageView)findViewById(R.id.ivTest);
	                                 
	                               // Best of quality is 80 and more, 3 is very low quality of image 
	                               Bitmap bJPGcompress = codec(thumbnail, Bitmap.CompressFormat.PNG, 3);
	                                 ivTest.setImageBitmap(bJPGcompress);
	                                
	                                 introLayout.setVisibility(View.VISIBLE);
	                                 photo.setVisibility(View.GONE);
	                                 textphoto.setVisibility(View.VISIBLE);
	                                 text.setVisibility(View.VISIBLE);
	                                 
	                                 LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
	             	 		  		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	             	 		  			//如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
	             	 		  			getService=true;
	             	 		  			locationServiceInitial();
	             	 		  		} else {
	             	 		  			Log.e("!!!!!!!!!!!!!!", "請開啟定位服務");
	             	 		  			//Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
	             	 		  			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
	             	 		  		}
	                                 
	                                 httppost(bJPGcompress);///////////傳取得的照片出去
	                         }    
	                         break;    
	                 case REQUEST_CODE_TAKE_VIDEO://拍摄视频    
	                         if (resultCode == RESULT_OK) {    
	                                 Uri uriVideo = data.getData();    
	                                 Cursor cursor=this.getContentResolver().query(uriVideo, null, null, null, null);    
	                                 if (cursor.moveToNext()) {    
	                                         /* _data：文件的绝对路径 ，_display_name：文件名 */    
	                                         strVideoPath = cursor.getString(cursor.getColumnIndex("_data"));    
	                                        Toast.makeText(this, strVideoPath, Toast.LENGTH_SHORT).show();    
	                                 }    
	                         }    
	                         break;     
	                 }    
	         }    

	      

			private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,int quality) {
	    		ByteArrayOutputStream os = new ByteArrayOutputStream();
	    		src.compress(format, quality, os);
	    		byte[] array = os.toByteArray();
	    		return BitmapFactory.decodeByteArray(array, 0, array.length);
	    	}
			
			
	        
	        /////////////post照片和資料////////////
	        public void httppost(Bitmap bitmap){//目前引數(BITMAP)沒有用到
	        	final AutoCompleteTextView textview1=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
	        	final AutoCompleteTextView textview2=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);
	        	final AutoCompleteTextView textview3=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView3);
	        	Button send=(Button)findViewById(R.id.Buttonsend);
	        	
	        	send.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View arg0) {
						if ((textview1.getText().length()!=0) && (textview2.getText().length()!=0) && (textview3.getText().length()!=0)){
	                		String lalo=latitude+","+longitude;
	                		URL url;
	        	        	FileInputStream fileInputStream;
	        				try {
	        					fileInputStream = new FileInputStream(new File(path));//path是新照照片的檔案路徑
	        				
	        	        	final String BOUNDARY 	= "==================================";//
	        	        	final String HYPHENS 	= "--";								   //android 傳FILE固定格式需要用到的字串
	        	        	final String CRLF 		= "\r\n";							   //
	        	        	
	        				try {
	        					url = new URL("http://cyc.mrshih.com/dog/upload.php");
	        				
	        	            HttpURLConnection URLConn = (HttpURLConnection) url.openConnection(); 
	        	       
	        	            URLConn.setDoOutput(true); 
	        	            URLConn.setDoInput(true); 
	        	            ((HttpURLConnection) URLConn).setRequestMethod("POST"); 
	        	            URLConn.setUseCaches(false); 	          
	        	            URLConn.setInstanceFollowRedirects(false);
	        	            
	                        URLConn.setRequestProperty("Connection", "Keep-Alive");///////////////////////**********
	        	            
	        	            URLConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
	        	        	DataOutputStream dataOS = new DataOutputStream(URLConn.getOutputStream());//開OUTPUT串流	 
	        	        	////////////////////寫FILE////////////////DOWN
	        	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"name\""+CRLF);
	        	        	dataOS.writeBytes(CRLF);
	        	        	//String t =URLEncoder.encode("王", "utf-8");
	        	        	
	        	        	dataOS.writeBytes(URLEncoder.encode(textview1.getText().toString(), "utf-8")+CRLF);
	        	        	
	        	        	
	        	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"phone\""+CRLF);
	        	        	dataOS.writeBytes(CRLF);
	        	        	dataOS.writeBytes(URLEncoder.encode(textview2.getText().toString(), "utf-8")+CRLF);
	        	        	
	        	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"address\""+CRLF);
	        	        	dataOS.writeBytes(CRLF);
	        	        	dataOS.writeBytes(URLEncoder.encode(textview3.getText().toString(), "utf-8")+CRLF);
	        	        	
	        	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	        	dataOS.writeBytes("Content-Disposition:from-data; name=\"gps\""+CRLF);
	        	        	dataOS.writeBytes(CRLF);
	        	        	dataOS.writeBytes(URLEncoder.encode(lalo, "utf-8")+CRLF);
	        	        	Log.e("GPSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS", lalo);
	        	        	dataOS.writeBytes(CRLF);
	        	        	
	        	        	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);		// 寫--==================================
	        	            String strContentDisposition = "Content-Disposition: form-data; name=\"file\"; filename=\"temp.jpeg\"";
	        	            String strContentType = "Content-Type: image/jpeg";
	        	            
	        	        	dataOS.writeBytes(strContentDisposition+CRLF);	// 寫(Disposition)
	        	        	dataOS.writeBytes(strContentType+CRLF);			// 寫(Content Type)
	        	        	dataOS.writeBytes(CRLF);	
	        	        	
	        	        	int iBytesAvailable = fileInputStream.available();
	        	        	byte[] byteData = new byte[iBytesAvailable];
	        	            int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
	        	            
	        	            /////寫照片//
	        	        	while (iBytesRead > 0) {
	        	        		dataOS.write(byteData, 0, iBytesAvailable);	// 開始寫內容
	        	        		iBytesAvailable = fileInputStream.available();
	        	        		iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
	        	        	}
	        	        	//////寫照片//
	        	        	
	        	        	dataOS.writeBytes(CRLF+HYPHENS+BOUNDARY+HYPHENS+CRLF);	// (結束)寫--==================================--		
	        	        	fileInputStream.close();
	        	            URLConn.connect();  
	        	          
	        	       
	        	            dataOS.flush();
	        	            dataOS.close(); // flush and close
	        	            
	        	            java.io.BufferedReader rd = new java.io.BufferedReader( new java.io.InputStreamReader(URLConn.getInputStream())); //開INPUT串流
	        	            
	        	            /*收PHP ECHO的字串*/
	        	            String line; 
	        	            Log.e("%%%%%%%%%%%%%%%%%", path);
	        	            while ((line = rd.readLine()) != null) { 
	        	             Log.e("##################",line); 
	        	            } 	            
	        	            /**********************/
	        	            rd.close(); 

	        	            URLConn.disconnect();
	        				} catch (MalformedURLException e) {
	        					// TODO Auto-generated catch block
	        					e.printStackTrace();
	        				} catch (IOException e) {
	        					// TODO Auto-generated catch block
	        					e.printStackTrace();
	        				} 
	        				} catch (FileNotFoundException e1) {
	        					// TODO Auto-generated catch block
	        					e1.printStackTrace();
	        				}
	                	}else{
	                		
	                	}
					}
	        		
	        	});
	        	
	                	
				
	        }
	         /**  
	          * 照相功能  
	          */    
	         private void cameraMethod() {    
	        	 
	                 Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    
	                 strImgPath = Environment.getExternalStorageDirectory().toString() + "/CONSDCGMPIC/";//存放照片的文件夹    
	                 
	                // Log.e("@@@@@@@@@@@@@@", Environment.getExternalStorageDirectory().toString());
	                 
	                 String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//照片命名    
	                 
	                 File out = new File(strImgPath);    
	                 if (!out.exists()) {    
	                         out.mkdirs();    
	                 }    
	                 out = new File(strImgPath, fileName);    
	                 strImgPath = strImgPath + fileName;//该照片的绝对路径    
	                Uri uri = Uri.fromFile(out);    
	                 imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);    
	                 imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);    
	                 path=uri.getPath();
	                 startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE); //取得系統定位服務
	     
	         }    
	     
	         /**  
	          * 拍摄视频  
	          */    
	         private void videoMethod() {    
	                 Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);    
	                 intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);    
	                 startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);    
	        }    
	     
	        /**  
	          * 录音功能  
	          */       
	     
	         /**  
	          * 提示信息  
	          * @param text  
	          * @param duration  
	          */    
	        private void showToast(String text, int duration) {    
	                 Toast.makeText(MainActivity.this, text, duration).show();    
	        }
	        public void onClick(View v) {  	        	
				int id = v.getId();  
		    	switch(id){  
		    		case R.id.ButtonShot:  
		    				cameraMethod();  
		    				break;  
		    				/*
		    		case R.id.ButtonVideo:  
		    				videoMethod();  
		    				break;
		    				*/  
		    	}  
	       }    
	           
	}  