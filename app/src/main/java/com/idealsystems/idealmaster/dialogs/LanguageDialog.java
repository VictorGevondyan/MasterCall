package com.idealsystems.idealmaster.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.settings.AppSettings;
import com.idealsystems.idealmaster.utils.TypefaceLoader;

/**
 * Created by acerkinght on 9/8/16.
 */
public class LanguageDialog extends DialogFragment implements View.OnClickListener {
    private static final String LANGUAGE = "language";

    private View view;
    private OnLanguageChosenListener listener;

    public static LanguageDialog getInstance(String language) {
        Bundle arguments = new Bundle();
        arguments.putString(LANGUAGE, language);

        LanguageDialog languageDialog = new LanguageDialog();
        languageDialog.setArguments(arguments);

        return languageDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_select_language, container, false);

        String language = getArguments().getString(LANGUAGE);

        processSection(R.id.english, language.equals(AppSettings.LANGUAGES.EN), AppSettings.LANGUAGES.EN, R.string.english);
        processSection(R.id.armenian, language.equals(AppSettings.LANGUAGES.HY), AppSettings.LANGUAGES.HY, R.string.armenian);
        processSection(R.id.russian, language.equals(AppSettings.LANGUAGES.RU), AppSettings.LANGUAGES.RU, R.string.russian);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    private void processSection(int sectionId, boolean isSelected, String language, int text) {
        View section = view.findViewById(sectionId);

        TextView textView = (TextView) section.findViewById(R.id.text);
        textView.setTypeface(TypefaceLoader.loadTypeface(getActivity().getAssets(), TypefaceLoader.AVENIR_BOOK, getActivity()));
        textView.setText(text);

        Button button = (Button) section.findViewById(R.id.select_button);

        if (isSelected) {
            button.setBackgroundResource(R.drawable.oval_green_complex);
        }

        button.setTag(R.id.select_button, language);
        button.setOnClickListener(this);
    }

    public void setListener(OnLanguageChosenListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        dismiss();
        listener.onLanguageChosen((String) view.getTag(R.id.select_button));
    }

    public interface OnLanguageChosenListener {
        void onLanguageChosen(String language);
    }
}
