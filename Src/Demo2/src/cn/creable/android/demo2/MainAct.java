package cn.creable.android.demo2;

import java.util.Vector;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.CustomDrawGeometrySelector;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.ucmap.ILocalSearchListener;
import cn.creable.ucmap.IPathSearchListener;
import cn.creable.ucmap.MapLoader;
import cn.creable.ucmap.OpenSourceMapLayer;
import cn.creable.ucmap.OpenSourceMapLayer.Path;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainAct extends Activity {
	MapView mapView;
	ProgressDialog dlg;
	Activity act;
	PathTool pathTool;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pathTool=null;
        act=this;
        mapView=(MapView)findViewById(R.id.mapView);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(0, 0, 0, "װ���ͼ");
		menu.add(0, 1, 0, "�Ŵ��ͼ");
		menu.add(0, 2, 0, "��С��ͼ");
		menu.add(0, 3, 0, "ƽ�Ƶ�ͼ");
		menu.add(0,12,0,"�����ѯ����");
		menu.add(0,10,0,"�ô��������ͼ");
		menu.add(0, 11, 0, "�鿴gps��");
		menu.add(0, 4, 0, "�ؼ��ֲ�ѯ");
		menu.add(0, 5, 0, "·����ѯ");
		menu.add(0, 6, 0, "�л���ͼ����Դ");
		menu.add(0, 7, 0, "�л���ͼģʽ");
		menu.add(0, 8, 0, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 0:	//װ���ͼ
			MapLoader.loadMapXML(mapView.getMapControl(), "/sdcard/OpenSourceMap.xml");
			mapView.getMapControl().setCustomDraw(new GPSCustomDraw(mapView.getMapControl()));
			mapView.getMapControl().setPanTool();
			break;
		case 1:	//�Ŵ��ͼ
			mapView.getMapControl().setZoomInTool();
			break;
		case 2:	//��С��ͼ
			mapView.getMapControl().setZoomOutTool();
			break;
		case 3:	//ƽ�Ƶ�ͼ
			mapView.getMapControl().setPanTool();
			break;
		case 10://�ô�����ͼ����
		{
			mapView.getMapControl().getDisplay().getDisplayTransformation().setZoom(10);//ֱ��������ʾ�����ߣ�ʵ�ַŴ���Ҫ�ı��������������Ҫ���У�����ע�͵���һ��
			//���´���ʵ����(117,30)Ϊ���ľ���
			OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
			Point pt=oslayer.fromLonLat(117,30);
			Point offset=oslayer.getOffset(117,30);
			pt.setX(pt.getX()+offset.getX());
			pt.setY(pt.getY()+offset.getY());
			IEnvelope env=mapView.getMapControl().getExtent();//TODO:ע�⣬�������Ҫ�ƶ����ĵ�ʱ��setZoom��getExtent������setZoom֮����ò���
			env.centerAt(pt);
			mapView.getMapControl().refresh(env);
			break;
		}
		case 4:	//�ؼ��ֲ�ѯ
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				oslayer.setLocalSearchListener(new ILocalSearchListener(){

					@Override
					public void localSearchFinished(Vector pois) {
						if (pois==null) return;
						int size=pois.size();
						StringBuilder sb=new StringBuilder();
						for (int i=0;i<size;++i)
						{
							cn.creable.ucmap.OpenSourceMapLayer.POI poi=(cn.creable.ucmap.OpenSourceMapLayer.POI)pois.get(i);
							sb.append(poi.title);
							sb.append("\n");
						}
						sb.deleteCharAt(sb.length()-1);
						//����Toast��ʾ��ѯ������Ϣ�������
						Bundle b=new Bundle();
						b.putString("string",sb.toString());
						Message msg=new Message();
						msg.what=1;
						msg.setData(b);
						handler.sendMessage(msg);
						sb=null;
					}
					
				});
				oslayer.localSearch("�Ͼ�,��ɽ", 0);
				//��ʾ�ȴ�����
				dlg = new ProgressDialog(act);   
				dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
				dlg.setTitle("��ʾ");   
				dlg.setMessage("���ڽ��йؼ��ֲ�ѯ�����Ժ�");   
				dlg.setIcon(R.drawable.icon);   
				dlg.setIndeterminate(false);   
				dlg.setCancelable(true);
				dlg.setButton("ȡ�� ", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
						oslayer.cancel();
					}
					
				});
				dlg.show();   
			}
			break;
		case 5:	//·����ѯ
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				if (pathTool==null)
				{
					pathTool=new PathTool(mapView.getMapControl(),oslayer,act);
					mapView.getMapControl().addCustomDraw(pathTool);
				}
				mapView.getMapControl().setCurrentTool(pathTool);
				mapView.getMapControl().refresh();
//				oslayer.setPathSearchListener(new IPathSearchListener(){
//
//					@Override
//					public void pathSearchFinished(OpenSourceMapLayer$Path path) {
//						if (path==null) return;
//						int size=path.markArray.length;
//						StringBuilder sb=new StringBuilder();
//						sb.append("·������:");
//						sb.append(path.name);
//						sb.append("\n");
//						for (int i=0;i<size;++i)
//						{
//							sb.append("�յ�����:");
//							sb.append(path.markArray[i].name);
//							sb.append("\n");
//						}
//						sb.deleteCharAt(sb.length()-1);
//						//����Toast��ʾ��ѯ����·�����Լ��յ���
//						Bundle b=new Bundle();
//						b.putString("string",sb.toString());
//						Message msg=new Message();
//						msg.what=1;
//						msg.setData(b);
//						handler.sendMessage(msg);
//						sb=null;
//					}
//					
//				});
//				oslayer.getPath(118.855484f,32.055214f,118.733574f,32.087738f,false);
//				//��ʾ�ȴ�����
//				dlg = new ProgressDialog(act);   
//				dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
//				dlg.setTitle("��ʾ");   
//				dlg.setMessage("���ڽ���·����ѯ�����Ժ�");   
//				dlg.setIcon(R.drawable.icon);   
//				dlg.setIndeterminate(false);   
//				dlg.setCancelable(true);
//				dlg.setButton("ȡ�� ", new DialogInterface.OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
//						oslayer.cancel();
//					}
//					
//				});
//				dlg.show();   
			}
			break;
		case 6:	//�л���ͼ����Դ
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				switch (oslayer.getMapMode())
				{
				case GOOGLE:
					oslayer.setMapMode(1);break;
				case BING:
					oslayer.setMapMode(0);break;
				}
				mapView.getMapControl().refresh();
			}
			break;
		case 7:	//�л���ͼģʽ
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				switch (oslayer.getMode())
				{
				case 0:
					oslayer.setMode(1);
					break;
				case 1:
					oslayer.setMode(0);
					break;
				}
				mapView.getMapControl().refresh();
			}
			break;
		case 8:	//�˳�
			System.exit(0);
			break;
		case 11:
			ICustomDraw draw=mapView.getMapControl().getCustomDraw();
			if (draw!=null && draw instanceof ICustomDrawDataCenter)
			{
				CustomDrawGeometrySelector s=new CustomDrawGeometrySelector(mapView.getMapControl(),(ICustomDrawDataCenter)draw);
				s.setOffset(0, 60);
				mapView.getMapControl().setCurrentTool(s);
			}
			break;
		case 12:
			mapView.getMapControl().setCurrentTool(new PointTool(mapView.getMapControl()));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh(mapView);
		super.onResume();
	}
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) 
		{    
            switch (msg.what) 
            {    
            case 1:
            	//�رյȴ�����
            	dlg.cancel();
            	dlg=null;
            	//����Toast��ʾ��ѯ�Ľ��
            	Toast.makeText(App.getInstance(), msg.getData().getString("string"), Toast.LENGTH_SHORT).show();
            	break;
            }
		}
	};
}