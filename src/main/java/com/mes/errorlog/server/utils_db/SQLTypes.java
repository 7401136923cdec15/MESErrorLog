package com.mes.errorlog.server.utils_db;

public enum SQLTypes {

	Default(0, "默认"),
	Mysql(1, "Mysql"), 
	SqlServer(2, "SqlServer"),
	Orecle(3, "Orecle"),
	Access(4, "Access");

	private int value;
	private String lable;

	private SQLTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SQLTypes getEnumType(int val) {
		for (SQLTypes type : SQLTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Mysql;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
