package cn.creable.android.demo7;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.ucmap.MapLoader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ZoomControls;

public class MainActivity extends Activity {
	
	MapView mapView;
	Activity act;
	PathAnalysisTool pathTool;
	
	String mapPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		act=this;
		mapPath=Environment.getExternalStorageDirectory().getPath()+"/lujiang";
		//���÷Ŵ���С��ť�¼�
  		ZoomControls zc=(ZoomControls)findViewById(R.id.zoomControls);
  		zc.setOnZoomInClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomInTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool(true);
  			}
  		});
  		zc.setOnZoomOutClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomOutTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool(true);
  			}
  		});
        
        mapView=(MapView)findViewById(R.id.mapView);
        mapView.setListener(new IMapViewListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				MapControl mapControl=mapView.getMapControl();
				mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				if (mapControl.getMap()==null)
				{
					mapControl.loadMap(mapPath+"/map.ini", (byte)0);
					mapControl.setPanTool(true);
				}
			}
        	
        });
        
        Button btn1=(Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path=Environment.getExternalStorageDirectory().getPath();
				MapControl mapControl=mapView.getMapControl();
				mapControl.loadMap(path+"/bj2/map.ini", (byte)0);
				mapControl.setPanTool();
				Intent intent=new Intent(App.getInstance(), MainActivity2.class);
				startActivity(intent); 
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,0,"·������");
		menu.add(0, 2, 0, "���µ�ͼ");
		menu.add(0, 3, 0, "����DT_ROAD_polylineͼ��");
		menu.add(0, 100, 0, "�˳�");
		return true;
	}
	
	private ProgressDialog m_pDialog;
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					if (msg.arg1==1)
					{
						final int mapVersion=msg.arg2;
					new AlertDialog.Builder(act)
							.setTitle("��ͼ�и��£�ȷ�ϸ�����")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											m_pDialog=null;
									        // ����ProgressDialog����   
									        m_pDialog = new ProgressDialog(act);   
									          
									        // ���ý�������񣬷��ΪԲ��   
									        m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   
									          
									        // ����ProgressDialog ����   
									        m_pDialog.setTitle("��ʾ");   
									          
									        // ����ProgressDialog ��ʾ��Ϣ   
									        m_pDialog.setMessage("�������ص�ͼ");   
									          
									        // ����ProgressDialog ����ͼ��   
									        m_pDialog.setIcon(R.drawable.ic_launcher);   
									          
									        // ����ProgressDialog ����������   
									        m_pDialog.setProgress(100);   
									          
									        // ����ProgressDialog �Ľ������Ƿ���ȷ   
									        m_pDialog.setIndeterminate(false);   
									          
									        // ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��   
									        m_pDialog.setCancelable(false);   
									          
									        // ��ProgressDialog��ʾ   
									        m_pDialog.show();   
											
									        mapView.getMapControl().closeMap();//���µ�ͼǰ���ȹرյ�ͼ
											MapUpdater mu = new MapUpdater(mapVersion);
											mu.update(mapPath,handler);
										}
									})
							.setNegativeButton("����",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).show();
						
					}
					else if (msg.arg1==0)
					{
						AlertDialog.Builder builder1 = new AlertDialog.Builder(act);
    					builder1.setTitle("��Ϣ");
    					builder1.setMessage("���ĵ�ͼ�����µ�");
    					builder1.setCancelable(true);
    					builder1.setPositiveButton("OK", null);
    					builder1.create().show();
					}
					break;
				case 2:
					m_pDialog.setProgress((int)((double)msg.arg1/msg.arg2*100));
					break;
				case 3:
					m_pDialog.setProgress(100);
					m_pDialog.setMessage("����ɾ���ɵ�ͼ");
					break;
				case 4:
					m_pDialog.setMessage("���ڽ�ѹ���µ�ͼ");
					break;
				case 5:
					mapView.getMapControl().loadMap(mapPath+"/map.ini", (byte)0);
					m_pDialog.setMessage("��ͼ�������");
					m_pDialog.setCancelable(true);//���֮���û����԰���back�����ص�ͼ
					//m_pDialog.cancel();
					break;
				case 6:
					if (msg.arg1>0)
					{
						m_pDialog.setProgress(100);
						mapView.getMapControl().loadMap(mapPath+"/map.ini", (byte)0);
						m_pDialog.setMessage("ͼ��������");
						m_pDialog.setCancelable(true);//���֮���û����԰���back�����ص�ͼ
					}
					else
					{
						m_pDialog.setMessage("ͼ�����ʧ�ܣ������Ƿ���ͼ����.dat��ͼ����.bin�ļ�");
						m_pDialog.setCancelable(true);//���֮���û����԰���back�����ص�ͼ
					}
					break;
			}
		};
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 1:
			if (pathTool==null)
			{
				pathTool=new PathAnalysisTool(mapView.getMapControl(),act);
				mapView.getMapControl().addCustomDraw(pathTool);
			}
			mapView.getMapControl().setCurrentTool(pathTool);
			pathTool.clearPath();
			mapView.getMapControl().refresh();
			break;
		case 2:
			MapUpdater mu=new MapUpdater(0);
			mu.check(mapPath,handler);
			break;
		case 3:
		{
			m_pDialog=null;
	        // ����ProgressDialog����   
	        m_pDialog = new ProgressDialog(act);   
	          
	        // ���ý�������񣬷��ΪԲ��   
	        m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   
	          
	        // ����ProgressDialog ����   
	        m_pDialog.setTitle("��ʾ");   
	          
	        // ����ProgressDialog ��ʾ��Ϣ   
	        m_pDialog.setMessage("���ڸ���ͼ��");   
	          
	        // ����ProgressDialog ����ͼ��   
	        m_pDialog.setIcon(R.drawable.ic_launcher);   
	          
	        // ����ProgressDialog ����������   
	        m_pDialog.setProgress(100);   
	          
	        // ����ProgressDialog �Ľ������Ƿ���ȷ   
	        m_pDialog.setIndeterminate(true);   
	          
	        // ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��   
	        m_pDialog.setCancelable(false);   
	          
	        // ��ProgressDialog��ʾ   
	        m_pDialog.show();   
			new Thread() {
				@Override
				public void run() {
					ILayer layer=mapView.getMapControl().getMap().getLayer("DT_ROAD_polyline");
					int ret=0;
					if (layer instanceof IShapefileLayer)
						ret=((IShapefileLayer)layer).update();
					Message msg=new Message();
					msg.what=6;
					msg.arg1=ret;
					handler.sendMessage(msg);
				}
			}.start();
			break;
		}
		case 100:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) 
		{
			MapControl mapControl=mapView.getMapControl();
			String path=Environment.getExternalStorageDirectory().getPath();
			mapControl.loadMap(path+"/lujiang/map.ini", (byte)0);
			mapControl.setPanTool();
			mapView.getMapControl().refresh(mapView);
		}
		super.onResume();
	}

}
