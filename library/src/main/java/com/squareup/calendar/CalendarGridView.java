package com.squareup.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * ViewGroup that draws a grid of calendar cells.  All children must be {@link CalendarRowView}s.
 * The first row is assumed to be a header and no divider is drawn above it.
 */
public class CalendarGridView extends ViewGroup {
  private final Paint dividerPaint = new Paint();

  public CalendarGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    dividerPaint.setColor(getResources().getColor(R.color.calendar_divider));
  }

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    if (getChildCount() > 0) {
      ((CalendarRowView) child).setDividerPaint(dividerPaint);
    }
    super.addView(child, index, params);
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    final boolean retVal = super.drawChild(canvas, child, drawingTime);
    // Draw a bottom border.
    final int bottom = child.getBottom() - 1;
    canvas.drawLine(child.getLeft(), bottom, child.getRight(), bottom, dividerPaint);
    return retVal;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    long start = System.currentTimeMillis();
    int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
    int cellSize = totalWidth / 7;
    totalWidth = cellSize * 7; // Remove any extra pixels since /7 is unlikely to give whole nums.
    int totalHeight = 0;
    final int rowWidthSpec = makeMeasureSpec(totalWidth, EXACTLY);
    final int rowHeightSpec = makeMeasureSpec(cellSize, EXACTLY);
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      if (child.getVisibility() == View.VISIBLE) {
        if (c == 0) { // It's the header: height should be wrap_content.
          measureChild(child, rowWidthSpec, makeMeasureSpec(cellSize, AT_MOST));
        } else {
          measureChild(child, rowWidthSpec, rowHeightSpec);
        }
        totalHeight += child.getMeasuredHeight();
      }
    }
    setMeasuredDimension(totalWidth, totalHeight);
    Log.d("CalendarPicker", "Grid.onMeasure " + (System.currentTimeMillis() - start) + "ms");
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    long start = System.currentTimeMillis();
    top = 0;
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      final int rowHeight = child.getMeasuredHeight();
      child.layout(left, top, right, top + rowHeight);
      top += rowHeight;
    }
    Log.d("CalendarPicker", "Grid.onLayout " + (System.currentTimeMillis() - start) + "ms");
  }
}
