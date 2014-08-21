package org.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.catalina.tribes.group.interceptors.TwoPhaseCommitInterceptor.MapEntry;
import org.util.Constant;

/**
 * class num start with 1 to 19.
 * @author Victor
 *
 */
public class MapClass {
	private HashMap<String,Integer> map;
	private String[] num2Class;
	
	// TODO 变成单例模式
	public MapClass(){
		map=new HashMap<String,Integer>();
		map.put("地名", 1);
		map.put("宾馆饭店",2);
		map.put("餐饮服务",3);
		map.put("场馆会所",4);
		map.put("房地产",5);
		map.put("公司企业",6);
		map.put("购物场所",7);
		map.put("交通出行",8);
		map.put("金融银行",9);
		map.put("旅游景点",10);
		map.put("汽车服务",11);
		map.put("体育场馆",12);
		map.put("新闻媒体",13);
		map.put("休闲娱乐",14);
		map.put("学校科研",15);
		map.put("医疗卫生",16);
		map.put("邮政电信",17);
		map.put("政府机关",18);
		map.put("其它",19);
		num2Class= new String[20];
		for(String t:map.keySet()){
			num2Class[map.get(t)]=t;
		}

	}
	public String Num2Class(int index){
		return num2Class[index];
	}
	/**
	 * 从map.txt中读取到类别和数字的map
	 * 从预测的类别文件夹中得到的数字match到对应的类别。这个类别文件是挺长的可以是任意长度。
	 * 将对应的类别写到classNamefile中。这个里面是汉字的类别名称。
	 * @param numFile  预测的类别
	 * @param classFile
	 */
    public static void getClassFromNum(String mapFile,String numFile,String classNameFile){
    	try{
	    	//read in map
	    	BufferedReader inMap=new BufferedReader(new InputStreamReader (new FileInputStream(mapFile),Constant.encoding));
	    	ArrayList<String> al= new ArrayList<String>();
			String tmp;
			while((tmp=inMap.readLine())!=null){
				String[] split=tmp.split(",");
				if(tmp.length()!=0&&split.length==2){
					al.add(Integer.valueOf(split[1])-1,split[0]);
				}
			}
			inMap.close();
			BufferedReader inNum=new BufferedReader(new InputStreamReader (new FileInputStream(numFile),Constant.encoding));
			
			String num=null;			
			BufferedWriter out=null;
			File f=new File(classNameFile);
			if(!f.exists()){
				f.createNewFile();
			}
			out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),Constant.encoding));
			while((num=inNum.readLine())!=null){
				out.write(al.get((int) (Double.valueOf(num)-1))+"\n");
			}
			inNum.close();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    /**
	 * 从map.txt中读取到类别和数字的map
	 * 从网页请求而预测的类别文件中得到的数字match到对应的类别 这个类别文件是极小的 只有一行数据。
	 * 最后返回值为数字对应的类别
	 * @param numFile  预测的类别
	 * @param classFile
	 */
    public static String getClassFromNum(String mapFile,String numFile){
    	try{
	    	//read in map
	    	BufferedReader inMap=new BufferedReader(new InputStreamReader (new FileInputStream(mapFile),Constant.encoding));
	    	ArrayList<String> al= new ArrayList<String>();
			String tmp;
			while((tmp=inMap.readLine())!=null){
				String[] split=tmp.split(",");
				if(tmp.length()!=0&&split.length==2){
					
					al.add(Integer.valueOf(split[1])-1,split[0]);
				}
			}
			inMap.close();
			BufferedReader inNum=new BufferedReader(new InputStreamReader (new FileInputStream(numFile),Constant.encoding));
			
			String num;
			num=inNum.readLine();
			inNum.close();
			return al.get((int)(Double.valueOf(num)-1));
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
    }
    /**
     * 将类别名称映射成数字写到numFile中
     * @param classFile
     * @param numFile
     */
    public void Class2Num(String classFile,String numFile){
    	try {
			BufferedReader in=new BufferedReader(new InputStreamReader (new FileInputStream(classFile),Constant.encoding));
			String tmp;
			BufferedWriter out=null;
			File f=new File(numFile);
			if(!f.exists()){
				f.createNewFile();
			}
			out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),Constant.encoding));
			
			while((tmp=in.readLine())!=null){
				String name=tmp.trim();
				if(name.length()!=0&&map.containsKey(name)){
				    //System.out.println(name);
					out.write(map.get(name).toString());
				}else{
					out.write("19");//其它
				}
				out.write("\n");
			}
			in.close();
			out.close();
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
