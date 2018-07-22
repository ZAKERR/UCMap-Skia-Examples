package cn.creable.android.demo2;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.AttributeSet;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.ucmap.MapLoader;
import cn.creable.ucmap.OpenSourceMapLayer;

public class MyMapView extends MapView {
	
	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.isInEditMode()) return;
		MapControl mapControl=getMapControl();
		
		//���´�����ڵ�ͼ���ڵ����½���ʾһ�������߷��ţ��������˵����ο�doc
		//getResources().getDisplayMetrics().xdpi ��ȡ����Ļ�ĺ���dpi������2.54���Եõ�1�����ж��ٸ����ص�
		mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
		if (mapControl.getMap()==null)
		{
			//mapControl.drawOnScreen=true;
			String path=Environment.getExternalStorageDirectory().getPath();
			MapLoader.loadMapXML(mapControl, path+"/OpenSourceMap.xml");
			//MapLoader.loadMapXML(mapControl,Environment.getExternalStorageDirectory().getPath()+"/cache/OpenSourceMapWithLocal.xml",null,1,0.00001f,true);
			//mapControl.setCustomDraw(new GPSCustomDraw(mapControl));
			mapControl.setPanTool();
			//((OpenSourceMapLayer)mapControl.getMap().getLayer(0)).openZoomInMode();
			//mapControl.refresh();
		}
	}
}
