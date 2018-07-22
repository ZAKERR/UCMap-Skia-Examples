package cn.creable.android.demo;

import cn.creable.arrow.ARROW_STYLE;
import cn.creable.arrow.ArrowTool;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.CustomDrawGeometrySelector;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.IInfoToolListener;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.controls.PanTool;
import cn.creable.gridgis.display.FillSymbol;
import cn.creable.gridgis.display.ISymbol;
import cn.creable.gridgis.display.UniqueValueRenderer;
import cn.creable.gridgis.geodatabase.DataProvider;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.IEditTool;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.ucmap.MapLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainAct extends Activity {
	
	static  
	{    
		System.loadLibrary( "UCMAP" );
	}
	MapView mapView;
	
	int type=0;
	
	private ArrowTool arrowTool;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.main);
        App.getInstance();
        mapView=(MapView)findViewById(R.id.mapView);
        
        Button btnm=(Button)findViewById(R.id.menu);
        btnm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Runtime runtime = Runtime.getRuntime();

				try {
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_MENU);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        });
        
        Button btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//�������slideAnimation����������ǰ��ͼ�����Ļ�����116.368392,39.92476
				//mapView.getMapControl().getDisplay().getDisplayTransformation().setZoom(0.0000349015f);
				//mapView.getMapControl().refreshSync();
				mapView.getMapControl().slideAnimation(116.368392,39.92476);
			}
        	
        });
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,99,0,"����google��ͼ");
		menu.add(0,98,0,"����bj��ͼ");
		//menu.add(0,100,0,"�л�Activity");
		menu.add(0, 0, 0, "���Ƶ���ͷ");
		menu.add(0,10,0,"����˫��ͷ");
		menu.add(0,4,0,"��������");
		menu.add(0,5,0,"ƽ������");
		menu.add(0,20,0,"�ı��ע��ʽ");
		menu.add(0,21,0,"�ָ���ע��ʽ");
		menu.add(0,22,0,"��ĳһ��͸��");
		menu.add(0,23,0,"���ɵ�ͼ����");
		menu.add(0,28,0,"���ɵ�ͼ����2");
		menu.add(0,24,0,"���Զ����");
		menu.add(0,25,0,"����Զ����");
		menu.add(0,26,0,"��ѡ�Զ����");
		menu.add(0,27,0,"��ѯ��ͼ���깤��");
		menu.add(0,28,0,"�Զ���ѡ�񹤾�");
		menu.add(0, 1, 0, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 99:
//			ILayer layer=mapView.getMapControl().getMap().getLayer("�ܵ�");
//			UniqueValueRenderer ur=(UniqueValueRenderer) ((IFeatureLayer)layer).getRenderer();
//			String[] keys=ur.getKeys();
//			MyRenderer mr=new MyRenderer(ur.getSymbolByFeature(null),(IShapefileLayer) layer);
//			mr.setFieldIndex(ur.getFieldIndex());
//			for (int i=0;i<keys.length;++i)
//				mr.setSymbol(keys[i], ur.getSymbol(keys[i]));
//			((IFeatureLayer)layer).setRenderer(mr);
//			mr.fieldIndex=5;//GXXL�ֶ����
//			mr.key="��Ȼ��";//�ؼ���
//			mapView.getMapControl().refresh();
			
			if (arrowTool!=null)
			{
				mapView.getMapControl().removeCustomDraw(arrowTool);
				arrowTool=null;
			}
			type=1;
			MapLoader.loadMapXML(mapView.getMapControl(), "/sdcard/OpenSourceMap.xml",null,0,0.00001f,false);
			//mapView.getMapControl().getRefreshManager().endLayerId=-1;
			mapView.getMapControl().setPanTool();
			break;
		case 98:
			if (arrowTool!=null)
			{
				mapView.getMapControl().removeCustomDraw(arrowTool);
				arrowTool=null;
			}
			type=0;
			mapView.getMapControl().loadMap("/sdcard/bj2/map.ini", (byte)0);
			mapView.getMapControl().setPanTool();
			break;
//		case 100:
//			Intent intent=new Intent(this,MainAct.class);
//			startActivity(intent);
//			break;
		case 0:
			if (arrowTool==null)
			{
				arrowTool=new ArrowTool(mapView.getMapControl());
				arrowTool.setColor(0xC0FF0000, 0xC00000FF);
				mapView.getMapControl().addCustomDraw(arrowTool);
			}
			arrowTool.setArrowStyle(ARROW_STYLE.SINGLE_HEAD_OPEN_TAIL);
			mapView.getMapControl().setCurrentTool(arrowTool);
			break;
		case 10:
			if (arrowTool==null)
			{
				arrowTool=new ArrowTool(mapView.getMapControl());
				arrowTool.setColor(0xC0FF0000, 0xC00000FF);
				mapView.getMapControl().addCustomDraw(arrowTool);
			}
			arrowTool.setArrowStyle(ARROW_STYLE.MULTI_HEAD);
			mapView.getMapControl().setCurrentTool(arrowTool);
			break;
		case 4:
			mapView.getMapControl().setPanTool(false,0);
			break;
		case 5:
			mapView.getMapControl().setPanTool(true,0);//����Ϊtrue˵������ƽ������ģʽ
			break;
		case 20:
			mapView.getMapControl().addLabelStyle(15, 20, 0xFFFF0000);
			mapView.getMapControl().refresh();
			break;
		case 21:
			mapView.getMapControl().removeLabelStyle(15);
			mapView.getMapControl().refresh();
			break;
		case 22:
			IFeatureLayer fl=(IFeatureLayer)mapView.getMapControl().getMap().getLayer(5);//��ȡ��6��ͼ��
			ISymbol sym=fl.getRenderer().getSymbolByFeature(null);//��ȡ���ͼ����õķ�����ʽ
			if (sym instanceof FillSymbol)//�������FillSymbol
			{
				FillSymbol fs=(FillSymbol)sym;//������ת��ΪFillSymbol
				fs.setColor(0x00000000);//������ɫ����ɫ��ʽΪAARRGGBB
			}
			mapView.getMapControl().refresh();//ˢ�µ�ͼ������Ч��
			break;
		case 23:
			//�������ĳЩͼ�㲻�ܵ��У��Ǿ͵�����Щͼ���setSelectable(false);����������Ϊ����ѡ��
			MyPanTool tool=new MyPanTool(mapView.getMapControl(),new IInfoToolListener(){

				@Override
				public void notify(MapControl mapControl, IFeatureLayer flayer,
						IFeature ft, String[] fields, String[] values) {
					mapControl.flashFeatures(flayer, ft.getOid());//��˸��ѡ�еĵ�ͼҪ��
					//��ʾ��ͼҪ�ص�����
					StringBuilder sb=new StringBuilder();
					for (int i=0;i<fields.length;++i)
					{
						sb.append(fields[i]);
						sb.append(":");
						sb.append(values[i]);
						sb.append("\n");
					}
					sb.deleteCharAt(sb.length()-1);
					//����Toast��ʾ��ͼҪ�ص�����
					Toast.makeText(App.getInstance(), sb.toString(), Toast.LENGTH_SHORT).show();
					sb=null;
				}
				
			});
			mapView.getMapControl().setCurrentTool(tool);
			break;
		case 24://���Զ����
			if (type!=0) break;
			//�����ĸ���γ�ȵ�
			Point[] pts=new Point[4];
			pts[0]=new Point(116.33,39.9);//(119.178771,36.743880);//
			pts[1]=new Point(116.38,39.95);
			pts[2]=new Point(116.35,39.89);
			pts[3]=new Point(116.36,39.92);
			String[] text=new String[4];
			text[0]="��A111111";
			text[1]="��A222222";
			text[2]="��A333333";
			text[3]="��A444444";
			
			MyCustomDraw mcd=new MyCustomDraw(mapView.getMapControl(),pts,text,this);//����CustomDraw���󣬲�����Ҫ���Ƶĵ㴫��
			mapView.getMapControl().setCustomDraw(mcd);//��CustomDraw������Ϊ��ǰ
			mapView.getMapControl().repaint();//�����ػ��ͼ����ʾ�Զ������
			break;
		case 25://����Զ����
			if (type!=0) break;
			mapView.getMapControl().setCustomDraw(null);//���CustomDraw����
			mapView.getMapControl().refresh();//�ػ��ͼ
			break;
		case 26://��ѡ�Զ����
			ICustomDraw draw=mapView.getMapControl().getCustomDraw();
			if (draw!=null && draw instanceof ICustomDrawDataCenter)
			{
				CustomDrawGeometrySelector s=new CustomDrawGeometrySelector(mapView.getMapControl(),(ICustomDrawDataCenter)draw);
				s.setOffset(0, 80);
				mapView.getMapControl().setCurrentTool(s);
			}
			break;
		case 27://��ѯ��ͼ���깤��
			mapView.getMapControl().setCurrentTool(new PointTool(mapView.getMapControl()));
			break;
		case 28:
//			MyMeasureTool mt=new MyMeasureTool(mapView.getMapControl(),0);
//			mapView.getMapControl().setCurrentTool(mt);
			MySelector ms=new MySelector(mapView.getMapControl());
			mapView.getMapControl().setCurrentTool(ms);
			break;
		case 1:	//�˳�
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			IMapTool mapTool=mapView.getMapControl().getCurrentTool();
			if (mapTool!=null && mapTool instanceof ArrowTool)
			{
				if (((ArrowTool)mapView.getMapControl().getCurrentTool()).undo()==true)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh(mapView);
		super.onResume();
	}
}