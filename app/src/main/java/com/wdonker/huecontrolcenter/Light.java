package com.wdonker.huecontrolcenter;

// A simple representation of a Philips Hue light
public class Light {
    private int id;
    private String name;
    private boolean enabled;
    private int brightness;
    // "Lux bulbs" have no hue and saturation so the following values can be null
    private Integer hue;
    private Integer saturation;

    public Light(int id, String name, boolean isEnabled, Integer hue, Integer saturation, int brightness){
        this.id = id;
        this.name = name;
        this.enabled = isEnabled;
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    public void setEnabled(boolean isEnabled){
        this.enabled = isEnabled;
    }

    public Integer getHue(){
        return this.hue;
    }

    public void setHue(Integer hue){
        this.hue = hue;
    }

    public Integer getSaturation(){
        return this.saturation;
    }

    public void setSaturation(Integer saturation){
        this.saturation = saturation;
    }

    public int getBrightness(){
        return this.brightness;
    }

    public void setBrightness(int brightness){
        this.brightness = brightness;
    }
}
