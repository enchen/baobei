/**
 * 
 */
package cn.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import cn.beans.ArticleIndex;
import cn.beans.EPicture;


/**
 * @author chenen
 *
 */
public class ElasticServices {

	public boolean CreateIndex()
	{
		return false;//下一步需求
	}
	public static List<ArticleIndex> queryByMatch(String matchString)
	{
		List<ArticleIndex> al=new ArrayList<ArticleIndex>();
		Client client=StaticClient.getClient();
		SearchResponse response = client
				
				.prepareSearch("dp")
		        .setTypes("article")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.matchQuery("content", matchString))                 // Query
		        .addHighlightedField("content") 
		         .setHighlighterFragmentSize(2000)
                 .setHighlighterNumOfFragments(1)
		        // Filter
		       .setFrom(0).setSize(200).setExplain(true)
		        .execute()
		        .actionGet();
		if(response!=null)
		{
			
			  SearchHit[] results = response.getHits().getHits();
		       // System.out.println("Current results: " + results.length);
		        int i=1;
		        for (SearchHit hit : results) {
		            //System.out.println("------------------------------");
		            Map<String,Object> result = hit.getSource();   
		            try
		            {
		            ArticleIndex ai=new ArticleIndex();
		            ai.setFileid(hit.id());
		            ai.setContent(i+": "+hit.getHighlightFields().get("content").toString().replace("[content], fragments[[", "").replace("]]", ""));
		           // ai.setContent(result.get(i+": "+"content").toString());
		         i++;
		         ai.setPath(result.get("path").toString());
		         ai.setFilename(result.get("filename").toString());
		         ai.setUpdatetime(result.get("updatetime").toString());
		            al.add(ai);
		            }catch(Exception e)
		            {
		            	System.out.print(e.getMessage());
		            }
		        }
		}
		return al;
	}
	
	public static List<EPicture> queryPicByMatch(String matchString)
	{
		List<EPicture> ep=new ArrayList<EPicture>();
		Client client=StaticClient.getClient();
		SearchResponse response = client
				
				.prepareSearch("dp2")
		        .setTypes("picture")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.matchQuery("describle", matchString))                 // Query
		        //.addHighlightedField("describle") 
		         //.setHighlighterFragmentSize(2000)
                 //.setHighlighterNumOfFragments(1)
		        // Filter
		       .setFrom(0).setSize(200).setExplain(true)
		        .execute()
		        .actionGet();
		if(response!=null)
		{
			
			  SearchHit[] results = response.getHits().getHits();
		       // System.out.println("Current results: " + results.length);
		       
		        for (SearchHit hit : results) {
		            //System.out.println("------------------------------");
		            Map<String,Object> result = hit.getSource();   
		            try
		            {
		            	EPicture ai=new EPicture();
		            ai.setDescrible(result.get("describle").toString());
		            ai.setHeight(result.get("height").toString());
		            ai.setPicpath(result.get("picpath").toString());
		            ai.setWidth(result.get("width").toString());
		            ep.add(ai);
		           // al.add(ai);
		            }catch(Exception e)
		            {
		            	System.out.print(e.getMessage());
		            }
		        }
		}
		return ep;
	}

}
