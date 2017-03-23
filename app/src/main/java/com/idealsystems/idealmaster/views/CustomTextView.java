package com.idealsystems.idealmaster.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.idealsystems.idealmaster.settings.AppSettings;
import com.idealsystems.idealmaster.utils.TypefaceLoader;

/**
 * Created on 11/15/16 __ Schumakher .
 */

public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        String language = AppSettings.sharedSettings(getContext()).getLanguage();

        if (armContains(text) && !rusContains(text)) {


           // if (language.equals("hy") && !typefaceName.equals("icomoon.ttf")) {
//            if (language.equals("hy") && !text.toString().contains("&#ex")) {
                setTypeface(TypefaceLoader.loadTypeface(getContext().getAssets(), TypefaceLoader.NOH45, getContext()));
//            }
        }
        super.setText(text, type);
    }

    public boolean armContains(CharSequence text) {

        String[] armArray = {"ա", "բ", "գ", "դ", "ե", "զ", "է", "ը", "թ", "ժ", "ի", "լ", "խ",
                "ծ", "կ", "հ", "ձ", "ղ", "ճ", "մ", "յ", "ն", "շ", "ո", "չ", "պ", "ջ", "ռ", "ս",
                "վ", "տ", "ր", "ց", "ու", "փ", "ք"};

        if (text == null) {
            return false;
        }

        for (String anArmArray : armArray) {
            if (text.toString().contains(anArmArray)) {
                return true;
            }
        }
        return false;
    }

    public boolean rusContains(CharSequence text) {

        String[] rusArray = {"о", "е", "а", "и", "н", "т", "с", "р", "в", "л", "к", "м", "д",
                "п", "у", "я", "ы", "ь", "г", "з", "б", "ч", "й"};

        if (text == null) {
            return false;
        }

        for (String aRusArray : rusArray) {
            if (text.toString().contains(aRusArray)) {
                return true;
            }
        }
        return false;
    }
}
