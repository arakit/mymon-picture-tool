/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool;

import jp.crudefox.mymon.picturetool.tool.LireIndexer;
import jp.crudefox.mymon.picturetool.tool.Downloader;
import jp.crudefox.mymon.picturetool.tool.SamplerMaker;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import jp.crudefox.mymon.picturetool.app.ApiUrl;
import jp.crudefox.mymon.picturetool.tool.Leaener;
import jp.crudefox.mymon.picturetool.tool.LireSearcher;
import jp.crudefox.mymon.picturetool.tool.ProgressPublisher;
import jp.crudefox.mymon.picturetool.util.FileChooseButtonEventHandler;
import jp.crudefox.mymon.picturetool.util.HandlerUtil;

/**
 *
 * @author chikara
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML private TextField mHostAndPortTextField;
    @FXML private TextField mAccessTokenTextField;    

    @FXML private TextField mFoodIdTextField;
    
    @FXML private TextField mSaveDirectoryTextField;    
    @FXML private TextField mVecFileTextField;
    @FXML private TextField mOkListFileTextField;
    @FXML private TextField mNgListFileTextField;
    @FXML private TextField mMakeNgListFileTextField;
    
    @FXML private Label mInfoLabel;
    @FXML private ProgressBar mInfoProgressBar;    
    //@FXML private TextArea mInfoTextArea;
    @FXML private ListView mInfoListView;
    
    @FXML private Button mSaveDirectoryChooseButton;
    @FXML private Button mVecFileChooseButton;
    @FXML private Button mOkListFileChooseButton;
    @FXML private Button mNgListFileChooseButton;
    @FXML private Button mMakeNgListFileChooseButton;
    
    @FXML private TableView mEnvTableView;
    @FXML private TableColumn mEnvTableKeyColumn;
    @FXML private TableColumn mEnvTableValueColumn;

    // create vec file & leaning
    @FXML private TextField mSaveVecFileTextField;
    @FXML private Button mSaveVecFileChooseButton;
    @FXML private TextField mSaveTrainingDirTextField;
    @FXML private Button mSaveTrainingDirChooseButton;
    

    // make ng sample
    @FXML private TextField mSaveMakeNgListFileTextField;
    @FXML private Button mSaveMakeNgListFileChooseButton;    
    
    // all
    @FXML private TextField mAllStartFoodIdTextField;

    // Lire
    @FXML private TextField mLireIndexerDirectoryTextField;
    @FXML private Button mLireIndexerDirectoryChooseButton;
    @FXML private TextField mLireSearchFileTextField;
    @FXML private Button mLireSearchFileChooseButton;

    
    //private StringBuilder mInfoText = new StringBuilder();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        mHostAndPortTextField.setText(ApiUrl.DEFAULT_HOST_AND_PORT);
        mAccessTokenTextField.setText("XcrowxWdWhdokCjm");
        
        
        mSaveDirectoryChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mSaveDirectoryTextField));
        mVecFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.OpenFile, mVecFileTextField));
        mOkListFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.OpenFile, mOkListFileTextField));
        mNgListFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.OpenFile, mNgListFileTextField));        
        mMakeNgListFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mMakeNgListFileTextField));        
        mSaveMakeNgListFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.SaveFile, mSaveMakeNgListFileTextField));        
        mSaveVecFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.SaveFile, mSaveVecFileTextField));                
        mSaveTrainingDirChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mSaveTrainingDirTextField));                        
        mLireIndexerDirectoryChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mLireIndexerDirectoryTextField));                                
        mLireSearchFileChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.OpenFile, mLireSearchFileTextField));                                        
        
        
        mEnvTableKeyColumn.setCellValueFactory(new PropertyValueFactory<EnvVarItem, String>(EnvVarItem.KEY));
        mEnvTableValueColumn.setCellValueFactory(new PropertyValueFactory<EnvVarItem, String>(EnvVarItem.VALUE));
     
        for ( Map.Entry<String, String> e : System.getenv().entrySet() ) {
            mEnvTableView.getItems().add(new EnvVarItem(e.getKey(), e.getValue()));
            //mPublisher.publishCurrentTask( e.getKey() + " : " + e.getValue());
        }
        
        

    }
        
    
    
    
    
    
    
    
    
    
    
    public void publishInfo (String text) {
        mInfoListView.getItems().add(text);
        //mInfoText.append(text);
        //mInfoText.append('\n');
        //mInfoTextArea.appendText(text + '\n');
        //mInfoTextArea.setText(mInfoText.toString());
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
    private void handleSetupButtonAction(ActionEvent event) {
        
        String strFoodId = mAllStartFoodIdTextField.getText();
        if (strFoodId.length() == 0) {
            return;
        }
        long food_id = Long.parseLong( strFoodId );
        
        mFoodIdTextField.setText("" + food_id);

//        File outEntityFile = new File(saveDir, "ok_food_" + food_id + ".txt");
//        mOkListFileTextField.setText( outEntityFile.getAbsolutePath() );
        
    }
    
    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        
    }
    
    
    @FXML
    private void handleStartLireIndexerButtonAction(ActionEvent event) {
        
        File dir = new File(mLireIndexerDirectoryTextField.getText());
        
        LireIndexer maker = new LireIndexer();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        HandlerUtil.postBackground(()->{
            maker.execute(dir);
            
            HandlerUtil.post(() -> {
                
                publishInfo("complete.");
                publishProgress(0, 0);
            });
        });
        
    }   
    @FXML
    private void handleStartLireSearchButtonAction(ActionEvent event) {
        
        File dir = new File(mLireIndexerDirectoryTextField.getText());
        File searchFile = new File(mLireSearchFileTextField.getText());
        
        LireSearcher maker = new LireSearcher();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        HandlerUtil.postBackground(()->{
            maker.execute(searchFile, new File(dir, "index-lire"));
            
            HandlerUtil.post(() -> {
                
                publishInfo("complete.");
                publishProgress(0, 0);
            });
        });
        
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
            
            HandlerUtil.post(()->{
                publishProgress(0, 0);
            });
        });
        
    }
    
    @FXML
    private void handleMakeNgSamplesButtonAction(ActionEvent event) {
        
//        String strFoodId = mFoodIdTextField.getText();
//        if (strFoodId.length() == 0) {
//            return;
//        }
//        long food_id = Long.parseLong( strFoodId );
//        File saveDir = new File ( mSaveDirectoryTextField.getText() );
//        if (!saveDir.isDirectory()) {
//            publishInfo("not directory, save directoy.");
//            return;
//        }
        
        File fromDir = new File ( mMakeNgListFileTextField.getText() );
        if (!fromDir.isDirectory()) {
            publishInfo("not directory, from directory.");
            return;
        }
        
        String strNgFile = mSaveMakeNgListFileTextField.getText();
        //File okFile = new File("OK.txt");
        File ngFile = new File(strNgFile);
        
        Downloader maker = new Downloader();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        HandlerUtil.postBackground(()->{
            maker.executeCreateNgFileList(fromDir, ngFile);
            
            HandlerUtil.post(()->{
                publishProgress(0, 0);
            });
        });
        
    }
   
    
    @FXML
    private void handleCreateSamplesButtonAction(ActionEvent event) {
        
//        String strFoodId = mFoodIdTextField.getText();
//        if (strFoodId.length() == 0) {
//            return;
//        }
//        long food_id = Long.parseLong( strFoodId );
        
        //File dir = new File ( mSaveDirectoryTextField.getText() );
        
        File okFile = new File(mOkListFileTextField.getText());
        File ngFile = new File(mNgListFileTextField.getText());
        File vecFile = new File(mSaveVecFileTextField.getText());
        
        SamplerMaker maker = new SamplerMaker();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        HandlerUtil.postBackground(()->{
            
            boolean ret = maker.executeCreateVectorFile(vecFile, okFile, ngFile);
            
            HandlerUtil.post(()->{
                if (ret) {
                    mVecFileTextField.setText(vecFile.getAbsolutePath());
                    publishInfo("success.");
                } else {
                    publishInfo("failed.");
                }
                publishProgress(0, 0);
                
            });
        });
        
    }   
    
    @FXML
    private void handleStartLearningButtonAction(ActionEvent event) {
        
        File okFile = new File(mOkListFileTextField.getText());
        File ngFile = new File(mNgListFileTextField.getText());
        File vecFile = new File(mVecFileTextField.getText());
        File outTraningDir = new File(mSaveTrainingDirTextField.getText());
        
        
        Leaener maker = new Leaener();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());
        
        HandlerUtil.postBackground(() -> {
            maker.excecute(vecFile, okFile, ngFile, outTraningDir);
            
            HandlerUtil.post(() -> {
                
                publishInfo("completed.");
                publishProgress(0, 0);
            });
        });
        
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
    
    
    
    
    
    
    
    
    
    public static class EnvVarItem {
        
        public static final String KEY = "key";
        public static final String VALUE = "value";
        
        private StringProperty key;
        private StringProperty value;

        public EnvVarItem(String key, String value) {
            this.key = new SimpleStringProperty(key);
            this.value = new SimpleStringProperty(value);
        }

        public StringProperty keyProperty() {
            return this.key;
        }

        public StringProperty valueProperty() {
            return this.value;
        }

    }
    
}
