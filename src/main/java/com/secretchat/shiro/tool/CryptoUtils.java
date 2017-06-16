package com.secretchat.shiro.tool;

import java.security.Key;
import java.security.MessageDigest;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;


public class CryptoUtils {
	/**对上传的密码进行MD5加密
	 * @param txt 明文
	 * @return
	 */
	public static String encryptMD5(String txt) {  
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
	    try {  
	    	byte[] btInput = txt.getBytes();  
	    	//获得MD5摘要算法的 MessageDigest 对象  
	        MessageDigest mdInst = MessageDigest.getInstance("MD5");  
	        //使用指定的字节更新摘要  
	        mdInst.update(btInput);  
	        //获得密文  
	        byte[] md = mdInst.digest();  
	        //把密文转换成十六进制的字符串形式  
	        int len = md.length;  
	        char str[] = new char[len * 2];  
	        int k = 0;  
	        for (int i = 0; i < len; i++) {  
	        	byte byte0 = md[i];  
	            str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
	            str[k++] = hexDigits[byte0 & 0xf];  
	        }  
	        return new String(str);  
	    } catch (Exception e) {  
	    	e.printStackTrace();  
	        return null;  
	    }  
	}
	
	/**
	 * 加密密码字符串
	 * 数据经过AES128位算法加密
	 * @param decrypt 待加密字符串
	 */
	public static String encryptAES128(String decrypt) {
		AesCipherService aesCipher = new AesCipherService();
		aesCipher.setKeySize(128); // 设置key长度
		// 生成key
		Key key = aesCipher.generateNewKey();
		// 加密
		String encrypt = aesCipher.encrypt(decrypt.getBytes(), key.getEncoded()).toHex();
		return encrypt;
	}

	/**
	 * 解密密码字符串
	 * 数据经过AES128位算法解密
	 * @param encrypt 待解密字符串
	 */
	public static String decryptAES128(String encrypt) {
		AesCipherService aesCipher = new AesCipherService();
		aesCipher.setKeySize(128); // 设置key长度
		// 生成key
		Key key = aesCipher.generateNewKey();
		// 解密
		String decrypt = new String(aesCipher.decrypt(Hex.decode(encrypt), key.getEncoded()).getBytes());
		return decrypt;
	}
	
	
	/**
	 * 0x01：数据不加密；0x02：数据经过RSA算法加密；0x03:数据经过AES128位算法加密；“0xFE”表示异常，“0xFF”表示无效，其他预留。
	 * @param type
	 * @param encrypt
	 */
	public String decryptData(int type,String encrypt) {
		// TODO Auto-generated method stub
		String decrypt = encrypt;
		if (type == 0x02) {
			
		} else if (type == 0x03) {
			
		}
		
		return decrypt;
	}
}
