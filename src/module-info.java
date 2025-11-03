/**
 * 
 */
/**
 * jpackage -n MyApp03 --type app-image --module-path "C:\Users\jerom_71obc72\eclipse-workspace\Essai\bin;C:\Program Files\Java\javafx-jmods-21.0.9" --add-modules javafx.controls,javafx.graphics,javafx.base,java.prefs,java.desktop,jdk.unsupported,java.logging --module Essai/essai.SelectFolderApp --win-console
 */
module Essai {
	requires javafx.controls;
    requires javafx.graphics;
    requires java.prefs;
    exports essai;
}