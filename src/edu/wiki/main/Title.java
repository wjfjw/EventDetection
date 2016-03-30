package edu.wiki.main;

import java.util.ArrayList;

public class Title 
{
	//基本属性
	public String content;					//新闻标题内容
	public ArrayList<Concept> conceptList;	//对应的语义概念列表
	
	//DBSCAN聚类的属性
	public int cid;							//title聚类后的聚簇类别
	public boolean iscore;					//是否为核心对象
	public boolean vis;						//是否被访问过
	public ArrayList<Integer> neighbours;	//邻域中的对象列表
	
	
	public Title(String content)
	{
		this.content = content;
		
		conceptList = new ArrayList<Concept>();
		this.cid = 0;
		this.iscore = false;
		this.vis = false;
		this.neighbours = new ArrayList<Integer>();
	}
	
	public void addConcept(Concept c)
	{
		this.conceptList.add(c);
	}
	
	@Override public boolean equals(Object other)
	{
		if(this == other)	return true;
		if(other == null)	return false;
		if(this.getClass() != other.getClass())
			return false;
		Title temp = (Title)other;
		return content.equals(temp.content);
	}

}
