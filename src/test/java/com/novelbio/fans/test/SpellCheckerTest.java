package com.novelbio.fans.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class SpellCheckerTest {
	
private static String filepath = "C:\\Users\\fans.fan\\git\\luceneTest\\src\\test\\resources\\dictionaryfile.txt";
private Document document;
private Directory directory;
private IndexWriter indexWriter;
private SpellChecker spellchecker;
private IndexReader indexReader;
private IndexSearcher indexSearcher;

private IndexWriterConfig getConfig() {
    return new IndexWriterConfig(new SmartChineseAnalyzer(true));
}

private IndexWriter getIndexWriter() {
    directory = new RAMDirectory();
    try {
        return new IndexWriter(directory, getConfig());
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

/**
 * Create index for test
 *
 * @param content
 * @throws IOException
 */
public void createIndex(String content) {
    indexWriter = getIndexWriter();
    document = new Document();
    document.add(new TextField("content", content, Field.Store.YES));
    try {
        indexWriter.addDocument(document);
        indexWriter.commit();
        indexWriter.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public ScoreDoc[] gethits(String content) {
    try {
        indexReader = DirectoryReader.open(directory);
        indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser("content", new SmartChineseAnalyzer());
        Query query = parser.parse(content);
        TopDocs td = indexSearcher.search(query, 1000);
        return td.scoreDocs;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

/**
 * @param scoreDocs
 * @return
 * @throws IOException
 */
public List<Document> getDocumentList(ScoreDoc[] scoreDocs) throws IOException {
    List<Document> documentList = null;
    if (scoreDocs.length >= 1) {
        documentList = new ArrayList<Document>();
        for (int i = 0; i < scoreDocs.length; i++) {
            documentList.add(indexSearcher.doc(scoreDocs[i].doc));
        }
    }
    return documentList;
}

public String[] search(String word, int numSug) {
    directory = new RAMDirectory();
    try {
        spellchecker = new SpellChecker(directory);
        spellchecker.indexDictionary(new PlainTextDictionary(new File(filepath).toPath()), getConfig(), true);
        return getSuggestions(spellchecker, word, numSug);
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}

private String[] getSuggestions(SpellChecker spellchecker, String word, int numSug) throws IOException {
    return spellchecker.suggestSimilar(word, numSug);
}

public static void main(String[] args) throws IOException {
    SpellCheckerTest spellCheckerTest = new SpellCheckerTest();
    spellCheckerTest.createIndex("开源中国-找到您想要的开源项目，分享和交流");
    spellCheckerTest.createIndex("CSDN-全球最大中文IT社区");

    String word = "开园中国";

    /*
    ScoreDoc[] scoreDocs = spellCheckerTest.gethits(word);

    List<Document> documentList = spellCheckerTest.getDocumentList(scoreDocs);
    if (documentList.size() >= 1) {
        for (Document d : documentList) {
            System.out.println("搜索结果：" + d.get("content"));
        }
    }
    */
    String[] suggest = spellCheckerTest.search(word, 5);
    if (suggest != null && suggest.length >= 1) {
        for (String s : suggest) {
            System.out.println("您是不是要找：" + s);
        }
    } else {
        System.out.println("拼写正确");
    }
}
}
