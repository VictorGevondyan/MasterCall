package com.flycode.paradoxidealmaster.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.viewholders.ProfileViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.SuperViewHolder;
import com.flycode.paradoxidealmaster.model.User;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.flycode.paradoxidealmaster.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by acerkinght on 9/3/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<SuperViewHolder> implements ProfileViewHolder.ProfileActionListener, ProfileViewHolder.ProfileProvider {
    private enum ROW_TYPES {
        FULL_NAME, GENDER, BIRTHDAY, CERTIFICATE
    }

    private Context context;
    private ArrayList<ROW_TYPES> rows;

    public  ProfileAdapter(Context context) {
        this.context = context;
        initRows();
    }

    private void initRows() {
        this.rows = new ArrayList<>();

        this.rows.add(ROW_TYPES.FULL_NAME);

        if (UserData.sharedData(context).getDateOfBirth() != null) {
            this.rows.add(ROW_TYPES.BIRTHDAY);
        }

        this.rows.add(ROW_TYPES.GENDER);
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ProfileViewHolder.getInstance(parent, context, this, this);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.setupForPosition(position);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @Override
    public void onExpendSection() {

    }

    @Override
    public void onSuspendSection() {

    }

    @Override
    public String getProfileValueForPosition(int position) {
        if (rows.get(position).equals(ROW_TYPES.FULL_NAME)) {
            return UserData.sharedData(context).getName() + " " + UserData.sharedData(context).getSurname();
        } else if (rows.get(position).equals(ROW_TYPES.CERTIFICATE)) {
            return context.getString(R.string.certificate);
        } else if (rows.get(position).equals(ROW_TYPES.GENDER)) {
            return UserData.sharedData(context).isSex() ? context.getString(R.string.male) : context.getString(R.string.female);
        } else if (rows.get(position).equals(ROW_TYPES.BIRTHDAY)) {
            return DateUtils.birthdayDateFormat(UserData.sharedData(context).getDateOfBirth());
        }

        return "";
    }

    @Override
    public String getProfileIconForPosition(int position) {
        return null;
    }

    @Override
    public boolean isExpendable(int position) {
        return false;
    }

    @Override
    public boolean isExpended(int position) {
        return false;
    }

    @Override
    public boolean showsRating(int position) {
        return false;
    }

    @Override
    public double getProfileRating() {
        return 0;
    }
}