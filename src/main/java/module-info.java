module net.coderextreme {
    requires javafx.controls;
    requires transitive java.desktop;
    requires java.rmi;
    requires com.formdev.flatlaf;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.firefox_driver;
    requires org.seleniumhq.selenium.edge_driver;
    requires org.seleniumhq.selenium.safari_driver;
    requires org.seleniumhq.selenium.ie_driver;
    requires org.seleniumhq.selenium.remote_driver;
    requires io.github.electronstudio.jaylib.ffm;
    opens net.coderextreme.motion to java.rmi;
    exports net.coderextreme.dev;
    exports net.coderextreme.icbm;
    exports net.coderextreme.main;
    exports net.coderextreme.motion;
    exports org.openjfx;
}
