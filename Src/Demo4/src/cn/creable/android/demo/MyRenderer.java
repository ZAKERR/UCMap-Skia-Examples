package cn.creable.android.demo;

import java.util.Hashtable;

import cn.creable.gridgis.display.FeatureRenderer;
import cn.creable.gridgis.display.FillSymbol;
import cn.creable.gridgis.display.ISymbol;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.gridgis.shapefile.ShapefileLayer;

public class MyRenderer extends FeatureRenderer {
	
	private Hashtable<String,ISymbol> syms;
	private IShapefileLayer layer;
	
	public MyRenderer(IShapefileLayer layer)
	{
		super();
		this.layer=layer;
		syms=new Hashtable<String,ISymbol>();
	}

	@Override
	public ISymbol getSymbolByFeature(IFeature ft) {
		String[] values=ft.getValues();
		if (values==null) 
		{
			layer.loadFeatureAttribute(ft);//���Ҫ�ص�����û�м��أ���ô���������
			values=ft.getValues();
		}
		int val=Integer.parseInt(values[1]);//���ｫ�ڶ�������ֵת��Ϊ����
		//�������val��ֵ�����жϲ���������ʽ����ͼ
		if (110101<=val && val<=110105)
		{
			ISymbol result=syms.get("110101_110105");	//�������ȼ��hashtable���Ƿ��Ѿ��д����õ���ʽ���������ֱ�ӷ���
			if (result==null) 
			{
				result=new FillSymbol(0xFF00FF00,new LineSymbol(2,0xFF000000));
				syms.put("110101_110105", result);
			}
			return result;
		}
		else if (110106<=val && val<=110110)
		{
			ISymbol result=syms.get("110106_110110");	//�������ȼ��hashtable���Ƿ��Ѿ��д����õ���ʽ���������ֱ�ӷ���
			if (result==null) 
			{
				result=new FillSymbol(0xFFFFFF00,new LineSymbol(2,0xFF000000));
				syms.put("110106_110110", result);
			}
			return result;
		}
		else if (110111<=val && val<=110115)
		{
			ISymbol result=syms.get("110111_110115");	//�������ȼ��hashtable���Ƿ��Ѿ��д����õ���ʽ���������ֱ�ӷ���
			if (result==null) 
			{
				result=new FillSymbol(0xFF00FFFF,new LineSymbol(2,0xFF000000));
				syms.put("110111_110115", result);
			}
			return result;
		}
		else if (110116<=val && val<=110230)
		{
			ISymbol result=syms.get("110116_110230");	//�������ȼ��hashtable���Ƿ��Ѿ��д����õ���ʽ���������ֱ�ӷ���
			if (result==null) 
			{
				result=new FillSymbol(0xFF0000FF,new LineSymbol(2,0xFF000000));
				syms.put("110116_110230", result);
			}
			return result;
		}
		return null;
	}

}
