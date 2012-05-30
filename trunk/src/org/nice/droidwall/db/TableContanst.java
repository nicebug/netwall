package org.nice.droidwall.db;

public class TableContanst {
	public static final String BLACK_NUMBER_TABLE = "number";
	public static final String BLACK_SMS_TABLE = "sms";
	public static final String DIRTY_DATA = "dirty_data";
    public static final class NumberColumns {
        public static final String ID = "_id";
        public static final String NUMBER = "number";
    }
    
    public static final class SMSColunms{
    	public static final String ID = "_id";
    	public static final String NUMBER = "number";
    	public static final String MODIFY_TIME = "modify_time";
    }
    
    public static final class DirtyColunms{
    	public static final String ID = "_id";
    	public static final String DIRTY_DATA = "dirty_data";
    	public static final String MODIFY_TIME = "modify_time";
    }
}
