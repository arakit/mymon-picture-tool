/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool;

import jp.crudefox.mymon.picturetool.tool.Downloader;
import jp.crudefox.mymon.picturetool.tool.SamplerMaker;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 *
 * @author chikara
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField mSaveDirectoryTextField;
    
    @FXML
    private TextField mFoodIdTextField;
    
    @FXML
    private Label mInfoLabel;
    
    @FXML
    private ProgressBar mInfoProgressBar;    
    
    @FXML
    private ListView mInfoListView;
    
        
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
        
    
    
    
    
    
    
    
    
    
    
    
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        
    }
    
    
    @FXML
    private void handleSaveDirecoryChoooseButtonAction(ActionEvent event) {
        Window window = mSaveDirectoryTextField.getScene().getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(window);
        if (file!=null) {
            mSaveDirectoryTextField.setText(file.getAbsolutePath());
        }
    }
    
    
    @FXML
    private void handleDownloadButtonAction(ActionEvent event) {
        
        Downloader downloader = new Downloader();
        downloader.executeAllDownload();
        
    }
   
    
    @FXML
    private void handleMakeSamplesButtonAction(ActionEvent event) {
        File dir = new File ( mSaveDirectoryTextField.getText() );
        
        File okFile = new File("OK.txt");
        File ngFile = new File("NG.txt");
        
        SamplerMaker samplerMaker = new SamplerMaker();
        samplerMaker.setOnProgressListener(new SamplerMaker.OnProgressListener() {

            @Override
            public void onProgress(int current, int max) {
                mInfoProgressBar.setProgress(current / (double)max);
            }

            @Override
            public void onCurrentTask(String text) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        samplerMaker.execute(SamplerMaker.Mode.ok, dir, okFile);
        
    }   
    
    @FXML
    private void handleStartLearningButtonAction(ActionEvent event) {
        
    }
    
    
}
