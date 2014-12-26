/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.crudefox.mymon.picturetool.util;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author chikara
 */
public class FileChooseButtonEventHandler implements EventHandler<ActionEvent>{
    
    public enum Mode {
        Directory,
        OpenFile,
        SaveFile
    }
    
    
    private Mode mMode;
    private TextField mTextField;
    
    public FileChooseButtonEventHandler (Mode mode, TextField textField) {
        mMode = mode;
        mTextField = textField;
    }
    
    @Override
    public void handle(ActionEvent event) {
        Window window = mTextField.getScene().getWindow();
        
        File file = null;
        
        switch (mMode) {
            case Directory:{
                DirectoryChooser chooser = new DirectoryChooser();
                file = chooser.showDialog(window);                                                
            } break;
            case SaveFile:{
                FileChooser chooser = new FileChooser();
                file = chooser.showSaveDialog(window);
            } break;
            case OpenFile:{
                FileChooser chooser = new FileChooser();
                file = chooser.showOpenDialog(window);                
            } break;                
        }         

        if (file!=null) {
            mTextField.setText(file.getAbsolutePath());
        }
    }    
    
}
