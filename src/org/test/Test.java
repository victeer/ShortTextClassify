package org.test;

import java.io.File;
import org.service.MapClass;
import org.service.textPreProcess.DictGenerator;
import org.service.textPreProcess.Segment2Vector;
import org.service.textPreProcess.TextSegment;
import org.util.file.FileOperation;



public class Test {
	public static void main(String args[]){
		if(args.length!=2){
			System.out.println("Usage:java -jar test.jar < path> <poi and class file>");
			System.exit(0);
		}
		String path=args[0];
		String oriFileName=args[1];
		String oriFile=path+File.separator+oriFileName;
		String textFile=path+File.separator+"nav_name.txt";
		String separateFile=path+File.separator+"segment.txt";
		String dictFile=path+File.separator+"dict.txt";
		String vecFile=path+File.separator+"docVec.txt";
		String classFile=path+File.separator+"className.txt";
		String numFile=path+File.separator+"classNum.txt";
		String libsvmFile=path+File.separator+"libsvm.txt";
		FileOperation.splitTwoColumn(oriFile, ",", textFile, classFile);
		//deal with name 
		TextSegment.segment(textFile, separateFile);
		DictGenerator.createDict(separateFile, dictFile);
		Segment2Vector.getDocVecFromQiefenText(dictFile,separateFile, vecFile, "libsvm");

		//deal with class 
		MapClass t=new MapClass();

		t.Class2Num(classFile, numFile);
		FileOperation.merge(numFile, vecFile, "\t", libsvmFile);
		
	}
}
