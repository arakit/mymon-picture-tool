package jp.crudefox.mymon.picturetool.tool;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * User: Mathias Lux, mathias@juggle.at
 * Date: 25.05.12
 * Time: 12:19
 */
public class LireSearcher {
    
    
    private final ProgressPublisher mPublisher = new ProgressPublisher();
    
    public ProgressPublisher getPublisher () {
        return mPublisher;
    }
    


    public void execute (File imgFile, File indexDir) {
        try {
            BufferedImage img = ImageIO.read(imgFile);
            execute(img, indexDir);
        } catch (Exception ex) {
            publishError(ex);
        }
    }
    
    public void execute (BufferedImage img, File indexDir) {
        
        if (img == null) {
            publishError("can not is null, img.");
            return;
        }
        
        try {
            IndexReader ir = DirectoryReader.open(FSDirectory.open(indexDir));
            ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);

            ImageSearchHits hits = searcher.search(img, ir);
            for (int i = 0; i < hits.length(); i++) {
                String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
                publish(hits.score(i) + ": \t" + fileName);
            }        
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
        // Checking if arg[0] is there and if it is an image.
        BufferedImage img = null;
        boolean passed = false;
        if (args.length > 0) {
            File f = new File(args[0]);
            if (f.exists()) {
                try {
                    img = ImageIO.read(f);
                    passed = true;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        if (!passed) {
            System.out.println("No image given as first argument.");
            System.out.println("Run \"Searcher <query image>\" to search for <query image>.");
            System.exit(1);
        }

    }
}
