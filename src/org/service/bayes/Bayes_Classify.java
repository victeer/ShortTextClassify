package org.service.bayes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.service.MapClass;
import org.service.textPreProcess.DictGenerator;
import org.util.Constant;
import org.util.file.FileOperation;

import libsvm.svm_node;

/**
 * @Reference:http://blog.jqian.net/post/classification.html#content 
 * @author Victor
 *
 */
public class Bayes_Classify {
	final static double lapalace=0.001;
	Bayes_Classify(String dictPath){
		//classTerm=new ArrayList<HashSet<Integer>> ();
		classProb=new double [Constant.CLASS_NUM];
		wordlist=DictGenerator.getWordList(dictPath);
		totalTermCount=wordlist.size();
		condProb=new double[totalTermCount][Constant.CLASS_NUM];
		classTermFreq=new double[Constant.CLASS_NUM];
	}
	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}
	private void train(BufferedReader input, BufferedWriter output){
		try{
			int lineCount=0;
			while(true)
			{
				String line = input.readLine();
				if(line == null) break;
				lineCount++;
				System.out.println(lineCount);
				StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
	
				int target = atoi(st.nextToken())-1;
				int m = st.countTokens()/2;
				svm_node[] x = new svm_node[m];
				for(int j=0;j<m;j++)
				{
					x[j] = new svm_node();
					x[j].index = atoi(st.nextToken());
					x[j].value = atof(st.nextToken());
				}
				
				classProb[target]+=1;
				for(svm_node tmp:x){
					classTermFreq[target]+=tmp.value;
					condProb[tmp.index-1][target]+=tmp.value;
				}
			}
			//calculate prior probability
			double totalDocNum=0.0;
			for(double d:classProb){
				totalDocNum+=d;
			}
			for(int i=0;i<Constant.CLASS_NUM;i++){
				classProb[i]=Math.log((classProb[i]+1)/(Constant.CLASS_NUM+totalDocNum));
				classTermFreq[i]=-Math.log(classTermFreq[i]+totalTermCount);//also used for those haven't appear in the wordlist
			}
			
			for(int i=0;i<totalTermCount;i++){
				for(int j=0;j<Constant.CLASS_NUM;j++){
					condProb[i][j]=Math.log((lapalace+condProb[i][j]))+classTermFreq[j];//用0.001用于平滑可否？？
				}
			}
			for(int i=0;i<Constant.CLASS_NUM;i++){
				classTermFreq[i]=Math.log(lapalace)+classTermFreq[i];//also used for those haven't appear in the wordlist
			}

			//write to file 
			store_parameter(output);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void store_parameter(BufferedWriter output){
		try{
			//write classPro 
			output.write("classPro\n");
			String tmp="";
			for(double x:classProb){
				tmp+=x+" ";
			}
			tmp+="\n";
			output.write(tmp);
			
			//write condProb
			output.write("condProb\n");
			for(int i=0;i<totalTermCount;i++){
				tmp="";
				for(int j=0;j<Constant.CLASS_NUM;j++){
					tmp+=condProb[i][j]+" ";
				}
				tmp+="\n";
				output.write(tmp);	
			}
			//write classTermFreq
			output.write("classTermFreq\n");
			tmp="";
			for(double x:classTermFreq){
				tmp+=x+" ";
			}
			tmp+="\n";
			output.write(tmp);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void load_parameter(String modelPath) throws Exception{
		BufferedReader input = new BufferedReader(new FileReader(modelPath));
		
		String line=input.readLine();
		if(line!=null && line.equalsIgnoreCase("classPro")){
			line=input.readLine();
			StringTokenizer st = new StringTokenizer(line," \t\n\r\f");
			if(st.countTokens()==classProb.length){
				for(int i=0;i<classProb.length;i++){
					classProb[i]=atof(st.nextToken());
				}
			}
		}
		//read condProb
		line=input.readLine();
		if(line!=null && line.equalsIgnoreCase("condProb")){
			for(int i=0;i<totalTermCount;i++){
				line=input.readLine();
				StringTokenizer st = new StringTokenizer(line," \t\n\r\f");
				if(st.countTokens()==Constant.CLASS_NUM){
					for(int j=0;j<Constant.CLASS_NUM;j++){
						condProb[i][j]=atof(st.nextToken());
					}
				}
				else {
					System.out.println("class_num is not match split result");
					break;
				}
			}
		}
		//classTermFreq
		line=input.readLine();
		if(line!=null && line.equalsIgnoreCase("classTermFreq")){
			line=input.readLine();
			StringTokenizer st = new StringTokenizer(line," \t\n\r\f");
			if(st.countTokens()==classTermFreq.length){
				for(int i=0;i<classTermFreq.length;i++){
					classTermFreq[i]=atof(st.nextToken());
				}
			}
		}
		input.close();
	}
	public double[] predict(svm_node[] instance){
		double[] score=new double[Constant.CLASS_NUM];
		for(int i=0;i<Constant.CLASS_NUM;i++){
			score[i]=classProb[i];
			for(svm_node n:instance){
				if(n.index-1<totalTermCount){//index represent the word that are in the term list 
					score[i]+=(n.value*condProb[n.index-1][i]);
				}else{//has some word not in the list 
					score[i]+=(n.value*classTermFreq[i]);
				}
			}
		}
		return score;
	}
	public void run_train(String trainDataPath,String modelPath) throws Exception{
		BufferedReader input = new BufferedReader(new InputStreamReader (new FileInputStream(trainDataPath),Constant.encoding));
		BufferedWriter output=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelPath),Constant.encoding));
		train(input,output);
		input.close();
		output.close();
	}
	public void run_predict(String testDataPath,String resultPath) throws Exception{
		BufferedReader input = new BufferedReader(new InputStreamReader (new FileInputStream(testDataPath),Constant.encoding));
		BufferedWriter output=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath),Constant.encoding));
		int total=0;
		int correct=0;

		while(true)
		{
			String line = input.readLine();
			if(line == null) break;
			total++;
			System.out.println(total);
			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			int target = atoi(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}
			double[] score=predict(x);
			boolean right=evaluate(target,score,output);
			if(right){
				correct++;
			}
		}
		System.out.println("Accuracy = "+(double)correct/total*100+
				 "% ("+correct+"/"+total+") (classification)\n");
		input.close();
		output.close();
	}
	private boolean evaluate(int target,double[] score,BufferedWriter output) throws Exception{
		HashMap<Double,Integer> scorelist=new HashMap<Double,Integer>();
		for(int i=0;i<score.length;i++){
			scorelist.put(score[i], i+1);// class index start with 1
		}
		ArrayList<Map.Entry<Double,Integer>> l = new ArrayList<Map.Entry<Double,Integer>>(scorelist.entrySet());   
        Collections.sort(l, new Comparator<Map.Entry<Double,Integer>>() {   
            public int compare(Map.Entry<Double,Integer> o1, Map.Entry<Double,Integer> o2) {   
                if(o2.getKey() - o1.getKey() > 0)
                	return 1;
                else
                	return -1;
            }
        }); 
        MapClass map=new MapClass();
        for(Map.Entry<Double,Integer> t:l){
        	String className=map.Num2Class(t.getValue());
        	output.write(className+":"+t.getKey()+",");
        }
        output.write("\n");
        
		if(l.get(0).getValue()==target )//||l.get(1).getValue()==target
			return true;
		else 
			return false;
		
		
	}
	//private ArrayList<HashSet<Integer>> classTerm;
	private double [] classProb;
	private double [][] condProb;
	private double [] classTermFreq;
	private HashMap<String,Integer> wordlist;
	private int totalTermCount;
	public static void main(String[] args) throws Exception{
		String dictPath="D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\tianjin test\\test\\nb\\0.07-1.07万数据\\conf\\dict.txt";
		Bayes_Classify nb_c=new Bayes_Classify(dictPath);
		String modelPath="D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\tianjin test\\test\\nb\\0.07-1.07万数据\\conf\\nb.model";
		String trainDataPath="D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\tianjin test\\test\\nb\\3.0001-5万\\result\\libsvm.txt";
		String resultClassPath="D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\tianjin test\\test\\nb\\3.0001-5万\\result\\resultClass.txt";
		//nb_c.run_train(trainDataPath, modelPath);
		nb_c.load_parameter(modelPath);
		nb_c.run_predict(trainDataPath, resultClassPath);
//		String oriPath="D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\resource\\0.07-1.07万数据\\conf\\0.07-1.07万数据.csv";
//		String comparePath="D:\\实践活动\\项目\\搜狗地图\\POI描述分类\\resource\\0.07-1.07万数据\\result\\nb\\compare.csv";
//		FileOperation.merge(oriPath, resultClassPath, ",", comparePath,"POI名称,实际类别,预测类别");

	}
}
;