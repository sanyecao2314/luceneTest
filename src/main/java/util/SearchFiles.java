package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import gui.MainFrame;

public class SearchFiles {
	private static final Logger logger = Logger.getLogger(SearchFiles.class.getName());
	
	String indexPath;
	int repeat = 0;
	boolean raw = false;
	Analyzer analyzer;
	int hitsPerPage = 300;
	
	public SearchFiles(String indexPath) throws Exception {
		this.indexPath = indexPath;
		analyzer = new SmartChineseAnalyzer();
	}

	public String[][] query(String time0, String time1, String queryString, String field) throws IOException, ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		QueryParser parser = new QueryParser(field, analyzer);

		if (queryString == null || queryString.length() == -1) {
			new Exception("查询不能为空!");
		}
		queryString = queryString.trim();
		if (queryString.length() == 0) {
			new Exception("查询不能为空格!");
		}

		Query query = parser.parse(queryString);
		logger.info("...Searching for: " + query.toString(field));

		if (repeat > 0) {                      // repeat & time as benchmark
			Date start = new Date();
			for (int i = 0; i < repeat; i++) {
				searcher.search(query,100);
			}
			Date end = new Date();
			logger.info("Time: "+(end.getTime()-start.getTime())+"ms");
		}

		ScoreDoc[] hits = doPagingSearch(in, searcher, query, hitsPerPage, raw, queryString == null);
		String[][] result = new String[hitsPerPage > hits.length ? hits.length :hitsPerPage][3];
		
		int index = -1;
		for(int i=0; i < hits.length && index < hitsPerPage-1 ;++i) 
		{
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			String p = d.get("path");
			// judge if the doc is to choose
			boolean fin = true;
			logger.info(p.charAt(13) +" "+ time0.charAt(3) +" "+ time1.charAt(3));
			if(p.charAt(13)>=time0.charAt(3) && p.charAt(13)<=time1.charAt(3))
			{
				int tm0 = (time0.charAt(5) - '0')*10 + (time0.charAt(6) - '0');
				int tm1 = (time1.charAt(5) - '0')*10 + (time1.charAt(6) - '0');
				int m = (p.charAt(15) - '0')*10 + (p.charAt(16) - '0');
				if(m >= tm0 && m <=tm1)
				{
					int td0 = (time0.charAt(8) - '0')*10 + (time0.charAt(9) - '0');
					int td1 = (time1.charAt(8) - '0')*10 + (time1.charAt(9) - '0');
					int day = (p.charAt(18) - '0')*10 + (p.charAt(19) - '0');
					if(tm0 < tm1 && m == tm0 && day >= td0) fin = true;
					if(tm0 < tm1 && m == tm1 && day <= td1) fin = true;
					if(m > tm0 && m < tm1) fin = true; 
					if(m == tm0 && m == tm1)
						if(day >= td0 && day <= td1) fin = true;
				}
			}
			logger.info("fin: "+fin); // for debug use
			if(fin){
				result[index+1][0] = ""+(index+2);
				result[index+1][1] = d.get("title");
				result[index+1][2] = p.substring(10,20);
				logger.info(d.get("path")); // for debug use
				index += 1;
			}
		}
		reader.close();
		if(index == -1) {new Exception("查询无果!");}
		return result;
	}

	private ScoreDoc[] doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
			int hitsPerPage, boolean raw, boolean interactive) throws IOException {
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		logger.info(numTotalHits + " total matching documents");
		return hits;
	}
}