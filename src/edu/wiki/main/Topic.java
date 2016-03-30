package edu.wiki.main;

import java.util.ArrayList;

public class Topic 
{
	//conceptList与conceptNumList的下标相对应
	public ArrayList<Concept> conceptList;
	public ArrayList<Integer> conceptNumList;
	
	public ArrayList<Title> titleList;
	
	public Topic()
	{
		conceptList = new ArrayList<Concept>();
		conceptNumList = new ArrayList<Integer>();
		titleList = new ArrayList<Title>();
	}
	
	public void addConcepts(ArrayList<Concept> concepts)
	{
		boolean flag = false;
		for(Concept c1 : concepts)
		{
			for(int i=0 ; i<conceptList.size() ; ++i)
			{
				if(c1.equals(conceptList.get(i)))
				{
					IncConceptNum(i);
					break;
				}
			}
			if( !flag )
			{
				conceptList.add(c1);
				conceptNumList.add(1);
			}
		}
	}
	
	public void IncConceptNum(int i)
	{
		int temp = conceptNumList.get(i) + 1;
		conceptNumList.set(i,temp);
	}
	
	public void addTitle(Title t)
	{
		titleList.add(t);
	}
	
	
}
