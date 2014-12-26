/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import jp.crudefox.mymon.picturetool.util.InputStreamThread;

/**
 *
 * @author chikara
 */
public class Leaener {
    
    
    private final ProgressPublisher mPublisher = new ProgressPublisher();
    
    public ProgressPublisher getPublisher () {
        return mPublisher;
    }
        
    public void excecute(File vecFile, File okFile, File ngFile, File outLeaningDir){
        
        if (!okFile.isFile() || !ngFile.isFile() || !vecFile.isFile() ) {
            mPublisher.publishError("file is not found.");
            return;
        }
        
        if (!outLeaningDir.exists()) {
            if (!outLeaningDir.mkdirs()) {
                mPublisher.publishError("dir can not create");                
                return;
            }
        }
        if (outLeaningDir.isFile()) {
            mPublisher.publishError("dir is not directory.");            
            return;
        }
        
        File outLeaningLogFile = new File(
                outLeaningDir.getParent(),
                outLeaningDir.getName()+ "_log.txt");
        
        publish("mymon step 2 start");

//        File okFile = new File("OK.txt");
//        File ngFile = new File("NG.txt");
//        File vecFile = new File("samples.vec");
//        String samples = "samples";
//        File leaningDir = outLeaningXml;//new File(outLeaningXml);
        //File samplesXml = new File(samples+".xml");
        //File samplesXml = new File(samples+".xml");
        
        int ok_sample_num = countRow(okFile);
        int ng_num = countRow(ngFile);
        
        int ok_training_num = (int) (ok_sample_num * 0.75);

//        hr();
//        if ( ngFile.exists() ) skipBecauseFileExists(ngFile, "MymonMakeSamples");
//        else execf("java -jar MymonMakeSamples.jar ng NG/ %s", ngFile.getName());
//
//        hr();
//        if ( okFile.exists() ) skipBecauseFileExists(okFile, "MymonMakeSamples");
//        else execf("java -jar MymonMakeSamples.jar ok OK/ %s", okFile.getName());
//
//        int ok_sample_num = countRow(okFile);
//        int ng_num = countRow(ngFile);
//
//        hr();
//        System.out.println("ok sample count is " + ok_sample_num);
//        System.out.println("ng count is " + ng_num);
//
//        int ok_training_num = (int) (ok_sample_num * 0.75);
//        //int ng_num2 = (int) (ng_num * 1.0);s
//
//        hr();
//        System.out.println("ok training count is " + ok_training_num);
//        //System.out.println("normalized ng count is " + ng_num);

//        if (!samplesDir.exists()) {
//            samplesDir.mkdir();
//        }

//        hr();        
//        publish("start createsamples.");
//        // opencv_createsamples -info ${tok} -vec samples.vec -num ${ok_num} -bgcolor 255 -bg ${tng} -w 64 -h 64
//        // opencv_createsamples -info ${tok} -vec samples.vec -num ${ok_num} -bgcolor 255 -bg ${tng} -w 64 -h 64
//        execf("opencv_createsamples -vec samples.vec -info %1$s -bg %2$s -num %3$d -bgcolor 255 -bgthresh 5 -w 24 -h 24",
//                okFile.getName(), ngFile.getName(), ok_sample_num);
        

//        hr();
//        if ( samplesXml.exists() ) {
//            skipBecauseFileExists(samplesXml, "haartraining");
//        } else {
//            System.out.println("start haartraining.");
//
//            execf("opencv_haartraining -data %1$s -vec %2$s -bg %3$s -npos %4$d -nneg %5$d  -w 24 -h 24 -mode ALL",
//                    samplesDir.getName(), vecFile.getName(), ngFile.getName(), ok_training_num, ng_num);
//            // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -stageType BOOST -featureType LBP -w 64 -h 64 -mode ALL
//            // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -numStages 20 -mode ALL -w 24 -h 24 -precalcValBufSize 5000 -precalcIdxBufSize 1000 -featureType LBP
//        }
        

        
        publish("start haartraining.");

        execf(  outLeaningLogFile,
                "opencv_haartraining -data %1$s -vec %2$s -bg %3$s -npos %4$d -nneg %5$d  -w 24 -h 24 -mode ALL",
                outLeaningDir.getPath(), vecFile.getPath(), ngFile.getPath(), ok_training_num, ng_num);
        // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -stageType BOOST -featureType LBP -w 64 -h 64 -mode ALL
        // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -numStages 20 -mode ALL -w 24 -h 24 -precalcValBufSize 5000 -precalcIdxBufSize 1000 -featureType LBP        

        hr();
        publish("finish mymon step2." );
        
    }

    private final void skipBecauseFileExists (File file, String what) {
        publish("* " + file.getName() + " is exists, skip "+what+".");
    }

    private final void hr () {
        publish("-------------------------" );
    }

    private final boolean execf (File logFile, String s, Object... args) {
        try (PrintWriter pw = new PrintWriter(logFile)) {
            
            Runtime rt = Runtime.getRuntime();
            String command = String.format(s, (Object[]) args);
            mPublisher.publishCurrentTask(command);
            Process process = rt.exec(command);

            InputStreamThread it = new InputStreamThread(process.getInputStream(), (String line) -> {
                pw.println(line);
                pw.flush();
                mPublisher.publishCurrentTask(line);
            });
            InputStreamThread et = new InputStreamThread(process.getErrorStream(), (String line) -> {
                pw.println(line);
                pw.flush();
                mPublisher.publishError(line);
            });
            it.start();
            et.start();
            process.waitFor();
            it.join();
            et.join();
            pw.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static final int countRow (File file) {
        try ( BufferedReader in = new BufferedReader(new FileReader(file)) ) {
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
 
    
    
    private void publish (String text) {
         mPublisher.publishCurrentTask(text);
    }
}
