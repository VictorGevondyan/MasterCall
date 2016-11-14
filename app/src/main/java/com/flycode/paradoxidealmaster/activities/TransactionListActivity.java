package com.flycode.paradoxidealmaster.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.adapters.TransactionAdapter;
import com.flycode.paradoxidealmaster.api.APIBuilder;
import com.flycode.paradoxidealmaster.api.response.TransactionResponse;
import com.flycode.paradoxidealmaster.api.response.TransactionsListResponse;
import com.flycode.paradoxidealmaster.model.IdealTransaction;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.DeviceUtil;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends AppCompatActivity implements View.OnClickListener, RealmChangeListener<RealmResults<IdealTransaction>> {

    private TransactionAdapter adapter;
    private static final String TAG = "myLogs";
    private boolean alreadyUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        setupActionbarUI();
        alreadyUpdated = false;

        adapter = new TransactionAdapter(this, new ArrayList<IdealTransaction>());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.transactions_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        alreadyUpdated = false;

        loadOrdersViaDatabase();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down_out);
    }

    private void setupActionbarUI() {
        ViewGroup actionBar = (ViewGroup) findViewById(R.id.action_bar);

        ImageView actionBarBackgroundImageView = (ImageView) actionBar.findViewById(R.id.action_background);
        actionBarBackgroundImageView.setImageResource(R.drawable.transactions_background);

        TextView titleTextView = (TextView) actionBar.findViewById(R.id.title);
        titleTextView.setText(R.string.transactions);

        Button backButton = (Button) actionBar.findViewById(R.id.back);
        backButton.setOnClickListener(this);
        backButton.setTypeface(TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON, this));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            onBackPressed();
        }
    }


    @Override
    public void onChange(RealmResults<IdealTransaction> transactionRealmResults) {
        ArrayList<IdealTransaction> transactions = new ArrayList<>();

        for (int index = 0; index < transactionRealmResults.size(); index++) {
            transactions.add(transactionRealmResults.get(index));
        }

        if (transactions.isEmpty()) {
            findViewById(R.id.no_transactions).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.no_transactions).setVisibility(View.GONE);
        }

        Date startDate = null;

        if (!transactions.isEmpty()) {
            startDate = transactions.get(0).getDate();
        }

        adapter.setTransaction(transactions);

        if (!alreadyUpdated) {
            alreadyUpdated = true;
            loadTransactionsViaServer(startDate);
        }
    }

    private void loadTransactionsViaServer(Date startDate) {
        APIBuilder
                .getIdealAPI()
                .getTransactions(
                        AppSettings.sharedSettings(this).getBearerToken(),
                        startDate, null
                ).enqueue(new Callback<TransactionsListResponse>() {
            @Override
            public void onResponse(Call<TransactionsListResponse> call, final Response<TransactionsListResponse> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "good, good");
                    return;
                }

                new AsyncTask<Void, Void, ArrayList<IdealTransaction>>() {

                    @Override
                    protected ArrayList<IdealTransaction> doInBackground(Void... args) {
                        final ArrayList<TransactionResponse> transactionResponses = response.body().getObjs();
                        final ArrayList<IdealTransaction> idealTransactions = new ArrayList<>();

                        for (TransactionResponse transactionResponse : transactionResponses) {
                            idealTransactions.add(IdealTransaction.transactionFromResponse(transactionResponse));
                        }

                        return idealTransactions;
                    }

                    @Override
                    protected void onPostExecute(final ArrayList<IdealTransaction> idealTransactions) {
                        super.onPostExecute(idealTransactions);

                        Realm
                                .getDefaultInstance()
                                .executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.insertOrUpdate(idealTransactions);
                                    }
                                });
                    }
                }.execute();
            }

            @Override
            public void onFailure(Call<TransactionsListResponse> call, Throwable t) {
                Log.i(TAG, "I will not load the transactions, how you even dare to request them!");
            }
        });

    }

    private void loadOrdersViaDatabase() {
        RealmQuery<IdealTransaction> query = Realm
                .getDefaultInstance()
                .where(IdealTransaction.class);

        query
                .findAllSortedAsync("date", Sort.DESCENDING)
                .addChangeListener(this);
    }


    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private Paint dividerPaint;
        private Paint colorPaint;

        int verticalDividerX;
        int colorDash;
        int colorWidth;
        int[] androidColors;

        public DividerItemDecoration(Context context) {
            verticalDividerX = (int) DeviceUtil.getPxForDp(context, 100);
            colorDash = (int) DeviceUtil.getPxForDp(context, 15);
            colorWidth = (int) DeviceUtil.getPxForDp(context, 5);

            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1);
            dividerPaint.setStyle(Paint.Style.STROKE);
            dividerPaint.setColor(context.getResources().getColor(R.color.divider_grey));

            colorPaint = new Paint();
            colorPaint.setStrokeWidth(colorWidth);
            colorPaint.setStyle(Paint.Style.STROKE);
            colorPaint.setColor(context.getResources().getColor(R.color.divider_grey));

            androidColors = getResources().getIntArray(R.array.transactions_right_colors);
        }

        @Override
        public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int left = 0;
                int right = parent.getWidth();

                if (i != childCount-1) {
                    canvas.drawLine(left, top, right, top, dividerPaint);
                }

                int midTop = child.getTop() + params.topMargin + colorDash;
                int midLeft = verticalDividerX;
                int midBottom = child.getBottom() - colorDash;

                canvas.drawLine(midLeft, midTop, midLeft, midBottom, dividerPaint);

                int realPosition = parent.getChildAdapterPosition(child);
                int androidColor = androidColors[realPosition%androidColors.length];

                colorPaint.setColor(androidColor);

                int rightTop = child.getTop() + params.topMargin + colorDash;
                int rightLeft = parent.getRight() - colorWidth / 2;
                int rightBottom = child.getBottom() - colorDash;

                canvas.drawLine(rightLeft, rightTop, rightLeft, rightBottom, colorPaint);
            }
        }
    }
}

