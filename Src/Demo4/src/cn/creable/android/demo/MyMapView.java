package cn.creable.android.demo;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.display.IDisplayListener;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.shapefile.ShapefileLayer;

public class MyMapView extends MapView implements IDisplayListener {

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.isInEditMode()) return;
		MapControl mapControl=getMapControl();
		if (mapControl!=null)
		{//�����ͼ�ؼ���Ϊnull������Ҫ�ȴ�ˢ�ߵ�ͼ�߳�ִ�����
			try{
				while (mapControl.getRefreshManager().isThreadRunning())
					Thread.sleep(200);
			}catch(Exception ex){ex.printStackTrace();}
		}
		super.onSizeChanged(w, h, oldw, oldh);
		
		mapControl=getMapControl();
		//���´�����ڵ�ͼ���ڵ����½���ʾһ�������߷��ţ��������˵����ο�doc
		//getResources().getDisplayMetrics().xdpi ��ȡ����Ļ�ĺ���dpi������2.54���Եõ�1�����ж��ٸ����ص�
		mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
		if (mapControl.getMap()==null)
		{
			mapControl.setDisplayListener(this);
			
			String path=Environment.getExternalStorageDirectory().getPath();
			mapControl.loadMap(path+"/bj/map.ini", (byte)0);
			
			mapControl.setPanTool();
			
			//����undo redo���ܣ�������������undo 100��
			ShapefileLayer.openUndoRedo(100);
		}
	}

	@Override
	public void onDisplayNotify(IDisplayTransformation dt, long costTime) {
		System.out.println(costTime);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (MainAct.myt!=null)
		{
			MainAct.myt.cancel();
			MainAct.myt=null;
		}
		return super.onTouchEvent(arg0);
	}

}
