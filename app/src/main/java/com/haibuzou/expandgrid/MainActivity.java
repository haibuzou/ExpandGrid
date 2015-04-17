package com.haibuzou.expandgrid;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {
    //整个界面的 Layout
    private RelativeLayout mainLayout;
    //点击展开的layout
    private LinearLayout expandLayout;
    //记录上一次点击的位置
    private int lastposition = -1;
    //一排3个item
    private int mNum = 3;
    private LayoutInflater inflater;
    //行数
    private int rows;
    //文字的左向padding距离
    private int padLeft;
    //文字竖直方向的padding距离
    private int padVertical;
    //主表格
    private TableLayout tableLayout;
    //点击下拉的列表数据
    private List<String> seondJobList;
    //展开和收起动画的时长
    private int duration = 200;
    //viewpager下面的点点layout
    private LinearLayout circleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView() {
        seondJobList = Arrays.asList(Jobs.SECOND_JOB);
        inflater = LayoutInflater.from(this);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        tableLayout = new TableLayout(this);
        tableLayout.setStretchAllColumns(true);
        padLeft = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));
        padVertical = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()));
        int size = Jobs.FIRST_JOB.length;
        //计算table的行数
        rows = size / mNum;
        if (size % mNum > 0) {
            rows += 1;
        }
        for (int i = 0; i < rows; i++) {
            TableRow tableRow = new TableRow(this);
            for (int j = 0; j < mNum; j++) {
                GridItemView itemView = (GridItemView)inflater.inflate(R.layout.table_item,null);
                itemView.setText(Jobs.FIRST_JOB[i * 3 + j]);
                itemView.setOnClickListener(new onTextClickListener(i, i * 3 + j, itemView));
                tableRow.addView(itemView);
            }
            //下拉的layout
            View view = inflater.inflate(R.layout.expand_layout, null);
            view.setTag(i);
            //tablelayout 添加 tablerow 同时每个tablerow下面都有一个gone的 expand layout
            tableLayout.addView(tableRow);
            tableLayout.addView(view);
        }
        mainLayout.addView(tableLayout);
    }

    //初始化下拉的布局
    public void initExpandLayout(View view) {
        //viewpager底部点的大小
        int circle = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        //下拉的viewpager中的view
        List<View> viewList = new ArrayList<>();
        //下拉的viewpager
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.expand_viewpager);
        circleLayout = (LinearLayout) view.findViewById(R.id.circle_layout);
        //每次初始化需要remove掉之前的点
        circleLayout.removeAllViews();
        //计算生成gridview的数目,一行3个 最多4行，一张表最多12项
        int num = Jobs.SECOND_JOB.length / 12;
        //除12有余数 说明还需要多加一个
        if (Jobs.SECOND_JOB.length % 12 != 0) {
            num += 1;
        }
        //计算下拉的gridview的行数，用于设置ViewPager的高度
        int mrow = Jobs.SECOND_JOB.length / mNum;
        if (mrow % mNum != 0) {
            mrow += 1;
        }
        //最多有4行
        if (mrow > 4) {
            mrow = 4;
        }
        //生成gridview
        for (int i = 0; i < num; i++) {
            GridView gridView = new GridView(this);
            gridView.setNumColumns(3);
            GridAdapter gridAdapter = new GridAdapter(seondJobList.subList(i * 12, i * 12 + 12), this);
            gridView.setAdapter(gridAdapter);
            viewList.add(gridView);
            //如果生成的gridview大于1个才会显示底部的点
            if (num > 1) {
                //viewpager下方的红点 黑点
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.mipmap.black_circle);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(circle,circle));
                imageView.setPadding(0, 0, 5, 0);
                //这里+10为了不与上面的Tag 混淆
                imageView.setTag(i+10);
                circleLayout.addView(imageView);
            }
        }
        //ViewPager初始化
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(viewList);
        //girdview每一行的高度设为42dp
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mrow * height);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setLayoutParams(params);
        viewPager.setOnPageChangeListener(this);
    }


    GridItemView lastClickView;

    class onTextClickListener implements View.OnClickListener {

        int tag;
        int position;
        GridItemView itemView;


        public onTextClickListener(int tag, int position, GridItemView itemView) {
            this.tag = tag;
            this.position = position;
            this.itemView = itemView;
        }

        @Override
        public void onClick(View v) {
            View view = tableLayout.findViewWithTag(tag);
            initExpandLayout(view);
            if (expandLayout == null) {//当下拉的界面为时，直接初始化，并执行expand动画
                expandLayout = (LinearLayout) view.findViewById(R.id.expand_layout);
                itemView.drawtriangle(true);
                expand(expandLayout);
            } else {
                if (expandLayout.getVisibility() == View.VISIBLE) {
                    itemView.drawtriangle(false);
                    collapse(expandLayout);
                } else {
                    if (position == lastposition) {
                        itemView.drawtriangle(true);
                        expand(expandLayout);
                    }
                }

                if (position != lastposition) {
                    expandLayout = (LinearLayout) view.findViewById(R.id.expand_layout);
                    itemView.drawtriangle(true);
                    expand(expandLayout);
                    //上一次箭头清除
                    if (lastClickView != null) {
                        lastClickView.drawtriangle(false);
                    }
                }
            }

            lastClickView = itemView;
            lastposition = position;

        }
    }


    //View的展开动画
    public void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        // 最开始显示下拉控件的时候 将高度设为0 达到不显示的效果
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                // 不断变化高度来显示控件
                v.getLayoutParams().height = (interpolatedTime == 1) ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    //View的收缩动画
    public void collapse(final View v) {
        final int height = v.getMeasuredHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                // 不断减少高度来显示控件 直到消失
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = height - (int) (height * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        v.setAnimation(animation);
    }

    //上一个点的位置
    private int lastCircle = -1;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        //上一个点置灰
        if (lastCircle != -1) {
            ImageView lastImage = (ImageView) circleLayout.findViewWithTag(lastCircle+10);
            if (lastImage != null) {
                lastImage.setImageResource(R.mipmap.black_circle);
            }
        }

        ImageView imageView = (ImageView) circleLayout.findViewWithTag(position+10);
        if (imageView != null) {
            imageView.setImageResource(R.mipmap.orange_circle);
        }

        lastCircle = position;

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
