module USERGESTION {
    requires java.logging;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mail;
    requires spring.security.crypto;
    requires twilio;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires jbcrypt;
    requires AnimateFX;
    requires de.jensd.fx.glyphs.fontawesome;
    requires itextpdf;
    requires spring.context;
    opens Controllers to javafx.fxml;
    exports Main;
    exports Controllers;
    exports Entities;
    exports service;

}