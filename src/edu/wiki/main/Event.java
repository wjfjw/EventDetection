package edu.wiki.main;

import java.util.ArrayList;

import gnu.trove.TLinkable;

public class Event 
{
	//occurrenceDates与effectList、titleListList的下标相对应
	private ArrayList<DateTime> occurrenceDates;
	private ArrayList<Double> effectList;
	private ArrayList< ArrayList<Title> > titleListList;
	
	//conceptList与conceptNumList的下标相对应
	private ArrayList<Concept> conceptList;
	private ArrayList<Integer> conceptNumList;
	
	public Event()
	{
		occurrenceDates = new ArrayList<DateTime>();
		effectList = new ArrayList<Double>();
		titleListList = new ArrayList< ArrayList<Title> >();
		
		conceptList = new ArrayList<Concept>();
	}
	
	public void addConceptNumList( ArrayList<Integer> conceptNumList)
	{
		this.conceptNumList = conceptNumList;
	}
	
	public int getSumConceptNum()
	{
		int sum = 0;
		for(Integer count : conceptNumList)
			sum += count;
		return sum;
	}
	
	public int getConceptNum(int i)
	{
		return conceptNumList.get(i);
	}
	
	public ArrayList<Concept> getConceptList()
	{
		return conceptList;
	}
	
	public DateTime getOccurrenceDate(int i)
	{
		return occurrenceDates.get(i);
	}
	
	public ArrayList<Title> getTitleList(int i)
	{
		return titleListList.get(i);
	}
	
	public double getEffect(int i)
	{
		return effectList.get(i);
	}
	
	public int getLastDays()
	{
		return occurrenceDates.size();
	}
	
	public void addDate(int year , int month , int day)
	{
		occurrenceDates.add( new DateTime(year, month, day) );
	}
	
	public void addEffect(double effect)
	{
		effectList.add(effect);
	}
	
	public void incEffect(double inc)
	{
		int lastIndex = effectList.size()-1;
		double sum = effectList.get(lastIndex) + inc;
		effectList.set(lastIndex, sum);
	}
	
	public void addConceptList(ArrayList<Concept> concepts)
	{
		if(concepts == null)
			return;
		for(Concept c : concepts)
		{
			if( !conceptList.contains(c) )
				conceptList.add(c);
		}
	}
	
	public void addTitleList(ArrayList<Title> titleList)
	{
		if(titleList == null)
			return;
		titleListList.add(titleList);
	}
	
	public void addToLastTitleList(ArrayList<Title> titleList)
	{
		int lastIndex = titleListList.size()-1;
		ArrayList<Title> lastTitleList = titleListList.get(lastIndex);
		for(Title t : titleList)
		{
			lastTitleList.add(t);
		}
	}
	
}
