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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jp.crudefox.mymon.picturetool.api.output.common.Food;
import jp.crudefox.mymon.picturetool.app.PreferenceManager;
import jp.crudefox.mymon.picturetool.tool.ApiExecutor;
import jp.crudefox.mymon.picturetool.tool.Leaener;
import jp.crudefox.mymon.picturetool.tool.LireSearcher;
import jp.crudefox.mymon.picturetool.tool.ProgressPublisher;
import jp.crudefox.mymon.picturetool.tool.RRCroller;
import jp.crudefox.mymon.picturetool.util.FileChooseButtonEventHandler;
import jp.crudefox.mymon.picturetool.util.HandlerUtil;
import jp.crudefox.mymon.picturetool.util.Log;
import org.w3c.dom.Document;

/**
 *
 * @author chikara
 */
public class FXMLDocumentController implements Initializable {

    public static String TAG = "FXMLDocumentController";

    // setttings 
    @FXML
    private TextField mHostAndPortTextField;
    @FXML
    private TextField mAccessTokenTextField;

    // ok
    @FXML
    private ComboBox mMakeOkFoodIdTextField;

    //
    @FXML
    private TextField mSaveDirectoryTextField;
    @FXML
    private TextField mVecFileTextField;
    @FXML
    private TextField mOkListFileTextField;
    @FXML
    private TextField mNgListFileTextField;
    @FXML
    private TextField mMakeNgListFileTextField;

    @FXML
    private Label mInfoLabel;
    @FXML
    private ProgressBar mInfoProgressBar;
    //@FXML private TextArea mInfoTextArea;
    @FXML
    private ListView mInfoListView;

    @FXML
    private Button mSaveDirectoryChooseButton;
    @FXML
    private Button mVecFileChooseButton;
    @FXML
    private Button mOkListFileChooseButton;
    @FXML
    private Button mNgListFileChooseButton;
    @FXML
    private Button mMakeNgListFileChooseButton;

    @FXML
    private TableView mEnvTableView;
    @FXML
    private TableColumn mEnvTableKeyColumn;
    @FXML
    private TableColumn mEnvTableValueColumn;

    // create vec file & leaning
    @FXML
    private TextField mSaveVecFileTextField;
    @FXML
    private Button mSaveVecFileChooseButton;
    @FXML
    private TextField mSaveTrainingDirTextField;
    @FXML
    private Button mSaveTrainingDirChooseButton;

    // make ng sample
    @FXML
    private TextField mSaveMakeNgListFileTextField;
    @FXML
    private Button mSaveMakeNgListFileChooseButton;

    // all
    @FXML
    private TextField mAllStartNameTextField;
    @FXML
    private ComboBox mAllStartFoodIdTextField;
    @FXML
    private TextField mAllStartPictureSaveDirectoryTextField;
    @FXML
    private Button mAllStartPictureSaveDirectoryChooseButton;
    @FXML
    private TextArea mAllStartNgFoodIdListTextArea;
    @FXML
    private Button mAllStartNgFoodIdListAddAllButton;
    @FXML
    private TextField mAllStartDefaultNgDirectoryTextField;
    @FXML
    private Button mAllStartDefaultNgDirectoryChooseButton;
    @FXML
    private TextField mAllStartLeaningOutputDirectoryTextField;
    @FXML
    private Button mAllStartLeaningOutputDirectoryChooseButton;
    @FXML
    private Button mAllStartLeaningStopButton;
    @FXML
    private Button mAllStartLeaningStartButton;
    @FXML
    private Button mAllStartLeaningSetupButton;

    // Lire
    @FXML
    private TextField mLireIndexerDirectoryTextField;
    @FXML
    private Button mLireIndexerDirectoryChooseButton;
    @FXML
    private TextField mLireSearchFileTextField;
    @FXML
    private Button mLireSearchFileChooseButton;

    //  web
    @FXML
    private ComboBox mWebViewFoodIdField;
    @FXML
    private TextField mWebViewUrlBoxField;
    @FXML
    private CheckBox mWebViewAutoNextCheckBox;
    @FXML
    private ProgressBar mWebViewProgressBar;
    @FXML
    private Button mWebViewStartButton;
    @FXML
    private WebView mWebView;

    private final ApiExecutor mApiExecutor = new ApiExecutor();

    //private StringBuilder mInfoText = new StringBuilder();
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        mSaveDirectoryChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mSaveDirectoryTextField));
        mAllStartPictureSaveDirectoryChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mAllStartPictureSaveDirectoryTextField));
        mAllStartDefaultNgDirectoryChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mAllStartDefaultNgDirectoryTextField));
        mAllStartLeaningOutputDirectoryChooseButton.setOnAction(
                new FileChooseButtonEventHandler(FileChooseButtonEventHandler.Mode.Directory, mAllStartLeaningOutputDirectoryTextField));
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

        System.getenv().entrySet().stream().forEach((e) -> {
            mEnvTableView.getItems().add(new EnvVarItem(e.getKey(), e.getValue()));
            //mPublisher.publishCurrentTask( e.getKey() + " : " + e.getValue());
        });

        final WebView webView = mWebView;
        final ProgressBar progressBar = mWebViewProgressBar;
        final WebEngine engine = webView.getEngine();
        engine.getLoadWorker().progressProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Log.d(TAG, "" + newValue);
            progressBar.setProgress(newValue.doubleValue());
        });
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    progressBar.setProgress(0.0);
                    String url = engine.getLocation();
                    mWebViewUrlBoxField.setText(url);
                    PreferenceManager.putLatestUrl(url);
                } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                    progressBar.setProgress(0.0);
                } else if (newState == javafx.concurrent.Worker.State.CANCELLED) {
                    progressBar.setProgress(0.0);
                }
            }
        });

        mWebViewFoodIdField.setConverter(new FoodStringConverter());
        mMakeOkFoodIdTextField.setConverter(new FoodStringConverter());
        mAllStartFoodIdTextField.setConverter(new FoodStringConverter());

        mAccessTokenTextField.setText(PreferenceManager.getAccessToken());
        mAccessTokenTextField.setOnAction((ActionEvent event) -> {
            PreferenceManager.putAccessToken(mAccessTokenTextField.getText());
            publishInfo("save");
        });

        mHostAndPortTextField.setText(PreferenceManager.getHostAndPort());
        mHostAndPortTextField.setOnAction((ActionEvent event) -> {
            PreferenceManager.putHostAndPort(mHostAndPortTextField.getText());
            publishInfo("save");
        });

        mWebViewUrlBoxField.setText(PreferenceManager.getLatestUrl());

        mAllStartNgFoodIdListAddAllButton.setOnAction((ActionEvent event) -> {
            Long okFoodId = pickFoodId(mAllStartFoodIdTextField.getEditor().getText());
            Food[] foods = mApiExecutor.getCacheFoods();
            TextArea ta = mAllStartNgFoodIdListTextArea;
            if (foods == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Food food : foods) {
                if (okFoodId != null && okFoodId.equals(food.food_id)) {
                    continue;
                }
                sb.append(formatFoodId(food));
                sb.append('\n');
            }
            ta.setText(sb.toString());
        });

        mAllStartNameTextField.setText("haarcascade_test_v0");

        {
            ContextMenu cm = new ContextMenu();
            MenuItem itemClearAll = new CheckMenuItem("clear");
            itemClearAll.setOnAction((ActionEvent e) -> {
                mInfoListView.getItems().clear();
            });

            cm.getItems().add(itemClearAll);
            mInfoListView.setContextMenu(cm);
        }

        mAllStartLeaningStopButton.setOnAction((ActionEvent event) -> {
            synchronized (mRunningLeaningProcess) {
                mRunningLeaningProcess.stream().forEach((Process process) -> {
                    process.destroy();
                });
            }
        });

        mAllStartNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateCheckCanStartLeaning();
        });
        mAllStartLeaningOutputDirectoryTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateCheckCanStartLeaning();
        });
        mAllStartPictureSaveDirectoryTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateCheckCanStartLeaning();
        });

        updateAllStartLeaningStopButton();
        updateCheckCanStartLeaning();
    }

    public void publishInfo(String text) {
        mInfoListView.getItems().add(text);
        //mInfoText.append(text);
        //mInfoText.append('\n');
        //mInfoTextArea.appendText(text + '\n');
        //mInfoTextArea.setText(mInfoText.toString());
    }

    public void publishProgress(int current, int max) {
        if (max != 0) {
            mInfoProgressBar.setProgress(current / (double) max);
            mInfoLabel.setText(String.format("%d / %d (%d %%)", current, max, current * 100 / max));
        } else {
            mInfoProgressBar.setProgress(0);
            mInfoLabel.setText("-");
        }
    }

    public void postPublishInfo(String text) {
        HandlerUtil.post(() -> {
            publishInfo(text);
        });
    }

    public void postPublishProgress(int current, int max) {
        HandlerUtil.post(() -> {
            publishProgress(current, max);
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {

    }

    @FXML
    private void handleSetupButtonAction(ActionEvent event) {
        setupLeaning();
    }

    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        startLeaning();
    }

    @FXML
    private void handleStartLireIndexerButtonAction(ActionEvent event) {

        File dir = new File(mLireIndexerDirectoryTextField.getText());

        LireIndexer maker = new LireIndexer();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());

        HandlerUtil.postBackground(() -> {
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

        HandlerUtil.postBackground(() -> {
            maker.execute(searchFile, new File(dir, "index-lire"));

            HandlerUtil.post(() -> {

                publishInfo("complete.");
                publishProgress(0, 0);
            });
        });

    }

    @FXML
    private void handleDownloadButtonAction(ActionEvent event) {

        long foodId = pickFoodId(mMakeOkFoodIdTextField.getEditor().getText());

        File saveDir = new File(mSaveDirectoryTextField.getText());
        if (!saveDir.isDirectory()) {
            return;
        }

        File outEntityFile = new File(saveDir, "ok_food_" + foodId + ".txt");

        Downloader downloader = new Downloader();
        downloader.setHostAndPort(mHostAndPortTextField.getText());
        downloader.setAccessToken(mAccessTokenTextField.getText());
        downloader.getPublisher().setOnProgressListener(new DefaultProgressListener());

        HandlerUtil.postBackground(() -> {
            downloader.executeByFoodId(foodId, saveDir, outEntityFile, true);

            HandlerUtil.post(() -> {
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
        File fromDir = new File(mMakeNgListFileTextField.getText());
        if (!fromDir.isDirectory()) {
            publishInfo("not directory, from directory.");
            return;
        }

        String strNgFile = mSaveMakeNgListFileTextField.getText();
        //File okFile = new File("OK.txt");
        File ngFile = new File(strNgFile);

        Downloader maker = new Downloader();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());

        HandlerUtil.postBackground(() -> {
            maker.executeCreateNgFileList(fromDir, ngFile, false);

            HandlerUtil.post(() -> {
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

        HandlerUtil.postBackground(() -> {

            boolean ret = maker.executeCreateVectorFile(vecFile, okFile, ngFile);

            HandlerUtil.post(() -> {
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

        Leaener leaener = new Leaener();
        leaener.getPublisher().setOnProgressListener(new DefaultProgressListener());

        HandlerUtil.postBackground(() -> {
            leaener.excecute(vecFile, okFile, ngFile, outTraningDir, null);

            HandlerUtil.post(() -> {

                publishInfo("completed.");
                publishProgress(0, 0);
            });
        });

    }

    @FXML
    private void handleGoWebViewAction(ActionEvent event) {

        final String url = (mWebViewUrlBoxField.getText());
        final WebView webView = mWebView;
        final WebEngine engine = webView.getEngine();
        final RRCroller croller = new RRCroller();
        croller.setHostAndPort(mHostAndPortTextField.getText());
        croller.setAccessToken(mAccessTokenTextField.getText());
        croller.getPublisher().setOnProgressListener(new DefaultProgressListener());
        PreferenceManager.putLatestUrl(url);

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                Log.d(TAG, "" + newState.name());

                ChangeListener self = this;
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    engine.getLoadWorker().stateProperty().removeListener(self);

                    Document document = engine.getDocument();
                    List<RRCroller.CrollItem> cis = croller.parse(document);
                    String nextUrl = croller.parseNextPage(document);

                } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                    engine.getLoadWorker().stateProperty().removeListener(self);
                } else if (newState == javafx.concurrent.Worker.State.CANCELLED) {
                    engine.getLoadWorker().stateProperty().removeListener(self);
                }
            }
        });

        engine.load(url);
    }

    @FXML
    private void handleCancelWebViewAction(ActionEvent event) {

        final WebView webView = mWebView;
        final WebEngine engine = webView.getEngine();

        engine.getLoadWorker().cancel();
    }

    @FXML
    private void handleBackWebViewAction(ActionEvent event) {

        final WebView webView = mWebView;
        final WebEngine engine = webView.getEngine();

        WebHistory history = engine.getHistory();
        if (history.getCurrentIndex() > 0) {
            history.go(-1);
        }

    }
    
    @FXML
    private void handleBackAllWebViewAction(ActionEvent event) {

        final WebView webView = mWebView;
        final WebEngine engine = webView.getEngine();

        WebHistory history = engine.getHistory();
        history.go(-history.getCurrentIndex());

    }

    @FXML
    private void handleStartWebViewAction(ActionEvent event) {

        //final String url = (mWebViewUrlTextField.getText());
        ComboBox cb = mWebViewFoodIdField;
        final Long foodId = Long.valueOf(cb.getEditor().getText().split(" ", 2)[0]);

        final CheckBox autoNextCheckBox = mWebViewAutoNextCheckBox;
        final WebView webView = mWebView;
        final Button startButton = mWebViewStartButton;
        //final ProgressBar progressBar = mWebViewProgressBar;
        final WebEngine engine = webView.getEngine();

        final RRCroller croller = new RRCroller();
        croller.setHostAndPort(mHostAndPortTextField.getText());
        croller.setAccessToken(mAccessTokenTextField.getText());
        croller.getPublisher().setOnProgressListener(new DefaultProgressListener());

        WebHistory history = engine.getHistory();
        history.setMaxSize(1000);
        startButton.setDisable(true);

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {

            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                //Log.d(TAG, "" + newState.name());
                publishInfo("" + newState.name());

                ChangeListener self = this;
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {

                    Document document = engine.getDocument();
                    List<RRCroller.CrollItem> cis = croller.parse(document);
                    String nextUrl = croller.parseNextPage(document);

                    HandlerUtil.postBackground(() -> {
                        boolean completed = false;
                        p1 : try {
                            if (!croller.execute(cis, foodId)) {
                                break p1;
                            }
                            completed = true;
                        } catch (Exception ex) {
                            Log.d(TAG, "", ex);
                        }
                        
                        HandlerUtil.post(() -> {
                            boolean completedAll = true;
                            if (nextUrl != null) {
                                if (autoNextCheckBox.isSelected()) {
                                    engine.load(nextUrl);
                                    completedAll = false;
                                }
                            }
                            if (completedAll) {
                                engine.getLoadWorker().stateProperty().removeListener(self);
                                startButton.setDisable(false);
                                publishInfo("completed all.");
                            }
                        });                        

                    });

                } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                    engine.getLoadWorker().stateProperty().removeListener(self);
                    startButton.setDisable(false);
                } else if (newState == javafx.concurrent.Worker.State.CANCELLED) {
                    engine.getLoadWorker().stateProperty().removeListener(self);
                    startButton.setDisable(false);
                }
            }
        });

        engine.load(engine.getLocation());

    }

    @FXML
    private void handleReloadFoodList(ActionEvent event) {
        publishInfo("reload start.");
        requestReloadFoodList((Boolean success) -> {
            if (success) {
                publishInfo("reload success.");
            } else {
                publishInfo("reload failed.");
            }
            return null;
        });
    }

    private void requestReloadFoodList(Callback<Boolean, Void> callback) {
        final ApiExecutor api = mApiExecutor;
        api.setHostAndPort(mHostAndPortTextField.getText());
        api.setAccessToken(mAccessTokenTextField.getText());
        HandlerUtil.postBackground(() -> {
            Food[] foods = api.executeRequestFoodList();
            HandlerUtil.post(() -> {
                if (foods == null) {
                    if (callback != null) {
                        callback.call(false);
                    }
                    return;
                }
                mWebViewFoodIdField.getItems().setAll((Object[]) foods);
                mAllStartFoodIdTextField.getItems().setAll((Object[]) foods);

                if (callback != null) {
                    callback.call(true);
                }
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

    private class FoodStringConverter extends StringConverter<Food> {

        @Override
        public String toString(Food object) {
            if (object == null || object.food_id == null) {
                return "";
            }
            return String.format("%d %s", object.food_id, object.food_name);
        }

        @Override
        public Food fromString(String string) {
            if (string.length() == 0) {
                return null;
            }
            Long id = pickFoodId(string);
            if (id == null) {
                return null;
            }
            return mApiExecutor.getCacheFood(id);
        }

    }

    private static String formatFoodId(Food obj) {
        try {
            return String.format("%d %s", obj.food_id, obj.food_name);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Long pickFoodId(String str) {
        try {
            return Long.valueOf(str.split(" ", 2)[0]);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * leaning ready.
     */
    private void setupLeaning() {

        String title = mAllStartNameTextField.getText();
        long foodId = pickFoodId(mAllStartFoodIdTextField.getEditor().getText());
        File savePictureDir = new File(mAllStartPictureSaveDirectoryTextField.getText());
        List<Long> ngIds = Arrays.asList(mAllStartNgFoodIdListTextArea.getText().split("\n")).stream().map((String str) -> {
            return pickFoodId(str);
        }).filter((id) -> (id != null)).collect(Collectors.toList());
        File defNgDir = new File(mAllStartDefaultNgDirectoryTextField.getText());
        File defNgListFile = new File(savePictureDir, "ng_def_" + title + ".txt");
        File okListFile = new File(savePictureDir, "ok_" + title + ".txt");
        File ngAllListFile = new File(savePictureDir, "ng_" + title + ".txt");
        File vecFile = new File(savePictureDir, "" + title + ".vec");

        Downloader downloader = new Downloader();
        downloader.setHostAndPort(mHostAndPortTextField.getText());
        downloader.setAccessToken(mAccessTokenTextField.getText());
        downloader.getPublisher().setOnProgressListener(new DefaultProgressListener());

        SamplerMaker maker = new SamplerMaker();
        maker.getPublisher().setOnProgressListener(new DefaultProgressListener());

        Leaener leaener = new Leaener();
        leaener.getPublisher().setOnProgressListener(new DefaultProgressListener());

        final Button setupButton = mAllStartLeaningSetupButton;
        setupButton.setDisable(true);

        HandlerUtil.postBackground(() -> {

            boolean completedAll = false;
            try {

                if (!downloader.executeByFoodId(foodId, savePictureDir, okListFile, true)) {
                    postPublishInfo("failed ok.");
                    return;
                }
                postPublishInfo("success ok.");

                List<File> mergeNgFiles = new ArrayList<>();

                if (!downloader.executeCreateNgFileList(defNgDir, defNgListFile, true)) {
                    postPublishInfo("failed def ng.");
                    return;
                }
                mergeNgFiles.add(defNgListFile);
                postPublishInfo("success def ng.");

                for (long ngId : ngIds) {
                    File ngEachListFile = new File(savePictureDir, "ng_" + ngId + "_" + title + ".txt");
                    if (!downloader.executeByFoodId(ngId, savePictureDir, ngEachListFile, false)) {
                        postPublishInfo("failed each ng" + ngId + ".");
                        return;
                    }
                    postPublishInfo("success each ng.");
                    mergeNgFiles.add(ngEachListFile);
                }
                postPublishInfo("success all ng.");

                if (!Downloader.mergeFile(mergeNgFiles, ngAllListFile)) {
                    postPublishInfo("failed ng merge.");
                    return;
                }
                postPublishInfo("success merge file.");

                // ok and ng file list ready completed.
                // create vec file.
                if (!maker.executeCreateVectorFile(vecFile, okListFile, ngAllListFile)) {
                    postPublishInfo("failed create vec file.");
                    return;
                }
                postPublishInfo("success create vec file.");

                // 
                // leaener.excecute(vecFile, okFile, ngFile, outTraningDir);            
                completedAll = true;

            } finally {

                HandlerUtil.post(() -> {
                    publishProgress(0, 0);
                    updateCheckCanStartLeaning();
                    setupButton.setDisable(false);
                });

                if (completedAll) {
                    HandlerUtil.post(() -> {
                        publishInfo("completed setup.");
                    }
                    );
                }

            }

        });

    }

    private final HashSet<Process> mRunningLeaningProcess = new HashSet<>();

    private void updateAllStartLeaningStopButton() {
        int count;
        synchronized (mRunningLeaningProcess) {
            count = mRunningLeaningProcess.size();
        }
        Button button = mAllStartLeaningStopButton;
        if (count > 0) {
            button.setText("Stop (" + count + ")");
            button.setDisable(false);
        } else {
            button.setText("Stop");
            button.setDisable(true);
        }
    }

    private void updateCheckCanStartLeaning() {
        boolean check;
        try {
            LeaningInfo info = new LeaningInfo();
            check = info.check();
        } catch (Exception ex) {
            check = false;
        }
        mAllStartLeaningStartButton.setDisable(!check);
    }

    private class LeaningInfo {

        final String title = mAllStartNameTextField.getText();
        final File savePictureDir = new File(mAllStartPictureSaveDirectoryTextField.getText());
        final File vecFile = new File(savePictureDir, "" + title + ".vec");
        final File okListFile = new File(savePictureDir, "ok_" + title + ".txt");
        final File ngAllListFile = new File(savePictureDir, "ng_" + title + ".txt");

        final File outputDir = new File(mAllStartLeaningOutputDirectoryTextField.getText());
        final File traningDir = new File(outputDir, "" + title + "");

        public boolean check() {
            if (title.length() == 0) {
                return false;
            }
            if (!savePictureDir.isDirectory()) {
                return false;
            }
            if (!vecFile.isFile()) {
                return false;
            }
            if (!okListFile.isFile()) {
                return false;
            }
            if (!ngAllListFile.isFile()) {
                return false;
            }
            if (!outputDir.isDirectory()) {
                return false;
            }
            return true;
        }
    ;

    };
    
    private void startLeaning() {

        final LeaningInfo info = new LeaningInfo();
        if (!info.check()) {
            return;
        }

        Leaener leaener = new Leaener();
        leaener.getPublisher().setOnProgressListener(new DefaultProgressListener());

        HandlerUtil.postBackground(() -> {
            // leaning
            boolean success = leaener.excecute(
                    info.vecFile, info.okListFile, info.ngAllListFile, info.traningDir,
                    new Leaener.OnProcessListener() {
                        @Override
                        public void onStart(Process process) {
                            synchronized (mRunningLeaningProcess) {
                                mRunningLeaningProcess.add(process);
                            }
                            HandlerUtil.post(() -> {
                                updateAllStartLeaningStopButton();
                            });
                        }

                        @Override
                        public void onStop(Process process) {
                            synchronized (mRunningLeaningProcess) {
                                mRunningLeaningProcess.remove(process);
                            }
                            HandlerUtil.post(() -> {
                                updateAllStartLeaningStopButton();
                            });
                        }
                    }
            );
            if (!success) {
                postPublishInfo("failed leaning.");
                return;
            }
            postPublishInfo("success leaning.");

            HandlerUtil.post(() -> {
                publishInfo("completed leaning.");
                publishProgress(0, 0);
            }
            );
        });

    }

}
