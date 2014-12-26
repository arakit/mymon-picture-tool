/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.tool;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import jp.crudefox.mymon.picturetool.util.InputStreamThread;

/**
 *
 * @author chikara
 */
public class SamplerMaker {
    
    private final ProgressPublisher mPublisher = new ProgressPublisher();
    
    public ProgressPublisher getPublisher () {
        return mPublisher;
    }

    
    
    public boolean executeCreateVectorFile (File outVecFile, File okFile, File ngFile) {
        
        publish("start createsamples.");

        int okSampleNum = countRow(okFile);
        //int okTrainingNum = (int)( okNum * 0.75 );
        
        // opencv_createsamples -info ${tok} -vec samples.vec -num ${ok_num} -bgcolor 255 -bg ${tng} -w 64 -h 64
        // opencv_createsamples -info ${tok} -vec samples.vec -num ${ok_num} -bgcolor 255 -bg ${tng} -w 64 -h 64
        boolean ret = 
                execf("opencv_createsamples -vec %1$s -info %2$s -bg %3$s -num %4$d -bgcolor 255 -bgthresh 5 -w 24 -h 24",
                outVecFile.getPath(), okFile.getPath(), ngFile.getPath(), okSampleNum);
        
        return ret;                
    }
    
     private void excecute(){
        publish("mymon step 2 start");

        File okFile = new File("OK.txt");
        File ngFile = new File("NG.txt");
        File vecFile = new File("samples.vec");
        String samples = "samples";
        File samplesDir = new File(samples);
        File samplesXml = new File(samples+".xml");

        hr();
        if ( ngFile.exists() ) skipBecauseFileExists(ngFile, "MymonMakeSamples");
        else execf("java -jar MymonMakeSamples.jar ng NG/ %s", ngFile.getName());

        hr();
        if ( okFile.exists() ) skipBecauseFileExists(okFile, "MymonMakeSamples");
        else execf("java -jar MymonMakeSamples.jar ok OK/ %s", okFile.getName());

        int ok_sample_num = countRow(okFile);
        int ng_num = countRow(ngFile);

        hr();
        publish("ok sample count is " + ok_sample_num);
        publish("ng count is " + ng_num);

        int ok_training_num = (int) (ok_sample_num * 0.75);
        //int ng_num2 = (int) (ng_num * 1.0);s

        hr();
        publish("ok training count is " + ok_training_num);
        //System.out.println("normalized ng count is " + ng_num);

        if (!samplesDir.exists()) {
            samplesDir.mkdir();
        }

        hr();
        if (vecFile.exists() && vecFile.isFile()) {
            skipBecauseFileExists(vecFile, "createsamples");
        } else {
            publish("start createsamples.");
            // opencv_createsamples -info ${tok} -vec samples.vec -num ${ok_num} -bgcolor 255 -bg ${tng} -w 64 -h 64
            // opencv_createsamples -info ${tok} -vec samples.vec -num ${ok_num} -bgcolor 255 -bg ${tng} -w 64 -h 64
            execf("opencv_createsamples -vec samples.vec -info %1$s -bg %2$s -num %3$d -bgcolor 255 -bgthresh 5 -w 24 -h 24",
                    okFile.getName(), ngFile.getName(), ok_sample_num);
        }

        hr();
        if ( samplesXml.exists() ) {
            skipBecauseFileExists(samplesXml, "haartraining");
        } else {
            System.out.println("start haartraining.");

            execf("opencv_haartraining -data %1$s -vec %2$s -bg %3$s -npos %4$d -nneg %5$d  -w 24 -h 24 -mode ALL",
                    samplesDir.getName(), vecFile.getName(), ngFile.getName(), ok_training_num, ng_num);
            // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -stageType BOOST -featureType LBP -w 64 -h 64 -mode ALL
            // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -numStages 20 -mode ALL -w 24 -h 24 -precalcValBufSize 5000 -precalcIdxBufSize 1000 -featureType LBP
        }

        hr();
        publish("finish mymon step2." );
    }
     
     private void publish (String text) {
         mPublisher.publishCurrentTask(text);
     }

    private final void skipBecauseFileExists (File file, String what) {
        publish("* " + file.getName() + " is exists, skip "+what+".");
    }

    private final void hr () {
        publish( "-------------------------" );
    }

    private boolean execf (String s, Object... args) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = String.format(s, (Object[]) args);
            mPublisher.publishCurrentTask(command);
            Process process = rt.exec(command);           
            
            InputStreamThread it = new InputStreamThread(process.getInputStream(), (String line) -> {
                mPublisher.publishCurrentTask(line);
            });
            InputStreamThread et = new InputStreamThread(process.getErrorStream(), (String line) -> {
                mPublisher.publishError(line);                
            });
            it.start();
            et.start();
            process.waitFor();
            it.join();
            et.join();
            return true;
        } catch (Exception ex) {
            mPublisher.publishError(ex);
            return false;
        }
    }
    
    
    private static final int countRow (File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            int n = 0;
            while ((line = in.readLine()) != null) {
                n++;
            }
            in.close();
            return n;
        } catch (Exception ex) {
            return 0;
        }
    }
    
    
  

    
    
}
