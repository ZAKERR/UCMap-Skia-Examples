package cn.creable.demo8;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import cn.creable.demo8.Util.LocationPoint;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IInfoToolListener;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.display.MarkerSymbol;
import cn.creable.gridgis.display.PictureMarkerSymbol;
import cn.creable.gridgis.display.SimpleRenderer;
import cn.creable.gridgis.display.UniqueValueRenderer;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MainActivity extends Activity {
	
	MapView mapView;
	MediaRecorder mediaRecorder = new MediaRecorder();
	Button btn1;

	Activity act;
	
	private double x,y;
	private int type;
	private String pathname;
	
	private String mapPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		act=this;
		
		new File(Environment.getExternalStorageDirectory().getPath()+"/UCMap").mkdir();
		
		//���÷Ŵ���С��ť�¼�
  		ZoomControls zc=(ZoomControls)findViewById(R.id.zoomControls);
  		zc.setOnZoomInClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomInTool();
  				mapView.getMapControl().getCurrentTool().action();
  				setMapTool();
  			}
  		});
  		zc.setOnZoomOutClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomOutTool();
  				mapView.getMapControl().getCurrentTool().action();
  				setMapTool();
  			}
  		});
  		
  		Util.getInstance().openGPS(this, 15000, 0.01f);
        
        mapView=(MapView)findViewById(R.id.mapView);
        mapView.setListener(new IMapViewListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				MapControl mapControl=mapView.getMapControl();
				Util.getInstance().setMapControl(mapControl);
				mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				if (mapControl.getMap()==null)
				{
					mapPath=Environment.getExternalStorageDirectory().getPath()+"/bj2";
					mapControl.loadMap(mapPath+"/map.ini", (byte)0);
					setMapTool();
					
					mapControl.setCustomDraw(new MyCustomDraw(mapControl));
				}
			}
        	
        });
        
        btn1=(Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn1.getText().toString().equalsIgnoreCase("¼��"))
				{
					type=0;
					LocationPoint pos=Util.getInstance().getCurrentLocation();
					if (pos==null)
					{
						new android.app.AlertDialog.Builder(act) 
						.setTitle("ȷ��")
						.setMessage("δ�ܻ�ȡ��ǰgpsλ�ã��Ƿ�ģ��һ����")
						.setPositiveButton("��", new OnClickListener() {
	
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								getRandomPoint();
								btn1.setText("ֹͣ¼��");
								addVoicePoint();
							}
							
						})
						.setNegativeButton("��", null)
						.show();
					}
					else
					{
						x=pos.lon;
						y=pos.lat;
						btn1.setText("ֹͣ¼��");
						addVoicePoint();
					}
				}
				else
				{
					btn1.setText("¼��");
					mediaRecorder.stop();
					
					ShapefileLayer layer=getLayer();
					String[] values=new String[3];
					values[0]=pathname;
					values[1]="";
					values[2]=Integer.toString(type);
					layer.addFeature(new Point(x,y), values);
					mapView.getMapControl().refresh();
				}

			}
        	
        });
        
        Button btn2=(Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				type=1;
				LocationPoint pos=Util.getInstance().getCurrentLocation();
				if (pos==null)
				{
					new android.app.AlertDialog.Builder(act) 
					.setTitle("ȷ��")
					.setMessage("δ�ܻ�ȡ��ǰgpsλ�ã��Ƿ�ģ��һ����")
					.setPositiveButton("��", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							getRandomPoint();
							pathname=Environment.getExternalStorageDirectory().getPath()+"/UCMap/photo_"+System.currentTimeMillis()+".jpg";
							File file = new File(pathname);
							Uri uri = Uri.fromFile(file); 
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent, 1); 
						}
						
					})
					.setNegativeButton("��", null)
					.show();
				}
				else
				{
					x=pos.lon;
					y=pos.lat;
					
					pathname=Environment.getExternalStorageDirectory().getPath()+"/UCMap/photo_"+System.currentTimeMillis()+".jpg";
					File file = new File(pathname);
					Uri uri = Uri.fromFile(file); 
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					startActivityForResult(intent, 1); 
				}
			}
        	
        });
        
        Button btn3=(Button)findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				type=2;
				LocationPoint pos=Util.getInstance().getCurrentLocation();
				if (pos==null)
				{
					new android.app.AlertDialog.Builder(act) 
					.setTitle("ȷ��")
					.setMessage("δ�ܻ�ȡ��ǰgpsλ�ã��Ƿ�ģ��һ����")
					.setPositiveButton("��", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							getRandomPoint();
							
							pathname=Environment.getExternalStorageDirectory().getPath()+"/UCMap/video_"+System.currentTimeMillis()+".3gp";
							File file = new File(pathname);
							Uri uri = Uri.fromFile(file); 
							//create new Intent
							   Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
							   intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							   intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
							   startActivityForResult(intent, 2);

						}
						
					})
					.setNegativeButton("��", null)
					.show();
				}
				else
				{
					x=pos.lon;
					y=pos.lat;
					
					pathname=Environment.getExternalStorageDirectory().getPath()+"/UCMap/video_"+System.currentTimeMillis()+".3gp";
					File file = new File(pathname);
					Uri uri = Uri.fromFile(file); 
					//create new Intent
					   Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					   intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					   intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					   startActivityForResult(intent, 2);
				}
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0, 1, 0, "�˳�");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 1:	//�˳�
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private ShapefileLayer getLayer()
	{
		ILayer lyr=mapView.getMapControl().getMap().getLayer("�ҵı�ע");
		if (lyr==null)
		{
			//��3��png���Ÿ��ƽ���ͼ��lib�ļ���
			new File(mapPath+"/lib").mkdir();
			String ppp=mapPath+"/lib/voice.png";
			if (!new File(ppp).exists()) copyRawFile(R.raw.voice,ppp);
			ppp=mapPath+"/lib/photo.png";
			if (!new File(ppp).exists()) copyRawFile(R.raw.photo,ppp);
			ppp=mapPath+"/lib/video.png";
			if (!new File(ppp).exists()) copyRawFile(R.raw.video,ppp);
			
			//ͼ�㷶Χ
			IEnvelope env=mapView.getMapControl().getFullExtent();
			double[] bbox={env.getXMin(),env.getYMin(),env.getXMax(),env.getYMax()};
			//�ֶ���������
			String[] fieldNames={"�ļ�","����","����"};
			//�ֶγ�������
			int[] fieldLengths={50,80,30};
			//�ֶ��������飬C��ʾ�ַ�����N��ʾ���֣������д
			char[] fieldTypes={'C','C','C'}; 
			//����ͼ��
			ShapefileLayer layer=ShapefileLayer.create(
					(short)mapView.getMapControl().getMap().getLayerCount(), 
					"�ҵı�ע", 100000, 0, true, true, 
					new SimpleRenderer(null,null,new MarkerSymbol(8,0,0,0,0)), 
					fieldNames[1], bbox, GeometryType.Point, fieldNames, fieldLengths, fieldTypes);
			//����Ωһֵ��Ⱦ��������Ҫ�ص������ֶ�ֵ����ȾҪ��
			UniqueValueRenderer renderer=new UniqueValueRenderer(null,layer);
			renderer.setFieldIndex(2);
			renderer.setSymbol("0", PictureMarkerSymbol.createFromLib("voice.png"));
			renderer.setSymbol("1", PictureMarkerSymbol.createFromLib("photo.png"));
			renderer.setSymbol("2", PictureMarkerSymbol.createFromLib("video.png"));
			layer.setRenderer(renderer);
			//���á��ҵı�ע��ͼ��ı�ע��ʽ
			mapView.getMapControl().addLabelStyle(layer.getID(), 20, 0xFFFF0000, 2, 10);
			//��ͼ�����map
			mapView.getMapControl().getMap().addLayer(layer);
			//����дini�ļ��������´δ򿪵�ͼʱ���������������ͼ��
			mapView.getMapControl().rewriteIni(); 
			
			return layer;
		}
		else
			return (ShapefileLayer)lyr;
	}
	
	private void addVoicePoint()
	{
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			pathname=Environment.getExternalStorageDirectory().getPath()+"/UCMap/voice_"+System.currentTimeMillis()+".amr";
			mediaRecorder.setOutputFile(pathname);//(audioFile.getAbsolutePath());
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 || requestCode==2) {
			if (resultCode == RESULT_OK) {
				ShapefileLayer layer=getLayer();
				String[] values=new String[3];
				values[0]=pathname;
				values[1]="";
				values[2]=Integer.toString(type);
				layer.addFeature(new Point(x,y), values);
				mapView.getMapControl().refresh();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void copyRawFile(int rawId,String dest)
	{
		try {
			InputStream is=this.getResources().openRawResource(rawId);
			FileOutputStream fos=new FileOutputStream(dest);
			byte[] buffer=new byte[8912];
			int count=0;
			while ((count=is.read(buffer))>0)
			{
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setMapTool()
	{
		//�������ĳЩͼ�㲻�ܵ��У��Ǿ͵�����Щͼ���setSelectable(false);����������Ϊ����ѡ��
		MyPanTool tool=new MyPanTool(mapView.getMapControl(),new IInfoToolListener(){

			@Override
			public void notify(MapControl mapControl, IFeatureLayer flayer,
					IFeature ft, String[] fields, String[] values) {
				mapControl.flashFeatures(flayer, ft.getOid());//��˸��ѡ�еĵ�ͼҪ��

				String path=values[0];
				if (path.endsWith(".amr"))
				{
					Intent intent=getAudioFileIntent(path);
					startActivity(intent);
//					MediaPlayer player=new MediaPlayer();
//		            
//		            try {
//						player.setDataSource(path);
//						player.prepare();
//			            player.start();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
				else if (path.endsWith(".3gp"))
				{
					Intent intent=getVideoFileIntent(path);
					startActivity(intent);
//					Uri uri = Uri.parse(path);     
//					//����ϵͳ�Դ��Ĳ�����    
//					Intent intent = new Intent(Intent.ACTION_VIEW);    
//					intent.setDataAndType(uri, "video/mp4");    
//					startActivity(intent);  

				}
				else
				{
					Intent intent=getImageFileIntent(path);
					startActivity(intent);
				}

			}
			
		});
		mapView.getMapControl().setCurrentTool(tool);
	}
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh();
		super.onResume();
	}
	
	private void getRandomPoint()
	{
		ShapefileLayer layer=getLayer();
		IEnvelope full=layer.getFullExtent();
		IEnvelope env=mapView.getMapControl().getExtent();
		Random r=new Random();
		while (true)
		{
			x=env.getXMin()+((env.getXMax()-env.getXMin())*(double)(r.nextInt(100))/100);
			y=env.getYMin()+((env.getYMax()-env.getYMin())*(double)(r.nextInt(100))/100);
			if (full.getXMin()<x && x<full.getXMax() && full.getYMin()<y && y<full.getYMax())
				break;
		}
	}
	
	public static Intent getVideoFileIntent( String param )
	  {
	    Intent intent = new Intent("android.intent.action.VIEW");
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra("oneshot", 0);
	    intent.putExtra("configchange", 0);
	    Uri uri = Uri.fromFile(new File(param ));
	    intent.setDataAndType(uri, "video/*");
	    return intent;
	  }
	
	public static Intent getAudioFileIntent( String param )
	  {
	    Intent intent = new Intent("android.intent.action.VIEW");
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra("oneshot", 0);
	    intent.putExtra("configchange", 0);
	    Uri uri = Uri.fromFile(new File(param ));
	    intent.setDataAndType(uri, "audio/*");
	    return intent;
	  }

	public static Intent getImageFileIntent( String param )
	  {
	    Intent intent = new Intent("android.intent.action.VIEW");
	    intent.addCategory("android.intent.category.DEFAULT");
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    Uri uri = Uri.fromFile(new File(param ));
	    intent.setDataAndType(uri, "image/*");
	    return intent;
	  }
}
