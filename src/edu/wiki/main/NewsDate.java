package edu.wiki.main;

import java.util.*;

public class NewsDate 
{
	private ArrayList<Title> titleList;
	private ArrayList<Topic> topicList;
	private DateTime dateTime;
	private static Comparator<Title> cmp;
	
	static
	{
		cmp = new Comparator<Title>() 
		{
			public int compare(Title o1, Title o2) {
				return o1.cid - o2.cid;
			}
		};
	}
	
	public NewsDate(int year , int month , int day)
	{
		this.dateTime = new DateTime(year, month, day);
		titleList = new ArrayList<Title>();
		topicList = new ArrayList<Topic>();
	}
	
	public ArrayList<Title> getTitleList()
	{
		return titleList;
	}
	
	public ArrayList<Topic> getTopicList()
	{
		return topicList;
	}
	
	public DateTime getDateTime()
	{
		return dateTime;
	}
	
	public void addTitle(Title t)
	{
		titleList.add(t);
	}
	
	/**
	 * 获取当天的总新闻标题数
	 * @return 总新闻标题数
	 */
	public int getTitleNum()
	{
		return titleList.size();
	}
	
	public void createTopicList()
	{
		titleList.sort(cmp);
		int clusterID;
		for(int i=0 ; i<titleList.size() ; ++i)
		{
			clusterID = titleList.get(i).cid;
			if(clusterID > 0)
			{
				Topic tp = new Topic();
				while( i<titleList.size() && clusterID==titleList.get(i).cid )
				{
					tp.addTitle(titleList.get(i));
					tp.addConcepts(titleList.get(i).conceptList);
					++i;
				}
				this.topicList.add(tp);
				--i;
			}
		}
	}
	
}
