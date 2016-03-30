package edu.wiki.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class DBSCAN 
{
	private int n;							//新闻标题的个数
	private ArrayList<Title> titleList;		//新闻标题
	private double [][] similarity;			//新闻标题的相关度矩阵
	private int minPts;
	private double eps;
	private int titleToConceptNum;
	
	public DBSCAN(int minPts , double eps , ArrayList<Title> titleList , int titleToConceptNum)
	{
		this.minPts = minPts;
		this.eps = eps;
		this.titleList = titleList;
		this.n = titleList.size();
		similarity = new double [n][n];
		this.titleToConceptNum = titleToConceptNum;
	}
	
	private void init()
	{
		int i,j;
		for(i=0 ; i<n ; ++i)
			for(j=0 ; j<n ; ++j)
				similarity[i][j] = 0;
	}
	
	/**
	 * 计算概念与概念之间的相关度矩阵
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void calSimilarity() throws ClassNotFoundException, SQLException, IOException
	{
		//ESASearcher searcher = new ESASearcher();
		init();
		int i,j;
		int intersectNum,unionNum;
		for( i=0 ; i<n ; ++i )
		{
			for( j=0 ; j<=i ; ++j )
			{
				//计算新闻标题之间的相关度
				intersectNum = 0;
				unionNum = 0;
				if(i==j)
					similarity[i][j] = 1;
				else
				{
					/*****************************************
					//采用ESA将概念当做文本计算相关度
					similarity[i][j] = searcher.getVectorRelatedness(titleList.get(i).vector, titleList.get(j).vector);
					similarity[j][i] = similarity[i][j];
					*******************************************/
					
					// 采用新闻标题的概念列表之间的Jaccard系数计算相关度
					for(Concept c1 : titleList.get(i).conceptList)
					{
						for(Concept c2 : titleList.get(j).conceptList)
						{
							if( c1.equals(c2) )
							{
								++intersectNum;
								break;
							}	
						}
					}
					//用Jaccard系数计算相关度
					unionNum = titleToConceptNum*2 - intersectNum;
					similarity[i][j] = (double)intersectNum / (double)unionNum;
					similarity[j][i] = similarity[i][j];
				}
				
				//将eps邻域内的点加入arrayList（除了本身）
				if( similarity[i][j]>=eps && i!=j )
				{
					titleList.get(i).neighbours.add(j);
					titleList.get(j).neighbours.add(i);
				}
			}
		}
		//判断每一个对象是否为核心对象
		for(i=0 ; i<n ; ++i)
		{
			if( titleList.get(i).neighbours.size() >= minPts )
				titleList.get(i).iscore = true;
		}
	}
	
	public void work()
	{
		//noise为cid==0的点
		Queue <Integer> queue  = new LinkedList<Integer>();
		int i,j;
		int Cid = 0;
		for( i=0 ; i<n ; ++i )
		{
			if( !titleList.get(i).vis )
			{
				titleList.get(i).vis = true;
				if( titleList.get(i).iscore )
				{
					++Cid;
					titleList.get(i).cid = Cid;
					queue.offer(i);
					while( !queue.isEmpty() )
					{
						int x = queue.poll().intValue();
						for( Integer temp : titleList.get(x).neighbours )
						{
							j = temp.intValue();
							if( !titleList.get(j).vis )
							{
								titleList.get(j).vis = true;
								if( titleList.get(j).iscore )
									queue.offer(j);
							}
							if( titleList.get(j).cid == 0 )
								titleList.get(j).cid = Cid;
						}
					}
				}
			}
		}
	}
	
}
