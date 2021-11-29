package com.github.zhuaidadaya.modMdo.Commands;

public interface Here {
    String formatHereTip(String dimension, XYZ xyz, String playerName,DimensionTips dimensionTips);
    String formatHereFeedBack(String playerName);
    String formatHereFailedFeedBack();
}
