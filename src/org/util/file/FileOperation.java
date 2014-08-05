package org.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.util.Constant;

/**
 * this class is used to operate file, split file to two or three file </br>
 * or merge them together </br>
 * @author Victor
 *
 */
public class FileOperation {
	
	/**
	 * 程序用于将两列数据形式如
	 * “红名村，地名
	 * 红民村，地名”
	 * 的文件分割成两个文件，第一个文件都是名称，第二个文件都是类别名称
	 * @param path
	 * @param fileName
	 * @param separator
	 * @param firstFile
	 * @param secondFile
	 */
	public static void splitTwoColumn(String infileName,String separator,String firstFile,String secondFile){
		try{
			BufferedReader   in   =   new   BufferedReader(new InputStreamReader (new FileInputStream(infileName),Constant.encoding)); // new   BufferedReader(new InputStreamReader (new FileInputStream(infileName),encoding));  
			BufferedWriter out1=null,out2=null;
			File f1=new File(firstFile);
			if(!f1.exists()){
				f1.createNewFile();
			}
			File f2=new File(secondFile);
			if(!f2.exists()){
				f2.createNewFile();
			}
			out1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f1),Constant.encoding));
			out2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2),Constant.encoding));
			
			String str;
			int index=0;
			while((str=in.readLine())!=null){
				index=str.lastIndexOf(separator);
				out1.write(str.substring(0, index)+"\n");
				out2.write(str.substring(index+1)+"\n");
			}
			out1.close();
			out2.close();
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 将两个文件merge成一个文件 
	 * @param classFile
	 * @param vecFile
	 * @param separator
	 * @param resultFile
	 */
	public static void merge(String classFile,String vecFile,String separator,String resultFile){
		try{
			BufferedWriter out=null;
			File f=new File(resultFile);
			if(!f.exists()){
				f.createNewFile();
			}
			out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),Constant.encoding));
			BufferedReader inClass,inVec;
			inClass=new BufferedReader(new InputStreamReader (new FileInputStream(classFile),Constant.encoding));
			inVec=new BufferedReader(new InputStreamReader (new FileInputStream(vecFile),Constant.encoding));
			String className,vec,resultString;
			while((className=inClass.readLine())!=null){
				vec=inVec.readLine();
				if(vec!=null){
					resultString=className+separator+vec+"\n";
					out.write(resultString);
				}
			}
			out.close();
			inClass.close();
			inVec.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
