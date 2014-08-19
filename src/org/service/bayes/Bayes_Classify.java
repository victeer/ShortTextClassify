package org.service.bayes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.service.textPreProcess.DictGenerator;
import org.util.Constant;



import libsvm.svm_node;

/**
 * @Reference:http://blog.jqian.net/post/classification.html#content 
 * @author Victor
 *
 */
public class Bayes_Classify {
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
	public void train(BufferedReader input, DataOutputStream output){
		try{
			int lineCount=0;
			while(true)
			{
				String line = input.readLine();
				if(line == null) break;
				lineCount++;
				System.out.println(lineCount);
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
					condProb[i][j]=Math.log((1+condProb[i][j]))+classTermFreq[j];
				}
			}
			//write to file 
			store_parameter(output);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void store_parameter(DataOutputStream output){
		try{
			//write classPro 
			output.writeBytes("classPro\n");
			String tmp="";
			for(double x:classProb){
				tmp+=x+" ";
			}
			tmp+="\n";
			output.writeBytes(tmp);
			
			//write condProb
			output.writeBytes("condProb\n");
			for(int i=0;i<totalTermCount;i++){
				tmp="";
				for(int j=0;j<Constant.CLASS_NUM;j++){
					tmp+=condProb[i][j]+" ";
				}
				tmp+="\n";
				output.writeBytes(tmp);	
			}
			//write classTermFreq
			output.writeBytes("classTermFreq\n");
			tmp="";
			for(double x:classTermFreq){
				tmp+=x+" ";
			}
			tmp+="\n";
			output.writeBytes(tmp);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public double[] predict(svm_node[] instance){
		double[] score=new double[Constant.CLASS_NUM];
		for(int i=0;i<Constant.CLASS_NUM;i++){
			score[i]=classProb[i];
			for(svm_node n:instance){
				if(n.index<totalTermCount){
					score[i]+=n.value*condProb[n.index][i];
				}else{//has some word not in the list 
					score[i]+=n.value*classTermFreq[i];
				}
			}
		}
		return score;
	}
	//private ArrayList<HashSet<Integer>> classTerm;
	private double [] classProb;
	private double [][] condProb;
	private double [] classTermFreq;
	private HashMap<String,Integer> wordlist;
	private int totalTermCount;
}
