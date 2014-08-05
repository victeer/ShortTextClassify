package org.service.textPreProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.util.Constant;
/**
 * 
 }  
 * @author Victor
 *
 */
public class Segment2Vector {
	/**
	 * 根据字典将读入的POI单字文本转化为mySVMform、libsvmForm、shortForm等形式的向量表示
	 * @param docTermFile
	 * @param docVecFileString
	 * @param form
	 */
	public static void getDocVecFromQiefenText(String dictFile,String docTermFile,String docVecFileString,String form){
		HashMap<String,Integer> wordList=new HashMap<String,Integer>();
		int FeatureNum=0;
			
			//read the wordlist into hashmap and make every word map a unique number.
		try{
			String word;
			BufferedReader reader=new BufferedReader(new InputStreamReader (new FileInputStream(dictFile),Constant.encoding));
			while((word=reader.readLine())!=null){
				if (word.length()==0)
					continue;
				if(word.matches("[0-9]+"))
						continue;
					wordList.put(word, FeatureNum);
					FeatureNum++;
				}
				System.out.println(FeatureNum);
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		System.out.println("Feature num:"+FeatureNum);
		//read docTerm.txt to get the doc vector
		try{
			BufferedWriter out=null;
	    	File docVecFile=new File(docVecFileString);
	    	 if (!docVecFile.exists()) {
	    		 docVecFile.createNewFile();
	    	 }
	        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docVecFile),Constant.encoding));  
	        String termString;
			BufferedReader reader=new BufferedReader(new InputStreamReader (new FileInputStream(docTermFile),Constant.encoding));
			while((termString=reader.readLine())!=null){
				int[] docArray=new int[FeatureNum];
				for(int i=0;i<FeatureNum;i++)
					docArray[i]=0;
				String[] docTermList=termString.split("\t");
				Integer pos=null;
				for(String term:docTermList){
					if((pos=wordList.get(term.trim().toLowerCase())) != null){
						docArray[pos]++;
					}
				}
				
				//write to docVec.txt
				String docVec="";
				String className="";
					
				if(form.equalsIgnoreCase("shortForm")){
					docVec=shortForm(docArray);
				}else if(form.equalsIgnoreCase("libSvm")){
					docVec=libsvmForm(className, docArray);
				}else if(form.equalsIgnoreCase("mySvm")){
					docVec=mySvmForm(className, docArray);
				}
				out.write(docVec);
			}
			out.close();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 根据字典将输入的POI单字文本转化为mySVMform、libsvmForm、shortForm等形式的向量表示
	 * @param segmentResult  POI单字字符串
	 * @param docVecFileString 输出的文本位置
	 * @param form
	 */
	public static void getDocVecFromSegmentString(String dictPath,String segmentResult,String docVecFileString,String form){
		HashMap<String,Integer> wordList=DictGenerator.getWordList(dictPath);
		int FeatureNum=wordList.size();
		System.out.println("Feature num:"+FeatureNum);
		
		//read docTerm.txt to get the doc vector
		try{
			BufferedWriter out=null;
	    	File docVecFile=new File(docVecFileString);
	    	 if (!docVecFile.exists()) {
	    		 docVecFile.createNewFile();
	    	 }
	        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docVecFile),Constant.encoding));  
			int[] docArray=new int[FeatureNum];
			for(int i=0;i<FeatureNum;i++)
				docArray[i]=0;
			String[] docTermList=segmentResult.split("\t");
			Integer pos=null;
			for(String term:docTermList){
				if((pos=wordList.get(term.trim().toLowerCase())) != null){
					docArray[pos]++;
				}
			}
				
			//write to docVec.txt
			String docVec="";
			String className="-1";
				
			if(form.equalsIgnoreCase("shortForm")){
				docVec=shortForm(docArray);
			}else if(form.equalsIgnoreCase("libSvm")){
				docVec=libsvmForm(className, docArray);
			}else if(form.equalsIgnoreCase("mySvm")){
				docVec=mySvmForm(className, docArray);
			}
			out.write(docVec);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static String mySvmForm(String className,int[] docArray){
		String docVec="";
		if(className!="")
			docVec+=className+"\t";
		
		for(Integer x:docArray){
			docVec+=x.toString()+"\t";
		}
		docVec+="\n";
		return docVec;
	}
	private static String libsvmForm(String className,int[] docArray){
		String docVec="";
		if(className!="")
			docVec+=className+"\t";
		
		for(int i=0;i<docArray.length;i++){
			if(docArray[i]!=0){
				docVec+=String.valueOf(i+1)+":"+String.valueOf(docArray[i])+" ";
			}
		}
		docVec+="\n";
		return docVec;
	}
	private static String shortForm(int[] docArray){
		String docVec="";
		for(int i=0;i<docArray.length;i++){
			if(docArray[i]!=0){
				//写多次该维向量
				for(int j=0;j<docArray[i];j++){
					docVec+=String.valueOf(i+1)+" ";
				}
			}
		}
		if(docVec==""){
			System.out.println("zero");
			//return "";  即使是空值，也写到文件中，和类别进行对应，这样的话比较容易找到对应的类别是哪个。
		}
		docVec+="-1"+"\n";
		return docVec;
	}
	public static void main(String[] args){
		//System.out.println(System.getProperty("java.class.path")+"\n"+System.getProperty("user.dir"));
		//String s = "test中d文dsaf中男大3443n中国43中国人0ewldfls=103NO.津007";  
		//getDocVecFromText("D:\\Project\\Java\\PoiClassify\\tmp\\data\\dict.txt", SingleWordSegment.seg(s), "D:\\Project\\Java\\PoiClassify\\tmp\\data\\vec.txt", "libSvm");
		getDocVecFromQiefenText("","D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\tianjin test\\nav_切分.txt", "D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\tianjin test\\docVec.txt", "libsvm");
	}
}
