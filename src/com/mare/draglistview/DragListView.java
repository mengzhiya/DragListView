package com.mare.draglistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * @Description:����ק��ListView
 * @csdnblog http://blog.csdn.net/mare_blue
 * @author mare
 * @date 2016��5��17��
 * @time ����4:42:48
 */
public class DragListView extends ListView {

	private ImageView dragImageView;// ����ק�����ʵ����һ��ImageView
	private int dragSrcPosition;// ��ָ�϶���ԭʼ���б��е�λ��
	private int dragPosition;// ��ǰ�϶������б��е�λ��

	private int dragPoint;// �϶�����item������λ��
	private int dragOffset;// ��ǰList��ͼ����Ļ�ľ���

	private WindowManager windowManager;// windows���ڿ�����
	private WindowManager.LayoutParams windowParams;// ���ڿ�����ק�����ʾ�Ĳ���

	private int scaledTouchSlop;// �жϻ�����һ������
	private int upScrollBounce;// �϶���ʱ�򣬿�ʼ���Ϲ����ı߽�
	private int downScrollBounce;// �϶���ʱ�򣬿�ʼ���¹����ı߽�

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// ����ʱ��ʼ�϶�
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();

			dragSrcPosition = dragPosition = pointToPosition(x, y);
			if (dragPosition == AdapterView.INVALID_POSITION) {
				return super.onInterceptTouchEvent(ev);
			}

			ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
			dragPoint = y - itemView.getTop();
			dragOffset = (int) (ev.getRawY() - y);

			View dragger = itemView.findViewById(R.id.drag_image);
			if (dragger != null && x > dragger.getLeft() && x < dragger.getRight()) {
				upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 2);
				downScrollBounce = Math.max(y + scaledTouchSlop, getHeight() /2);

				itemView.setDrawingCacheEnabled(true);
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				itemView.destroyDrawingCache();
				startDrag(bm, y);
			}
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * �����¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			default:
				break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * ׼���϶�����ʼ���϶����ͼ��
	 * 
	 * @param bm
	 * @param y
	 */
	public void startDrag(Bitmap bm, int y) {
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP;
		windowParams.x = 0;
		windowParams.y = y - dragPoint + dragOffset;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		imageView.setBackgroundColor(Color.argb(80, 180, 60, 60));
		imageView.setScaleY(1.5f);
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(imageView, windowParams);
		dragImageView = imageView;
	}

	/**
	 * ֹͣ�϶���ȥ���϶���ľ���
	 */
	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	/**
	 * �϶�ִ�У���Move������ִ��
	 * 
	 * @param y
	 */
	public void onDrag(int y) {
		if (dragImageView != null) {
			windowParams.alpha = 0.8f;
			windowParams.y = y - dragPoint + dragOffset;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
		// Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		int scrollHeight = 0;
		if (y < upScrollBounce) {
			scrollHeight = scaledTouchSlop;// �������Ϲ���8������
		} else if (y > downScrollBounce) {
			scrollHeight = -scaledTouchSlop;// �������¹���8������
		}

		if (scrollHeight != 0) {
			setSelectionFromTop(dragPosition,
					getChildAt(dragPosition - getFirstVisiblePosition()).getTop() + scrollHeight);
		}
	}

	/**
	 * �϶����µ�ʱ��
	 * 
	 * @param y
	 */
	public void onDrop(int y) {

		// Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		// �����߽紦��
		if (y < getChildAt(1).getTop()) {
			// �����ϱ߽�
			dragPosition = 0;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			// �����±߽�
			dragPosition = getAdapter().getCount() - 1;
		}

		// ���ݽ���
		if (dragPosition >= 0 && dragPosition < getAdapter().getCount()) {
			DragListAdapter adapter = (DragListAdapter) getAdapter();
			String dragItem = adapter.getItem(dragSrcPosition);
			adapter.remove(dragItem);
			adapter.insert(dragItem, dragPosition);
		}

	}
}