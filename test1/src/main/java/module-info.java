/**
 * Created by quanchangnai on 2020/5/14.
 */
module quan.test {
    requires java.desktop;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    exports quan.test.roster;
    opens quan.test.roster to javafx.fxml;
}