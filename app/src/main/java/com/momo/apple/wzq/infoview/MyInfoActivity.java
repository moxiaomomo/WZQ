package com.momo.apple.wzq.infoview;

import com.momo.apple.wzq.R;
import com.momo.apple.wzq.database.DBWzq;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MyInfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        WindowManager.LayoutParams p =this.getWindow().getAttributes();
        p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(p);

        inittitle();

        DBWzq helpter = new DBWzq(this);
        Cursor c = helpter.query();

        String[] from = {"_id", "score"};
        int[] to = {R.id.jilu_name, R.id.jilu_score};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.myinfolist, c, from, to);
        ListView listView = (ListView) findViewById(R.id.scorelist);
        listView.setAdapter(adapter);
        //c.close();
        helpter.close();
    }

    private void inittitle() {
        View topView = findViewById(R.id.top_view);        //ͷ����ͼ
        TextView titlename = (TextView) topView.findViewById(R.id.titlename);
        ImageButton backbtn = (ImageButton) topView.findViewById(R.id.btn_back);
        backbtn.setVisibility(View.VISIBLE);
        titlename.setText(this.getString(R.string.myInfo));

        backbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
