package cn.creable.android.demo5;

import java.util.Vector;

import cn.creable.gridgis.controls.FingerPaintTool;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.Arithmetic;
import cn.creable.gridgis.geometry.Envelope;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.MultiLineString;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.AddFeatureTool;
import cn.creable.gridgis.shapefile.FixedShapefileLayer;
import cn.creable.gridgis.shapefile.IEditListener;
import cn.creable.gridgis.shapefile.IEditTool;
import cn.creable.gridgis.shapefile.ISpatialAnalysisToolListener;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.gridgis.shapefile.SpatialAnalysisTool;
import cn.creable.so.SpatialOperator;
import cn.creable.ucmap.OpenSourceMapLayer;
import cn.creable.ucmap.RasterLayer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainAct extends Activity implements IEditListener{
	MapView mapView;
	MainAct act;
	private Vector<ShapefileLayer> layers;
	private IEditTool editTool;
    private IFeature ft;
    private Vector<EditText> ets=new Vector<EditText>();
    private HighlightTool hTool;
    
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn=(Button)findViewById(R.id.menu);
        btn.setOnClickListener(new View.OnClickListener() {

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
        
        act=this;
        layers=new Vector<ShapefileLayer>();
        mapView=(MapView)findViewById(R.id.mapView);
        
        ImageView zoomin=(ImageView)findViewById(R.id.ToolButton1);
        zoomin.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()!=MotionEvent.ACTION_UP) return true;
				mapView.getMapControl().setZoomInTool();
				mapView.getMapControl().getCurrentTool().action();
				mapView.getMapControl().setPanTool(false,2);
				return true;
			}
		});
        
        ImageView zoomout=(ImageView)findViewById(R.id.ToolButton2);
        zoomout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()!=MotionEvent.ACTION_UP) return true;
				mapView.getMapControl().setZoomOutTool();
				mapView.getMapControl().getCurrentTool().action();
				mapView.getMapControl().setPanTool(false,2);
				return true;
			}
		});
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "��Բ");
		menu.add(0, 1, 0, "������");
		menu.add(0, 10,0, "��ͼ���");
		menu.add(0, 2, 0, "�ֻ�");
		menu.add(0, 3, 0, "ɾ�����һ���ֻ�");
		menu.add(0, 4, 0, "����ֻ�");
		menu.add(0, 5, 0, "�󽻼�");
		menu.add(0, 6, 0, "�󲢼�");
		menu.add(0, 7, 0, "��");
		menu.add(0, 8, 0, "���������");
		menu.add(0, 12,0, "���߲�����");
		menu.add(0, 13,0, "����");
		menu.add(0, 9, 0, "ɾ��Ҫ��");
		menu.add(0, 14,0, "��������α���");
		menu.add(0, 11, 0, "��ʾȫͼ");
		menu.add(0, 100, 0, "�˳�");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 0://��Բ��ע�⣺���ѡ���˵�ͼ�㣬���ǻ�����Բ��
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
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
					tool.openSnap();//���￪����׽
					tool.setOffset(0, 60);
					tool.setType(1);//���ｫtype����Ϊ1����ʾ��Բ
					editTool.setListener(act);
					mapView.getMapControl().setCurrentTool(tool);
				}
			});
			AlertDialog dialog=builder.create();
			dialog.show();
			break;
		}
		case 1://�����Σ�ע�⣺���ѡ���˵�ͼ�㣬���ǻ����˾��ε�
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
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
					tool.openSnap();//���￪����׽
					tool.setOffset(0, 60);
					tool.setType(2);//���ｫtype����Ϊ2����ʾ������
					editTool.setListener(act);
					mapView.getMapControl().setCurrentTool(tool);
				}
			});
			AlertDialog dialog=builder.create();
			dialog.show();
			break;
		}
		case 10://��ͼ���
			mapView.getMapControl().setPanTool();
			break;
		case 2://�ֻ�
			mapView.getMapControl().setCurrentTool(FingerPaintTool.getInstance(mapView.getMapControl()));
			break;
		case 3://ɾ�����һ���ֻ�
			if (FingerPaintTool.getInstance(mapView.getMapControl()).removeLast())
				mapView.getMapControl().refresh();
			break;
		case 4://����ֻ�
			if (FingerPaintTool.getInstance(mapView.getMapControl()).clear())
				mapView.getMapControl().refresh();
			break;
		case 5://�󽻼�
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.intersection(ft1.getShape(), ft2.getShape());//������2����ͼҪ�صĽ���
					return geo;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 6://�󲢼�
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.union(ft1.getShape(), ft2.getShape());//������2����ͼҪ�صĲ���
					return geo;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 7://��
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.difference(ft1.getShape(), ft2.getShape());//������2����ͼҪ�صĲ
					return geo;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 8://���������
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					if (ft1.getShape().getGeometryType()!=GeometryType.Polygon ||
						ft2.getShape().getGeometryType()!=GeometryType.Polygon)
						return null;//�����ѡ�е�2��Ҫ�أ���һ������Polygon���򲻴���
					if (!(layer1 instanceof ShapefileLayer))
						return null;//�����һ��ͼ�㲻�ǿɱ༭ͼ�㣬�򲻴���
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.intersection(ft1.getShape(), ft2.getShape());//������2����ͼҪ�صĽ���
					if (geo==null) return null;
					IGeometry geo2=so.difference(ft1.getShape(), ft2.getShape());//������2����ͼҪ�صĲ
					//�������ɿռ�����ӿ����ɵ�2����ͼҪ�أ���ɾ��ft1
					String[] values=new String[ft1.getValues().length];
					System.arraycopy(ft1.getValues(), 0, values, 0, ft1.getValues().length);//���ｫft1�����Ը��Ƹ����Ҫ��
					String[] values2=new String[ft1.getValues().length];
					System.arraycopy(ft1.getValues(), 0, values2, 0, ft1.getValues().length);//���ｫft1�����Ը��Ƹ����Ҫ��
					ShapefileLayer sLayer=(ShapefileLayer)layer1;
					//ShapefileLayer.beginAddUndo();
					ShapefileLayer.beginEdit();//��ʼ�����༭�������Ҫͬʱ���úü��α༭����������beginEdit���ܶ�
					sLayer.addFeature(geo,values);//���һ��Ҫ�ص�ͼ��1
					sLayer.addFeature(geo2, values2);//�����һ��Ҫ�ص�ͼ��2
					sLayer.deleteFeature(ft1);//ɾ��ft1
					ShapefileLayer.endEdit();//���������༭
					//ShapefileLayer.endAddUndo();
					mapView.getMapControl().refresh();
					return null;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			
			break;
		}
		case 12://���߲�����
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					if (ft1.getShape().getGeometryType()!=GeometryType.LineString ||
						ft2.getShape().getGeometryType()!=GeometryType.LineString)
						return null;//�����ѡ�е�2��Ҫ�أ���һ������LineString���򲻴���
					if (!(layer1 instanceof ShapefileLayer))
						return null;//�����һ��ͼ�㲻�ǿɱ༭ͼ�㣬�򲻴���
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.difference(ft1.getShape(), ft2.getShape());//������2����ͼҪ�صĲ
					if (geo==null) return null;
					if (geo.getGeometryType()==GeometryType.MultiLineString)
					{
						//���潫���еõ��Ķ��ߣ�ÿһ��������ͼ��1����ɾ��ft1
						ShapefileLayer sLayer=(ShapefileLayer)layer1;
						MultiLineString mls=(MultiLineString)geo;
						int count=mls.getNumGeometries();
						//ShapefileLayer.beginAddUndo();
						ShapefileLayer.beginEdit();//��ʼ�����༭�������Ҫͬʱ���úü��α༭����������beginEdit���ܶ�
						for (int i=0;i<count;++i)
						{
							String[] values=new String[ft1.getValues().length];
							System.arraycopy(ft1.getValues(), 0, values, 0, ft1.getValues().length);//���ｫft1�����Ը��Ƹ����Ҫ��
							sLayer.addFeature(mls.getGeometry(i), values);//�����ߵ�ÿһ����Ա���Ե����ĵ�ͼҪ�������ӽ�ͼ��1
						}
						sLayer.deleteFeature(ft1);//ɾ��ft1
						ShapefileLayer.endEdit();//���������༭
						//ShapefileLayer.endAddUndo();
						mapView.getMapControl().refresh();
					}
					
					return null;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 13://����
		{
			SpatialAnalysisTool tool = new SpatialAnalysisTool(mapView.getMapControl(), new ISpatialAnalysisToolListener() {
				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					if (!(layer1 instanceof ShapefileLayer))
						return null;//�����һ��ͼ�㲻�ǿɱ༭ͼ�㣬�򲻴���
					IGeometry[] geo=Arithmetic.cut(ft1.getShape(), ft2.getShape());
					if (geo!=null)
					{
						ShapefileLayer sLayer=(ShapefileLayer)layer1;
						int count=geo.length;
						//ShapefileLayer.beginAddUndo();
						ShapefileLayer.beginEdit();//��ʼ�����༭�������Ҫͬʱ���úü��α༭����������beginEdit���ܶ�
						for (int i=0;i<count;++i)
						{
							String[] values=new String[ft1.getValues().length];
							System.arraycopy(ft1.getValues(), 0, values, 0, ft1.getValues().length);//���ｫft1�����Ը��Ƹ����Ҫ��
							sLayer.addFeature(geo[i], values);//��geo��ÿһ����Ա���Ե����ĵ�ͼҪ�������ӽ�ͼ��1
						}
						sLayer.deleteFeature(ft1);//ɾ��ft1
						ShapefileLayer.endEdit();//���������༭
						//ShapefileLayer.endAddUndo();
						mapView.getMapControl().refresh();
					}
					return null;
				}
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 9://ɾ��Ҫ��
		{
			MyDeleteFeatureTool tool=new MyDeleteFeatureTool(mapView.getMapControl(),act);
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 11://��ʾȫͼ
		{
			int count=mapView.getMapControl().getMap().getLayerCount();
			if (count>0)
			{
				IEnvelope env=mapView.getMapControl().getFullExtent();
				mapView.getMapControl().adjustEnvelope2(env);
				mapView.getMapControl().refresh(env);
			}
			break;
		}
		case 14://��������α���
		{
			if (hTool==null)
			{
				hTool=new HighlightTool(mapView.getMapControl());
				hTool.selector.setOffset(0, 80);
				mapView.getMapControl().addCustomDraw(hTool);
			}
			mapView.getMapControl().setCurrentTool(hTool);
			hTool.clear();
			mapView.getMapControl().refresh();
			break;
		}
		case 100://�˳�
			System.exit(0);break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}
	
	private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	
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
	    	tv = new TextView(act);
	    	tv.setText(fields[i]);
	    	tv.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	layout.addView( tv );
	    	et=new EditText(act);
	    	et.setLayoutParams(lp);
	    	et.setSingleLine(true);
	    	et.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	et.setTextColor(0xFF000000);
	    	if (values!=null) et.setText(values[i]);
	    	layout.addView(et);
	    	ets.addElement(et);
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(act);
    	builder.setView(sv).setTitle("�޸�����").setIcon(R.drawable.icon)
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

	@Override
	public void onAddFeature(IFeature ft, ILayer layer) {
		this.ft=ft;
		IFeatureLayer flayer=(IFeatureLayer)layer;
		this.showModifyDialog(flayer.getFeatureClass().getFields(), ft.getValues());
	}

	@Override
	public void onDeleteFeature(IFeature ft, ILayer layer) {
		
	}

	@Override
	public void onUpdateFeature(IFeature ft, ILayer layer) {
		
	}
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh();
		super.onResume();
	}
}