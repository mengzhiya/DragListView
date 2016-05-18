package com.mare.draglistview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

/**
 * @Description:
 * @csdnblog http://blog.csdn.net/mare_blue
 * @author mare
 * @date 2016年5月17日
 * @time 下午5:06:53
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setData();
	}

	private void setData() {
		DragListView dragListView = (DragListView) findViewById(R.id.list);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			list.add(" Test " + i);
		}
		DragListAdapter adapter = new DragListAdapter(this, list);
		dragListView.setAdapter(adapter);
	}

}
