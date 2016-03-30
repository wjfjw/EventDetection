package edu.wiki.util;

import java.io.*;
import java.util.*;

import java.nio.file.Paths;

public class ReadData 
{
	private ArrayList<NewsTitle> nt;
	
	public ReadData() 
	{
		nt = new ArrayList<NewsTitle>();
	}
	
	private void addTitle(String line)
	{
		String yearStr = line.substring(8, 12);
		String monthStr = line.substring(2,4);
		String dayStr = line.substring(5, 7);
		int titleIndex = 15;
		if( line.charAt(15)==' ' )
			++titleIndex;
		String title = line.substring(titleIndex);
		
		//去掉2005年的数据
		if(yearStr.equals("2005"))
			return;
		
		//如果title中包含"http"，则去掉"http"的部分
		int noiseIndex = title.indexOf("http");
		if(noiseIndex == 0)
			return;
		else if(noiseIndex > 0)
			title = title.substring(0, noiseIndex-1);
		
		//过滤字符串前后的空格
		title = title.trim();
		
		//过滤一些空的title和一些无用字符串
		if(title.equals("") || title.equals("[-]</a>"))
			return;
		
		int year = Integer.parseInt(yearStr);
		int month = Integer.parseInt(monthStr);
		int day = Integer.parseInt(dayStr);
		nt.add( new NewsTitle(title, year, month, day) );
	}
	
	private boolean isLineBegin(String line)
	{
		if( line.substring(0,2).equals("T,") )
			return true;
		else
			return false;
	}
	
	public void readFile() throws IOException
	{
		try {
			
			File parentFile = new File("./data");
			String [] fileName = parentFile.list();
			String preLine = null;
			String curLine = null;
			for(String file : fileName)
			{
				if(file.length() < 41)
					continue;
				//去除2005年的新闻
				if( file.substring(37, 41).equals("2005") )
					continue;
				
				Scanner input = new Scanner(Paths.get("./data/" + file));
				while(input.hasNextLine())
				{
					preLine = curLine;
					curLine = input.nextLine();
					if( preLine==null || !isLineBegin(preLine) )
						continue;
					//preline是一条信息的开始
					else
					{
						if( curLine != null )
						{
							if( !isLineBegin(curLine) )
								preLine += curLine;
							addTitle(preLine);
						}		
					}
				}
				if( isLineBegin(curLine) )
					addTitle(curLine);
				
				input.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}
	
	public void output() throws FileNotFoundException
	{
		Comparator<NewsTitle>  cmp = new Comparator<NewsTitle>() 
		{
			public int compare(NewsTitle o1, NewsTitle o2) {
				if(o1.year == o2.year)
				{
					if(o1.month == o2.month)
					{
						return o1.day - o2.day;
					}
					else
						return o1.month - o2.month;
				}
				else
					return o1.year - o2.year;
			}
		};
		
		nt.sort(cmp);
		
		PrintWriter out = new PrintWriter("./result/orderedData2");
		int i,j,tempday,lasti;
		int n = nt.size();
		tempday = nt.get(0).day;
		lasti = 0;
		for(i=0 ; i<n ; ++i)
		{
			if( nt.get(i).day != tempday )
			{
				NewsTitle temp = nt.get(lasti);
				out.printf("%d %d %d\n", temp.year, temp.month, temp.day);
				out.println(i-lasti);
				for(j=lasti ; j<i ; ++j)
				{
					out.println(nt.get(j).title);
				}
				out.println();
				tempday = nt.get(i).day;
				lasti = i;
			}
		}
		out.close();
	}
	
	
	public static void main(String[] args) throws IOException
	{
		ReadData r = new ReadData();
		r.readFile();
		r.output();
	}
}
