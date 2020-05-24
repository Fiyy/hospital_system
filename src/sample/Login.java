/**
 * FileName: Login
 * Author:   Zhang Yun
 * Date:     2020/5/19 17:35
 * Description:
 * History:
 */
package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.SimpleTimeZone;

/**
 * 〈一句话功能简述〉<br>
 * @author Zhang Yun
 * @create 2020/5/19
 * @since 1.0.0
 */
public class Login implements Initializable {

    @FXML
    public JFXRadioButton doctorRadioButton;
    @FXML
    public JFXRadioButton patientRadioButton;
    @FXML
    public JFXPasswordField passwordField;
    @FXML
    public JFXButton closeButton;
    @FXML
    public JFXButton loginButton;
    @FXML
    public JFXTextField usernameField;
    @FXML
    public Label passwordLabel;
    @FXML
    public Label usernameLabel;

    private Stage stage;
    @FXML
    public void onClick(javafx.event.ActionEvent actionEvent) throws IOException, SQLException {
        stage = Main.stage;
        if (usernameField.getText().isEmpty()) {
            usernameLabel.setVisible(true);
            usernameLabel.setText("账号为空!");
            JOptionPane.showMessageDialog(new JFrame().getContentPane(),
                    "用户名为空！请输入用户名", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }
        else if (passwordField.getText().isEmpty()) {
            passwordLabel.setVisible(true);
            passwordLabel.setText("密码为空！");
            JOptionPane.showMessageDialog(new JFrame().getContentPane(),
                    "密码为空！请输入密码", "警告", JOptionPane.WARNING_MESSAGE);
        }
        else if(!doctorRadioButton.isSelected() && !patientRadioButton.isSelected()){
            JOptionPane.showMessageDialog(new JFrame().getContentPane(),
                    "请选择登录身份！", "警告", JOptionPane.WARNING_MESSAGE);
        }
        else {
            Connection connection = ConnectionFactory.connect(Main.URL,Main.username, Main.password);
            Statement statement =connection.createStatement();
            String sql = null;
            ResultSet resultSet =null;
            String username = usernameField.getText();
            String password = passwordField.getText();
            // 医生身份登录
            if (doctorRadioButton.isSelected()) {
                sql = "select DLKL from T_KSYS " + "where YSBH = '"+username+"'";
                resultSet = statement.executeQuery(sql);
                if(resultSet.next()){
                    String right_pass = resultSet.getString("DLKL");
                    if(right_pass.equals(password)){
                        Main.name = username;
                        System.out.println(Main.name);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        sql = "update T_KSYS set DLRQ = " + "'"+simpleDateFormat.format(new Date())+"' where YSBH= '"+username+"'";
                        statement.executeUpdate(sql);
                        System.out.println("登录成功");
                        Main.setDoctor();
                    }
                    else{
                        JOptionPane.showMessageDialog(new JFrame().getContentPane(), "密码错误！", "警告", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(new JFrame().getContentPane(), "账户不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                }
                usernameField.clear();
                passwordField.clear();
                resultSet.close();
                statement.close();
                connection.close();
            }
            // 患者身份登录
            else {
                sql = "select DLKL from T_BRXX " + "where BRBH = '"+username+"'";
                resultSet = statement.executeQuery(sql);
                if(resultSet.next()){
                    String right_pass = resultSet.getString("DLKL");
                    if(right_pass.equals(password)){
                        Main.name = username;
                        System.out.println(Main.name);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        sql = "update T_BRXX set DLRQ = " + "'"+simpleDateFormat.format(new Date())+"' where BRBH= '"+username+"'";
                        statement.executeUpdate(sql);
                        System.out.println("登录成功");
                        Main.setPatient();
                    }
                    else{
                        JOptionPane.showMessageDialog(new JFrame().getContentPane(), "密码错误！", "警告", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(new JFrame().getContentPane(), "账户不存在！", "警告", JOptionPane.WARNING_MESSAGE);
                }
                usernameField.clear();
                passwordField.clear();
                resultSet.close();
                statement.close();
                connection.close();
            }
        }
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameLabel.setVisible(true);
        passwordLabel.setVisible(true);
        patientRadioButton.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal)
                doctorRadioButton.setSelected(false);
            else
                doctorRadioButton.setSelected(true);
        });
        doctorRadioButton.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal)
                patientRadioButton.setSelected(false);
            else
                patientRadioButton.setSelected(true);
        });
        usernameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!passwordField.getText().isEmpty()) {
                    passwordField.clear();
                }
            }
        });
        usernameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(() -> {
                    if (usernameField.isFocused()) {
                        usernameLabel.setVisible(false);
                    }
                    if (usernameField.isFocused() && !usernameField.getText().isEmpty()) {
                        usernameField.selectAll();
                    }
                });
            }
        });
        passwordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(() -> {
                    if (passwordField.isFocused()) {
                        passwordLabel.setVisible(false);
                    }
                });
            }
        });
    }
    public void closeClick(ActionEvent actionEvent) {
        Event.fireEvent(Main.stage, new WindowEvent(Main.stage, WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
}