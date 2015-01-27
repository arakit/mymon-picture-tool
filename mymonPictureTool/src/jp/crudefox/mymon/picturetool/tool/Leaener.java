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
import jp.crudefox.mymon.picturetool.util.Log;

/**
 *
 * @author chikara
 */
public class Leaener {
    
    private static final String TAG = "Leaener";
    
    
    public interface OnProcessListener {
        public void onStart (Process process);
        public void onStop (Process process);
    }
    
    
    private final ProgressPublisher mPublisher = new ProgressPublisher();
    
    public ProgressPublisher getPublisher () {
        return mPublisher;
    }
        
    public boolean excecute(File vecFile, File okFile, File ngFile, File outLeaningDir, OnProcessListener processListener){
        
        if (!okFile.isFile() || !ngFile.isFile() || !vecFile.isFile() ) {
            publishError("file is not found.");
            return false;
        }
        
        if (!outLeaningDir.exists()) {
            if (!outLeaningDir.mkdirs()) {
                publishError("dir can not create");                
                return false;
            }
        }
        if (outLeaningDir.isFile()) {
            publishError("dir is not directory.");            
            return false;
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

        boolean successTraning = execf( outLeaningLogFile, processListener,
                "opencv_haartraining -data %1$s -vec %2$s -bg %3$s -npos %4$d -nneg %5$d  -w 24 -h 24 -mode ALL",
                outLeaningDir.getPath(), vecFile.getPath(), ngFile.getPath(), ok_training_num, ng_num);
        // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -stageType BOOST -featureType LBP -w 64 -h 64 -mode ALL
        // opencv_traincascade -data samples -vec samples.vec -bg ${tng} -numPos ${ok_num2} -numNeg ${ng_num} -numStages 20 -mode ALL -w 24 -h 24 -precalcValBufSize 5000 -precalcIdxBufSize 1000 -featureType LBP        

        hr();
        publish("finish mymon step2." );
        
        return successTraning;
    }

//    private final void skipBecauseFileExists (File file, String what) {
//        publish("* " + file.getName() + " is exists, skip "+what+".");
//    }

    private void hr () {
        publish("-------------------------" );
    }

    private boolean execf (File logFile, OnProcessListener processListener, String s, Object... args) {
        
        Process listenerStartProccess = null;
        try (PrintWriter pw = new PrintWriter(logFile)) {
            
            Runtime rt = Runtime.getRuntime();
            String command = String.format(s, (Object[]) args);
            mPublisher.publishCurrentTask(command);
            final Process process = rt.exec(command);
            Thread shutdownHock = new Thread(){
                @Override
                public void run() {
                    process.destroy();
                }
            };
            rt.addShutdownHook(shutdownHock);
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
            if (processListener!=null) {
                listenerStartProccess = process;
                processListener.onStart(process);
            }
            it.start();
            et.start();
            process.waitFor();
            it.join();
            et.join();
            pw.close();
            rt.removeShutdownHook(shutdownHock);
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "err exec command", ex);
            return false;
        } finally {
            if (processListener!=null && listenerStartProccess!=null) {
                processListener.onStop(listenerStartProccess);
            }            
        }
    }

    private static int countRow (File file) {
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
 
    
    
    private void publish(String text) {
        mPublisher.publishCurrentTask(text);
    }

    private void publishProgress(int now, int max) {
        mPublisher.publishProgress(now, max);
    }

    private void publishError(String text) {
        mPublisher.publishError(text);
    }

    private void publishError(Throwable throwable) {
        mPublisher.publishError(throwable);
    }    
}
