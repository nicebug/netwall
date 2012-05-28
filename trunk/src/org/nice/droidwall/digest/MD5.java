package org.nice.droidwall.digest;

import java.security.MessageDigest;

public class MD5 {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",  
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };  
	
	public MD5(){
	}
	
	public String encodeByMD5(String msg){
		if (msg != null && msg.length() > 0)
		{
			try
			{
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] results = md.digest(msg.getBytes());
				String resultStr = byteArrayToHexString(results);
				return resultStr.toUpperCase();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return "";
	}

	private String byteArrayToHexString(byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < b.length; i++)
		{
			sb.append(byteToHexString(b[i]));
		}
		return sb.toString();
	}

	private String byteToHexString(byte b)
	{
		int n = b;
		if (n < 0)
			n = 256 +n;
		int d1 = n / 16;
		int d2 = n % 16;
		
		return hexDigits[d1] + hexDigits[d2];
	}
}
