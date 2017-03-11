package business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.queryparser.classic.ParseException;

import util.IndexFiles;
import util.SearchFiles;

public class LuceneSearch {
	private static final Logger logger = Logger.getLogger(LuceneSearch.class.getName());
	
	SearchFiles sf;
	public LuceneSearch()
	{
		String indexPath = "src/main/resources/index";
		String docsPath = "src/main/resources/documents";
		IndexFiles lw = new IndexFiles(indexPath,docsPath);
		try {
			sf = new SearchFiles(indexPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String[][] query(String queryString, String time0, String time1) throws ParseException, IOException{
		String[][] result = sf.query(time0, time1, queryString, "contents");		
		return result;
	}
}