package com.mes.errorlog.server.utils_db;
 

public  class DataBaseTypes {
	public static final int Default = 0;
	public static final int MES = 1;
	public static final int DMS = 2;
	public static final int EXC = 3;
	public static final int ERP = 4;
	public static final int Data = 5;

	public static String GetDateBaseName(int filed) {
		String wResult = "";
		switch (filed) {
		case DataBaseTypes.Default:
			wResult = "iplantmes";
			break;
		case DataBaseTypes.MES:
			wResult = "iplantmes";
			break;
		case DataBaseTypes.DMS:
			wResult = "iplantdms";
			break;
		case DataBaseTypes.EXC:
			wResult = "iplantexc";
			break;
		case DataBaseTypes.ERP:
			wResult = "iplanterp";
			break;
		case DataBaseTypes.Data:
			wResult = "iplantmds";
			break;

		default:
			wResult = "iplantmes";
		}
		return wResult;
	}
}
