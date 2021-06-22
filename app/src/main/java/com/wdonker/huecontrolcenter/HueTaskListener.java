package com.wdonker.huecontrolcenter;

// Interface with a single callback method for when a HUE task (class implementing AbstractHueTask) is completed
public interface HueTaskListener {
    void onHueTaskComplete(Object sender, Object result);
}
