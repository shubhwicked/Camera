package com.example.camera.gpuImagePlusHelper;

/**
 * Created by Divy on 7/5/2017.
 */

public class Effect {
    public int preRes;
    public String name;
    private String config;
    public float value;
    public float defaultValue;
    public float min, max;
    public boolean isPaid;
    public boolean isFilter, applyFilterIndex = true;

    public Effect(int preRes, String name, String config, boolean applyFilterIndex,boolean isPaid) {
        this(preRes, name, config, 0, 0, 0);
        this.applyFilterIndex = applyFilterIndex;
        this.isPaid=isPaid;
    }

    public Effect(int preRes, String name, boolean isFilter) {
        this(preRes, name, null, 1, 0, 1);
        this.isFilter = isFilter;
    }

    public Effect(int preRes, String name, String config, float defaultValue, float min, float max) {
        this.preRes = preRes;
        this.name = name;
        this.config = config;
        this.value = this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public String getConfig() {
        if (config == null) {
            return "";
        } else if (isFilter || (defaultValue == 0 && min == 0 && max == 0)) {
            return config;
        } else {
            if (config.contains("vignette")) {
                if (value == defaultValue) {
                    return String.format(config, 1f, value);
                } else {
                    return String.format(config, 0.3f, value);
                }
            } else {
                return String.format(config, value);
            }
        }
    }

    public void setConfig(String config) {
        if (isFilter)
            this.config = config;
    }
}
