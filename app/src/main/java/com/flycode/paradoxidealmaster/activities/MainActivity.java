package com.flycode.paradoxidealmaster.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.flycode.paradoxidealmaster.R;
import com.flycode.paradoxidealmaster.settings.AppSettings;
import com.flycode.paradoxidealmaster.utils.TypefaceLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView menuGridView = (GridView) findViewById(R.id.menu);
        assert menuGridView != null;
        menuGridView.setAdapter(new MenuAdapter(this));
        menuGridView.setOnItemClickListener(menuClickListener);
    }

    AdapterView.OnItemClickListener menuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 5) {
                AppSettings.sharedSettings(MainActivity.this).setIsUserLoggedIn(false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }
    };

    private class MenuAdapter extends ArrayAdapter {
        private Context context;
        private String titles[];
        private String dashes[];
        private String icons[];
        private Typeface icomoon;
        private Typeface avenirRoman;

        public MenuAdapter(Context context) {
            super(context, R.layout.item_menu);

            this.context = context;
            this.titles = context.getResources().getStringArray(R.array.menu_titles);
            this.dashes = context.getResources().getStringArray(R.array.menu_dashes);
            this.icons = context.getResources().getStringArray(R.array.menu_icons);
            this.icomoon = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.ICOMOON);
            this.avenirRoman = TypefaceLoader.loadTypeface(getAssets(), TypefaceLoader.AVENIR_ROMAN);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_menu, parent, false);
            }

            TextView iconTextView = (TextView) convertView.findViewById(R.id.icon);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
            View dashView = convertView.findViewById(R.id.dash);
            View bottomView = convertView.findViewById(R.id.bottom);
            View rightView = convertView.findViewById(R.id.right);

            iconTextView.setTypeface(icomoon);
            titleTextView.setTypeface(avenirRoman);

            iconTextView.setText(icons[position]);
            titleTextView.setText(titles[position]);
            dashView.setBackgroundColor(Color.parseColor(dashes[position]));

            rightView.setVisibility(position % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
            bottomView.setVisibility(position < 4 ? View.VISIBLE : View.INVISIBLE);

            return convertView;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
