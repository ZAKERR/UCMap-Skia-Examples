package cn.creable.demo12;

import java.io.File;
import java.util.Vector;

import cn.creable.arrow.ArrowTool;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.display.IFeatureRenderer;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.display.MarkerSymbol;
import cn.creable.gridgis.display.SimpleRenderer;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.Envelope;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.AddFeatureTool;
import cn.creable.gridgis.shapefile.DeleteFeatureTool;
import cn.creable.gridgis.shapefile.EditFeatureAttTool;
import cn.creable.gridgis.shapefile.EditFeatureTool;
import cn.creable.gridgis.shapefile.IEditListener;
import cn.creable.gridgis.shapefile.IEditTool;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.ucmap.GetFeatureResult;
import cn.creable.ucmap.WMSLayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ZoomControls;

public class MainActivity extends Activity implements IEditListener{
	
	private MapView mapView;
	
	private String layerName="poi";
	private String wfsURL="http://192.168.1.8:8080/geoserver/wfs";
	private String shapeFieldName="the_geom";
	private String projection="EPSG:4326";
	private String workspace="tiger";
	private String namespaceURI="http://www.census.gov";
	
	private Vector<ShapefileLayer> layers=new Vector<ShapefileLayer>();
    
    private IEditTool editTool;
    private IFeature ft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//���÷Ŵ���С��ť�¼�
  		ZoomControls zc=(ZoomControls)findViewById(R.id.zoomControls);
  		zc.setOnZoomInClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomInTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool();
  			}
  		});
  		zc.setOnZoomOutClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomOutTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool();
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
					String path=Environment.getExternalStorageDirectory().getPath();
					if (!new File(path+"/"+workspace+"/map.ini").exists())//�������Ƿ���������ͼ��û�����½�
						App.getInstance().createMap(path+"/"+workspace);
					mapControl.loadMap(path+"/"+workspace+"/map.ini",(byte)0);//װ�ص�ͼ
					mapControl.setPanTool();
				}
			}
        	
        });
        
        Button btn1=(Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IEnvelope env=Envelope.createEnvelope(-74.047185, -73.90782, 40.679648, 40.882078);//��Ҫץȡ��ͼ�㷶Χ
				IFeatureRenderer renderer=new SimpleRenderer("","",new MarkerSymbol(30,0,0xFFFFFF00,0,0));//�������ͼ�㱾����Ⱦ����ʽ
				//�������WMSLayer�ĺ���ȥ��������ץȡ����
				GetFeatureResult gfr=WMSLayer.getFeatureByEnvelope(wfsURL, layerName, env, shapeFieldName, projection, 0, workspace, namespaceURI, null, null);
				ILayer layer=mapView.getMapControl().getMap().getLayer(layerName);
				if (layer==null)
				{//���ͼ�㲻�������½�����������
					ShapefileLayer slayer=ShapefileLayer.createFromFeatureClass((short)mapView.getMapControl().getMap().getLayerCount(), layerName, gfr.getFeatureClass(0),env, 100000, 0, true, true, renderer, "GBK");
					mapView.getMapControl().getMap().addLayer(slayer);//��ͼ����ӽ�map
					mapView.getMapControl().adjustEnvelope2(env);//��env������Ļ�Ŀ�߱Ƚ��е���
					mapView.getMapControl().refreshSync(env);//ͬ����ʽˢ�µ�ͼ
					mapView.getMapControl().rewriteIni();//��дini�ļ����Ὣ��ͼ�����Ϣд��ȥ
					//��wfs�Ĳ������ý�ShapefileLayer��
					slayer.setWFSParams(wfsURL, shapeFieldName, projection, workspace, namespaceURI, null, null, false);
					ShapefileLayer.openUndoRedo(100);//����open/redo���ܣ��������ܼ�¼�༭�Ĳ�����֮�����ʹ��upload����������ͬ������������
				}
				else
				{//���ͼ����������֮ǰ�������ٵ���������
					if (layer instanceof ShapefileLayer)
					{
						ShapefileLayer slayer=(ShapefileLayer)layer;
						slayer.deleteFeature("1=1");//���ͼ��
						slayer.importDataFromFeatureClass(gfr.getFeatureClass(0),env);//�����µ����ݽ�ͼ��
						mapView.getMapControl().adjustEnvelope2(env);//��env������Ļ�Ŀ�߱Ƚ��е���
						mapView.getMapControl().refresh(env);//ˢ�µ�ͼ
						//��wfs�Ĳ������ý�ShapefileLayer��
						slayer.setWFSParams(wfsURL, shapeFieldName, projection, workspace, namespaceURI, null, null, false);
						ShapefileLayer.openUndoRedo(100);//����open/redo���ܣ��������ܼ�¼�༭�Ĳ�����֮�����ʹ��upload����������ͬ������������
					}
				}
			}
        	
        });
        
        Button btn2=(Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ILayer layer=mapView.getMapControl().getMap().getLayer(layerName);
				if (layer!=null && layer instanceof ShapefileLayer)
				{
					ShapefileLayer slayer=(ShapefileLayer)mapView.getMapControl().getMap().getLayer(layerName);
					slayer.upload();//����upload����������ͬ����������
				}
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "���Ҫ��");
		menu.add(0, 1, 0, "ɾ��Ҫ��");
		menu.add(0, 2, 0, "�޸�Ҫ��ͼ��");
		menu.add(0, 3, 0, "�޸�Ҫ������");
		menu.add(0, 100, 0, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 0://���Ҫ��
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("��ѡ��ͼ��:");
			layers.clear();
			Vector<String> strs=new Vector<String>();
			int count=mapView.getMapControl().getMap().getLayerCount();
			for (int i=0;i<count;++i)
			{
				ILayer layer=mapView.getMapControl().getMap().getLayer(i);
				if (layer instanceof ShapefileLayer)
				{
					strs.addElement(layer.getName());
					layers.addElement((ShapefileLayer)layer);
				}
			}
			String[] layerNames=new String[strs.size()];
			strs.copyInto(layerNames);
			strs=null;
			builder.setSingleChoiceItems(layerNames, -1,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();	
					AddFeatureTool tool=new AddFeatureTool(mapView.getMapControl(),layers.elementAt(which));
					editTool=tool;
					tool.setOffset(0, 60);
					tool.openSnap();
					editTool.setListener(MainActivity.this);
					mapView.getMapControl().setCurrentTool(tool);
				}
			});
			AlertDialog dialog=builder.create();
			dialog.show();
			break;
		case 1://ɾ��Ҫ��
		{
			DeleteFeatureTool tool=new DeleteFeatureTool(mapView.getMapControl());
			tool.selector.setOffset(0, 80);
			editTool=tool;
			editTool.setListener(MainActivity.this);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 2://�޸�Ҫ��ͼ��
		{
			EditFeatureTool editTool=null;
	        if (mapView.getMapControl().getCurrentTool()!=null && mapView.getMapControl().getCurrentTool() instanceof EditFeatureTool)
	            editTool=(EditFeatureTool)mapView.getMapControl().getCurrentTool();
	        else
	        {
	            editTool=new EditFeatureTool(mapView.getMapControl());
	            mapView.getMapControl().setCurrentTool(editTool);
	        }
	        editTool.selector.setOffset(0, 80);
	        editTool.setType(EditFeatureTool.Type_MoveNode);
			break;
		}
		case 3://�޸�Ҫ������
		{
			EditFeatureAttTool tool=new EditFeatureAttTool(mapView.getMapControl());
			tool.selector.setOffset(0, 80);
			editTool=tool;
			editTool.setListener(MainActivity.this);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 100://�˳�
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			IMapTool mapTool=mapView.getMapControl().getCurrentTool();
			if (mapTool!=null && mapTool instanceof IEditTool)
			{
				if (((IEditTool)mapView.getMapControl().getCurrentTool()).undo()==true)
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
	
	@Override
	public void onAddFeature(IFeature ft, ILayer layer) {
		this.ft=ft;
		IFeatureLayer flayer=(IFeatureLayer)layer;
		this.showModifyDialog(flayer.getFeatureClass().getFields(), ft.getValues());
	}

	@Override
	public void onDeleteFeature(IFeature ft, ILayer layer) {
		editTool.confirm();	//����ʾ��ֱ��ɾ��Ҫ��
	}

	@Override
	public void onUpdateFeature(IFeature ft, ILayer layer) {
		this.ft=ft;
		IFeatureLayer flayer=(IFeatureLayer)layer;
		this.showModifyDialog(flayer.getFeatureClass().getFields(), ft.getValues());
	}
	
	private Vector<EditText> ets=new Vector<EditText>();
    private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);     
    private LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);     
    private LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
	
	private void showModifyDialog(String[] fields,String[] values)
    {
    	ScrollView sv   = new ScrollView(this);     
    	sv.setLayoutParams( LP_FF );
    	
    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation( LinearLayout.VERTICAL );
    	sv.addView( layout ); 
    	
    	TextView tv;
    	EditText et;
    	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(400,LayoutParams.WRAP_CONTENT);
    	//lp.setMargins(70, 0, 0, 0);
    	
    	int count=fields.length;
    	ets.clear();
    	for (int i=0;i<count;++i)
    	{
	    	tv = new TextView(MainActivity.this);
	    	tv.setText(fields[i]);
	    	tv.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Medium);
	    	layout.addView( tv );
	    	et=new EditText(MainActivity.this);
	    	et.setLayoutParams(lp);
	    	et.setSingleLine(true);
	    	et.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_Medium);
	    	et.setTextColor(0xFF000000);
	    	if (values!=null) et.setText(values[i]);
	    	layout.addView(et);
	    	ets.addElement(et);
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setView(sv).setTitle("�޸�����").setIcon(R.drawable.ic_launcher)
    	.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int count=ets.size();
				String[] values=new String[count];
				for (int i=0;i<count;++i)
				{
					values[i]=ets.elementAt(i).getText().toString();
				}
				ft.setValues(values);
				editTool.confirm();
			}
    		
    	})
    	.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editTool.cancel();
			}
		});
    	builder.create().show();
    }

}
