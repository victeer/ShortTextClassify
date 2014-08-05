package org.util.string;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
public class ByteHexExchange {
   
	/** Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
    * @param src byte[] data
    * @return hex string
    */   
   public static String bytesToHexString(byte[] src){
       StringBuilder stringBuilder = new StringBuilder("");
       if (src == null || src.length <= 0) {
           return null;
       }
       for (int i = 0; i < src.length; i++) {
           int v = src[i] & 0xFF;
           String hv = Integer.toHexString(v);
           if (hv.length() < 2) {
               stringBuilder.append(0);
           }
           stringBuilder.append(hv);
       }
       return stringBuilder.toString();
   }
   /**
    * Convert hex string to byte[]
    * @param hexString the hex string
    * @return byte[]
    */
   public static byte[] hexStringToBytes(String hexString) {
       if (hexString == null || hexString.equals("")) {
           return null;
       }
       hexString = hexString.toUpperCase();
       int length = hexString.length() / 2;
       char[] hexChars = hexString.toCharArray();
       byte[] d = new byte[length];
       for (int i = 0; i < length; i++) {
           int pos = i * 2;
           d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
       }
       return d;
   }
   /**
    * Convert char to byte
    * @param c char
    * @return byte
    */
    private static byte charToByte(char c) {
       return (byte) "0123456789ABCDEF".indexOf(c);
   }
  //将指定byte数组以16进制的形式打印到控制台
    public static void toHex( byte[] b) {  
       for (int i = 0; i < b.length; i++) { 
         String hex = Integer.toHexString(b[i]&0xFF ); 
         if (hex.length() == 1) { 
           hex = '0' + hex; 
         } 
         System.out.print(hex.toUpperCase() ); 
       } 
       System.out.println();
    }

	public static void encode() { 
        String name = "I am 君山"; 
        toHex(name.toCharArray()); 
        try { 
            byte[] iso8859 = name.getBytes("ISO-8859-1"); 
            toHex(iso8859); 
            byte[] gb2312 = name.getBytes("GB2312"); 
            toHex(gb2312); 
            byte[] gbk = name.getBytes("GBK"); 
            toHex(gbk); 
            byte[] utf16 = name.getBytes("UTF-16"); 
            toHex(utf16); 
            byte[] utf8 = name.getBytes("UTF-8"); 
            toHex(utf8); 
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace(); 
        } 
	}
	private static void toHex(char[] b) {
		// TODO Auto-generated method stub
	       for (int i = 0; i < b.length; i++) { 
	           String hex = Integer.toHexString(b[i] ); 
	           if (hex.length()%2 == 1) { 
	             hex = '0' + hex; 
	           } 
	           System.out.print(hex.toUpperCase() ); 
	         } 
	         System.out.println();
	}
    public static void main(String args[]) throws IOException {
    	encode();
    }
 }
