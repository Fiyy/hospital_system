/**
 * FileName: Doctor
 * Author:   Zhang Yun
 * Date:     2020/5/19 18:44
 * Description:
 * History:
 */
package sample;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.function.Predicate;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zhang Yun
 * @create 2020/5/19
 * @since 1.0.0
 */
public class Doctor implements Initializable {
    @FXML
    ObservableList<Patient> patients_list = FXCollections.observableArrayList();
    @FXML
    ObservableList<Income> income_list = FXCollections.observableArrayList();
    @FXML
    public JFXTimePicker startTimePicker;
    @FXML
    public JFXTimePicker endTimePicker;
    @FXML
    public DatePicker startDatePicker;
    @FXML
    public DatePicker endDatePicker;
    @FXML
    public Label timeLabel;
    @FXML
    public JFXTextField searchField;
    @FXML
    public Label nameLabel;
    @FXML
    public JFXButton searchButton;
    @FXML
    public JFXButton backButton;
    @FXML
    public Tab incomeTab;
    @FXML
    public Tab patientTab;
    @FXML
    public JFXButton logoutButton;
    @FXML
    public JFXButton backButton1;
    @FXML
    public JFXButton logoutButton1;
    @FXML
    public Pane titlePane;
    @FXML
    private LocalDateTime startDateTime;
    @FXML
    private LocalDateTime endDateTime;
    @FXML
    public JFXTreeTableView<Patient> patientTable;
    @FXML
    public JFXTreeTableView<Income> incomeTable;

    boolean isChange = false;
    public void backToLogin(ActionEvent actionEvent) throws IOException {
        Main.setLogin();
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
        //设置医生名字
        String dateString = LocalDate.now().toString() + " 00:00:00";
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 创建Table
        JFXTreeTableColumn<Patient, String> idColumn = new JFXTreeTableColumn<>("挂号编号");
        idColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Patient, String> param) -> {
            if (idColumn.validateValue(param)) {
                return param.getValue().getValue().id;
            } else {
                return idColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Patient, String> nameColumn = new JFXTreeTableColumn<>("病人名称");
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Patient, String> param) -> {
            if (nameColumn.validateValue(param)) {
                return param.getValue().getValue().name;
            } else {
                return nameColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Patient, String> timeColumn = new JFXTreeTableColumn<>("挂号日期时间");
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Patient, String> param) -> {
            if (timeColumn.validateValue(param)) {
                return param.getValue().getValue().time;
            } else {
                return timeColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Patient, String> typeColumn = new JFXTreeTableColumn<>("号种类别");
        typeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Patient, String> param) -> {
            if (typeColumn.validateValue(param)) {
                return param.getValue().getValue().type;
            } else {
                return typeColumn.getComputedValue(param);
            }
        });

        patientTable.setId("tablePatient");

        JFXTreeTableColumn<Income, String> departmentColumn = new JFXTreeTableColumn<>("科室名称");
        departmentColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Income, String> param) -> {
            if (departmentColumn.validateValue(param)) {
                return param.getValue().getValue().officeName;
            } else {
                return departmentColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Income, String> idColumn2 = new JFXTreeTableColumn<>("医生编号");
        idColumn2.setCellValueFactory((TreeTableColumn.CellDataFeatures<Income, String> param) -> {
            if (idColumn2.validateValue(param)) {
                return param.getValue().getValue().docNum;
            } else {
                return idColumn2.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Income, String> nameColumn2 = new JFXTreeTableColumn<>("医生名称");
        nameColumn2.setCellValueFactory((TreeTableColumn.CellDataFeatures<Income, String> param) -> {
            if (nameColumn2.validateValue(param)) {
                return param.getValue().getValue().docName;
            } else {
                return nameColumn2.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Income, String> typeColumn2 = new JFXTreeTableColumn<>("号种类别");
        typeColumn2.setCellValueFactory((TreeTableColumn.CellDataFeatures<Income, String> param) -> {
            if (typeColumn2.validateValue(param)) {
                return param.getValue().getValue().regType;
            } else {
                return typeColumn2.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Income, String> numberColumn = new JFXTreeTableColumn<>("挂号人次");
        numberColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Income, String> param) -> {
            if (numberColumn.validateValue(param)) {
                return param.getValue().getValue().regCount;
            } else {
                return numberColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<Income, String> incomeColumn = new JFXTreeTableColumn<>("收入合计");
        incomeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Income, String> param) -> {
            if (incomeColumn.validateValue(param)) {
                return param.getValue().getValue().totalIncome;
            } else {
                return incomeColumn.getComputedValue(param);
            }
        });

        incomeTable.setId("tableIncome");

        patientTable.setShowRoot(false);
        patientTable.setEditable(false);
        patientTable.getColumns().setAll(idColumn, nameColumn, timeColumn, typeColumn);
        patientTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);

        incomeTable.setShowRoot(false);
        incomeTable.setEditable(false);
        incomeTable.getColumns().setAll(departmentColumn, idColumn2, nameColumn2, typeColumn2, numberColumn, incomeColumn);
        incomeTable.setColumnResizePolicy(JFXTreeTableView.CONSTRAINED_RESIZE_POLICY);


        // 显示当前时间
        DateFormat datetimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            String curTimeStr = datetimeFormat.format(timestamp);
                            timeLabel.setText(curTimeStr);
                        }
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        startDatePicker.valueProperty().addListener((o, oldVal, newVal) -> {
            LocalDate startDate = LocalDate.MIN;
            if (newVal!=null)
                startDate = newVal;
            startDateTime = startDate.atStartOfDay();
            LocalTime startTime = startTimePicker.getValue();
            if (startTime!=null)
                startDateTime = startDate.atTime(startTime.getHour(), startTime.getMinute());
        });
        startTimePicker.valueProperty().addListener((o, oldVal, newVal) -> {
            LocalTime startTime = LocalTime.MIN;
            if (newVal!=null)
                startTime = newVal;
            LocalDate startDate = startDatePicker.getValue();
            if (startDate!=null)
                startDateTime = startTime.atDate(startDate);
            else
                startDateTime = startTime.atDate(LocalDate.MIN);
        });
        endDatePicker.valueProperty().addListener((o, oldVal, newVal) -> {
            LocalDate endDate = LocalDate.now();
            if (newVal!=null)
                endDate = newVal;
            endDateTime = endDate.atStartOfDay();
            LocalTime endTime = endTimePicker.getValue();
            if (endTime!=null)
                endDateTime = endDate.atTime(endTime);
        });
        endTimePicker.valueProperty().addListener((o, oldVal, newVal) -> {
            LocalTime endTime = LocalTime.MAX;
            if (newVal!=null)
                endTime = newVal;
            LocalDate endDate = endDatePicker.getValue();
            if (endDate!=null)
                endDateTime = endTime.atDate(endDate);
            else
                endDateTime = endTime.atDate(LocalDate.now());
        });
    }
    public void onMouseEntered(MouseEvent event) throws SQLException {
        // 时间筛选
        String time_begin,time_end;
        if(startDateTime==null||endDateTime==null)
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time_end = df.format(new Date());
            time_begin = LocalDate.now().toString() + " 00:00:00";

            if(startDateTime==null) time_begin = "1999-01-01 00:00:00";
            if(endDateTime== null) time_end = "2999-01-01 00:00:00";
        }else {
            time_begin = startDateTime.toString();
            //System.out.println("time_begin is " + time_begin);
            time_end = endDateTime.toString();
        }

        patients_list.clear();
        income_list.clear();

        Connection connection = ConnectionFactory.connect(Main.URL, Main.username, Main.password);
        Statement statement1 = null;
        try {
            statement1 = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement statement2 = null;
        try {
            statement2 = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String name = Main.name;
        if(isChange == false) {
            nameLabel.setText(nameLabel.getText() + name);
            isChange = true;
        }
        String sql = "select GHBH,HZBH,BRBH," + "RQSJ,THBZ from T_GHXX where " + "YSBH= '"+name+"'";
        ResultSet rs = statement1.executeQuery(sql);
        String register_num=null,registration_num=null,pat_num=null;
        String reg_datetime=null,pat_name=null,expertstr=null;
        String office_name = null,doc_name=null,unreg_str=null;
        boolean expert=true,unreg=false;
        ResultSet rs2 =null;
        int regcount = 0;
        double totalcost = 0;
        while(rs.next())
        {

            register_num = rs.getString("GHBH");
            System.out.println("GHBH"+ register_num);
            registration_num = rs.getString("HZBH");
            System.out.println("HZBH"+registration_num);
            pat_num = rs.getString("BRBH");
            System.out.println("BRBH"+pat_num);
            reg_datetime = rs.getString("RQSJ");
            System.out.println("RQSJ"+reg_datetime);
            unreg = rs.getBoolean("THBZ");
            System.out.println("THBZ"+unreg);

            sql = "select SFZJ from T_HZXX where " + "HZBH= '"+registration_num+"'";
            rs2 = statement2.executeQuery(sql);
            if(rs2.next())
            {
                expert = rs2.getBoolean("SFZJ");
            }

            sql = "select BRMC from T_BRXX where" + " BRBH= '"+pat_num+"'";
            rs2 = statement2.executeQuery(sql);
            if(rs2.next())
            {
                pat_name = rs2.getString("BRMC");
            }
            expertstr = expert? "专家号":"普通号";
            Patient patient = new Patient(register_num, pat_name, reg_datetime,expertstr);
            System.out.println("--------");
            System.out.println(patient.getId());
            System.out.println(patient.getName());
            System.out.println(patient.getTime());
            System.out.println(patient.getType());
            patients_list.add(patient);
        }

        //收入列表
        sql = "select T_KSXX.KSMC,T_GHXX.YSBH,"
                + "T_KSYS.YSMC,T_HZXX.SFZJ,"
                + "count(T_GHXX.YSBH),sum(T_GHXX.GHFY) "
                + "from T_GHXX,T_HZXX,T_KSYS,T_KSXX "
                + "where "
                + "T_GHXX.HZBH=T_HZXX.HZBH "
                + "and T_KSYS.YSBH=T_GHXX.YSBH "
                + "and T_KSXX.KSBH=T_HZXX.KSBH "
                + "and T_GHXX.RQSJ>='"+time_begin+"' and "
                + "T_GHXX.RQSJ<='"+time_end+"'"
                + "group by T_GHXX.YSBH,T_HZXX.SFZJ";
        rs = statement1.executeQuery(sql);
        while(rs.next())
        {
            office_name = rs.getString("T_KSXX.KSMC");
            name = rs.getString("T_GHXX.YSBH");
            doc_name = rs.getString("T_KSYS.YSMC");

            expert = rs.getBoolean("T_HZXX.SFZJ");
            regcount = rs.getInt("count(T_GHXX.YSBH)");
            totalcost = rs.getDouble("sum(T_GHXX.GHFY)");
            expertstr = expert? "专家号":"普通号";
            income_list.add(new Income(office_name, name,doc_name, expertstr,Integer.toString(regcount),Double.toString(totalcost)));
        }
        if(rs!=null)
            rs.close();
        if(rs2!=null)
            rs2.close();
        statement2.close();
        statement1.close();
        connection.close();
        final TreeItem<Patient> patientRoot = new RecursiveTreeItem<>(patients_list, RecursiveTreeObject::getChildren);
        patientTable.setRoot(patientRoot);
        final TreeItem<Income> IncomeRoot = new RecursiveTreeItem<>(income_list, RecursiveTreeObject::getChildren);
        incomeTable.setRoot(IncomeRoot);
        for(Income income : income_list){
            System.out.println("??? " + income.docName + " "+ income.docNum);
        }
    }

    public class Income extends RecursiveTreeObject<Income> {
        private final SimpleStringProperty officeName;
        private final SimpleStringProperty docNum;
        private final SimpleStringProperty docName;
        private final SimpleStringProperty regType;
        private final SimpleStringProperty regCount;
        private final SimpleStringProperty totalIncome;

        public Income(String oName, String dNum, String dName,
                      String rType,String rCount,String tIncome)
        {
            this.officeName = new SimpleStringProperty(oName);
            this.docNum = new SimpleStringProperty(dNum);
            this.docName = new SimpleStringProperty(dName);
            this.regType = new SimpleStringProperty(rType);
            this.regCount = new SimpleStringProperty(rCount);
            this.totalIncome = new SimpleStringProperty(tIncome);
        }

        public String getOfficeName() {
            return officeName.get();
        }
        public void setOfficeName(String fName) {
            officeName.set(fName);
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

        public String getRegType() {
            return regType.get();
        }
        public void setRegType(String fName) {
            regType.set(fName);
        }

        public String getRegCount() {
            return regCount.get();
        }
        public void setRegCount(String fName) {
            regCount.set(fName);
        }

        public String getTotalIncome() {
            return totalIncome.get();
        }
        public void setTotalIncome(String fName) {
            totalIncome.set(fName);
        }
    }

    private static final class Patient extends RecursiveTreeObject<Patient> {
        public StringProperty id;
        public StringProperty name;
        public StringProperty time;
        public StringProperty type;

        public Patient() {

        }
        public Patient(String id, String name, String time, String type) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.time = new SimpleStringProperty(time);
            this.type = new SimpleStringProperty(type);
        }

        public String getId() { return id.get(); }

        public String getName() {
            return name.get();
        }

        public String getTime() {
            return time.get();
        }

        public String getType() {
            return type.get();
        }

        public void setId(String id) {
            this.id.set(id);
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public void setTime(String time) {
            this.time.set(time);
        }

        public void setType(String type) {
            this.type.set(type);
        }

    }
    @FXML
    public void closeClick(ActionEvent actionEvent) {
        Event.fireEvent(Main.stage, new WindowEvent(Main.stage, WindowEvent.WINDOW_CLOSE_REQUEST ));
    }
}