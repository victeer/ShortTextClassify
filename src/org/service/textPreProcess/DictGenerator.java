package org.service.textPreProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.util.Constant;
/**
 * The DictGenerator is used to get the dictionary of</br>
 * total corpus by reading the text from textSement </br>  
 * result file and filtering same words , strange </br>
 * symbols  and single character like A,x... then </br>
 * write them to a new file named like "dict.txt".
 * @author Victor
 *
 */
public class DictGenerator {
	public static HashMap<String, Integer> getWordList(String dictPath) {
		HashMap<String,Integer> wordList=new HashMap<String,Integer>();
		int FeatureNum=0;	
			//read the wordlist into hashmap and make every word map a unique number.
		try{
			String word;
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(dictPath),Constant.encoding));
			while((word=reader.readLine())!=null){
				if (word.length()==0)
					continue;
				if(word.matches("[0-9]+"))
						continue;
					wordList.put(word, FeatureNum);
					FeatureNum++;
				}
				//System.out.println(FeatureNum);
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		return wordList;
	}

	/**
	 * 将POI名称切分成的一行一行的单字读入文件中，筛选掉重复的单字，得到poi中出现的所有单字的词典，包括英文单词和汉语单字。
	 * @param infileName
	 * @param outfile
	 */
	public static void createDict(String infileName,String outfile) {
        File file = new File(infileName);
        BufferedReader reader = null;
        Set<String> wordSet = new HashSet<String>();
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader =new BufferedReader(new InputStreamReader(new FileInputStream(file),Constant.encoding));
            String tempString = null;
            
            while ((tempString = reader.readLine()) != null) {
            	String [] tmp=tempString.split("\t");//test when \t\t what is the result
            	
            	for (String x:tmp){
            		if(x.length()!=0 
            				&&!x.matches("[_+\\-&|!,(){}\\[\\]‘“”’^/\"~*?:·.@'%]+")
            				//&&!x.matches("[a-zA-Z]") this maybe useful for those name as  " D.D.K.S"
            				&&!wordSet.contains(x.toLowerCase())){
            			wordSet.add(x.toLowerCase());
            			System.out.println(x.toLowerCase());
            		}
            	}
            }
            System.out.println("WordSet"+wordSet.size());
            
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile),Constant.encoding)); 
            for(String t:wordSet){
            	bw.write(t+"\n");
            }
            bw.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
