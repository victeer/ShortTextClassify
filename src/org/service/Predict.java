package org.service;
import org.service.svm.*;
import org.service.textPreProcess.Segment2Vector;
import org.service.textPreProcess.wordSegment.SingleWordSegment;
public class Predict {
	public static String predict(String poi_name,String mode,String path) {
		try{
		
		String dict=path+"\\tmp\\data\\dict.txt";
		String vecFile=path+"\\tmp\\data\\vec.txt";
		if(mode.equalsIgnoreCase("SVM")){
			String segmentResult=SingleWordSegment.seg(poi_name);
			Segment2Vector.getDocVecFromSegmentString(dict,segmentResult,vecFile, "libsvm");
		}
		
		String modelFile=path+"\\tmp\\data\\final.model";
    	String numFile=path+"\\tmp\\data\\result.txt";
	   	String[] testArgs = {vecFile, modelFile, numFile};//directory of test file, model file, result file  
        svm_predict.main(testArgs); 
        System.out.println("ok");
        String mapFile=path+"\\tmp\\data\\map.txt";
        //read predict result and according to map to get its className;
        return MapClass.getClassFromNum(mapFile, numFile);
			}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args){
		//System.out.println(predict("美国.UMA(优玛)汽车隔热防爆膜","SVM"));//雪薇诗澜(天津)科技发展公司
	   	//    	String modelFile="C:\\Users\\weiwei\\Documents\\GitHub\\libsvm\\tools\\10000_libsvm.txt.model";
//    	String[] testArgs = {"C:\\Users\\weiwei\\Documents\\GitHub\\libsvm\\tools\\1000_libsvm_test.txt", modelFile, ".\\result.txt"};//directory of test file, model file, result file  
//        svm_predict.main(testArgs); 

	}
}
