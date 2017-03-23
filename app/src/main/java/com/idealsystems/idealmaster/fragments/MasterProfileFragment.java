package com.idealsystems.idealmaster.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idealsystems.idealmaster.R;
import com.idealsystems.idealmaster.adapters.ProfileAdapter;
import com.idealsystems.idealmaster.api.APIBuilder;
import com.idealsystems.idealmaster.api.response.IdealFeedbackListResponse;
import com.idealsystems.idealmaster.api.response.IdealFeedbackResponse;
import com.idealsystems.idealmaster.dialogs.LanguageDialog;
import com.idealsystems.idealmaster.gcm.GCMSubscriber;
import com.idealsystems.idealmaster.gcm.GCMUtils;
import com.idealsystems.idealmaster.model.IdealFeedback;
import com.idealsystems.idealmaster.settings.AppSettings;
import com.idealsystems.idealmaster.settings.UserData;
import com.idealsystems.idealmaster.utils.LocaleUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 9/1/16 __ acerkinght .
 */
public class MasterProfileFragment extends Fragment implements ProfileAdapter.OnChangeLanguageListener, LanguageDialog.OnLanguageChosenListener {
    private static final java.lang.String LANGUAGE_DIALOG = "LANGUAGE_DIALOG";
    private ProfileAdapter adapter;
    private MasterProfileFragmentActionListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View masterProfileView = inflater.inflate(R.layout.fragment_master_profile, container, false);

        adapter = new ProfileAdapter(getActivity(), this);

        APIBuilder
                .getIdealAPI()
                .getFeedbackByMaster(
                        AppSettings.sharedSettings(getActivity()).getBearerToken(),
                        UserData.sharedData(getActivity()).getId()
                )
                .enqueue(new Callback<IdealFeedbackListResponse>() {
                    @Override
                    public void onResponse(Call<IdealFeedbackListResponse> call, Response<IdealFeedbackListResponse> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        ArrayList<IdealFeedback> idealFeedbackArrayList = new ArrayList<>();
                        int index = 0;

                        for (IdealFeedbackResponse idealFeedbackResponse : response.body().getObjs()) {
                            index++;

                            if (index > 3) {
                                adapter.setFeedbackArrayList(idealFeedbackArrayList);
                                return;
                            }

                            ArrayList<IdealFeedback.CommentItem> commentItems = new ArrayList<>();
                            commentItems.add(new IdealFeedback.CommentItem(
                                    idealFeedbackResponse.getUpdated(),
                                    idealFeedbackResponse.getFeedback()
                            ));

                            IdealFeedback idealFeedback = new IdealFeedback();
                            idealFeedback.setRating(idealFeedbackResponse.getStars());
                            idealFeedback.setComments(commentItems);
                            idealFeedback.setUserFullName(idealFeedbackResponse.getUser().getName() + " " + idealFeedbackResponse.getUser().getSurname());

                            idealFeedbackArrayList.add(idealFeedback);

                            adapter.setFeedbackArrayList(idealFeedbackArrayList);
                        }
                    }

                    @Override
                    public void onFailure(Call<IdealFeedbackListResponse> call, Throwable t) {
                        Log.d("Feedbacks", "wtf");
                    }
                });

        RecyclerView recyclerView = (RecyclerView) masterProfileView.findViewById(R.id.profile_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        return masterProfileView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof MasterProfileFragmentActionListener) {
            listener = (MasterProfileFragmentActionListener) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onChangeLanguage() {
        LanguageDialog languageDialog = LanguageDialog.getInstance(AppSettings.sharedSettings(getActivity()).getLanguage());
        languageDialog.setListener(this);
        languageDialog.show(getActivity().getFragmentManager(), LANGUAGE_DIALOG);
    }

    @Override
    public void onLanguageChosen(String language) {
        if (listener != null) {
            listener.onHasChangedLanguage();
        }
        
        AppSettings.sharedSettings(getActivity()).setLanguage(language);
        GCMSubscriber.sendingToServer(GCMUtils.getRegistrationId(getActivity()), getActivity().getApplicationContext());
        LocaleUtils.setLocale(getActivity(), language);
        getActivity().recreate();
    }

    public interface MasterProfileFragmentActionListener {
        void onHasChangedLanguage();
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private Context context;

        public DividerItemDecoration(Context context) {
            this.context = context;
        }

        @Override
        public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            Paint paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(context.getResources().getColor(R.color.divider_grey));

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int left = 0;
                int right = parent.getWidth();

                if (i != childCount-1) {
                    canvas.drawLine(left, top, right, top, paint);
                }
            }
        }
    }
}
