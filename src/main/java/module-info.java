module org.prh {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.slf4j;

    exports org.prh;
    exports org.prh.server;
    exports org.prh.client;
}