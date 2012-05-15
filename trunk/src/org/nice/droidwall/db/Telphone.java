package org.nice.droidwall.db;

import java.io.Serializable;

public class Telphone implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 575799216564277892L;
	
	private long _id;
	private String number;
	private String modifyDateTime;
	
	public Telphone(String number) {
		this.number = number;
	}

	public Telphone(long _id, String number, String modifyDateTime) {
		super();
		this._id = _id;
		this.number = number;
		this.modifyDateTime = modifyDateTime;
	}
	
	public Telphone() {
	}
	public Telphone(long _id, String number) {
		super();
		this._id = _id;
		this.number = number;
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

	public String getModifyDateTime()
	{
		return modifyDateTime;
	}

	public void setModifyDateTime(String modifyDateTime)
	{
		this.modifyDateTime = modifyDateTime;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_id ^ (_id >>> 32));
		result = prime * result
				+ ((modifyDateTime == null) ? 0 : modifyDateTime.hashCode());
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
		Telphone other = (Telphone) obj;
		if (_id != other._id)
			return false;
		if (modifyDateTime == null)
		{
			if (other.modifyDateTime != null)
				return false;
		}
		else
			if (!modifyDateTime.equals(other.modifyDateTime))
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
		return "Telphone [_id=" + _id + ", number=" + number
				+ ", modifyDateTime=" + modifyDateTime + "]";
	}
	
	
	
}
