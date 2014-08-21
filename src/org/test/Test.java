package org.test;

import java.io.File;
import org.service.MapClass;
import org.service.svm.svm_predict;
import org.service.textPreProcess.DictGenerator;
import org.service.textPreProcess.Segment2Vector;
import org.service.textPreProcess.TextSegment;
import org.util.Criteria;
import org.util.file.FileOperation;


 enum Function {  
	  Transform, Guess,QuietGuess
	  /** Transform means 转换文本为向量，并生成特征词典，
	   * Guess是对有类别标签的数据进行预测和评估，quietGuess是没有类别标签的数据进行预测
	   **/
	   
	} 
public class Test {

	public static void main(String args[]){
		try{
		int i;
		String path="";
		String oriFileName="";
		Function ff=null;
		for(i=0;i<args.length;i++)
		{
			if(args[i].charAt(0) != '-') break;
			++i;
			
			switch(args[i-1].charAt(1))
			{
				case 'p'://path 
					path = args[i];
					break;
				case 'n':
					oriFileName = args[i];
					break;
				case 't':
					ff=Function.Transform;
					i--;
					break;
				case 'g':
					ff=Function.Guess;
					i--;
					break;
				case 'q':
					//TODO need to add this function later.
					
					ff=Function.QuietGuess;
					i--;
					break;
				default:
					System.err.print("Unknown option: " + args[i-1] + "\n");
					exit_with_help();
			}
		}

        String confPath=path+File.separator+"conf";
        {	
        	File file = new File(confPath); 
        	if(file!=null&&!file.exists()){ 
        		file.mkdirs(); 
        	}
        }
        String resultPath=path+File.separator+"result";
        {
        	File file = new File(resultPath); 
	        if(file!=null&&!file.exists()){ 
	        	file.mkdirs(); 
	        }
        }
		String oriFile=path+File.separator+oriFileName;
		//confPath
		String dictFile=confPath+File.separator+"dict.txt";
		String modelFile=confPath+File.separator+"final.model";
		String mapFile=confPath+File.separator+"map.txt";
		
		//resultPath
		String textFile=resultPath+File.separator+"nav_name.txt";
		String separateFile=resultPath+File.separator+"segment.txt";
		String vecFile=resultPath+File.separator+"docVec.txt";
		String classFile=resultPath+File.separator+"className.txt";
		String numFile=resultPath+File.separator+"classNum.txt";
		String libsvmFile=resultPath+File.separator+"libsvm.txt";
		String resultNumFile=resultPath+File.separator+"resultNum.txt";
        String predictClassNameFile=resultPath+File.separator+"predictClassName.txt";
        String compareFile=resultPath+File.separator+"compare.csv";
    	String assessmentFile=resultPath+File.separator+"assessment.csv";
    	
		if(ff==Function.Transform){
//			FileOperation.splitTwoColumn(oriFile, ",", textFile, classFile);
//			//deal with name 
//			TextSegment.segment(textFile, separateFile);
			//DictGenerator.createDict(separateFile, dictFile);
			Segment2Vector.getDocVecFromQiefenText(dictFile,separateFile, vecFile, "libsvm");

			//deal with class 
//			MapClass t=new MapClass();
//
//			t.Class2Num(classFile, numFile);
			FileOperation.merge(numFile, vecFile, "\t", libsvmFile,"");
		}else if(ff==Function.Guess){
			//FileOperation.splitTwoColumn(oriFile, ",", textFile, classFile);
			//deal with name 
			//TextSegment.segment(textFile, separateFile);
			Segment2Vector.getDocVecFromQiefenText(dictFile,separateFile, vecFile, "libsvm");
			//deal with class 
			MapClass t=new MapClass();
			t.Class2Num(classFile, numFile);
			FileOperation.merge(numFile, vecFile, "\t", libsvmFile,"");
	    	
	    	
		   	String[] testArgs = {libsvmFile, modelFile, resultNumFile};//directory of test file, model file, result file  
	        svm_predict.main(testArgs); 
	        

	        //read predict result and according to map to get its className;
	        
	        MapClass.getClassFromNum(mapFile, resultNumFile,predictClassNameFile);
	        FileOperation.merge(oriFile, predictClassNameFile, ",", compareFile,"POI名称,实际类别,预测类别");

	    	Criteria.calCriteria(numFile, resultNumFile, assessmentFile);
		}else{
			exit_with_help();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	private static void exit_with_help()
	{
		System.err.print("Usage:java -jar textProcess.jar [options] \n"
		+"options:\n"
		+"-p :directory contains the file waiting for process \n"
		+"-n :file name which need to process \n"
		//+"-t :transform the file to libsvm form and create dict.txt in addition.\n"
		+"-g :guess the class of data and compare with oriClass, and assess criteria on all classes.\n"
		);//+"-q :no class already assign to name \n");
		System.exit(1);
	}
}
