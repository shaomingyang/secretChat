package com.secretchat.tools.common;

public class GwConstants {
	// 起始符
	public static final String delimiter = "##";				// 标识位
	
	//0x01：数据不加密；0x02：数据经过RSA算法加密；0x03:数据经过AES128位算法加密；“0xFE”表示异常，“0xFF”表示无效，其他预留。
	public static final int CRYPTO = 0x01;		
	
	// 命令标识
	public static final int cmd_car_login = 0x01;				// 车辆登入
	public static final int cmd_car_out = 0x04;					// 车辆登出
	public static final int cmd_terminal_login = 0x05;			// 平台登入
	public static final int cmd_terminal_out = 0x06;			// 平台登出
	public static final int cmd_real_info = 0x02;				// 实时信息上报
	public static final int cmd_reis_info = 0x03;				// 补发信息上报
	public static final int cmd_heat = 0x07;					// 心跳
	
	
	// 应答标志
	public static final int resp_sign_success = 0x01;			//成功
	public static final int resp_sign_fail = 0x02;				//失败
	public static final int resp_sign_vin = 0x03;				//VIN重复
	public static final int resp_sign_cmd = 0xfe;				//命令
	
	// 信息类型标志
	/*public static final int db_type_all = 0x01;					//整车数据
	public static final int db_type_drive_motor = 0x02;			//驱动电机数据
	public static final int db_type_fuel_cell = 0x03;			//燃料电池数据
	public static final int db_type_transmitter = 0x04;			//发动机数据
	public static final int db_type_location = 0x05;			//车辆位置数据
	public static final int db_type_extreme = 0x06;				//极值数据
	public static final int db_type_alarm = 0x07;				//报警数据
	public static final int db_type_voltage = 0x08;				//可充电储能装置电压数据
	public static final byte db_type_temperature = 0x09;		//可充电储能装置温度数据
*/}
