package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class IndexFiles {

	public IndexFiles(String indexDestiny, String documentsPath){
		boolean create = true;
		//******** find the document_path *********//
		final Path docDir = Paths.get(documentsPath);
		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexDestiny + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexDestiny));      
			//Analyzer analyzer = new StandardAnalyzer();
			Analyzer analyzer = new SmartChineseAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			//******** construct an IndexWriter *******//
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			writer.close();
			Date end = new Date();
			//*********** calculate the time **********//
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}
	//*********** traverse the file tree ************//
	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
					} catch (IOException ignore) {
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}
	
	//******** Index single document ********//
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			Document doc = new Document();
			//*********** title ************//
			Field titleField = new StringField("title", file.getFileName().toString(), Field.Store.YES);
			doc.add(titleField);
			//************ path ************//
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			//******* last modify time ******//
			doc.add(new LongPoint("modified", lastModified));
			//******** content ********//
			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}