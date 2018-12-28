package org.openjfx;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TitlebarController {
    @FXML private ToolBar toolbar;
    @FXML private HBox titlebar;
    @FXML private Button closeButton;
    @FXML private Button minimizeButton;
    @FXML private Button resizeButton;

    private Stage stage;
    private boolean isMaximized;
    private double oldWindowX;
    private double oldWindowY;
    private boolean isFocused;
    private double xOffset;
    private double yOffset;

    public void initialize() {
        isMaximized = false;
        xOffset = 0;
        yOffset = 0;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        handleUnfocusedStage();
        toolbar.setOnMousePressed((MouseEvent event) -> {
            System.out.println("HEY!");
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        toolbar.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    /**
     * Changes decorations to light-gray when window is
     * not focused, and back to default colors when focused.
     */
    private void handleUnfocusedStage() {
        this.stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            this.isFocused = isNowFocused;
            if (!isNowFocused) {
                // Changing all three decorations to light gray
                closeButton.getStyleClass().add("unfocused-button");
                minimizeButton.getStyleClass().add("unfocused-button");
                resizeButton.getStyleClass().add("unfocused-button");
            } else {
                // Reverting all three decorations back to default
                closeButton.getStyleClass().remove("unfocused-button");
                minimizeButton.getStyleClass().remove("unfocused-button");
                resizeButton.getStyleClass().remove("unfocused-button");
            }
        });
        titlebar.setOnMouseEntered((MouseEvent t) -> {
            if (!this.stage.isFocused()) {
                closeButton.getStyleClass().remove("unfocused-button");
                minimizeButton.getStyleClass().remove("unfocused-button");
                resizeButton.getStyleClass().remove("unfocused-button");
            }
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
            minimizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
            resizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
        });
        titlebar.setOnMouseExited((MouseEvent t) -> {
            if (!this.stage.isFocused()) {
                closeButton.getStyleClass().add("unfocused-button");
                minimizeButton.getStyleClass().add("unfocused-button");
                resizeButton.getStyleClass().add("unfocused-button");
            }
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
            minimizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
            resizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
        });
    }

    /**
     * Closes window when close decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleCloseButtonAction(ActionEvent event) {
        this.stage.close();
    }

    /**
     * Minimizes window when the close decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleMinimizeButtonAction(ActionEvent event) {
        this.stage.toBack();
        this.stage.setIconified(true);
    }

    /**
     * Resizes window (to windowed fullscreen or back to original size)
     * when the resize decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleResizeButtonAction(ActionEvent event) {
        if(!stage.isFullScreen()) {
            titlebar.setVisible(false);
            stage.setFullScreen(true);
            stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                if (!stage.isFullScreen()) {
                    titlebar.setVisible(true);
                    stage.setFullScreen(false);
                }
            });
        }
        /*
        if (!isMaximized) {
            this.isMaximized = true;
            // Remembers old window position (for when resize decoration is pressed again)
            this.oldWindowX = this.stage.getX();
            this.oldWindowY = this.stage.getY();

            // Resizing window be windows fullscreen
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            this.stage.setX(bounds.getMinX());
            this.stage.setY(bounds.getMinY());
            this.stage.setWidth(bounds.getWidth());
            this.stage.setHeight(bounds.getHeight());
        } else {
            this.isMaximized = false;
            // Revert back to smaller window
            this.stage.setX(this.oldWindowX);
            this.stage.setY(this.oldWindowY);
            this.stage.setWidth(390);
            this.stage.setHeight(660);
        }
        */
    }



}
