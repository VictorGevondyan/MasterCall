package com.flycode.paradoxidealmaster.fragments;

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

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.ProfileAdapter;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.IdealFeedbackListResponse;
import com.flycode.paradoxidealmaster.api.response.IdealFeedbackResponse;
import com.flycode.paradoxidealmaster.model.IdealFeedback;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.settings.UserData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by acerkinght on 9/1/16.
 */
public class MasterProfileFragment extends Fragment {
    private ProfileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View masterProfileView = inflater.inflate(R.layout.fragment_master_profile, container, false);

        adapter = new ProfileAdapter(getActivity());

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

                        for (IdealFeedbackResponse idealFeedbackResponse : response.body().getObjs()) {
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
            paint.setColor(context.getResources().getColor(R.color.lighter_grey));

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int left = 0;
                int right = parent.getWidth();

                canvas.drawLine(left, top, right, top, paint);
            }
        }
    }
}
