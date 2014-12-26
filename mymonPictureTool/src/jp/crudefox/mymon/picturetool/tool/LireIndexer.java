package jp.crudefox.mymon.picturetool.tool;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.utils.FileUtils;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * User: Mathias Lux, mathias@juggle.at
 * Date: 25.05.12
 * Time: 12:04
 */
public class LireIndexer {
    
    
    private final ProgressPublisher mPublisher = new ProgressPublisher();
    
    public ProgressPublisher getPublisher () {
        return mPublisher;
    }
    
    
    public void execute (File dir) {
        
        if (!dir.exists() || !dir.isDirectory()) {
            publishError("not directory.");
            return;
        }
        
        publish("start indexer");
        
        try {
            
            
            // Getting all images from a directory and its sub directories.
            // ArrayList<File> images = FileUtils.getAllImageFiles(dir, true);
            publish("Getting all images from a directory and its sub directories.");
            List<File> images = Arrays.asList( dir.listFiles((File pathname) -> {
                try {
                    BufferedImage image = ImageIO.read(pathname);
                    return image!=null;
                } catch (Exception ex) {
                    publishError("can not open with image, " + pathname + ". "+ex.getMessage());
                    return false;
                }
            }));
            
            publish("image files count is "+images.size());

            // Creating a CEDD document builder and indexing al files.
            DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
            // Creating an Lucene IndexWriter
            IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_40,
                    new WhitespaceAnalyzer(Version.LUCENE_40));
            IndexWriter iw = new IndexWriter(FSDirectory.open(new File(dir, "index-lire")), conf);
            // Iterating through images building the low level features            
            int progress = 0;
            for ( File imageFile : images ) {
                publish("Indexing " + imageFile.getPath());
                try {
                    BufferedImage img = ImageIO.read(new FileInputStream(imageFile));
                    Document document = builder.createDocument(img, imageFile.getPath());
                    iw.addDocument(document);
                } catch (Exception ex) {
                    publishError("Error reading image or indexing it.");
                    ex.printStackTrace();
                }
                mPublisher.publishProgress(++progress, images.size());
            }
            // closing the IndexWriter
            iw.close();
            publish("Finished indexing.");        
            
        } catch (Exception ex) {
            publishError(ex);
        }
    }    
    

    private void publish (String text) {
         mPublisher.publishCurrentTask(text);
    }
    private void publishError (String text) {
         mPublisher.publishError(text);
    }    
    private void publishError (Throwable throwable) {
         mPublisher.publishError(throwable);
    }        
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void main(String[] args) throws IOException {
        // Checking if arg[0] is there and if it is a directory.
        boolean passed = false;
        if (args.length > 0) {
            File f = new File(args[0]);
            System.out.println("Indexing images in " + args[0]);
            if (f.exists() && f.isDirectory()) passed = true;
        }
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"Indexer <directory>\" to index files of a directory.");
            System.exit(1);
        }

        LireIndexer li = new LireIndexer();
        li.execute(new File(args[0]));
    }

    
    
}
