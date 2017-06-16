package com.secretchat.tools.common;

/**
 * 获取报警描述 
 * 位置基础信息的数据格式之 报警标志 见表18 （808协议）
 * @author smachen
 *
 */
public enum AlarmEnum {
    A0("紧急报警，触动报警开关后触发",0),A1("超速预警",1),A2("疲劳驾驶预警",2),
    A3("危险预警",3),A4("GNSS模块发生故障",4),A5("GNSS天线未接或被剪断",5),A6("GNSS天线短路",6),
    A7("终端主电源欠压",7),A8("终端主电源掉电",8),A9("终端LCD或显示器故障",9),
    A10("TTS模块故障",10),A11("摄像头故障",11),A12("道路运输证IC卡模块故障",12),
    A13("超速预警",13),A14("疲劳驾驶预警",14),
    A15("",15),A16("",16),A17("",17),
    A18("当天累计驾驶超时",18), A19("超时停车",19),A20("进出区域",20),A21("进出线路",21),
    A22("路段行驶时间不足/过长",22),A23("路线偏离报警",23),A24("车辆VSS故障",24),
    A25("车辆油量异常",25),A26("车辆被盗（通过车辆防盗器）",26),A27("车辆非法点火",27),
    A28("车辆非法位移",28),A29("碰撞预警",29),A30("侧翻预警",30),A31("非法开门报警",31),
    A38("非运行时动车",38);
    
    private String name;
    private int value;
    private AlarmEnum(String name,int value){
    	this.name = name;
    	this.value = value;
    }
    
    public static String getName(int value) {
    	for (AlarmEnum v : AlarmEnum.values()) {
			if(v.getValue() == value) {
				return v.getName();
			}
		}
    	return "报警状态不存在";
    }
    public static String getName(String value) {
    	for (AlarmEnum v : AlarmEnum.values()) {
			if(String.valueOf(v.getValue()).equals(value)) {
				return v.getName();
			}
		}
    	return "报警状态不存在";
    }
    public static String valueOf(int value) {
		for (AlarmEnum v : AlarmEnum.values()) {
			if(v.getValue() == value) {
				return v.getName();
			}
		}
    	return "报警状态不存在";
	}
    
    // get set 方法
 	public String getName() {
 		return name;
 	}
 	public void setName(String name) {
 		this.name = name;
 	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
