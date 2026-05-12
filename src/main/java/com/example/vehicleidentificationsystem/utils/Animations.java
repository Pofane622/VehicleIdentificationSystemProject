// src/main/java/com/example/vehicleidentificationsystem/utils/Animations.java
package com.example.vehicleidentificationsystem.utils;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Animations {

    public static void addDropShadow(Button button) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        button.setEffect(dropShadow);

        button.setOnMouseEntered(e -> {
            dropShadow.setRadius(15.0);
            dropShadow.setOffsetX(5.0);
            dropShadow.setOffsetY(5.0);
        });

        button.setOnMouseExited(e -> {
            dropShadow.setRadius(10.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
        });
    }

    public static void addFadeTransition(Button button) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), button);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }
}