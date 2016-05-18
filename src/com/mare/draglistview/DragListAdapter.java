package com.mare.draglistview;

import java.util.List;

import com.mare.draglistview.R;
import com.mare.draglistview.R.id;
import com.mare.draglistview.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DragListAdapter extends ArrayAdapter<String> {
	private List<String> list;
	public DragListAdapter(Context context, List<String> objects) {
		super(context, 0, objects);
		this.list = objects;
	}

	public List<String> getList() {
		return list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.drag_list_item, null);
		}

		TextView textView = (TextView) convertView.findViewById(R.id.drag_list_item_text);
		textView.setText(list.get(position));

		return convertView;
	}
}