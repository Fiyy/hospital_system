/**
 * FileName: Patient
 * Author:   Zhang Yun
 * Date:     2020/5/21 11:44
 * Description:
 * History:
 */
package sample;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.mysql.cj.xdevapi.SqlResult;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Zhang Yun
 * @create 2020/5/21
 * @since 1.0.0
 */
public class Patient implements Initializable
{
    @FXML
    public Label changeAccountLabel;    // 找零
    @FXML
    public Label payAccountLabel;       // 付款
    @FXML
    public Label accountLabel;          // 账户余额
    @FXML
    public Label welcomeLabel;          // 加名字
    @FXML
    public JFXTextField filterField;
    @FXML
    public JFXComboBox<String> deptBox;         //科室
    @FXML
    public JFXComboBox<String> doctorBox;       //医生
    @FXML
    public JFXComboBox<String> typeBox;         //挂号种类
    @FXML
    public JFXComboBox<String> nameBox;         //挂号名称
    public Label timeLabel;

    String patient_name;
    double patient_account;
    boolean isChanged;
    String selectOfficeNum, selectDoctorNum, selectRegName, selectRegtypeName;
    double reg_cost;
    String dep_num;
    String ysbh;
    boolean isCompleted;
    boolean isEnough; // 是否足够用余额付款
    double needtoPay; // 余额不足时至少需要付款
    boolean isExpert;
    Vector<String> office_list,cut_of_list,doc_list,reg_list;
    @FXML
    private TextField text_pay;
    HashMap<String,String> kxbhToKxmc = new HashMap<>();
    HashMap<String,String> ysmcToYsbh = new HashMap<>();
    HashMap<String,String> hzmcToHzbh = new HashMap<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String dateString = LocalDate.now().toString() + " 00:00:00";
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 显示当前时间
        DateFormat datetimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String curTimeStr = datetimeFormat.format(timestamp);
            timeLabel.setText(curTimeStr);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // 初始钱为0
        payAccountLabel.setText("");
        changeAccountLabel.setText("");

        Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
        Statement statement1 = null;
        office_list = new Vector<>();
        cut_of_list = new Vector<>();
        doc_list = new Vector<>();
        reg_list = new Vector<>();
        try {
            statement1 = connection.createStatement();
            String sql = "select KSBH,KSMC from T_KSXX";
            ResultSet rs = statement1.executeQuery(sql);
            String office_name, office_num;
            deptBox.getItems().clear();
            office_list.clear();
            while (rs.next()) {
                office_num = rs.getString("KSBH");
                office_name = rs.getString("KSMC");
                kxbhToKxmc.put(office_name, office_num);
                deptBox.getItems().add(office_name);
                office_list.add(office_name);
            }
            rs.close();
            statement1.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
        deptBox的值改变（从列表中选中或编辑完成）时，清空后续comboBox，检查当前comboBox的值是否合法，不合法则清空。
         */
        deptBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue!= null && !newValue.equals("") && !newValue.equals(oldValue)) {
                    doctorBox.getEditor().clear();
                    typeBox.getEditor().clear();
                    nameBox.getEditor().clear();
                    payAccountLabel.setText("");
                    List itemList = deptBox.getItems();
                    if (!office_list.contains(newValue)) {
                        JOptionPane.showMessageDialog(new JFrame().getContentPane(), "请输入正确的科室！" + newValue + "不合法！", "警告", JOptionPane.WARNING_MESSAGE);
                        deptBox.getEditor().clear();
                        newValue = null;
                    }
                    else{
                        dep_num = kxbhToKxmc.get(newValue);
                        try {
                            Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                            Statement statement1 = connection.createStatement();
                            String doc_name, doc_num;
                            dep_num = kxbhToKxmc.get(newValue);
                            System.out.println("科室标号是" + dep_num);
                            String sql = "select YSBH,YSMC from T_KSYS " + "where KSBH =" + "'"+dep_num+"'";
                            ResultSet rs = statement1.executeQuery(sql);
                            ysmcToYsbh.clear();
                            cut_of_list.clear();
                            doctorBox.getItems().clear();
                            while (rs.next()) {
                                doc_num = rs.getString("YSBH");
                                doc_name = rs.getString("YSMC");
                                ysmcToYsbh.put(doc_name,doc_num);
                                doctorBox.getItems().add(doc_name);
                                cut_of_list.add(doc_name);
                            }
                            selectOfficeNum = dep_num;
                            doctorBox.show();
                            rs.close();
                            statement1.close();
                            connection.close();
                        } catch (SQLException se) {
                            se.printStackTrace();
                        }
                    }
                }
            }
        });
        doctorBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue!= null && !newValue.equals("") && !newValue.equals(oldValue)) {
                    typeBox.getEditor().clear();
                    nameBox.getEditor().clear();
                    payAccountLabel.setText("");
                    if(cut_of_list.contains(newValue)){
                        try {
                            isCompleted = false;
                            selectDoctorNum = ysmcToYsbh.get(newValue);
                            Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                            Statement statement = connection.createStatement();
                            String sql = "select SFZJ from T_KSYS " + "where YSBH =" + "'"+ selectDoctorNum +"'";
                            ResultSet rs = statement.executeQuery(sql);
                            while (rs.next()) {
                                isExpert = rs.getBoolean("SFZJ");
                            }
                            typeBox.getItems().clear();
                            if(isExpert){ // 是专家
                                typeBox.getItems().add("专家号");
                                typeBox.getItems().add("普通号");
                            }
                            else{
                                typeBox.getItems().add("普通号");
                            }
                            typeBox.show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                    try {
                        Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                        Statement statement1 = connection.createStatement();
                        String doc_name, doc_num;
                        String sql = "select YSBH,YSMC from T_KSYS " + "where KSBH =" + "'"+ selectOfficeNum +"' and " +"PYZS like '" + newValue + "%'";
                        ResultSet rs = statement1.executeQuery(sql);
                        doctorBox.getItems().clear();
                        while (rs.next()) {
                            doc_num = rs.getString("YSBH");
                            doc_name = rs.getString("YSMC");
                            doctorBox.getItems().add(doc_name);
                            // doc_list记录的是首字母检索出来的医生名称，cut_of_list记录的是部门所有的医生名称
                        }
                        doctorBox.show();
                        rs.close();
                        statement1.close();
                        connection.close();
                    } catch (SQLException se) {
                            se.printStackTrace();
                    }
                    }
                }
                else if(newValue.equals("")){
                    doctorBox.getItems().clear();
                    for(String name : cut_of_list){
                        doctorBox.getItems().add(name);
                    }
                    doctorBox.show();
                }
            }
        });
        typeBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue!= null && !newValue.equals("") && !newValue.equals(oldValue)) {
                    nameBox.getEditor().clear();
                    payAccountLabel.setText("");
                    if(newValue.equals("专家号") || newValue.equals("普通号")){
                        isCompleted = false;
                        try {
                            selectRegtypeName = newValue;
                            Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                            Statement statement = connection.createStatement();
                            int expert = newValue.equals("专家号")?1:0;
                            isExpert = (expert==1);
                            String sql = "select HZBH,HZMC from T_HZXX " + "where KSBH =" + "'"+ selectOfficeNum +"' and SFZJ = '" + expert +"'";
                            ResultSet rs = statement.executeQuery(sql);
                            nameBox.getItems().clear();
                            while(rs.next()){
                                String reg_num = rs.getString("HZBH");
                                String reg_name = rs.getString("HZMC");
                                reg_list.add(reg_name);
                                hzmcToHzbh.put(reg_name, reg_num);
                                nameBox.getItems().add(reg_name);
                            }
                            nameBox.show();
                            rs.close();
                            statement.close();
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        typeBox.getItems().clear();
                        String type = newValue.toLowerCase();
                        if(isExpert && type.length()==1 && type.equals("z") || type.length() == 2 &&type.equals("zj") || type.length()==3 && type.equals("zjh"))
                        {
                            typeBox.getItems().add("专家号");
                        }
                        else if(type.length()==1 && type.equals("p") || type.length() == 2 &&type.equals("pt") || type.length()==3 && type.equals("pth")){
                            typeBox.getItems().add("普通号");
                        }
                        typeBox.show();
                    }
                }
                else if(newValue.equals("")){
                    typeBox.getItems().clear();
                    if(isExpert) typeBox.getItems().add("专家号");
                    typeBox.getItems().add("普通号");
                    typeBox.show();
                }
            }
        });
        nameBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue!= null && !newValue.equals("") && !newValue.equals(oldValue)) {
                    payAccountLabel.setText("");
                    if(reg_list.contains(newValue)){
                        isCompleted = true;
                        try {
                            selectRegName = newValue;
                            Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                            Statement statement = connection.createStatement();
                            String reg_num = hzmcToHzbh.get(newValue);
                            String sql = "select GHFY from T_HZXX where " + "HZBH = '"+reg_num+"'";
                            selectRegName = newValue;
                            ResultSet rs = statement.executeQuery(sql);
                            while(rs.next()){
                                reg_cost = rs.getDouble("GHFY");
                            }
                            if(reg_cost <= patient_account){
                                isEnough = true;
                                text_pay.setEditable(false);
                                text_pay.setText("无需交款");
                                needtoPay = 0.0;
                            }else{
                                text_pay.clear();
                                isEnough = false;
                                text_pay.setEditable(true);
                                needtoPay = reg_cost - patient_account;
                            }
                            payAccountLabel.setText(Double.toString(reg_cost));
                            rs.close();
                            statement.close();
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                            Statement statement = connection.createStatement();
                            int expert = isExpert==true?1:0;
                            String sql = "select HZBH,HZMC from T_HZXX " + "where KSBH =" + "'"+ selectOfficeNum +"' and SFZJ = '" + expert +"' and " +"PYZS like '" + newValue + "%'";
                            ResultSet rs = statement.executeQuery(sql);
                            nameBox.getItems().clear();
                            while(rs.next()){
                                String reg_num = rs.getString("HZBH");
                                String reg_name = rs.getString("HZMC");
                                nameBox.getItems().add(reg_name);
                            }
                            nameBox.show();
                            rs.close();
                            statement.close();
                            connection.close();
                        } catch (SQLException se) {
                            se.printStackTrace();
                        }
                    }
                }
                else if(newValue.equals("")){
                    nameBox.getItems().clear();
                    for(String name : reg_list){
                        nameBox.getItems().add(name);
                    }
                    nameBox.show();
                }
            }
        });
        text_pay.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus

                double payment = 0;
                if(!isEnough && text_pay.getText()!=null && !text_pay.getText().equals(""))
                    payment= Double.parseDouble(text_pay.getText());
                double changeAccout = patient_account + payment - reg_cost;
                System.out.println(text_pay.getText());
                changeAccountLabel.setText(String.valueOf(changeAccout));
            }
        });
    }
    @FXML
    public void closeClick(ActionEvent actionEvent) {
        Event.fireEvent(Main.stage, new WindowEvent(Main.stage, WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
    @FXML
    public void backClick(ActionEvent actionEvent) {
        Main.setLogin();
    }

    public void confirmClick(ActionEvent actionEvent) {
        if(isChanged == false){
            JOptionPane.showMessageDialog(new JFrame().getContentPane(), "填写挂号信息！", "警告", JOptionPane.WARNING_MESSAGE);
        }
        else if(isEnough == false && (text_pay.getText() == null || text_pay.getText().equals(""))){
            JOptionPane.showMessageDialog(new JFrame().getContentPane(), "余额不足，输入缴费金额！", "警告", JOptionPane.WARNING_MESSAGE);
        }
        else if(isEnough == false && needtoPay > Double.parseDouble(text_pay.getText()))
        {
            JOptionPane.showMessageDialog(new JFrame().getContentPane(), "缴费金额不足！", "警告", JOptionPane.WARNING_MESSAGE);
        }
        else {
            try {
                String currtime = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
                Statement statement1 = connection.createStatement();
                String regNum = hzmcToHzbh.get(selectRegName);
                String sql = "select GHRS from T_HZXX where " + "HZBH='" + regNum + "'";
                int max_pat = 0;
                ResultSet rs = statement1.executeQuery(sql);
                if (rs.next()) {
                    max_pat = rs.getInt("GHRS");
                }

                sql = "select current_date as currtime";
                rs = statement1.executeQuery(sql);
                if (rs.next()) {
                    currtime = rs.getString("currtime");
                    currtime += " 00:00:00";
                }
                int regcount = 0;
                int registernum = 0;
                sql = "select count(*) as regcount from T_GHXX " + "where HZBH ='" + regNum + "' and " + "RQSJ>='" + currtime + "' and THBZ=0";
                rs = statement1.executeQuery(sql);
                if (rs.next()) {
                    regcount = rs.getInt("regcount");
                }
                if (regcount >= max_pat) {
                    System.out.println(regcount);
                    System.out.println(max_pat);
                    JOptionPane.showMessageDialog(new JFrame().getContentPane(),
                            "该号种已经挂满！", "警告", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    sql = "select count(*) as regtotal from T_GHXX";
                    rs = statement1.executeQuery(sql);
                    if (rs.next()) {
                        registernum = rs.getInt("regtotal");
                        registernum++;
                    }
                    sql = "insert into T_GHXX (GHBH,HZBH,"
                            + "YSBH,BRBH,GHRC,THBZ,RQSJ,GHFY) "
                            + "values ('" + registernum + "','" + regNum + "','" + selectDoctorNum + "',"
                            + "'" + Main.name + "','" + (1 + regcount) + "','" + 0 + "','" + df.format(new Date()) + "'"
                            + ",'" + String.valueOf(reg_cost) + "')";
                    statement1.executeUpdate(sql);
                    double charge = 0;
                    if(!isEnough)
                        charge = Double.parseDouble(changeAccountLabel.getText());
                    if (text_pay.isEditable()) {
                        sql = "update T_BRXX set YCJE = "
                                + "" + charge + "+YCJE where "
                                + "BRBH= '" + Main.name + "'";
                    } else {
                        sql = "update T_BRXX set YCJE = "
                                + "" + charge + "where BRBH= '" + Main.name + "'";
                    }
                    statement1.executeUpdate(sql);

                    sql = "update T_GHXX set GHRC= '" + (1 + regcount) + "' where "
                            + "HZBH='" + regNum + "' and RQSJ>="
                            + "'" + currtime + "'";
                    statement1.executeUpdate(sql);
                    changeAccountLabel.setText("");
                    payAccountLabel.setText("");
                    accountLabel.setText("");
                    JOptionPane.showMessageDialog(new JFrame().getContentPane(),
                            "挂号成功!\n挂号编号为" + registernum + "!", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                rs.close();
                statement1.close();
                connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void clearClick(ActionEvent actionEvent) {
        deptBox.getEditor().clear();
        doctorBox.getEditor().clear();
        typeBox.getEditor().clear();
        nameBox.getEditor().clear();
        text_pay.setText("");
    }

    public void onMouseEntered(MouseEvent event) throws SQLException {
        if(isChanged == false) {
            Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
            Statement statement1 = connection.createStatement();
            ResultSet rs;
            String sql = "select BRMC,YCJE" + " from T_BRXX where " + "BRBH= '" + Main.name + "'";
            rs = statement1.executeQuery(sql);
            while (rs.next()) {
                patient_name = rs.getString("BRMC");
                patient_account = Double.parseDouble(rs.getString("YCJE"));
            }
            accountLabel.setText(String.valueOf(patient_account));
            welcomeLabel.setText(welcomeLabel.getText() + patient_name + "你好！");
            isChanged = true;
            rs.close();
            statement1.close();
            connection.close();
        }
    }

    public class PatientReg
    {
        private final SimpleStringProperty regNum;
        private final SimpleStringProperty regName;
        private final SimpleStringProperty docNum;
        private final SimpleStringProperty docName;
        private final SimpleStringProperty regCount;
        private final SimpleStringProperty regCost;
        private final SimpleStringProperty unReg;
        private final SimpleStringProperty regTime;

        public PatientReg(String rNum, String rName, String dNum, String dName,String rCount,String rCost,String uReg,String rTime)
        {
            this.regNum = new SimpleStringProperty(rNum);
            this.regName = new SimpleStringProperty(rName);
            this.docNum = new SimpleStringProperty(dNum);
            this.docName = new SimpleStringProperty(dName);
            this.regCount = new SimpleStringProperty(rCount);
            this.regCost = new SimpleStringProperty(rCost);
            this.unReg = new SimpleStringProperty(uReg);
            this.regTime = new SimpleStringProperty(rTime);
        }
        public String getRegNum() {
            return regNum.get();
        }
        public void setRegNum(String fName) {
            regNum.set(fName);
        }

        public String getRegName() {
            return regName.get();
        }
        public void setRegName(String fName) {
            regName.set(fName);
        }
        public String getDocNum() {
            return docNum.get();
        }
        public void setDocNum(String fName) {
            docNum.set(fName);
        }

        public String getDocName() {
            return docName.get();
        }
        public void setDocName(String fName) {
            docName.set(fName);
        }

        public String getRegCount() {
            return regCount.get();
        }
        public void setRegCount(String fName) {
            regCount.set(fName);
        }

        public String getRegCost() {
            return regCost.get();
        }
        public void setRegCost(String fName) {
            regCost.set(fName);
        }

        public String getUnReg() {
            return unReg.get();
        }
        public void setUnReg(String fName) {
            unReg.set(fName);
        }
        public String getRegTime() {
            return regTime.get();
        }
        public void setRegTime(String fName) {
            regTime.set(fName);
        }
    }
}
