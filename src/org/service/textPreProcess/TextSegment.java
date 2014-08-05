package org.service.textPreProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.service.textPreProcess.wordSegment.SingleWordSegment;
import org.util.Constant;

/**
 * this class is for text segment, so that the text is </br>
 * divided as you want, single Chinese word or term, </br>
 * and write the segment result to a new file for next use.</br> 
 * @author Victor
 *
 */
public class TextSegment {
	public static void segment(String inFileName,String outFileName){

		try{
			
			String word;
			BufferedReader reader=new BufferedReader(new InputStreamReader (new FileInputStream(inFileName),Constant.encoding));

			BufferedWriter out=null;
	    	File docVecFile=new File(outFileName);
	    	 if (!docVecFile.exists()) {
	    		 docVecFile.createNewFile();
	    	 }
	        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docVecFile),Constant.encoding));  
			
			while((word=reader.readLine())!=null){
//				System.out.println(word);
				out.write(SingleWordSegment.seg(word)+"\n");
			}
			out.close();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
