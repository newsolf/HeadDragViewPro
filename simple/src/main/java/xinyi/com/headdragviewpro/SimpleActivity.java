package xinyi.com.headdragviewpro;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import xinyi.com.headdragview.HeadDragView;

/**
 * Created by 陈章 on 2017/6/13 0013.
 * func:
 */

public class SimpleActivity extends Activity implements HeadDragView.OnDragUpdateListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        HeadDragView hdv = (HeadDragView) findViewById(R.id.hdv);


        hdv.setOnDragUpdateListener(this);
    }

    @Override
    public void onOpen() {
        Toast.makeText(this, "open", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClose() {
        Toast.makeText(this, "close", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDraging(float percent) {

    }

}
