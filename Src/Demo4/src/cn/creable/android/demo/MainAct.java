package cn.creable.android.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IInfoToolListener;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.IMeasureToolListener;
import cn.creable.gridgis.controls.InfoTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MeasureTool;
import cn.creable.gridgis.controls.PanTool;
import cn.creable.gridgis.display.ISymbol;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.LineString;
import cn.creable.gridgis.geometry.LinearRing;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.geometry.Polygon;
import cn.creable.gridgis.gridMap.IMap;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.AddFeatureTool;
import cn.creable.gridgis.shapefile.AddFeatureTool2;
import cn.creable.gridgis.shapefile.DeleteFeatureTool;
import cn.creable.gridgis.shapefile.EditFeatureAttTool;
import cn.creable.gridgis.shapefile.EditFeatureTool;
import cn.creable.gridgis.shapefile.IEditListener;
import cn.creable.gridgis.shapefile.IEditTool;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.gridgis.spatialReference.ProjectFactory;
import cn.creable.gridgis.spatialReference.SpatialReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainAct extends Activity implements IEditListener{
	
	class MyToast {
		private Context mContext = null;
		private Toast mToast = null;
		private Handler mHandler = null;
		private Runnable mToastThread = new Runnable() {

			@Override
			public void run() {
				mToast.show();
				mHandler.postDelayed(mToastThread, 3000);// ÿ��3����ʾһ�Σ������ԣ����ʱ����Ч�������
			}
		};

		MyToast(Context context) {
			mContext = context;
			mHandler = new Handler(mContext.getMainLooper());
			mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
		}

		void setText(String text) {
			mToast.setText(text);
		}

		void show() {
			mHandler.post(mToastThread);
		}

		void cancel() {
			mHandler.removeCallbacks(mToastThread);// �Ȱ���ʾ�߳�ɾ��
			mToast.cancel();// �����һ���̵߳���ʾЧ��cancel������һ�˰�����
		}
	}
	
	AlertDialog menuDialog;
    GridView menuGrid;
    View menuView;
    
    static MyToast myt;
    
    private Vector<ILayer> layers2=new Vector<ILayer>();
	private boolean[] visible;
    
    ImageView zoomin,zoomout;
	
    int[] menu_image_array = { R.drawable.menu_add,R.drawable.menu_add,
    		R.drawable.menu_delete,
    		R.drawable.menu_add,
    		R.drawable.menu_update,
    		R.drawable.menu_update2,
    		R.drawable.menu_update2,
    		R.drawable.menu_update2,
    		R.drawable.menu_update2,
    		R.drawable.menu_update2,
    		R.drawable.menu_update2,
    		R.drawable.menu_map,
    		R.drawable.menu_output,
    		R.drawable.menu_opensnap,
    		R.drawable.menu_closesnap,
    		R.drawable.menu_infotool,
    		R.drawable.menu_output,
    		R.drawable.menu_infotool,
    		R.drawable.menu_infotool,
    		R.drawable.menu_infotool,
    		R.drawable.menu_infotool,
    		R.drawable.menu_quit};

    String[] menu_name_array = { "���Ҫ��","���Ҫ��(���)","ɾ��Ҫ��","�ύ�༭","�޸Ľڵ�","��ӽڵ�","ɾ���ڵ�","�����ƶ�","undo","redo","�޸�����","��ͼ���","������ͼ","������׽","�رղ�׽","��Ϣ����","ͶӰ�任","�����","�����","ר��ͼ","ͼ�����","�˳�����" };
	
    private MyMapView mapView;
    
    private MainAct act;
    
    private Vector<ShapefileLayer> layers;
    
    private IEditTool editTool;
    private IFeature ft;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        act=this;
        layers=new Vector<ShapefileLayer>();
        mapView=(MyMapView)findViewById(R.id.mapView);
        
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
        
//        mapView.setListener(new IMapViewListener() {
//
//			@Override
//			public void onSizeChanged(int w, int h, int oldw, int oldh) {
//				MyPanTool tool=new MyPanTool(mapView.getMapControl(),new IInfoToolListener(){
//
//					@Override
//					public void notify(MapControl mapControl, IFeatureLayer flayer,
//							IFeature ft, String[] fields, String[] values) {
//						Intent intent=new Intent(act,MyActivity.class);
//						intent.putExtra("fields", fields);
//						intent.putExtra("values", values);
//						startActivity(intent);
//					}
//					
//				});
//				mapView.getMapControl().setCurrentTool(tool);
//			}
//        	
//        });
        
        zoomin=(ImageView)findViewById(R.id.ToolButton1);
        zoomin.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()!=MotionEvent.ACTION_UP) return true;
//				mapView.getMapControl().setZoomInTool();
//				mapView.getMapControl().getCurrentTool().action();
//				mapView.getMapControl().setPanTool();
				
				IEnvelope extent=mapView.getMapControl().getExtent();
				extent.expand(-0.5f, -0.5f, false);
				mapView.getMapControl().refresh(extent);
				return true;
			}
		});
        
        zoomout=(ImageView)findViewById(R.id.ToolButton2);
        zoomout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()!=MotionEvent.ACTION_UP) return true;
//				mapView.getMapControl().setZoomOutTool();
//				mapView.getMapControl().getCurrentTool().action();
//				mapView.getMapControl().setPanTool();
				
				IEnvelope extent=mapView.getMapControl().getExtent();
				extent.expand(0.5f, 0.5f, false);
				mapView.getMapControl().refresh(extent);
				return true;
			}
		});
        
        menuView = View.inflate(this, R.layout.menu, null);
        // ����AlertDialog
        menuDialog = new AlertDialog.Builder(this).create();
        menuDialog.setView(menuView,1, 1, 1, 1);
        menuDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)
                    dialog.dismiss();
                return false;
			}
		});
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        Window w=menuDialog.getWindow();
        WindowManager.LayoutParams lp =w.getAttributes();
        lp.x=10;
        lp.y=metrics.heightPixels-lp.height;

        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        menuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int cmd,
					long arg3) {
				menuDialog.dismiss();
				switch (cmd)
				{
				case 0:
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
							tool.setOffset(0, 60);
							tool.openSnap();
							editTool.setListener(act);
							mapView.getMapControl().setCurrentTool(tool);
						}
					});
					AlertDialog dialog=builder.create();
					dialog.show();
					break;
				}
				case 1:
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
							AddFeatureTool2 tool=new AddFeatureTool2(mapView.getMapControl(),layers.elementAt(which),((BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.cross)).getBitmap());
							editTool=tool;
							editTool.setListener(act);
							mapView.getMapControl().setCurrentTool(tool);
							mapView.getMapControl().repaint();
						}
					});
					AlertDialog dialog=builder.create();
					dialog.show();
					break;
				}
				case 2:
				{
					DeleteFeatureTool tool=new DeleteFeatureTool(mapView.getMapControl());
					tool.selector.setOffset(0, 80);
					editTool=tool;
					editTool.setListener(act);
					mapView.getMapControl().setCurrentTool(tool);
					break;
				}
				case 3:
				{
					editTool.submit();
					break;
				}
				case 4:
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
			        editTool.setListener(act);
					break;
				}
				case 5:
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
			        editTool.setType(EditFeatureTool.Type_AddNode);
					break;
				}
				case 6:
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
			        editTool.setType(EditFeatureTool.Type_DeleteNode);
					break;
				}
				case 7:
				{
					//��ǰ����������Ϊ����ѡ��
			        int layerCount=mapView.getMapControl().getMap().getLayerCount();
			        if (layerCount>3) layerCount=3;
			        for (int i=0;i<layerCount;++i)
			        {
			            ShapefileLayer layer=null;
			            if (mapView.getMapControl().getMap().getLayer(i) instanceof ShapefileLayer) layer=(ShapefileLayer)mapView.getMapControl().getMap().getLayer(i);
			            if (layer!=null) 
			            {
			                layer.setSelectable(false);
			            }
			        }
			        
					EditFeatureTool tool=new EditFeatureTool(mapView.getMapControl());
			        tool.setType(EditFeatureTool.Type_Move);
			        mapView.getMapControl().setCurrentTool(tool);
					break;
				}
				case 8:
				{
					boolean ret=false;
			        if (mapView.getMapControl().getCurrentTool()!=null && mapView.getMapControl().getCurrentTool() instanceof IEditTool)
			            ret=((IEditTool)(mapView.getMapControl().getCurrentTool())).undo();
			        if (ret==false)
			        {
			            ShapefileLayer.undo();
			            mapView.getMapControl().refresh();
			        }
					break;
				}
				case 9:
				{
					boolean ret=false;
					if (mapView.getMapControl().getCurrentTool()!=null && mapView.getMapControl().getCurrentTool() instanceof IEditTool)
			            ret=((IEditTool)(mapView.getMapControl().getCurrentTool())).redo();
			        if (ret==false)
			        {
			        	ShapefileLayer.redo();
			            mapView.getMapControl().refresh();
			        }
					break;
				}
				case 10:
				{
					EditFeatureAttTool tool=new EditFeatureAttTool(mapView.getMapControl());
					tool.selector.setOffset(0, 80);
					editTool=tool;
					editTool.setListener(act);
					mapView.getMapControl().setCurrentTool(tool);
					break;
				}
				case 11:
					mapView.getMapControl().setPanTool();
					break;
				case 12:
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
							layers.elementAt(which).outputToShapefile("/sdcard/");
							Toast.makeText(act, "������ɣ�", Toast.LENGTH_LONG).show();
						}
					});
					AlertDialog dialog=builder.create();
					dialog.show();
					break;
				}
				case 13:
					if (editTool instanceof AddFeatureTool)
					{
						((AddFeatureTool)editTool).openSnap();
					}
					else if (mapView.getMapControl().getCurrentTool()!=null && mapView.getMapControl().getCurrentTool() instanceof EditFeatureTool)
						((EditFeatureTool)mapView.getMapControl().getCurrentTool()).openSnap();
					break;
				case 14:
					if (editTool instanceof AddFeatureTool)
					{
						((AddFeatureTool)editTool).closeSnap();
					}
					else if (mapView.getMapControl().getCurrentTool()!=null && mapView.getMapControl().getCurrentTool() instanceof EditFeatureTool)
						((EditFeatureTool)mapView.getMapControl().getCurrentTool()).closeSnap();
					break;
				case 15:
					//IFeatureLayer ly=(IFeatureLayer)mapView.getMapControl().getMap().getLayer("XZQ");
					//ly.setSelectable(false);
					InfoTool tool=new InfoTool(mapView.getMapControl(),new IInfoToolListener(){

						@Override
						public void notify(MapControl mapControl, IFeatureLayer flayer,
								IFeature ft, String[] fields, String[] values) {
//							Intent intent=new Intent(act,MyActivity.class);
//							intent.putExtra("fields", fields);
//							intent.putExtra("values", values);
//							startActivity(intent);
							
							if (ft.getShape().getGeometryType()==GeometryType.Polygon)
							{
								//���´���ʵ��  ������˸��Ҫ�ر߿��Ч��
								Polygon pg=(Polygon)ft.getShape();
								LinearRing ring=(LinearRing)pg.getExteriorRing();
								LineString line=new LineString(ring.getPoints());//������ı߿���һ����
								LineSymbol ls=new LineSymbol(3,0xFFFF0000);//����һ������ʽ
								Vector<IGeometry> geos=new Vector<IGeometry>();
								geos.add(line);
								Vector<ISymbol> syms=new Vector<ISymbol>();
								syms.add(ls);
								mapControl.flashFeatures(geos, syms, 10);//������˸�ӿڣ���ֻ��˸10�Σ������Ҫ�����Ƶ���˸���뽫10�ĳ�-1
							}
							else
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
							if (myt!=null) 
							{
								myt.cancel();
								myt=null;
							}
							myt=new MyToast(App.getInstance().getApplicationContext());
							myt.setText(sb.toString());
							myt.show();
							//Toast.makeText(App.getInstance(), sb.toString(), Toast.LENGTH_SHORT).show();
							sb=null;
						}
						
					});
					tool.selector.setOffset(0, 80);
					mapView.getMapControl().setCurrentTool(tool);
					break;
//				case 9:	//�����
//					{
//						//��ȡ��ʮ��ͼ������ã�ͼ���Ŵ�0��ʼ
//						ShapefileLayer slayer=(ShapefileLayer) mapView.getMapControl().getMap().getLayer(9);
//						//������������
//						IPoint[] pts=new IPoint[2];
//						pts[0]=new Point(116.3107652,39.80763534);
//						pts[1]=new Point(116.3248450,39.95548116);
//						//�������߶���
//						LineString line=new LineString(pts,2,null);
//						//��ͼ�����Ҫ��
//						ft=slayer.addFeature(line, null);
//						//ˢ�µ�ͼ������ʾ�����ĵ�ͼҪ��
//						mapView.getMapControl().refresh();
//					}
//					break;
//				case 10://�ƶ���
//					if (ft==null) break;
//					//��ȡ��ʮ��ͼ������ã�ͼ���Ŵ�0��ʼ
//					ShapefileLayer slayer=(ShapefileLayer) mapView.getMapControl().getMap().getLayer(9);
//					LineString line=(LineString)ft.getShape();
//					int count=line.getNumPoints();//�õ����߽ڵ����
//					IPoint pt;
//					for (int i=0;i<count;++i)
//					{
//						pt=line.getPoint(i);//�õ����ߵĽڵ�
//						pt.setX(pt.getX()+0.02);//�����ߵ����нڵ��x�������0.004159�����߽������ƶ�
//						line.setPoint(i,new Point(pt.getX(),pt.getY()));//�޸����ߵĽڵ�
//					}
//					slayer.updateFeature(ft, true, false);//��ͼ���ύ�޸�
//					mapView.getMapControl().refresh();//ˢ�µ�ͼ������ʾ�ƶ�֮���Ч��
//					break;
				case 16:
					//�����ע�͵����Ǵ��2000ͶӰ�Ķ���
					//String CGCS2000_3_Degree_GK_Zone_40="+proj=tmerc +lat_0=0 +lon_0=120 +k=1 +x_0=40500000 +y_0=0 +ellps=GRS80 +units=m +no_defs";
					String proj1=ProjectFactory.createWGS84();
					String proj2=ProjectFactory.createGaussKruger(114, 0, ProjectFactory.ellps_xian1980,500000, "m");
					SpatialReference sr=new SpatialReference(proj1,proj2);
					sr.addPoint(new Point(113.82133,23.27463));
					sr.addPoint(new Point(113.82233,23.27563));
					sr.addPoint(new Point(113.82333,23.27663));
					sr.addPoint(new Point(113.82433,23.27763));
					double[] result=sr.pj1Topj2();
					String msg=String.format("��γ�ȵ�(113.82133,23.27463)ת����3�ȴ���˹80����ϵ֮�������Ϊ(%f,%f)", result[0],result[1]);
					Toast.makeText(App.getInstance(), msg, Toast.LENGTH_LONG).show();
					break;
				case 17:
					MeasureTool mtool=new MeasureTool(mapView.getMapControl(),0);
					mtool.setListener(new IMeasureToolListener(){

						@Override
						public void notify(int type, double value) {
							Toast.makeText(App.getInstance(), String.format("����Ϊ:%f��", value), Toast.LENGTH_LONG).show();
						}
						
					});
					mtool.selector.setOffset(0, 80);
					mapView.getMapControl().setCurrentTool(mtool);
					break;
				case 18:
					MeasureTool mtool1=new MeasureTool(mapView.getMapControl(),1);
					mtool1.setListener(new IMeasureToolListener(){

						@Override
						public void notify(int type, double value) {
							Toast.makeText(App.getInstance(), String.format("���Ϊ:%fƽ����", value), Toast.LENGTH_LONG).show();
						}
						
					});
					mtool1.selector.setOffset(0, 80);
					mapView.getMapControl().setCurrentTool(mtool1);
					break;
//				case 18:
//					if (mapView.getMapControl().getCurrentTool()!=null)
//					{
//						IMapTool t=mapView.getMapControl().getCurrentTool();
//						if (t instanceof MeasureTool)
//						{
//							MeasureTool tool1=(MeasureTool)t;
//							double value=tool1.getResult();
//							tool1.submit();
//							if (tool1.getType()==0)
//								Toast.makeText(App.getInstance(), String.format("����Ϊ:%f��", value), Toast.LENGTH_LONG).show();
//							else
//								Toast.makeText(App.getInstance(), String.format("���Ϊ:%fƽ����", value), Toast.LENGTH_LONG).show();
//						}
//					}
//					break;
				case 19:
				{
					//���ŵ��ܹ�����ר��ͼЧ���ı�����
					mapView.getMapControl().getDisplay().getDisplayTransformation().setZoom(0.00805f);
					//��������ͼ�㡰�ؽ硱��renderer�滻ΪMyRenderer
					IShapefileLayer layer=(IShapefileLayer)mapView.getMapControl().getMap().getLayer(2);
					MyRenderer renderer=new MyRenderer(layer);
					IFeatureLayer flayer=(IFeatureLayer)layer;
					flayer.setRenderer(renderer);
					mapView.getMapControl().refresh();//ˢ�µ�ͼ
					break;
				}
				case 20://ͼ�����
					//������ǰ��ͼ������ͼ��
					int cur=-1;
					IMap map=mapView.getMapControl().getMap();
					int size=map.getLayerCount();
					layers2.clear();
					//float curZoom=mapView.getMapControl().getDisplay().getDisplayTransformation().getZoom();
					ILayer layer=null;
					for (int i=0;i<size;++i)
					{
						layer=map.getLayer(i);
						//if (layer.getMinimumScale()<curZoom && curZoom<layer.getMaximumScale())//ֻ��ͼ�����ʾ������Χ�����˵�ǰ��ʾ�������ſ��Կ��Ƹ�ͼ���Ƿ�ɼ�
						{
							layers2.add(layer);
						}
					}
					size=layers2.size();
			        //if (size==0) super.onOptionsItemSelected(item);
			        //��ͼ������ֺ�ͼ���Ƿ�ɼ������ƽ�2�����������ʾ�Ի���ʹ��
			        String[] layerNames=new String[size];
			        visible=new boolean[size];
			        for (int i=0;i<size;++i)
			        {
			        	layer=layers2.get(i);
			        	layerNames[i]=layer.getName();
			        	visible[i]=layer.getVisible();
			        }
			        //��ʾһ���Ի��򣬹��û�����ͼ��
			        AlertDialog.Builder builder = new AlertDialog.Builder(act);
			        builder.setIcon(R.drawable.icon);
			        builder.setTitle("ͼ�����");
			        builder.setMultiChoiceItems(layerNames, visible,new DialogInterface.OnMultiChoiceClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							visible[which]=isChecked;//�����û���ѡ��
						}
					});
			        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							int size=visible.length;
							for (int i=0;i<size;++i)
							{//��������ÿ�����Ա����Ƶ�ͼ��Ŀɼ���
								layers2.get(i).setVisible(visible[i]);
							}
							mapView.getMapControl().refresh();//ˢ�µ�ͼ��������Ч��
						}
					});
			        builder.setNegativeButton("ȡ��", null);//ȡ����ťʲô�¶�����
					builder.create().show();
					break;
				case 21:
					System.exit(0);
					break;
				}
			}
		});
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("menu");
        return super.onCreateOptionsMenu(menu);
	}
	
    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
            int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.menu_item, new String[] { "itemImage", "itemText" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
        } else {
            menuDialog.show();
        }
        return false;
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
		editTool.confirm();	//����ʾ��ֱ��ɾ��Ҫ��
	}

	@Override
	public void onUpdateFeature(IFeature ft, ILayer layer) {
		IMapTool mt=mapView.getMapControl().getCurrentTool();
		if (mt==null) return;
		if (!(mt instanceof EditFeatureTool))
		{
			this.ft=ft;
			IFeatureLayer flayer=(IFeatureLayer)layer;
			this.showModifyDialog(flayer.getFeatureClass().getFields(), ft.getValues());
		}
		else
		{
			//�ƶ����ϵĽڵ�֮������һ���ƶ���ͼ���ϵĶ�Ӧ�ĵ�Ҫ��
			ILayer lyr=mapView.getMapControl().getMap().getLayer("��");
			if (lyr!=null && lyr instanceof ShapefileLayer)
			{
				EditFeatureTool eTool=(EditFeatureTool)mt;
				ShapefileLayer sLayer=(ShapefileLayer)lyr;
				//ʹ��selectFeature������ѯ������ĵ�Ҫ��
				IFeature oldFeature=sLayer.selectFeature(mapView.getMapControl().getDisplay().getDisplayTransformation(), eTool.selector.getX(), eTool.selector.getY(), 10);
				if (oldFeature==null) return;
				IFeature newFeature=oldFeature.clone(true,false);//���������¡һ��������ͬshape����Ҫ��
				if (eTool.newFeature.getShape() instanceof LineString)
				{
					LineString line=(LineString)eTool.newFeature.getShape();
					IPoint pt=line.getPoint(eTool.nodeIndex);//��ȡ�ƶ�֮��Ľڵ�λ�ã���ͼ���꣩
					IPoint newPoint=(IPoint)newFeature.getShape();
					newPoint.setX(pt.getX());
					newPoint.setY(pt.getY());//�������Ҫ�ص������ƶ��������ڵ�λ����
					sLayer.updateFeature(oldFeature, newFeature, true, false);//���µ�Ҫ��λ��
				}
			}
		}
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
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh();
		super.onResume();
	}
}