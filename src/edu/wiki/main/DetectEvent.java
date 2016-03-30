package edu.wiki.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DetectEvent 
{
	private static Comparator<Concept> cmp;
	
	static
	{
		cmp = new Comparator<Concept>() 
		{
			public int compare(Concept o1, Concept o2) {
				return o1.id - o2.id;
			}
		};
	}
	
	/**
	 * 检测连续或中间断开的事件，将topic和事件作比较，若相似则加入事件
	 * @param dateList 日期列表
	 * @param eventList 事件列表
	 * @param topicSimParam 若两个topic的Jaccard系数大于此参数，则认为这两个topic相等
	 */
	public static void detectAllEvents(ArrayList<NewsDate> dateList , ArrayList<Event> eventList , double topicSimParam, int titleToConceptNum)
	{
		int i,j;
		int n = dateList.size();
		double maxSim,tempSim;
		int maxSimEventID = 0;
		int sumTitleNum = 0;		//每天的总新闻标题数
		
		for(i=0 ; i<n ; ++i)
		{
			DateTime dateTime = dateList.get(i).getDateTime();
			sumTitleNum = dateList.get(i).getTitleNum();
			
			for(Topic t : dateList.get(i).getTopicList())
			{
				//将当前topic与已有的每个事件比较，寻找最相似的事件
				t.conceptList.sort(cmp);
				maxSim = 0;
				for(j=0 ; j<eventList.size() ; ++j)
				{
					tempSim = getConceptListSim(t.conceptList, eventList.get(j).getConceptList());
					if( tempSim > maxSim )
					{
						maxSim = tempSim;
						maxSimEventID = j;
					}
				}
				//如果此topic与事件列表的某一事件相似，则为同一事件
				if( maxSim >= topicSimParam )
				{
					Event e = eventList.get(maxSimEventID);
					//如果当天有多个主题为同一事件，则不加入新的日期
					int lastIndex = e.getLastDays()-1;
					if(dateTime.equals(e.getOccurrenceDate(lastIndex)))
					{
						e.addToLastTitleList(t.titleList);
						e.incEffect( getEffect(t, sumTitleNum) );
					}
					else
					{
						e.addDate(dateTime.year, dateTime.month, dateTime.day);
						e.addTitleList(t.titleList);
						e.addEffect( getEffect(t, sumTitleNum) );
					}
				}
				//否则新建一个事件
				else
				{
					Event e = createEvent(dateTime, t, i);
					e.addEffect( getEffect(t, sumTitleNum) );
					eventList.add(e);
				}
			}
			
			//遍历已经建立的事件，若事件在这一天没有发生，则加入这一天，影响度为0（用于图表显示）
			for(Event e : eventList)
			{
				int lastIndex = e.getLastDays()-1;
				DateTime dt = e.getOccurrenceDate(lastIndex);
				if(!dt.equals(dateTime))
				{
					e.addDate(dateTime.year, dateTime.month, dateTime.day);
					e.addEffect(0);
					e.addTitleList(null);
				}
			}
		}
	}

	
	/**
	 * 获取当天某主题的影响度
	 * @param t topic
	 * @param sumTitleNum 当天的总新闻标题数
	 * @return 影响度
	 */
	private static double getEffect(Topic t, int sumTitleNum)
	{
		return (double)t.titleList.size() / (double)sumTitleNum;
	}
	
	/**
	 * 根据一个topic新建一个事件
	 * @param dateTime 当天的日期
	 * @param t topic
	 * @param dateIndex 日期列表的下标
	 * @return 新建的事件e
	 */
	private static Event createEvent(DateTime dateTime, Topic t, int dateIndex)
	{
		Event e = new Event();
		e.addDate(dateTime.year, dateTime.month, dateTime.day);
		e.addConceptList(t.conceptList);		//一个事件的conceptList为发生第一天的conceptList
		e.addConceptNumList(t.conceptNumList);
		e.addTitleList(t.titleList);
		return e;
	}
	
	
	private static double getConceptListSim(ArrayList<Concept> conceptList1 , ArrayList<Concept> conceptList2)
	{
		int intersectNum = 0;
		int unionNum = 0;
		
		for(Concept c1 : conceptList1)
		{
			if( Collections.binarySearch(conceptList2, c1, cmp) >= 0 )
				++intersectNum;
		}
		unionNum = conceptList1.size() + conceptList2.size() - intersectNum;
		return (double)intersectNum / (double)unionNum;
	}
}
