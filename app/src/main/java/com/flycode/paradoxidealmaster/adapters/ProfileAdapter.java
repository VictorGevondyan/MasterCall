package com.flycode.paradoxidealmaster.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.viewholders.MasterFeedbackViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.MasterPortfolioViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.ProfileViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.SuperViewHolder;
import com.flycode.paradoxidealmaster.model.IdealFeedback;
import com.flycode.paradoxidealmaster.settings.UserData;
import com.flycode.paradoxidealmaster.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by acerkinght on 9/3/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<SuperViewHolder> implements ProfileViewHolder.ProfileActionListener, ProfileViewHolder.ProfileProvider, MasterFeedbackViewHolder.MasterFeedbackProvider, MasterPortfolioViewHolder.PortfolioProvider {
    private enum ROW_TYPES {
        FULL_NAME, GENDER, BIRTHDAY, CERTIFICATE, RATING, PORTFOLIO, PORTFOLIO_ITEM
    }

    private Context context;
    private ArrayList<Object> rows;
    private ArrayList<IdealFeedback> feedbackArrayList;
    private boolean isPortfolioExpended;
    private boolean isRatingExpended;

    public  ProfileAdapter(Context context) {
        this.context = context;
        initRows();
    }

    private void initRows() {
        this.feedbackArrayList = new ArrayList<>();
        this.rows = new ArrayList<>();

        this.rows.add(ROW_TYPES.FULL_NAME);

        if (UserData.sharedData(context).getDateOfBirth() != null) {
            this.rows.add(ROW_TYPES.BIRTHDAY);
        }

        this.rows.add(ROW_TYPES.GENDER);

        if (UserData.sharedData(context).isSticker()) {
            this.rows.add(ROW_TYPES.CERTIFICATE);
        }

        this.rows.add(ROW_TYPES.RATING);
        this.rows.add(ROW_TYPES.PORTFOLIO);
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return ProfileViewHolder.getInstance(parent, context, this, this);
        } else if (viewType == 2) {
            return MasterPortfolioViewHolder.getInstance(parent, context, this);
        } else {
            return MasterFeedbackViewHolder.getInstance(parent, context, this);
        }
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.setupForPosition(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (rows.get(position) instanceof ROW_TYPES) {
            if (rows.get(position).equals(ROW_TYPES.PORTFOLIO_ITEM)) {
                return 2;
            }

            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @Override
    public void onActionClicked(int position) {
        if (rows.get(position).equals(ROW_TYPES.RATING)) {
            isRatingExpended = !isRatingExpended;

            notifyItemChanged(position);

            if (isRatingExpended) {

                if (feedbackArrayList.isEmpty()) {
                    return;
                }

                rows.addAll(position + 1, feedbackArrayList);

                notifyItemRangeInserted(position + 1, feedbackArrayList.size());
            } else {
                rows.removeAll(feedbackArrayList);

                notifyItemRangeRemoved(position + 1, feedbackArrayList.size());
            }
        } else if (rows.get(position).equals(ROW_TYPES.PORTFOLIO)) {
            isPortfolioExpended = !isPortfolioExpended;

            notifyItemChanged(position);

            if (isPortfolioExpended) {
                rows.add(ROW_TYPES.PORTFOLIO_ITEM);

                notifyItemInserted(position + 1);
            } else {
                rows.remove(ROW_TYPES.PORTFOLIO_ITEM);

                notifyItemRemoved(position + 1);
            }
        }
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
        } else if (rows.get(position).equals(ROW_TYPES.PORTFOLIO)) {
            return context.getString(R.string.portfolio);
        } else if (rows.get(position).equals(ROW_TYPES.RATING)) {
            return context.getString(R.string.rating);
        }

        return "";
    }

    @Override
    public String getProfileIconForPosition(int position) {
        if (rows.get(position).equals(ROW_TYPES.FULL_NAME)) {
            return context.getString(R.string.icon_username);
        } else if (rows.get(position).equals(ROW_TYPES.BIRTHDAY)) {
            return context.getString(R.string.icon_birthday);
        } else if (rows.get(position).equals(ROW_TYPES.GENDER)) {
            return context.getString(R.string.icon_gender);
        } else if (rows.get(position).equals(ROW_TYPES.CERTIFICATE)) {
            return context.getString(R.string.icon_certificate);
        } else if (rows.get(position).equals(ROW_TYPES.PORTFOLIO)) {
            return context.getString(R.string.icon_portfolio);
        } else if (rows.get(position).equals(ROW_TYPES.RATING)) {
            return context.getString(R.string.icon_empty_star);
        }

        return "";
    }

    @Override
    public int getColor(int position) {
        if (rows.get(position).equals(ROW_TYPES.CERTIFICATE)) {
            return context.getResources().getColor(R.color.ideal_red);
        }

        return context.getResources().getColor(R.color.darken_grey);
    }

    @Override
    public boolean isExpendable(int position) {
        return rows.get(position).equals(ROW_TYPES.RATING)
                || rows.get(position).equals(ROW_TYPES.PORTFOLIO);
    }

    @Override
    public boolean isExpended(int position) {
        if (rows.get(position).equals(ROW_TYPES.RATING)) {
            return isRatingExpended;
        } else if (rows.get(position).equals(ROW_TYPES.PORTFOLIO)) {
            return isPortfolioExpended;
        }

        return false;
    }

    @Override
    public boolean showsRating(int position) {
        return rows.get(position).equals(ROW_TYPES.RATING);
    }

    @Override
    public double getProfileRating() {
        return UserData.sharedData(context).getRating();
    }

    @Override
    public IdealFeedback getFeedbackForPosition(int position) {
        return (IdealFeedback)rows.get(position);
    }

    @Override
    public ArrayList<String> getPortfolio() {
        return UserData.sharedData(context).getPortfolio();
    }

    public void setFeedbackArrayList(ArrayList<IdealFeedback> feedbackArrayList) {
        this.feedbackArrayList = feedbackArrayList;
    }
}