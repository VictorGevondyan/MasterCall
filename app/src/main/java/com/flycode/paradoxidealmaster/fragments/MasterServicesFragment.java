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
import com.flycode.paradoxidealmaster.adapters.MasterServicesAdapter;
import com.flycode.paradoxidealmaster.model.IdealMasterService;
import com.flycode.paradoxidealmaster.model.IdealService;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by acerkinght on 9/1/16.
 */
public class MasterServicesFragment extends Fragment {
    private MasterServicesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View masterProfileView = inflater.inflate(R.layout.fragment_master_services, container, false);

        adapter = new MasterServicesAdapter(getActivity());
        RecyclerView recyclerView = (RecyclerView) masterProfileView.findViewById(R.id.services_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        Realm
                .getDefaultInstance()
                .where(IdealService.class)
                .findAllAsync()
                .addChangeListener(servicesChangeListener);

        Realm
                .getDefaultInstance()
                .where(IdealMasterService.class)
                .findAllAsync()
                .addChangeListener(masterServicesChangeListener);

        return masterProfileView;
    }

    @Override
    public void onResume() {
           super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private RealmChangeListener<RealmResults<IdealService>> servicesChangeListener = new RealmChangeListener<RealmResults<IdealService>>() {
        @Override
        public void onChange(RealmResults<IdealService> realmResults) {
            ArrayList<IdealService> services = new ArrayList<>();

            for (int index = 0; index < realmResults.size(); index ++) {
                services.add(realmResults.get(index));
            }

            adapter.setServices(services);
        }
    };

    private RealmChangeListener<RealmResults<IdealMasterService>> masterServicesChangeListener = new RealmChangeListener<RealmResults<IdealMasterService>>() {
        @Override
        public void onChange(RealmResults<IdealMasterService> realmResults) {
            ArrayList<IdealMasterService> services = new ArrayList<>();

            for (int index = 0; index < realmResults.size(); index ++) {
                services.add(realmResults.get(index));
            }

            adapter.setMasterServices(services);
        }
    };

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
