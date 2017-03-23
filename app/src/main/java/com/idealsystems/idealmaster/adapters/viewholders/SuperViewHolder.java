package com.idealsystems.idealmaster.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by acerkinght on 8/8/16.
 */
public abstract class SuperViewHolder extends RecyclerView.ViewHolder {
    public SuperViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setupForPosition(int position);
}
