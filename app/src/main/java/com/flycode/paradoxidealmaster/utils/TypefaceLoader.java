package com.flycode.paradoxidealmaster.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.LruCache;

/**
 * Created by anhaytananun on 04.07.16.
 */
public class TypefaceLoader {
    static {
        typefaces = new LruCache<>(4 * 1024 * 1024);
    }

    public static String AVENIR_BOOK = "AvenirLTStd-Book.otf";
    public static String AVENIR_LIGHT = "AvenirLTStd-Light.otf";
    public static String AVENIR_BLACK = "AvenirLTStd-Black.otf";
    public static String AVENIR_MEDIUM = "AvenirLTStd-Medium.otf";
    public static String AVENIR_ROMAN = "AvenirLTStd-Roman.otf";
    public static String ICOMOON = "icomoon.ttf";

    private static LruCache<String, Typeface> typefaces;

    public static Typeface loadTypeface(AssetManager assetManager, String typefaceName) {
        Typeface typeface = typefaces.get(typefaceName);

        if (typeface == null) {
            typeface = Typeface.createFromAsset(assetManager, typefaceName);
            typefaces.put(typefaceName, typeface);
        }

        return typeface;
    }
}
