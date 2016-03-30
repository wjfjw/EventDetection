package edu.wiki.main;

public class Concept
{
	public int id;			//concept的id
	public double value;	//vector中concept的数值
	public String name;		//concept的名字
	
	
	public Concept(int id , double value , String name)
	{
		this.id = id;
		this.value = value;
		this.name = name;
	}
	
	@Override public boolean equals(Object other)
	{
		if(this == other)	return true;
		if(other == null)	return false;
		if(this.getClass() != other.getClass())
			return false;
		Concept temp = (Concept)other;
		return id == temp.id;
	}
}
