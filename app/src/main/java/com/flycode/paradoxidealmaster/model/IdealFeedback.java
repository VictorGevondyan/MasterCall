package com.flycode.paradoxidealmaster.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by acerkinght on 9/7/16.
 */
public class IdealFeedback implements Parcelable {
    private boolean alreadyRated;
    private ArrayList<CommentItem> comments;
    private String userFullName;
    private double rating;

    public IdealFeedback() {

    }

    protected IdealFeedback(Parcel in) {
        comments = new ArrayList<>();

        userFullName = in.readString();
        alreadyRated = in.readInt() == 1;
        in.readTypedList(comments, CommentItem.CREATOR);
        rating = in.readDouble();
    }

    public static final Creator<IdealFeedback> CREATOR = new Creator<IdealFeedback>() {
        @Override
        public IdealFeedback createFromParcel(Parcel in) {
            return new IdealFeedback(in);
        }

        @Override
        public IdealFeedback[] newArray(int size) {
            return new IdealFeedback[size];
        }
    };

    public boolean isAlreadyRated() {
        return alreadyRated;
    }

    public void setAlreadyRated(boolean alreadyRated) {
        this.alreadyRated = alreadyRated;
    }

    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userFullName);
        parcel.writeInt(alreadyRated ? 1 : 0);
        parcel.writeTypedList(comments);
        parcel.writeDouble(rating);
    }

    public static class CommentItem implements Parcelable {
        private Date updated;
        private String comment;

        public CommentItem(Date updated, String comment) {
            this.updated = updated;
            this.comment = comment;
        }

        protected CommentItem(Parcel in) {
            comment = in.readString();
            updated = new Date(in.readLong());
        }

        public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
            @Override
            public CommentItem createFromParcel(Parcel in) {
                return new CommentItem(in);
            }

            @Override
            public CommentItem[] newArray(int size) {
                return new CommentItem[size];
            }
        };

        public Date getUpdated() {
            return updated;
        }

        public void setUpdated(Date updated) {
            this.updated = updated;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(comment);
            parcel.writeLong(updated.getTime());
        }
    }
}
