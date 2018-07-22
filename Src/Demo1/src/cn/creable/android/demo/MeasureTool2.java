package cn.creable.android.demo;

import java.util.Vector;

import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapTool2;
import cn.creable.gridgis.controls.IMeasureToolListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.Arithmetic;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.shapefile.Selector;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * �������ߣ�����ʵ�ֲ����Ͳ����
 *
 */
public class MeasureTool2 implements IMapTool,IMapTool2 {
	
	protected MapControl mapControl;
	protected int type;
	/**
	 * ѡ��������
	 */
	public Selector selector;
	protected boolean bDrag;
	protected Vector<IPoint> pts;
	protected Paint paint;
	
	protected IMeasureToolListener listener;
	
	protected boolean canMoveMap=true;
	
	protected int fillColor=0x80FC7F43;
	protected int lineColor=0xFFFC7F43;
	
	/**
	 * ���캯��
	 * @param mapControl ��ͼ�ؼ�����
	 * @param type ���������ͣ�0��ʾ�������룬1��ʾ�������
	 */
	public MeasureTool2(MapControl mapControl,int type)
    {
        this.mapControl=mapControl;
        this.type=type;
        selector=new Selector(mapControl);
        selector.setMode(1);
        pts=new Vector<IPoint>();
        paint=new Paint();
        paint.setStrokeWidth(3);
        paint.setTextSize(16);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }
	
	/**
	 * �����Ƿ�����ƶ���ͼ
	 * @param flag �Ƿ�����ƶ���ͼ�����Ϊtrue�����û�����ͨ�������Ļ�ı�Ե��ʵ�ֲ����Ĺ������ƶ���ͼ
	 */
	public void setCanMoveMap(boolean flag)
	{
		this.canMoveMap=flag;
	}
	
	/**
	 * ���ò��������ͣ�0��ʾ�������룬1��ʾ�������
	 * @param type ����������
	 */
	public void setType(int type)
	{
		this.type=type;
	}
	
	/**
	 * ���ü�������������������֮���û�������Ϻ󳤰���Ļ��MeasureTool����ü�������notify����
	 * @param listener ������
	 */
	public void setListener(IMeasureToolListener listener)
	{
		this.listener=listener;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}
	
	public void draw(Canvas g,float offsetx,float offsety)
	{
		IDisplayTransformation trans=mapControl.getDisplay().getDisplayTransformation();
        int size=pts.size();
        Point result=new Point();
        if (size>1 || (size>0 && bDrag))
        {
        	paint.setStrokeWidth(3);
            Path path = new Path();
            Point start=new Point();
            trans.fromMapPoint(pts.get(0),start);
            path.moveTo((float)start.getX()+offsetx, (float)start.getY()+offsety);
            for (int i=1;i<size;++i)
            {
                trans.fromMapPoint(pts.get(i), result);
                path.lineTo((float)result.getX()+offsetx, (float)result.getY()+offsety);
            }
//            if (bDrag)
//            	path.lineTo(selector.getX()+offsetx, selector.getY()+offsety);
//            if (type==1)
//            	path.lineTo((float)start.getX()+offsetx, (float)start.getY()+offsety);
            
            
            paint.setColor(lineColor);//0.98823529411765, 0.49803921568627, 0.26274509803922, 1);
            paint.setStyle(Paint.Style.STROKE);
            g.drawPath(path, paint);
            if (type==1 && (size>2 || (size>1 && bDrag)))
            {
            	paint.setStyle(Paint.Style.FILL);
            	paint.setColor(fillColor);
                //CGContextSetRGBFillColor(pDC, 0.98823529411765, 0.49803921568627, 0.26274509803922, 0.5);
                //CGContextAddPath(pDC, path);
                //CGContextFillPath(pDC);
            	g.drawPath(path, paint);
            }
                
        }
        for (int i=0;i<size;++i)
        {
        	paint.setStrokeWidth(3);
            trans.fromMapPoint(pts.get(i), result);
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);
            g.drawCircle((float)result.getX()+offsetx,(float)result.getY()+offsety, 4, paint);
            //CGContextFillEllipseInRect(pDC, CGRectMake(result.x-4,result.y-4,8,8));
            paint.setColor(0xFFFF0000);
            paint.setStyle(Paint.Style.STROKE);
            //CGContextSetRGBStrokeColor(pDC, 1, 0, 0, 1);
            g.drawCircle((float)result.getX()+offsetx,(float)result.getY()+offsety, 4, paint);
            //CGContextStrokeEllipseInRect(pDC, CGRectMake(result.x-4,result.y-4,8,8));
            paint.setStrokeWidth(1);
            if (type==0)
            {
                String string=null;
                if (i==0)
                    string="���";
                else
                {
                    double distance=getResult(i);
                    if (1<=distance && distance<1000) string=String.format("%.1f��",distance);
                    else if (distance>=1000) string=String.format("%.1fǧ��",distance/1000);
                    else if (0.01<=distance && distance<1) string=String.format("%.1f����",distance*100);
                    else string=String.format("%.1f����",distance*1000);
                }
                float width=paint.measureText(string)+4;
                float height=paint.measureText("��")+4;
                float x=(float)(result.getX()+9+offsetx);
                float y=(float)(result.getY()-height/2+offsety);
                RectF r=new RectF(x, y,x+width, y+height);
                paint.setColor(0xFFFFFFFF);
                paint.setStyle(Paint.Style.FILL);
                g.drawRect(r, paint);
                paint.setColor(0xFF101010);
                paint.setStyle(Paint.Style.STROKE);
                g.drawRect(r, paint);
                paint.setStyle(Paint.Style.FILL);
                g.drawText(string, x, y+height-2, paint);
            }
            else if (type==1)
            {
                if (size>2 && i==(size-1))
                {
                    double area=getResult();
                    String string=null;
                    if (area<100000) string=String.format("%.1fƽ����",area);
                    else string=String.format("%.1fƽ������",area/1000/1000);
                    float width=paint.measureText(string)+4;
                    float height=paint.measureText("��")+4;
                    float x=(float)(result.getX()+9+offsetx);
                    float y=(float)(result.getY()-height/2+offsety);
                    RectF r=new RectF(x, y,x+width, y+height);
                    paint.setColor(0xFFFFFFFF);
                    paint.setStyle(Paint.Style.FILL);
                    g.drawRect(r, paint);
                    paint.setColor(0xFF101010);
                    paint.setStyle(Paint.Style.STROKE);
                    g.drawRect(r, paint);
                    paint.setStyle(Paint.Style.FILL);
                    g.drawText(string, x, y+height-2, paint);
                }
            }
        }
        if (bDrag) selector.draw(g);
	}

	@Override
	public void draw(Canvas g) {
        draw(g,0,0);
	}

	@Override
	public boolean keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		selector.pointerDragged(x, y, x2, y2);
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		bDrag=true;
        selector.pointerPressed(x, y, x2, y2);
//        if (selector.getX()==x && selector.getY()==y)
//        {
//            IDisplayTransformation dt=mapControl.getDisplay().getDisplayTransformation();
//            
//            if (x>(dt.getDeviceFrame().getXMax()*0.9) || x<(dt.getDeviceFrame().getXMax()*0.1) ||
//                y>(dt.getDeviceFrame().getYMax()*0.9) || y<(dt.getDeviceFrame().getYMax()*0.1))
//            {
//                Point pt=new Point();
//                dt.toMapPoint(x, y, pt);
//                IEnvelope env=mapControl.getExtent();
//                env.centerAt(pt);
//                mapControl.refresh(env);
//                return;
//            }
//            
//            Point pt=new Point(selector.getX(),selector.getY());
//            Point result=new Point();
//            dt.toMapPoint(pt, result);
//            pts.add(result);
//            mapControl.repaint();
//        }
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		bDrag=false;
        selector.pointerReleased(x, y, x2, y2);
        //if (!(selector.getX()==x && selector.getY()==y))
        {
            IDisplayTransformation dt=mapControl.getDisplay().getDisplayTransformation();
            
            if (canMoveMap)
            {
	            if (x>(dt.getDeviceFrame().getXMax()*0.9) || x<(dt.getDeviceFrame().getXMax()*0.1) ||
	                y>(dt.getDeviceFrame().getYMax()*0.9) || y<(dt.getDeviceFrame().getYMax()*0.1))
	            {
	                Point pt=new Point();
	                dt.toMapPoint(x, y, pt);
	                IEnvelope env=mapControl.getExtent();
	                env.centerAt(pt);
	                mapControl.refresh(env);
	                return;
	            }
            }
            
            Point pt=new Point(selector.getX(),selector.getY());
            Point result=new Point();
            dt.toMapPoint(pt, result);
            pts.add(result);
            mapControl.repaint();
        }
	}
	
	/**
	 * ��ȡ����������
	 * @return ����������
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * ��ȡ�����Ľ��
	 * @return �����Ľ��
	 */
	public double getResult()
	{
		return getResult(-1);
	}
	
	/**
	 * ��ȡ�����Ľ���������������ָ���㵽�ĸ���Ϊֹ
	 * @param endPoint ������ֹ��ı��
	 * @return �����Ľ��
	 */
	public double getResult(int endPoint)
    {
        int size=pts.size();
        if (size<2) return 0;
        if (endPoint>0) size=endPoint+1;
        if (type==0)
        {
            IPoint pt1=pts.get(0);
            byte param=0;
            if (-180<=pt1.getX() && pt1.getX()<=180 && -90<=pt1.getY() && pt1.getY()<=90)
                param=1;
            double dis=0;
            for (int i=1;i<size;++i)
            {
                dis+=Arithmetic.Distance(pts.get(i-1),pts.get(i),param);
            }
            return dis;
        }
        else if (type==1)
        {
            IPoint pt1=pts.get(0);
            byte param=0;
            if (-180<=pt1.getX() && pt1.getX()<=180 && -90<=pt1.getY() && pt1.getY()<=90)
                param=1;
            return Arithmetic.Area(pts,param);
        }
        return 0;
    }
    
	/**
	 * �ύ��������ն���
	 */
    public void submit()
    {
        pts.clear();
    }

	@Override
	public void onRemove() {
	}

	@Override
	public int getLongPressTolerance() {
		return 8;
	}

	@Override
	public int getLongPressTime() {
		return 1000;
	}

	@Override
	public void onLongPressed() {
		bDrag=false;
		selector.reset();
		if (listener!=null) listener.notify(type, getResult());
		
		submit();
	}
	
	/**
	 * ����һ��
	 * @return �Ƿ���˳ɹ�
	 */
	public boolean undo()
	{
		if (pts.size()>0)
		{
			pts.remove(pts.size()-1);
			mapControl.repaint();
			return true;
		}
		return false;
	}

}
