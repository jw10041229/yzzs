package com.huimv.yzzs.db.helper;

public enum TableFieldEnum {

	
	
	/**
	 * 疾病登记字段
	 */
	JBDJ_ID(0), // id
	JBDJ_LQID(1), // lqid
	JBDJ_DWEB(2), // dweb
	JBDJ_DQTW(3), // dqtw
	JBDJ_QZSJ(4), 
	JBDJ_JBZL(5),
	JBDJ_YYQK(6);
	
	private final int value;

	TableFieldEnum(int val) {
		this.value = val;
	}

	public int getValue() {
		return value;
	}
}
