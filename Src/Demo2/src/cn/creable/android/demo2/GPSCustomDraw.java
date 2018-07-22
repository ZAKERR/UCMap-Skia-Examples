package cn.creable.android.demo2;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.util.Image;
import cn.creable.ucmap.LBS;
import cn.creable.ucmap.OpenSourceMapLayer;

public class GPSCustomDraw implements ICustomDraw,LocationListener,ICustomDrawDataCenter {
	
	private MapControl mapControl;
	public double lon,lat;
	public double acc;//�Ƿ�Χ��ֻ�л�վ��λ�����ݲ������ֵ
	public double x,y;
	private Image gps;
//	private Image gps1;
//	private boolean flag;
	private Paint paint;
	private IDisplayTransformation dt;
//	private MyTimerTask timer;
	
	private LBS lbs;
	
//	private class MyTimerTask extends TimerTask
//	{
//
//		@Override
//		public void run() {
//			if (lon!=0 && lat!=0 && mapControl.noCustomDraw==false)
//				mapControl.repaint();
//		}
//		
//	}
	
	/**
	 * ���ݸ��������ĵ�Ͱ뾶���ڵ�ͼ�ϻ�һ��Բ
	 * @param layer googleͼ��
	 * @param dt ת���������
	 * @param g Canvas����
	 * @param paint paint����
	 * @param x ���ĵ㣬��λ�Ƕ�
	 * @param y ���ĵ�
	 * @param radius �뾶����λ����
	 */
	private void drawCircle(OpenSourceMapLayer layer,IDisplayTransformation dt,Canvas g,Paint paint,double x,double y,float radius)
	{
		if (layer==null)
		{//������googleͼ��ʱ�Ĵ���
			double dis=0;
			float radius1=0;
			if (mapControl.getMap().getMapUnits()==1)//�����ͼ���ö�Ϊ��λ
			{
				dis=radius*180/(6370693.4856530580439461631130889*Math.PI);//������Ϊ��λ�ľ���ת��Ϊ�Զ�Ϊ��λ�ľ���
			}
			else//�����ͼ�����ö�Ϊ��λ
				dis=radius;
			radius1=dt.TransformMeasures((float)dis, false);//���Ե�ͼ�Ͼ���ת��Ϊ��Ļ�Ͼ���
			
			Point result=new Point();
			Point pt=new Point(x,y);
			dt.fromMapPoint(pt, result);//�����ĵ�ת��Ϊ��Ļ�ϵĵ�
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(0x2E0087FF);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(0xBF84B6D6);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
		}
		else
		{//����googleͼ��ʱ�Ĵ���
			double dis=radius*180/(6370693.4856530580439461631130889*Math.PI);//������Ϊ��λ�ľ���ת��Ϊ�Զ�Ϊ��λ�ľ���
			Point pt1=layer.fromLonLat(x, y);
			Point pt2=layer.fromLonLat(x+dis, y);
			double dis2=Math.abs(pt2.getX()-pt1.getX());//���Զ�Ϊ��λ�ľ���ת��Ϊgoogle�����ϵľ���
			float radius1=dt.TransformMeasures((float)dis2, false);//����google����Ϊ��λ�ľ���ת��Ϊ��Ļ�Ͼ���
			
			Point result=new Point();
			Point pt=layer.fromLonLat(x, y);
			Point offset=layer.getOffset(x, y);
			pt.setX(pt.getX()+offset.getX());
			pt.setY(pt.getY()+offset.getY());
			dt.fromMapPoint(pt, result);//�����ĵ�ת��Ϊ��Ļ�ϵĵ�
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(0x2E0087FF);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(0xBF84B6D6);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
		}
	}
	
	public GPSCustomDraw(MapControl mapControl)
	{
		this.mapControl=mapControl;
		dt=mapControl.getDisplay().getDisplayTransformation();
		paint=new Paint();
		paint.setAntiAlias(true);
		BitmapDrawable bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps);
		gps=new Image(bmpDraw.getBitmap());
//		bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps1);
//		gps1=new Image(bmpDraw.getBitmap());
//		Timer myTimer = new Timer();
//		timer=new MyTimerTask();
//		myTimer.schedule(timer, 500, 500);
		
		lbs=new LBS(App.getInstance());
		lbs.openGPS(1000, 0.01f, this);
		lbs.getPositionByNetwork(this);
		
	}
	
	public void close()
	{
		lbs.closeGPS(this);
		x=0;
		y=0;
		mapControl.repaint();
	}

	@Override
	public void draw(Canvas g) {
		if (x!=0 && y!=0)
		{
			Point pt=new Point(x,y);
			Point result=new Point();
			dt=mapControl.getDisplay().getDisplayTransformation();
			dt.fromMapPoint(pt, result);//��ͼ������ת��Ϊ��Ļ����
			gps.draw(g, (int)result.getX()-gps.getWidth()/2, (int)result.getY()-gps.getWidth()/2, null);
			OpenSourceMapLayer oslayer=null;
			if (mapControl.getMap().getLayerCount()>0 && mapControl.getMap().getLayer(0) instanceof OpenSourceMapLayer)
				oslayer=(OpenSourceMapLayer)mapControl.getMap().getLayer(0);
			
			if (acc>0) drawCircle(oslayer,dt,g,paint,lon,lat,(float)acc);
			pt=null;
			result=null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		lon=location.getLongitude();
		lat=location.getLatitude();
		acc=location.getAccuracy();
		//����γ��ת��Ϊͼ������
		if (mapControl.getMap().getLayerCount()>0 && mapControl.getMap().getLayer(0) instanceof OpenSourceMapLayer)
		{
			OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapControl.getMap().getLayer(0);
			Point offset=oslayer.getOffset(lon, lat);
			Point pt=oslayer.fromLonLat(lon, lat);
			x=pt.getX()+offset.getX();
			y=pt.getY()+offset.getY();
		}
		else
		{
			x=lon;
			y=lat;
		}
		IEnvelope env=mapControl.getExtent();
		if (env.getXMin()>x || env.getYMax()<x || env.getYMin()>y || env.getYMax()<y)
		{
			Point pt=new Point(x,y);
			env.centerAt(pt);
			mapControl.refresh(env);
		}
		else
			mapControl.repaint();
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	public IGeometry getGeometry(int index) {
		return new Point(x,y);
	}

	@Override
	public int getGeometryNum() {
		return 1;
	}

	@Override
	public void onGeometrySelected(int index, IGeometry geometry) {
		Toast.makeText(App.getInstance().getApplicationContext(), index+"   "+geometry, 100).show();
	}

}
