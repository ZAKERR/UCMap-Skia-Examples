package cn.creable.demo8;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import cn.creable.demo8.Util.LocationPoint;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.Display;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.LineString;
import cn.creable.gridgis.geometry.Point;

/**
 * �����ʵ�ֻ���gps���Լ��켣�Ĺ���
 *
 */
public class MyCustomDraw implements ICustomDraw {
	
	private LineString historyLine;//�켣��
	private LineSymbol ls;//����ʽ1
	private LineSymbol ls2;//����ʽ2
	private LocationPoint curLoc;//��ǰgps��
	
	private MapControl mapControl;
	
	private Point pt=new Point();
	
	private Bitmap gpsImage;//gps��ͼƬ����
	
	private boolean needRefresh=false;//�Ƿ���Ҫˢ�µ�ͼ
	
	public MyCustomDraw(MapControl mapControl)
	{
		this.mapControl=mapControl;
		gpsImage=((BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps)).getBitmap();
		ls=new LineSymbol(3,0xFFE049B2);
		ls2=new LineSymbol(5,0xFF49E0B2);
		
		curLoc=Util.getInstance().getCurrentLocation();
	}
	
	/**
	 * ���ù켣
	 * @param locPoints gps�����飬Ҳ���ǹ켣
	 */
	public void setLocaitonPoints(ArrayList<LocationPoint> locPoints)
	{
		if (locPoints==null)
			historyLine=null;
		else
		{
			//����LocationPoint���鹹��һ��LineString����
			int count=locPoints.size();
			if (count<2) return;
			double[] points=new double[count*2];
			for (int i=0;i<count;++i)
			{
				points[i*2]=locPoints.get(i).lon;
				points[i*2+1]=locPoints.get(i).lat;
			}
			historyLine=new LineString(points);
		}
	}
	
	/**
	 * ���õ�ǰgps��
	 * @param curLoc ��ǰgps��
	 */
	public void setCurrentLocation(LocationPoint curLoc)
	{
		this.curLoc=curLoc;
		if (curLoc==null) return;
		if (mapControl.pointerStatus==2 || mapControl.getRefreshManager().isThreadRunning()==true)
			return;//��mapControl�е�ͼ���ߴ����϶�ģʽ������ˢ���߳�û��ִ�����ʱ��ֱ��return
		IEnvelope env=mapControl.getExtent();
		//���gps���ڵ�ǰ��ʾ�ĵ�ͼ��Χ�ڣ�����������gps��
		if (env.getXMin()<=curLoc.lon && curLoc.lon<=env.getXMax() &&
				env.getYMin()<=curLoc.lat &&curLoc.lat<=env.getYMax())
		{
			if (needRefresh)
			{//�����Ҫˢ�������refresh
				mapControl.refresh();
				needRefresh=false;
			}
			else
			{//�������Ҫˢ�������repaint��repaint���ٶȱ�refresh��Ķ࣬��Ϊrepaint����Ҫ���»��Ƶ�ͼ�������ǽ��µ�gps�����һ�¶���
				mapControl.repaint();
			}
		}
	}

	@Override
	public void draw(Canvas g) {
		if (curLoc!=null)
		{
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(curLoc.lon, curLoc.lat, pt);
			g.drawBitmap(gpsImage, (float)(pt.getX()-gpsImage.getWidth()/2), (float)(pt.getY()-gpsImage.getHeight()/2), null);
			if (mapControl.smoothMode==true && mapControl.pointerStatus==0)
				needRefresh=true;//��smoothMode=true���Ұ��´�����������drawʱ����һ��gpsλ�õĸ�����Ҫ����refresh����ΪPanTool���ƶ���ͼʱ�������customDraw��draw�����������ݻ��ڵ�ͼ�����ϣ���gps�����ƶ��ģ�Ҫ��ʾ���ƶ���Ч������������һ��refresh���ϴβ�����gps�����
		}
		if (historyLine!=null)
		{
			Display display=(Display)mapControl.getDisplay();
			Canvas oldG=display.getCanvas();//��ȡdisplay�е�canvas
			display.setCanvas(g);//�滻display�е�canvas
			display.DrawPolyline(historyLine, ls2);//�����켣��
			//display.DrawPolyline(historyLine, ls);//�����켣��
			display.setCanvas(oldG);//��ԭdisplay�е�canvas
		}
	}

}
