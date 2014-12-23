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
import java.io.PrintStream;

/**
 *
 * @author chikara
 */
public class Leaener {
    
      public void excecute(){
        System.out.println("mymon step 2 start");

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
        System.out.println("ok sample count is " + ok_sample_num);
        System.out.println("ng count is " + ng_num);

        int ok_training_num = (int) (ok_sample_num * 0.75);
        //int ng_num2 = (int) (ng_num * 1.0);s

        hr();
        System.out.println("ok training count is " + ok_training_num);
        //System.out.println("normalized ng count is " + ng_num);

        if (!samplesDir.exists()) {
            samplesDir.mkdir();
        }

        hr();
        if (vecFile.exists() && vecFile.isFile()) {
            skipBecauseFileExists(vecFile, "createsamples");
        } else {
            System.out.println("start createsamples.");
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
        System.out.println( "finish mymon step2." );
    }

    private static final void skipBecauseFileExists (File file, String what) {
        System.out.println("* " + file.getName() + " is exists, skip "+what+".");
    }

    private static final void hr () {
        System.out.println( "-------------------------" );
    }

    private static final boolean execf (String s, Object... args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(String.format(s, (Object[]) args));
            InputStreamThread it = new InputStreamThread(process.getInputStream(), System.out);
            InputStreamThread et = new InputStreamThread(process.getErrorStream(), System.err);
            it.start();
            et.start();
            process.waitFor();
            it.join();
            et.join();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private static class InputStreamThread extends Thread {

        private BufferedReader br;
        private PrintStream out;

        /** コンストラクター */
        public InputStreamThread(InputStream is, PrintStream out) {
            br = new BufferedReader(new InputStreamReader(is));
            this.out = out;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    String line = br.readLine();
                    if (line == null) 	break;
                    out.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    br.close();
                } catch (Exception ex) {
                }
            }
        }

    }
    
}
