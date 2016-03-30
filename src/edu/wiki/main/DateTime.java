package edu.wiki.main;

public class DateTime 
{
	public int year;
	public int month;
	public int day;
	
	public DateTime(int year , int month , int day)
	{
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	@Override public boolean equals(Object other)
	{
		if(this == other)	return true;
		if(other == null)	return false;
		if(this.getClass() != other.getClass())
			return false;
		DateTime temp = (DateTime)other;
		return (this.year==temp.year && this.month==temp.month &&this.day==temp.day);
	}
}
