package org.nice.droidwall.db;

import java.io.Serializable;

public class SMS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5214373561776645612L;
	
	private long _id;
	private String number;
	private String modify_time;
	
	
	
	
	public SMS() {
		super();
	}

	
	public SMS(long _id, String number) {
		super();
		this._id = _id;
		this.number = number;
	}


	public SMS(String number) {
		super();
		this.number = number;
	}
	
	public SMS(long _id, String number, String modify_time) {
		super();
		this._id = _id;
		this.number = number;
		this.modify_time = modify_time;
	}
	public long get_id()
	{
		return _id;
	}
	public void set_id(long _id)
	{
		this._id = _id;
	}
	public String getNumber()
	{
		return number;
	}
	public void setNumber(String number)
	{
		this.number = number;
	}
	public String getModify_time()
	{
		return modify_time;
	}
	public void setModify_time(String modify_time)
	{
		this.modify_time = modify_time;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_id ^ (_id >>> 32));
		result = prime * result
				+ ((modify_time == null) ? 0 : modify_time.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SMS other = (SMS) obj;
		if (_id != other._id)
			return false;
		if (modify_time == null)
		{
			if (other.modify_time != null)
				return false;
		}
		else
			if (!modify_time.equals(other.modify_time))
				return false;
		if (number == null)
		{
			if (other.number != null)
				return false;
		}
		else
			if (!number.equals(other.number))
				return false;
		return true;
	}


	@Override
	public String toString()
	{
		return "SMS [_id=" + _id + ", number=" + number + ", modify_time="
				+ modify_time + "]";
	}
	
	

}
