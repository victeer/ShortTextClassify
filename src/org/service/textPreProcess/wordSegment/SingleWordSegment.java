package org.service.textPreProcess.wordSegment;

import java.io.IOException;

public class SingleWordSegment {
	/**
	 * 通过输入字符串，将字符串转化为一个一个的汉字，英文单词要在一起不进行切分。
	 * @param oriStr
	 * @return
	 */
	public static String seg(String oriStr){
		String s;
		s=oriStr.replaceAll("(?i)No\\.", "-Num-").replaceAll("[0-9]+", "-nn-");
//        System.out.println(s);
        int index=0;
        
        char[] buff=new char[s.length()*3+1];
        char[] dstChar=new char[s.length()] ;//= s.toCharArray();
        s.getChars(0, s.length(), dstChar, 0);
        for (int j = 0; j < dstChar.length; j++) 
        {
        	if(dstChar[j] <= 0x1F || dstChar[j] >= 0x30 && dstChar[j] <= 0x39 || dstChar[j]>= 0x41 && dstChar[j] <= 0x5A || dstChar[j] >= 0x61 && dstChar[j]<= 0x7A )//|| ch == 0x2D || ch == 0x27
            {
                buff[index++]=dstChar[j] ;
        		//[a-zA-Z0-9]   //-'
        		//System.out.print(dstChar[j]+ "\t");
            }else if(dstChar[j]==0x20){
            	buff[index++]='\t';
            }else{
            	buff[index++]='\t';
            	buff[index++]=dstChar[j];
            	buff[index++]='\t';
            }
        }
        
//        buff[index] = '\0';
        return String.valueOf(buff).trim();
	}
	public static void main(String args[]) throws IOException {
        String s = "test中d文dsaf中男大3443n中国43中国人0ewldfls=103NO.津007";
        System.out.println(seg(s));
    }
}
