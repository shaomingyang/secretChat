package com.secretchat.tools.common;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.util.IPAddressUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 * 通用工具类,功能：
 *  	获取报文校验码，
 *  	获取GPS坐标和百度坐标，
 *  	获取地址，
 *  	数组拼接,
 *  	GPS数据转义
 * @author smachen
 * 
 */
public class ToolUtils {
	private static Logger logger = LoggerFactory.getLogger(ToolUtils.class);
	
	/**
	 * 获取方向 
	 * @param value 角度
	 * @return
	 */
	public static String getDirection(String value) {
		int v = TypeUtils.getInt(value);
		return getDirection(v);
	}
	public static String getDirection(int v) {
		String direction = "";
		if (v == 0 || v == 360) {
			direction = "正北";
		} else if (v > 0 && v < 90) {
			direction = "东北";
		} else if (v == 90) {
			direction = "正东";
		} else if (v > 90 && v < 180) {
			direction = "东南";
		} else if (v == 180) {
			direction = "正南";
		} else if (v > 180 && v < 270) {
			direction = "西南";
		} else if (v == 270) {
			direction = "正西";
		} else if (v > 270 && v < 360) {
			direction = "西北";
		}
		return direction;
	}
	
	/**
	 * 处理经纬度，精确到百万分之一
	 * @param value
	 * @return
	 */
	public static String setBigDecimals(int value,String accuracy){
		BigDecimal bigVal = new BigDecimal(value);
    	return TypeUtils.getString(bigVal.divide(new BigDecimal(accuracy)));
	}
	public static BigDecimal setBigDecimal(int value,String accuracy){
		BigDecimal bigVal = new BigDecimal(value);
    	return bigVal.divide(new BigDecimal(accuracy));
	}
	
	/**
	 * 根据经纬度获取地址信息，调用百度API
	 * http://api.map.baidu.com/geocoder/v2/?ak=ajL89Y42fnCP2iOzcqwypfgZ2SSkTQic&callback=renderReverse&location=lat,lng&output=json&pois=0
	 * http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
	 * @param lng 经度
	 * @param lat 纬度
	 * @return
	 */
	public static String getBaiduAddress(String lng, String lat) {
		StringBuffer address = new StringBuffer();
		try {
			String url = "http://api.map.baidu.com/geocoder/v2/?ak=ajL89Y42fnCP2iOzcqwypfgZ2SSkTQic&callback=renderReverse&location=lat,lng&output=json&pois=0";
			url = url.replace("lng", lng).replace("lat", lat);
			String json = HttpRequest.sendPost(url, "");
			if (null != json && null != json) {
				// 去除非json格式数据
				json = json.replace("renderReverse&&renderReverse(", "");
				json = json.substring(0, json.lastIndexOf(")"));
				JSONObject object = JSON.parseObject(json);

				JSONObject objectResult = JSON.parseObject(object.get("result").toString());
				address.append(objectResult.getString("formatted_address"));
				address.append(",离"	+ objectResult.getString("sematic_description"));
				// 暂时不需要
				// address.append(",所在商圈："+objectResult.getString("business"));
			}
		} catch (Exception e) {
			return address.toString();
		}
		return address.toString();
	}
	
	/**
	 * 根据GPS经纬度获取百度经纬度 ，调用百度API(现在使用)
	 * http://api.map.baidu.com/geoconv/v1/?coords=lng,lat&from=1&to=5&ak=ajL89Y42fnCP2iOzcqwypfgZ2SSkTQic
	 * @param lng 经度
	 * @param lat 纬度
	 */
	public static Map<String, Object> getBaiDuLngLat(String lng, String lat) {
		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("x", "0");
		returnData.put("y", "0");
		try {
			String url = "http://api.map.baidu.com/geoconv/v1/?coords=lng,lat&from=1&to=5&ak=ajL89Y42fnCP2iOzcqwypfgZ2SSkTQic";
			url = url.replace("lng", lng).replace("lat", lat);
			String json = HttpRequest.sendPost(url, "");
			JSONObject object = JSON.parseObject(json);
			if ("0".equals(TypeUtils.getString(object.get("status")))) {
				JSONArray objectResult = JSON.parseArray(object.get("result").toString());
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) objectResult.get(0);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnData;
		}
		return returnData;
	}
	
	/**
	 * 合并字节数组
	 * 
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(List<byte[]> rest) {
		int totalLength = 0;
		for (byte[] array : rest) {
			if (array != null) {
				totalLength += array.length;
			}
		}
		byte[] result = new byte[totalLength];
		int offset = 0;
		for (byte[] array : rest) {
			if (array != null) {
				System.arraycopy(array, 0, result, offset, array.length);
				offset += array.length;
			}
		}
		return result;
	}
	/**
	 * 合并字节数组
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		for (byte[] array : rest) {
			if (array != null) {
				totalLength += array.length;
			}
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			if (array != null) {
				System.arraycopy(array, 0, result, offset, array.length);
				offset += array.length;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * GP消息拼接并转义消息
	 * 
	 * @param headerAndBody
	 * @param checkSum
	 * @return
	 * @throws Exception
	 * 
	 */
	public static byte[] doEncode(byte[] headerAndBody, int checkSum) throws Exception {
		byte[] noEscapedBytes = ToolUtils.concatAll(Arrays.asList(//
				new byte[] { GpsConstants.delimiter }, // 0x7e
				headerAndBody, // 消息头+ 消息体
				ParseBytes.integerTo1Bytes(checkSum), // 校验码
				new byte[] { GpsConstants.delimiter }	// 0x7e
		));
		// 转义
		return ToolUtils.doEscape4Send(noEscapedBytes, 1, noEscapedBytes.length - 2, true);
	}
	/**
	 * 
	 * GPS发送消息时转义<br>
	 * 
	 * <pre>
	 *  0x7e <====> 0x7d02
	 *  0x7d <====> 0x7d01
	 * </pre>
	 * 
	 * @param bs         要转义的字节数组
	 * @param start      起始索引
	 * @param end        结束索引
	 * @return  转义后的字节数组
	 * @throws Exception
	 */
	public static byte[] doEscape4Send(byte[] bs, int start, int end, boolean escape) throws Exception {
		if (start < 0 || end > bs.length)
			throw new ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
					+ ",end=" + end + ",bytes length=" + bs.length + ")");
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			for (int i = 0; i < start; i++) {
				baos.write(bs[i]);
			}
			for (int i = start; i < end; i++) {
				if (escape) {
					if (bs[i] == 0x7e) {
						baos.write(0x7d);
						baos.write(0x02);
					} else if (bs[i] == 0x7d) {
						baos.write(bs[i]);
						baos.write(0x01);
					} else {
						baos.write(bs[i]);
					}
				} else {
					if (bs[i] == 0x7d && (i+1) < end && bs[i+1] == 0x02) {
						baos.write(0x7e);
						i++;
					} else if (bs[i] == 0x7d && (i+1) < end && bs[i+1] == 0x01) {
						baos.write(0x7d);
						i++;
					} else {
						baos.write(bs[i]);
					}
				}
				
			}
			for (int i = end; i < bs.length; i++) {
				baos.write(bs[i]);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		}
	}
	
	/**
	 * BCC获取报文的校验码
	 * @param bs	 	报文数据
	 * @param start		开始索引位置
	 * @param end		结束索引位置
	 * @return
	 */
	public static int getCheckCodeBCC(byte[] bs, int start, int end) {
		if (start < 0 || end > bs.length)
			throw new ArrayIndexOutOfBoundsException("getVerCodeBCC error : index out of bounds(start=" + start
					+ ",end=" + end + ",bytes length=" + bs.length + ")");
		int cs = 0;
		for (int i = start; i < end; i++) {
			cs ^= bs[i];
		}
		return cs;
	}
	
	
	/**
	 * 校验手机号
	 * /*^匹配开始地方$匹配结束地方，[3|4|5|7|8]选择其中一个{4,8},\d从[0-9]选择 
     * {4,8}匹配次数4~8    ，java中/表示转义，所以在正则表达式中//匹配/,/匹配""
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobile(String mobile) {  
		boolean tre=false;
        if (mobile.matches("^1[3|4|5|7|8][0-9]\\d{4,8}$")){  
            tre = true;
        } else {
        	tre = false;
        	logger.info("手机号输入有误或为空，请重新输入");  
		}
        return tre;  
    }  
	
	/**
	 * 
	 * 判断IP是否是内网
	 * 
	 * @param ip IP地址字符串
	 * @return
	 */
	public static boolean internalIp(String ip) {
        byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
        return internalIp(addr);
    }
    public static boolean internalIp(byte[] addr) {
    	try {
    		final byte b0 = addr[0];
            final byte b1 = addr[1];
            //127.0.0.1
            final byte SECTION_0 = 0x7F;
            //10.x.x.x/8
            final byte SECTION_1 = 0x0A;
            //172.16.x.x/12
            final byte SECTION_2 = (byte) 0xAC;
            final byte SECTION_3 = (byte) 0x10;
            final byte SECTION_4 = (byte) 0x1F;
            //192.168.x.x/16
            final byte SECTION_5 = (byte) 0xC0;
            final byte SECTION_6 = (byte) 0xA8;
            switch (b0) {
            	case SECTION_0:
            		return true;
                case SECTION_1:
                    return true;
                case SECTION_2:
                    if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                        return true;
                    }
                case SECTION_5:
                    switch (b1) {
                        case SECTION_6:
                            return true;
                    }
                default:
                    return false;

            }
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("有问题的IP{}地址",IPAddressUtil.convertFromIPv4MappedAddress(addr));
		}
		return false;
    }
    
	public static void main(String[] args) {
		System.out.println(internalIp("192.168.0.55"));
	}
}
