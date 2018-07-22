package cn.creable.android.demo5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.shapefile.Selector;
import cn.creable.gridgis.shapefile.ShapefileLayer;

/**
 * ����һ��ʵ��ɾ����ͼҪ�صĵ�ͼ�������ӣ��б���sdk�Դ���DeleteFeatureTool
 * ����ͼ������ѡ�е�ͼҪ�أ�1.5��֮�󣬻��Զ�����ȷ�϶Ի��򣬲���Ҫ������Ҳ����Ҫ�����ť
 *
 */
public class MyDeleteFeatureTool implements IMapTool {
	
	private MapControl mapControl;
	public Selector selector;//selector��һ������ʵ��ѡ��ĵ�ͼ���ߣ����Ｏ��������������ʵ�ֶ��ڵ�ͼҪ�ص�ѡ��
	
	private IFeature ft;
	private ShapefileLayer layer;
	private Activity act;

	public MyDeleteFeatureTool(MapControl mapControl,Activity act) {
		this.mapControl=mapControl;
		this.act=act;
		selector=new Selector(mapControl);
		selector.reset();//����ѡ����
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		selector.pointerDragged(x, y, x2, y2);//ֱ�ӽ������¼����͸�ѡ����
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		selector.pointerPressed(x, y, x2, y2);//ֱ�ӽ������¼����͸�ѡ����
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		selector.pointerReleased(x, y, x2, y2);//ֱ�ӽ������¼����͸�ѡ����
		//һ��ѡ������pointerReleasedִ�����֮�����ǾͿ���ʹ������2�д���������û�������û��ѡ�е�ͼҪ��
		if (ft!=null) return;//����жϷ�ֹ��û�����Ի���ʱ���û��ֵ����ͼ�����
		this.ft=selector.getSelectedFeature();
		this.layer=(ShapefileLayer)selector.getSelectedLayer();//����ֱ�ӽ�ͼ��Ӧ��ǿ��ת��ΪShapefileLayer������Ϊѡ����Ĭ�������ֻ��ѡ��ɱ༭ͼ��(ShapefileLayer)
		if (this.ft!=null && this.layer!=null)
		{
			mapControl.flashFeature(layer, ft);//ɾ����ѡ�еĵ�ͼҪ��
			//��������һ���߳���ʵ�ֵ�ͼҪ����˸1.5��֮�����������Ƿ�ɾ��Ҫ�صĶԻ���
			new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);//�������߳�˯��1.5��
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg=new Message();
				msg.what=1;
				mhandler.sendMessage(msg);//���﷢����Ϣ��handler
			}
			}).start();
		}
	}
	
	private Handler mhandler=new Handler()
	{
		public void handleMessage(Message msg) 
		{    
            switch (msg.what) 
            {    
            case 1:
            	action();
            	break;
            }
		}
	};

	@Override
	public void action() {
		if (ft!=null)
		{
			mapControl.flashFeature(null, null);//flashFeature����2��NULL���ͻ�ֹͣ��˸
			//������ʾһ��ȷ�϶Ի���
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
	    	builder.setTitle("ȷ��ɾ����").setIcon(R.drawable.icon)
	    	.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//�û�����ȷ�ϣ������ͼ���deleteFeatureɾ��Ҫ��
					if (layer.deleteFeature(ft))
						mapControl.refresh();
					ft=null;
				}
	    		
	    	})
	    	.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//�û�ȡ��ɾ��
					ft=null;
				}
			});
	    	builder.create().show();
			
		}
	}

	@Override
	public boolean keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(Canvas g) {
		selector.draw(g);//�������ѡ������draw����������ʾ�Ŵ�Ч��
	}

}
