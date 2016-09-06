package com.flycode.paradoxidealmaster.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.ProfileAdapter;

/**
 * Created by acerkinght on 9/1/16.
 */
public class MasterProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View masterProfileView = inflater.inflate(R.layout.fragment_master_profile, container, false);

        RecyclerView recyclerView = (RecyclerView) masterProfileView.findViewById(R.id.profile_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ProfileAdapter(getActivity()));
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
