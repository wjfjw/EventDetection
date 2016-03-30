package edu.wiki.main;

import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import edu.wiki.api.concept.IConceptIterator;
import edu.wiki.api.concept.IConceptVector;
import edu.wiki.search.ESASearcher;
import edu.wiki.util.ConnectDB;

public class Cluster 
{
	private static ArrayList<NewsDate> dateList;
	private static ArrayList<Event> eventList;
	private static final int minPts = 5;
	private static final double eps = 0.5;
	private static final int titleToConceptNum = 5;
	private static final double topicSimParam = 0.6;
	private static final String inputFile = "./result/testData";
	private static final String outputFile = "./result/testResult";
	
	private static Connection conn;
	private static Statement stmtQuery;
	private static ESASearcher searcher;
	
	static
	{
		dateList = new ArrayList<NewsDate>();
		eventList = new ArrayList<Event>();
		try {
			conn = ConnectDB.initDB();
			stmtQuery = conn.createStatement();
			searcher = new ESASearcher();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void output(long startTime) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(outputFile);	//result,testResult
		int n = eventList.size();
		
		//按照事件发生的天数从长到短排序
		eventList.sort(new Comparator<Event>() {
			public int compare(Event o1, Event o2) {
				return o2.getLastDays() - o1.getLastDays();
			}
		});
		
		for(int i=0 ; i<n ; ++i)
		{
			Event e = eventList.get(i);
			
			out.printf("Event %d :\n" , i+1);
			out.println("Dates:");
			for(int j=0 ; j<e.getLastDays() ; ++j)
			{
				DateTime dateTime = e.getOccurrenceDate(j);
				if(e.getEffect(j) > 0)
				{
					out.printf("%d.%d.%d ", dateTime.year , dateTime.month, dateTime.day);
					out.printf("%.3f\t", e.getEffect(j));
				}
			}
			out.printf("\nLast for %d Days\n" , e.getLastDays());
			out.println("Concepts:");
			for(int j=0 ; j<e.getConceptList().size() ; ++j)
			{
				if(j>0)
					out.print(", ");
				out.print(e.getConceptList().get(j).name);
				out.printf("(%d)" , e.getConceptNum(j));
			}
//			for(Concept c : e.getConceptList())
//			{
//				out.print(c.name + ", ");
//			}
			
			out.print("\nRelated newsTitles:");
			for(int j=0 ; j<e.getLastDays() ; ++j)
			{
				if(e.getEffect(j) > 0)
				{
					DateTime dateTime = e.getOccurrenceDate(j);
					out.printf("\n%d %d %d\t", dateTime.year, dateTime.month, dateTime.day);
					out.printf("%.3f\n", e.getEffect(j));
					for(Title t : e.getTitleList(j))
					{
						out.println(t.content);
					}
				}
			}
			out.println();
		}
		
		long endTime = System.currentTimeMillis();
		long sumTime = endTime-startTime;
		out.printf("Time used: %d s\n" , sumTime/1000);
		
		out.close();
	}
	
	
	/**
	 * 得出新闻标题对应的语义概念
	 * @param title
	 * @return 是否能成功将新闻标题转化为语义概念
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private static boolean titleToConcept(Title title) throws IOException, SQLException
	{
		IConceptVector cvBase = searcher.getConceptVector(title.content);
		IConceptVector cv = searcher.getNormalVector(cvBase,titleToConceptNum);

		if(cv == null)
			return false;
		
		IConceptIterator it = cv.orderedIterator();
		
		for(int i=0 ; i<titleToConceptNum && it.next() ; ++i)
		{
			String query = "SELECT title FROM article WHERE id=" + it.getId();
			ResultSet res = stmtQuery.executeQuery(query);
			res.next();
			String name = res.getString(1);
			title.addConcept( new Concept(it.getId(), it.getValue(), name) );
		}
		return true;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException, SQLException
	{
		long startTime = System.currentTimeMillis();
		Scanner input = new Scanner(Paths.get(inputFile));	//orderedData,testData
		int year,month,day,titleNum,i;
		
		while( input.hasNextInt() )
		{
			//读取每一天的日期和新闻标题
			year = input.nextInt();
			month = input.nextInt();
			day = input.nextInt();
			NewsDate date = new NewsDate(year, month, day);
			titleNum = input.nextInt();
			input.nextLine(); 		//读入'\n'
			for(i=0 ; i<titleNum ; ++i)
			{
				Title title = new Title( input.nextLine() );
				if( titleToConcept(title) )
					date.addTitle(title);
			}

			//对新闻标题进行DBSCAN聚类
			DBSCAN dbscan  = new DBSCAN( minPts, eps, date.getTitleList(), titleToConceptNum );
			dbscan.calSimilarity();
			dbscan.work();
			
			//建立主题
			date.createTopicList();
			dateList.add(date);
		}
		input.close();
		conn.close();
		
		//检测事件，相似的topic标为同一号,并建立事件列表
		DetectEvent.detectAllEvents(dateList, eventList, topicSimParam, titleToConceptNum);
		output(startTime);
	}
}
