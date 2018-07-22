package cn.creable.android.demo7;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import cn.creable.gridgis.util.FileReader;
import cn.creable.gridgis.util.FileWriter;

/**
 * ��ͼ����������ͼ�滻
 * ʹ�ù�����ʵ�����ߵ�ͼ���£���Ҫ���Ե�ͼ����������html�ļ���д���ͼ�汾����ͼ�汾Ϊ���ͱ���
 * �ٽ���ͼ�ļ���ѹ��Ϊzip��ʽ����֮ǰ��html�ļ�һͬ�����������
 * check������������html�ļ������Աȵ�ͼ�汾
 * update�����Ὣzip�ļ����ص��豸�У���ѹ���滻ԭ���ĵ�ͼ
 * ������2����ַ����Ҫ�������Լ����������ı�
 *
 */
public class MapUpdater {
	
	private int curVersion;
	
	public MapUpdater(int curVersion)
	{
		this.curVersion=curVersion;
	}

	/**
	 * ����ͼ�汾�����ͨ��message��ʽ����
	 * @param mapPath
	 * @return
	 */
	public void check(String mapPath,Handler handler)
	{
		if (!mapPath.endsWith(File.separator))
			mapPath+=File.separator;
		String[] list=mapPath.split(File.separator);
		final String mapname=list[list.length-1];
		//���Ȼ�������ͼ�İ汾
		String verFile=mapPath+"version";
		File file=new File(verFile);
		if (!file.exists())
		{//���û�а汾�ļ������½�һ��
			FileWriter fw=new FileWriter(verFile);
			fw.write("0");
			fw.close();
			fw=null;
		}
		byte[] buf=FileReader.readAll(verFile);
		final int oldVer=Integer.parseInt(new String(buf));
		final Handler h=handler;
		
		new Thread(){

			@Override
			public void run() {
				super.run();
				//���ʷ���������ѯ���µĵ�ͼ�汾
				String html=String.format("http://www.creable.cn/map/%s.html", mapname);//�����ַ��Ҫ�ĳ������Լ���
				try {
					int newVer=Integer.parseInt(getOneHtml(html));
					if (newVer>oldVer)
					{//�����������°���Ҫ����
						Message msg=new Message();
						msg.what=1;
						msg.arg1=1;
						msg.arg2=newVer;
						h.sendMessage(msg);
					}
					else
					{
						Message msg=new Message();
						msg.what=1;
						msg.arg1=0;
						h.sendMessage(msg);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
					msg.what=1;
					msg.arg1=0;
					h.sendMessage(msg);
				}
			}
			
		}.start();
		
	}
	
	public void update(String mapPath,Handler handler)
	{
		if (!mapPath.endsWith(File.separator))
			mapPath+=File.separator;
		String[] list=mapPath.split(File.separator);
		final String mapname=list[list.length-1];
		
		StringBuilder sb=new StringBuilder();
		int count=list.length-1;
		for (int i=0;i<count;++i)
		{
			sb.append(File.separator);
			sb.append(list[i]);
		}
		final String path=sb.toString();
		final String mappath=mapPath;
		final Handler h=handler;
		
		new Thread(){

			@Override
			public void run() {
				super.run();
				String url=String.format("http://www.creable.cn/map/%s.zip", mapname);//�����ַ��Ҫ�ĳ������Լ���
				try {
					URL u = new URL(url);
				
					InputStream is;
					HttpURLConnection.setFollowRedirects(true);
					HttpURLConnection hc = (HttpURLConnection) u.openConnection();
					hc.setRequestMethod("GET");
					hc.setUseCaches(false);
					hc.addRequestProperty("Accept", "*/*");
					hc.addRequestProperty("User-Agent",
							"Mozilla/4.0 (compatible; MSIE)");
					hc.connect();
					int code = hc.getResponseCode();
					if (code != 200) {
						throw new IOException("Response code not ok");
					} else {
						is = hc.getInputStream();
					}
					int nContentLength=hc.getContentLength();
					
					String tmpPathname=Environment.getExternalStorageDirectory().getPath()+"/_tempFile.zip";
					File tmpFile=new File(tmpPathname);
					BufferedOutputStream fos=new BufferedOutputStream(new FileOutputStream(tmpFile,false));
					
					int length=102400;//����������
					byte[] buf=new byte[nContentLength];
					int pos = 0;
					int hasRead = 0;
					long time1=System.currentTimeMillis();
					long time2;
					if (nContentLength<length) length=nContentLength;
					while ((hasRead = is.read(buf, 0, length)) != -1) {
						pos+=hasRead;
						fos.write(buf, 0, hasRead);
						time2=System.currentTimeMillis();
						if ((time2-time1)>1000)
						{
							Message msg=new Message();
							msg.what=2;
							msg.arg1=pos;
							msg.arg2=nContentLength;
							h.sendMessage(msg);
							time1=time2;
						}
						int len=nContentLength-pos;
						if (len<length)
							length=len;
					}
					fos.flush();
					fos.close();
					
					if (is != null) is.close();
					if (hc != null) hc.disconnect();
					Message msg=new Message();msg.what=3;h.sendMessage(msg);
					deleteDirectory(mappath);//ɾ��֮ǰ�ĵ�ͼ
					msg=new Message();msg.what=4;h.sendMessage(msg);
					ZIP.UnZipFolder(tmpPathname, path);//��ѹ���µĵ�ͼ
					tmpFile.delete();//ɾ����ʱ��ѹ����
					String verFile=mappath+"version";
					File file=new File(verFile);
					if (!file.exists())
					{//д���ͼ�汾
						FileWriter fw=new FileWriter(verFile);
						fw.write(String.format("%d", curVersion));
						fw.close();
						fw=null;
					}
					msg=new Message();msg.what=5;h.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
		
		
	}
	
	private String getOneHtml(String htmlurl) throws IOException {
		URL url;
		String temp;
		final StringBuffer sb = new StringBuffer();
		try {
			url = new URL(htmlurl);
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "utf-8"));// ��ȡ��ҳȫ������
			while ((temp = in.readLine()) != null) {
				sb.append(temp);
			}
			in.close();
		} catch (final MalformedURLException me) {
			System.out.println("�������URL��ʽ�����⣡����ϸ����");
			me.getMessage();
			throw me;
		} catch (final IOException e) {
			e.printStackTrace();
			throw e;
		}
		return sb.toString();
	}
	

	private boolean deleteDirectory(String delpath) throws Exception {
		try {

			File file = new File(delpath);
			// ���ҽ����˳���·������ʾ���ļ������� ��һ��Ŀ¼ʱ������ true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deleteDirectory(delpath + File.separator + filelist[i]);
					}
				}
				file.delete();
			}

		} catch (FileNotFoundException e) {
			System.out.println("deletefile() Exception:" + e.getMessage());
		}
		return true;
	}

}
