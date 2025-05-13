module swp.com.zeitmessungfilewrite {
    requires javafx.controls;
    requires javafx.fxml;


    opens swp.com.zeitmessungfilewrite to javafx.fxml;
    exports swp.com.zeitmessungfilewrite;
}