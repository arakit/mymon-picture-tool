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
import jp.crudefox.mymon.picturetool.app.ApiUrl;
import jp.crudefox.mymon.picturetool.tool.ProgressPublisher;
import jp.crudefox.mymon.picturetool.util.HandlerUtil;

/**
 *
 * @author chikara
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField mHostAndPortTextField;
    
    @FXML
    private TextField mAccessTokenTextField;    
    
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
        
        mHostAndPortTextField.setText(ApiUrl.DEFAULT_HOST_AND_PORT);
        mAccessTokenTextField.setText("XcrowxWdWhdokCjm");
    }
        
    
    
    
    
    
    
    
    
    
    
    
    public void publishInfo (String text) {
        mInfoListView.getItems().add(text);
    }
    public void publishProgress (int current, int max) {
        if (max != 0) {
            mInfoProgressBar.setProgress(current / (double)max);
            mInfoLabel.setText( String.format("%d / %d (%d %%)", current, max, current * 100 / max)  );
        } else {
            mInfoProgressBar.setProgress(0);
            mInfoLabel.setText("-");
        }
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
        
        String strFoodId = mFoodIdTextField.getText();
        if (strFoodId.length() == 0) {
            return;
        }
        long food_id = Long.parseLong( strFoodId );
        
        File saveDir = new File ( mSaveDirectoryTextField.getText() );
        if (!saveDir.isDirectory()) {
            return;
        }
        
        File outEntityFile = new File(saveDir, "ok_food_" + food_id + ".txt");
        
        Downloader downloader = new Downloader();
        downloader.setHostAndPort(mHostAndPortTextField.getText());
        downloader.setAccessToken(mAccessTokenTextField.getText());
        downloader.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        
        HandlerUtil.postBackground(() -> {
            downloader.executeByFoodId(food_id, saveDir, outEntityFile);
        });
        
    }
   
    
    @FXML
    private void handleMakeSamplesButtonAction(ActionEvent event) {
        
        String strFoodId = mFoodIdTextField.getText();
        if (strFoodId.length() == 0) {
            return;
        }
        long food_id = Long.parseLong( strFoodId );
        
        File dir = new File ( mSaveDirectoryTextField.getText() );
        
        File okFile = new File("OK.txt");
        File ngFile = new File("NG.txt");
        
        SamplerMaker samplerMaker = new SamplerMaker();
        samplerMaker.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        HandlerUtil.postBackground(()->{
            samplerMaker.execute(SamplerMaker.Mode.ok, dir, okFile);
            
            HandlerUtil.post(()->{
                publishProgress(0, 0);
            });
        });
        
    }   
    
    @FXML
    private void handleStartLearningButtonAction(ActionEvent event) {
        
    }
    
    
    private class DefaultProgressListener implements ProgressPublisher.OnProgressListener {
        @Override
        public void onProgress(int current, int max) {
            publishProgress(current, max);
        }

        @Override
        public void onCurrentTask(String text) {
            publishInfo(text);
        }

        @Override
        public void onError(String text) {
            publishInfo(text);
        }        
    }
    
}
