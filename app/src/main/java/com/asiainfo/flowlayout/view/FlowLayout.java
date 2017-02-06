package com.asiainfo.flowlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者:小木箱 邮箱:yangzy3@asiainfo.com 创建时间:2/2/17/12:31 描述:流式布局自定义view
 */

public class FlowLayout extends ViewGroup {

    //存储所有的view
    private List<List<View>> mAllViews = new ArrayList<>();

    //每一行的高度
    private List<Integer> mLineHight = new ArrayList<>();

    /**
     * 描述:三个构造方法调用的时间是,我们在布局文件书写自定义控件,并且书写自定义属性
     */

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mAllViews.clear();
        mLineHight.clear();
        //当前viewGroup的宽度
        int width = getWidth();
        int lineHight = 0;
        int lineWidth = 0;

        List<View> lineViews = new ArrayList<>();

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {

            View child = getChildAt(i);

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHight = child.getMeasuredHeight();

            //如果需要换行
            if (childWidth + lineWidth + lineHight + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {

                //记录lineHeight
                mLineHight.add(lineHight);

                //记录当前行的view
                mAllViews.add(lineViews);

                //重置我们的行宽和行高
                lineWidth = 0;
                lineHight = childHight + lp.topMargin + lp.bottomMargin;

                //重置我们的view集合
                lineViews = new ArrayList<>();

            }

            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHight = Math.max(lineHight, childHight + lp.bottomMargin + lp.topMargin);
            lineViews.add(child);

        }//for end

        //处理最后一行
        mLineHight.add(lineHight);
        mAllViews.add(lineViews);

        //设置view的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();

        //行数
        int lineNum = mAllViews.size();
        for (int i = 0; i < lineNum; i++) {

            //当前行所有的view
            lineViews = mAllViews.get(i);
            lineHight = mLineHight.get(i);

            for (int j = 0; j < lineViews.size(); j++) {

                View child = lineViews.get(j);

                //判断child的状态
                if (child.getVisibility() == View.GONE) {

                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                //为子view进行布局
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

            }

            left = getPaddingLeft();
            top += lineHight;
        }

    }

    //父级在onMeasure方法传来的两个控件测量值--宽度的测量:withMeasureSpec,高度的测量:heightMeasureSpec
    @Override
    protected void onMeasure(int withMeasureSpec, int heightMeasureSpec) {

        int sizeWidth = MeasureSpec.getSize(withMeasureSpec);
        int modeWidth = MeasureSpec.getMode(withMeasureSpec);

        int sizeHigh = MeasureSpec.getSize(heightMeasureSpec);
        int modeHigh = MeasureSpec.getMode(heightMeasureSpec);

        //存储wrap_content的宽高值

        int width = 0;
        int height = 0;

        //记录每一行的高度和宽度
        int lineWidth = 0;
        int lineHight = 0;

        //得到内部元素的个数
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {

            View child = getChildAt(i);
            //测量子view的宽和高
            measureChild(child, withMeasureSpec, heightMeasureSpec);
            //得到layoutparam
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            //子view占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

            //子view占据的高度
            int childHight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {

                //换行
                //对比得到最大的宽度
                width = Math.max(width, lineWidth);
                //重置linewidth
                lineWidth = childWidth;
                //记录行高
                height += lineHight;

                lineHight = childHight;

            } else {//未换行

                //叠加行宽
                lineWidth += childWidth;
                //得到当前行最大的高度
                lineHight = Math.max(lineHight, childHight);

            }

            //最后一个控件
            if (i == cCount - 1) {
                height += lineHight;
                width = Math.max(lineWidth, width);
            }

        }
        Log.e("TAG", "sizeWidth = " + sizeWidth);
        Log.e("TAG", "sizeHigh = " + sizeHigh);

        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHigh == MeasureSpec.EXACTLY ? sizeHigh : height + getPaddingTop() + getPaddingBottom());
    }

    /**
     * 作者:小木箱 邮箱:569015640@qq.com 创建时间:2/2/17/15:48 描述:与当前ViewGroup对应的LayOutParams
     */

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
