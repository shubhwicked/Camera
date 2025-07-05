package com.example.camera.gpuImagePlusHelper;


import com.example.camera.R;

public class EffectsHelper {
    public static final String beauty0 = "";
    public static final String beauty1 = "@beautify face 1 720 1280";
    public static final String beauty2 = "@beautify face 1.5 720 1280";
    public static final String beauty3 = "@beautify face 1.8 720 1280";
    public static final String beauty4 = "@beautify face 2 720 1280";
    public static final String beauty5 = "@beautify face 2.5 720 1280";
    public static final String beauty6 = "@adjust saturation 0";


    public static final Effect effects[] = {
            new Effect(R.mipmap.ic_launcher, "None", "", false,false),
            new Effect(R.mipmap.ic_launcher, "Morning", "@beautify face 1 720 1280", false,false), //Beautify
            new Effect(R.mipmap.ic_launcher, "Mono", beauty6, false,false),
    };
}
