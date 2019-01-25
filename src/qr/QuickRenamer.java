package qr;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class QuickRenamer extends Application {
    private Stage primaryStage;
    private DirectoryChooser dcSomedir;

    public TextField tfFolder, tfNewName, tfNewFormat, tfNewCount;
    public Button btnGO;
    public RadioButton rb1, rb2;
    public ToggleButton tglSize, tglDate, tglTime, tglReverse;
    public ProgressIndicator inprogress;

    private static int countmod;
    private static String modifiedName, path, newFormat;

    private static boolean addsize, adddate, addtime, addreverse, addnewformat, addnewcount = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("qr.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Quick Renamer v5.0");
        this.primaryStage.setScene(new Scene(root, 600, 242));
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    public void rename(ActionEvent actionEvent) {
        try {
            confirmSettings();
            runRenamer();
            restoreSettings();
        }
        catch (NullPointerException exc) {
            exc.printStackTrace();
        }
    }

    private void confirmSettings() {
        // Потверждение нового имени
        if (tfNewName.getText().equals("")) {
            String defaultname = path.substring(path.lastIndexOf("\\"));
            modifiedName = defaultname.replace("\\", "");

            System.out.println("New name is set by default: " + defaultname);
        }
        else modifiedName = tfNewName.getText();

        // Подтверждение нового формата
        if (rb1.isSelected()) {
            newFormat = "." + tfNewFormat.getText();
            addnewformat = true;
        }

        // Подтверждение нового счетчика
        if (rb2.isSelected()) {
            countmod = Integer.parseInt(tfNewCount.getText());
        }
    }

    private void runRenamer() {
        try {
            inprogress.setDisable(false);
            inprogress.setVisible(true);
            btnGO.setText("");

            Renamer renamer = new Renamer();
            renamer.rename();
            renamer.showStats(renamer.container);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void restoreSettings() {
        tfNewName.clear();
        tfNewName.setPromptText("Enter new name here");

        countmod = 0;
        rb2.setSelected(false);
        tfNewCount.clear();
        tfNewCount.setPromptText("New count");
        tfNewCount.setDisable(true);
        addnewcount = false;

        modifiedName = "";

        inprogress.setVisible(false);
        inprogress.setDisable(true);

        btnGO.setText("DONE");
    }

    // Выбор директории
    public void selectFolder(ActionEvent actionEvent) {
        dcSomedir = new DirectoryChooser();
        configSelectedDirectory(dcSomedir);

        File dir = dcSomedir.showDialog(primaryStage);
        if (dir != null) {
            path = dir.getAbsolutePath();
            tfFolder.setText("Path: " + dir.getAbsolutePath());
            btnGO.setDisable(false);
            btnGO.setText("GO");
        }
        else {
            tfFolder.setText(null);
        }
    }
    // Настройка директории
    private void configSelectedDirectory(DirectoryChooser directoryChooser) {
        dcSomedir.setInitialDirectory(new File(System.getProperty("user.home")));
        dcSomedir.setTitle("Select folder");
    }

    // Добавить новый формат
    public void addNewFormat(ActionEvent actionEvent) {
        if (rb1.isSelected()) {

            tfNewFormat.setDisable(false);
            addnewformat = true;
        }
        else {
            tfNewFormat.clear();
            tfNewFormat.setPromptText("New format");
            tfNewFormat.setDisable(true);
            addnewformat = false;
        }
    }
    // Добавить новый счетчик
    public void addNewCount(ActionEvent actionEvent) {
        if (rb2.isSelected()) {
            tfNewCount.setDisable(false);
            addnewcount = true;
        }
        else {
            tfNewCount.clear();
            tfNewCount.setPromptText("New count");
            tfNewCount.setDisable(true);
            countmod = 0;
            addnewcount = false;
        }
    }

    // Триггеры дополнительных настроек
    public void addSize(ActionEvent actionEvent) {
        addsize = tglSize.isSelected();
    }
    public void addDate(ActionEvent actionEvent) {
        adddate = tglDate.isSelected();
    }
    public void addTime(ActionEvent actionEvent) {
        addtime = tglTime.isSelected();
    }
    public void reverseOrder(ActionEvent actionEvent) {
        addreverse = tglReverse.isSelected();
    }
    static boolean onlyDefaultSettings() {
        if (!addtime && !adddate && !addsize) return true;
        else return false;
    }

    // Проверка условий
    static boolean date() {
        return adddate;
    }
    static boolean time() {
        return addtime;
    }
    static boolean size() {
        return addsize;
    }
    static boolean reverseOrder() {
        return addreverse;
    }
    static boolean addNewFormat () {
        return addnewformat;
    }
    static boolean addNewCount() {
        return addnewcount;
    }

    // Геттеры
    static String getModifiedName() {
        return modifiedName + " ";
    }
    static String getPath() {
        return path + "\\";
    }
    static String getNewFormat() {
        return newFormat;
    }
    static int setNewCount() {
        return countmod;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
