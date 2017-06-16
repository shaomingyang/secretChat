package com.secretchat.tools.common;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Byte解析操作工具类
 * 
 * @author smachen
 *
 */
public class ParseBytes {
	private static Logger logger = LoggerFactory.getLogger(ParseBytes.class);

	/**
	 * 将byte数组转换成int
	 * 主要处理BYTE,WORD,DWORD 数据类型
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	  处理数组的长度
	 * @return
	 */
	public static int parseIntFromBytes(byte[] data, int startIndex, int length) {
		return parseIntFromBytes(data, startIndex, length, 0);
	}
	/**
	 * 将byte数组转换成int，默认值0
	 * 主要处理BYTE,WORD,DWORD 数据类型
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	  处理数组的长度
	 * @param defaultVal 默认值
	 * @return
	 */
	public static int parseIntFromBytes(byte[] data, int startIndex,
			int length, int defaultVal) {
		try {
			// 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
			final int len = length > 4 ? 4 : length;
			byte[] tmp = new byte[len];
			System.arraycopy(data, startIndex, tmp, 0, len);

			// 把byte[]转化位整形,通常为指令用
			int result;
			if (tmp.length == 1) {
				result = oneByteToInteger(tmp[0]);
			} else if (tmp.length == 2) {
				result = twoBytesToInteger(tmp);
			} else if (tmp.length == 3) {
				result = threeBytesToInteger(tmp);
			} else if (tmp.length == 4) {
				result = fourBytesToInteger(tmp);
			} else {
				result = fourBytesToInteger(tmp);
			}
			return result;
		} catch (Exception e) {
			logger.error("解析整数出错:{}", e.getMessage());
			e.printStackTrace();
			return defaultVal;
		}
	}

	/**
	 * 将bcd数组转换成字符串
	 * 主要处理 BCD 数据类型
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	 处理数组的长度
	 * @return
	 */
	public static String parseBcdStringFromBytes(byte[] data, int startIndex,
			int lenth) {
		return parseBcdStringFromBytes(data, startIndex, lenth, null);
	}
	/**
	 * 将bcd数组转换成字符串，默认值null
	 * 主要处理 BCD 数据类型
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	  处理数组的长度
	 * @param defaultVal 默认值
	 * @return
	 */
	public static String parseBcdStringFromBytes(byte[] data, int startIndex,
			int lenth, String defaultVal) {
		try {
			byte[] tmp = new byte[lenth];
			System.arraycopy(data, startIndex, tmp, 0, lenth);
			return bcd2String(tmp);
		} catch (Exception e) {
			logger.error("解析BCD(8421码)出错:{}", e.getMessage());
			e.printStackTrace();
			return defaultVal;
		}
	}
	/**
	 * 将String数组转换成字符串
	 * 主要处理 STRING 数据类型
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	  处理数组的长度
	 * @return
	 */
	public static String parseStringFromBytes(byte[] data, int startIndex,
			int lenth) {
		return parseStringFromBytes(data, startIndex, lenth, null);
	}
	/**
	 * 将String数组转换成字符串，默认值null
	 * 主要处理 STRING 数据类型
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	  处理数组的长度
	 * @param defaultVal 默认值
	 * @return
	 */
	public static String parseStringFromBytes(byte[] data, int startIndex,
			int lenth, String defaultVal) {
		try {
			byte[] tmp = new byte[lenth];
			System.arraycopy(data, startIndex, tmp, 0, lenth);
			return new String(tmp, Constants.string_charset);
		} catch (Exception e) {
			logger.error("解析字符串出错:{}", e.getMessage());
			e.printStackTrace();
			return defaultVal;
		}
	}
	/**
	 * 将国标协议时间处理成字符串
	 * 主要处理BYTE[6]日期数据
	 * @param data       待处理数据
	 * @param startIndex 起始下标
	 * @param length	  处理数组的长度
	 * @return
	 */
	public static String parseDateFromBytes(byte[] data, int startIndex, int length) {
		byte[] tmp = new byte[length];
		System.arraycopy(data, startIndex, tmp, 0, length);
		
		if (tmp.length == 6) {
			int year = ParseBytes.parseIntFromBytes(tmp, 0, 1);
			int month = ParseBytes.parseIntFromBytes(tmp, 1, 1);
			int day = ParseBytes.parseIntFromBytes(tmp, 2, 1);
			int hour = ParseBytes.parseIntFromBytes(tmp, 3, 1);
			int min = ParseBytes.parseIntFromBytes(tmp, 4, 1);
			int sec = ParseBytes.parseIntFromBytes(tmp, 5, 1);
			
			String time = handTime(year)+handTime(month)+handTime(day)+handTime(hour)+handTime(min)+handTime(sec);
			return DateUtils.format(time, "yyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
		}else {
			logger.info("要处理日期{}格式不正确",tmp);
			return "";
		}
	}
	private static String handTime(int num) {
		return num < 10 ? "0"+num : num+"";
	}
	/**
	 * 将时间处理成国标协议byte
	 * @param date  待处理时间，如果是空值，则是当前时间
	 * @return
	 * @throws ParseException
	 */
	public static byte[] dateToBytes(String date) {
		byte[] tmp = new byte[6];
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");//设置日期格式
		Date cDate;
		try {
			cDate = df.parse(df.format(new Date()));
			if (!TypeUtils.isEmpty(date)) {
				cDate = df.parse(date);
			}
			cal.setTime(cDate);
			int cyear = Integer.parseInt(new SimpleDateFormat("yy",Locale.CHINESE).format(cDate));
			int cmonth= cal.get(Calendar.MONTH)+ 1;
			int cdate = cal.get(Calendar.DATE);
			int chour = cal.get(Calendar.HOUR);
			int cminu = cal.get(Calendar.MINUTE);
			int csec  = cal.get(Calendar.SECOND);
			
			tmp[0]=integerTo1Byte(cyear);
			tmp[1]=integerTo1Byte(cmonth);
			tmp[2]=integerTo1Byte(cdate);
			tmp[3]=integerTo1Byte(chour);
			tmp[4]=integerTo1Byte(cminu);
			tmp[5]=integerTo1Byte(csec);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmp;
	}
	
	/**
	 * 把一个byte转化位整形,通常为指令用
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static int oneByteToInteger(byte value) {
		return (int) value & 0xFF;
	}

	/**
	 * 把一个2位的数组转化位整形
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static int twoBytesToInteger(byte[] value) {
		int temp0 = value[0] & 0xFF;
		int temp1 = value[1] & 0xFF;
		return ((temp0 << 8) + temp1);
	}

	/**
	 * 把一个3位的数组转化位整形
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static int threeBytesToInteger(byte[] value) {
		int temp0 = value[0] & 0xFF;
		int temp1 = value[1] & 0xFF;
		int temp2 = value[2] & 0xFF;
		return ((temp0 << 16) + (temp1 << 8) + temp2);
	}

	/**
	 * 把一个4位的数组转化位整形,通常为指令用
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static int fourBytesToInteger(byte[] value) {
		int temp0 = value[0] & 0xFF;
		int temp1 = value[1] & 0xFF;
		int temp2 = value[2] & 0xFF;
		int temp3 = value[3] & 0xFF;
		return ((temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
	}

	/**
	 * BCD字节数组===>String
	 * 
	 * @param bytes
	 * @return 十进制字符串
	 */
	public static String bcd2String(byte[] bytes) {
		StringBuilder temp = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			// 高四位
			temp.append((bytes[i] & 0xf0) >>> 4);
			// 低四位
			temp.append(bytes[i] & 0x0f);
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * 
	 * 把一个整形该为byte 把一个整形该为1位的byte数组
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static byte integerTo1Byte(int value) {
		return (byte) (value & 0xFF);
	}

	public static byte[] integerTo1Bytes(int value) {
		byte[] result = new byte[1];
		result[0] = (byte) (value & 0xFF);
		return result;
	}

	/**
	 * 把一个整形改为2位的byte数组
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static byte[] integerTo2Bytes(int value) {
		byte[] result = new byte[2];
		result[0] = (byte) ((value >>> 8) & 0xFF);
		result[1] = (byte) (value & 0xFF);
		return result;
	}

	/**
	 * 把一个整形改为3位的byte数组
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static byte[] integerTo3Bytes(int value) {
		byte[] result = new byte[3];
		result[0] = (byte) ((value >>> 16) & 0xFF);
		result[1] = (byte) ((value >>> 8) & 0xFF);
		result[2] = (byte) (value & 0xFF);
		return result;
	}

	/**
	 * 把一个整形改为4位的byte数组
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static byte[] integerTo4Bytes(int value) {
		byte[] result = new byte[4];
		result[0] = (byte) ((value >>> 24) & 0xFF);
		result[1] = (byte) ((value >>> 16) & 0xFF);
		result[2] = (byte) ((value >>> 8) & 0xFF);
		result[3] = (byte) (value & 0xFF);
		return result;
	}

	/**
	 * 二进制开关
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int byteIndex(int value) {
		int index = -1;
		try {
			String binary = Integer.toBinaryString(value);
			byte[] result = binary.getBytes("utf-8");

			for (int i = 0; i < result.length; i++) {
				if (result[i] == 49) {
					index = result.length - 1 - i;
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("获取二进制中开关位错误:{}", e.getMessage());
			return index;
		}
		return index;
	}

	/**
	 * 取二进制数值每一位数值
	 * 
	 * @param num
	 * @param index
	 * @return
	 */
	public static int getBinaryIndex(int value, int index) {
		if (index > 31) {
			logger.info("超过int类型的最大长度");
		}
		return (value & (0x1 << index)) >> index;
	}

	/**
	 * 字符串==>BCD字节数组
	 * 
	 * @param str
	 * @return BCD字节数组
	 */
	public static byte[] string2Bcd(String str) {
		// 奇数,前补零
		if ((str.length() & 0x1) == 1) {
			str = "0" + str;
		}

		byte ret[] = new byte[str.length() / 2];
		byte bs[] = str.getBytes();
		for (int i = 0; i < ret.length; i++) {

			byte high = ascII2Bcd(bs[2 * i]);
			byte low = ascII2Bcd(bs[2 * i + 1]);

			// TODO 只遮罩BCD低四位?
			ret[i] = (byte) ((high << 4) | low);
		}
		return ret;
	}

	private static byte ascII2Bcd(byte asc) {
		if ((asc >= '0') && (asc <= '9'))
			return (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			return (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			return (byte) (asc - 'a' + 10);
		else
			return (byte) (asc - 48);
	}

	/**
	 * 
	 * 字符串消息补全
	 * 
	 * @param str
	 * @param len
	 * @return
	 * 
	 */
	public static byte[] stringToBytes(String str, int len) {
		if (str.getBytes().length > len) {
			logger.error("请检查数据长度是否大于{}", len);
		}
		byte ret[] = new byte[len];
		byte[] bs = str.getBytes();
		System.arraycopy(bs, 0, ret, 0, bs.length);
		return ret;
	}

	public static byte[] double2Bytes(double d, int len) {
		long value = Double.doubleToRawLongBits(d);
		byte[] byteRet = new byte[len];
		for (int i = 0; i < len; i++) {
			byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
		}
		return byteRet;
	}

	public static double bytes2Double(byte[] arr, int len) {
		long value = 0;
		for (int i = 0; i < len; i++) {
			value |= ((long) (arr[i] & 0xff)) << (len * i);
		}
		return Double.longBitsToDouble(value);
	}

}
