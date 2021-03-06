package com.hzh.polygonview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Package: com.hzh.polygonview.widget
 * FileName: MyPolygonView
 * Date: on 2017/12/9  下午5:54
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class PolygonView extends View {
    //**************** 测试相关 ****************
    private boolean isDebug = false;
    private int[] TEST_SCORE_VALUE_ARR = new int[]{60, 80, 20, 40};
    private String[] DEFAULT_CATEGORY_TEXT_ARR = new String[]{
            "综合",
            "财运",
            "工作",
            "爱情"};
    private int[] DEFAULT_SCORE_VALUE_COLOR_ARR = (new int[]{
            Color.parseColor("#8943C9"),
            Color.parseColor("#DBA700"),
            Color.parseColor("#2EC9FF"),
            Color.parseColor("#FF6A91")});

    //**************** View相关 ****************
    //View的宽
    private int mWidth;
    //View的高
    private int mHeight;

    //**************** 图形相关 ****************
    //多边形的边数
    private int num = 4;
    //360度对应的弧度（为什么2π就是360度？弧度的定义：弧长 / 半径，一个圆的周长是2πr，如果是一个360度的圆，它的弧长就是2πr，如果这个圆的半径r长度为1，那么它的弧度就是，2πr / r = 2π）
    private double piDouble = 2 * Math.PI;
    //多边形中心角的角度（每个多边形的内角和为360度，一个多边形2个相邻角顶点和中心的连线所组成的角为中心角
    //中心角的角度都是一样的，所以360度除以多边形的边数，就是一个中心角的角度），这里注意，因为后续要用到Math类的三角函数
    //Math类的sin和cos需要传入的角度值是弧度制，所以这里的中心角的角度，也是弧度制的弧度
    private float centerAngle = (float) (piDouble / num);
    //最小的多边形的半径
    private int radius = (int) dp2px(20);
    //多边形层数
    private int PolygonLayerCount = 5;
    //着重点的半径
    private float focalPointCircleRadius = dp2px(2.5f);
    //多边形每个顶点旁边的的文字，例如爱情，事业
    private String[] categoryTextArr;
    //种类上分值对应的颜色值（例如：爱情 98%中的98%）
    private int[] scoreValueColorArr;
    //文字与多边形顶点之间的距离
    private final float textMargin = dp2px(5f);

    //**************** 绘制相关 ****************
    //坐标轴的画笔
    private Paint coordinateAxisPaint;
    //多边形的边的画笔
    private Paint borderPaint;
    //每个数值在中心线上的点连接画的区域的画笔
    private Paint areaPaint;
    //着重点原点画笔
    private Paint focalPointPaint;
    //种类文字的画笔
    private Paint textPaint;
    //种类文字颜色
    private int textColor = Color.parseColor("#5F5F5F");

    //**************** 配置相关 ****************
    //默认分数数组
    private int[] DEFAULT_SCORE_VALUE_ARR = new int[]{0, 0, 0, 0};
    //分数数组
    private int[] scoreValueArr = DEFAULT_SCORE_VALUE_ARR;
    //分数最大值
    private int maxValue = 100;
    //每个层级多边形按最大值分数值层，例如最大值是100，一共5层多边形
    //则100 / 5，每层的数值是20进制的，第一层是20，第二层是40，以此类推，一直到100
    private float rate = maxValue * 1.0f / PolygonLayerCount;
    //种类和分值文字之间的距离
    private float categoryScoreTextMargin = dp2px(5f);

    public PolygonView(Context context) {
        super(context);
        init();
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
//        setDebug(true);
        //设置默认种类文字\种类分值文字的颜色\
        config(DEFAULT_CATEGORY_TEXT_ARR, TEST_SCORE_VALUE_ARR, DEFAULT_SCORE_VALUE_COLOR_ARR);
        //初始化画笔
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //测试所用，画一个坐标轴
        coordinateAxisPaint = new Paint();
        coordinateAxisPaint.setAntiAlias(true);
        coordinateAxisPaint.setColor(Color.parseColor("#FF0000"));
        coordinateAxisPaint.setStrokeWidth(1.5f);
        coordinateAxisPaint.setStyle(Paint.Style.STROKE);
        //多边形的边的画笔
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#DFDFDF"));
        borderPaint.setStrokeWidth(3);
        borderPaint.setStyle(Paint.Style.STROKE);
        //每个数值在中心线上的点连接画的区域的画笔
        areaPaint = new Paint();
        areaPaint.setAntiAlias(true);
        areaPaint.setColor(Color.parseColor("#B7F5E1B5"));
        //设置画笔风格为填充，将区域的图形填充颜色
        areaPaint.setStyle(Paint.Style.FILL);
        //数值坐标点画圆的着重点画笔
        focalPointPaint = new Paint();
        focalPointPaint.setAntiAlias(true);
        focalPointPaint.setColor(Color.parseColor("#EDC577"));
        focalPointPaint.setStyle(Paint.Style.FILL);
        //多边形顶点旁边的种类文字画笔
        textPaint = new Paint();
        textPaint.setTextSize(sp2px(13));
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //记录整个View的宽高
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将坐标轴移到View的中心点
        canvas.translate(mWidth / 2, mHeight / 2);
        //测试模式下，画坐标轴和外接圆
        if (isDebug) {
            drawCoordinateAxis(canvas, coordinateAxisPaint);
            drawCircumCircle(canvas, coordinateAxisPaint);
        }
        //画多边形
        drawPolygon(canvas);
        //画每个多边形的对角线
        drawCatercornerLine(canvas);
        //画区域和着重点（按分数在轴上画点，连线，填充颜色）
        drawAreaAndFocalPointCircle(canvas);
        //画多边形每个顶点旁边的种类文字
        drawText(canvas);
    }

    /**
     * 画坐标轴
     */
    private void drawCoordinateAxis(Canvas canvas, Paint paint) {
        float halfWidth = mWidth / 2f;
        float halfHeight = mHeight / 2f;
        //画横坐标
        canvas.drawLine(-halfWidth, 0, halfWidth, 0, paint);
        //画纵坐标
        canvas.drawLine(0, -halfHeight, 0, halfHeight, paint);
    }

    /**
     * 画外接圆
     */
    private void drawCircumCircle(Canvas canvas, Paint paint) {
        //拿最外层的四边形的半径，画一个圆
        float r = PolygonLayerCount * radius;
        canvas.drawCircle(0, 0, r, paint);
    }

    /**
     * 画多边形
     */
    private void drawPolygon(Canvas canvas) {
        //多边形边角顶点的x坐标
        float pointX;
        //多边形边角顶点的y坐标
        float pointY;
        //总的圆的半径，就是全部多边形的半径之和
        Path path = new Path();
        //循环画出每个多边形
        for (int j = 1; j <= PolygonLayerCount; j++) {
            //多边形属性图，就是多少层的多边形的半径叠加，循环多遍就能组成多层
            int r = j * radius;
            //画前先重置路径
            path.reset();
            for (int i = 1; i <= num; i++) {
                //cos三角函数，中心角的邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
                pointX = (float) (Math.cos(i * centerAngle) * r);
                //sin三角函数，中心角的对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
                pointY = (float) (Math.sin(i * centerAngle) * r);
                //如果是一个点，则移动到这个点，作为起点
                if (i == 1) {
                    path.moveTo(pointX, pointY);
                } else {
                    //其他的点，就可以连线了
                    path.lineTo(pointX, pointY);
                }
            }
            path.close();
            canvas.drawPath(path, borderPaint);
        }
    }


    /**
     * 画每个多边形的对角线
     */
    private void drawCatercornerLine(Canvas canvas) {
        //最外层的多边形边角顶点的x坐标
        float x;
        //最外层的多边形边角顶点的y坐标
        float y;
        Path path = new Path();
        //这里取出最外层多边形的每个顶点
        //从中心向每个顶点划线，这样就组成对角线（例如4边形，2个顶点的线通过中心点连接就成了一条对角线）
        float r = PolygonLayerCount * radius;
        for (int i = 1; i <= PolygonLayerCount; i++) {
            //每次连线时，重置路径
            path.reset();
            x = (float) (Math.cos(i * centerAngle) * r);
            y = (float) (Math.sin(i * centerAngle) * r);
            path.lineTo(x, y);
            canvas.drawPath(path, borderPaint);
            //测试，画第一条起点直线
            if (isDebug) {
                if (i == 1) {
                    //画第一条起点直线（绿色直线）
                    Paint paint = new Paint();
                    paint.setStrokeWidth(8f);
                    paint.setColor(Color.parseColor("#00FF00"));
                    canvas.drawLine(0, 0, x, y, paint);
                    //画第一个起点（蓝色点）
                    Paint p = new Paint();
                    p.setStrokeWidth(15f);
                    p.setColor(Color.parseColor("#0000FF"));
                    canvas.drawPoint(x, y, p);
                }
            }
        }
    }

    /**
     * 画区域和着重点（按分数在轴上画点，连线，填充颜色）
     */
    private void drawAreaAndFocalPointCircle(Canvas canvas) {
        //画区域，其实就是给每个点连线，其实也是一个四边形
        Path path = new Path();
        //区域每个点的x坐标
        float x;
        //区域每个点的y坐标
        float y;
        for (int i = 1; i <= num; i++) {
            //每个分数除以倍率，就能求出对应点所对应的层级
            float r = (scoreValueArr[i - 1] / rate) * radius;
            //同样用三角函数计算坐标点
            x = (float) (Math.cos(i * centerAngle) * r);
            y = (float) (Math.sin(i * centerAngle) * r);
            //一开始点先移动到第一个点
            if (i == 1) {
                path.moveTo(x, y);
            } else {//后续将每个点连接
                path.lineTo(x, y);
            }
            //每个坐标点都画上一个着重色的圆点
            if (x != 0 && y != 0) {
                canvas.drawCircle(x, y, focalPointCircleRadius, focalPointPaint);
            }
        }
        //封闭图形，由于Paint
        path.close();
        canvas.drawPath(path, areaPaint);
    }

    /**
     * 画文字，画文字的流程：先测量出总文字占用的位置，使用drawText方法，start和end设置画部分文字的角标
     * 分2次画，先画种类文字，再切换颜色，画分数
     */
    private void drawText(Canvas canvas) {
        //最外层的多边形的半径
        float r = PolygonLayerCount * radius;
        //最外层多边形的顶点x坐标
        float x;
        //最外层多边形的顶点y坐标
        float y;

        Log.d("test ::: ", "width / 2 = " + mWidth / 2);
        Log.d("test ::: ", "height / 2 = " + mHeight / 2);
        Log.d("test ::: ", "图形宽度 = " + r);
        Log.d("test ::: ", "图形半宽度 = " + (mWidth - r));
        Log.d("test ::: ", "--------- ** --------");

        for (int i = 1; i <= num; i++) {
            //测量文字的宽高所用的区域的Rect对象
            Rect sumTextRect = new Rect();
            //分数种类文字，例如："爱情 98%"
            String sumStr = categoryTextArr[i - 1] + scoreValueArr[i - 1] + "%";
            //先测量出总文字的区域，再量分数文字的区域，最后画2次，组成总文字
            textPaint.getTextBounds(sumStr, 0, sumStr.length(), sumTextRect);
            //总文字长度
            float sumTextWidth = sumTextRect.width();
            //总文字宽度
            float sumTextHeight = sumTextRect.height();

            //测量分数文字和"%"所用区域的Rect对象
            Rect scoreRect = new Rect();
            //分数文字，例如"98%"
            String scoreStr = scoreValueArr[i - 1] + "%";
            //量分数文字的区域
            textPaint.getTextBounds(scoreStr, 0, scoreStr.length(), scoreRect);
            //分数加%的宽度
            float scoreTextWidth = scoreRect.width();
            //计算出多边形的每个顶点的坐标
            x = (float) (Math.cos(i * centerAngle) * r);
            y = (float) (Math.sin(i * centerAngle) * r);
            //文字的x坐标
            float textX = 0;
            //文字的y坐标
            float textY = 0;

            //先设置文字坐标为最外层的多边形的顶点的坐标，后面再做偏移，此时的文字的左下角都是贴着顶点的
            textX = x + textX;
            textY = y + textY;

            Log.d("test ::: ", "testX ::: " + textX);
            Log.d("test ::: ", "testY ::: " + textY);
            Log.d("test ::: ", "--------- ** --------");

            //如果y==0，并且x<0，则偏移一个总长度的位置
            if (y == r && x <= 0) {
                textX -= sumTextWidth / 2;
                textY += sumTextHeight;
            } else if (y == -r && x >= 0) {
                textX -= sumTextWidth / 2;
                textY -= sumTextHeight / 2;
            } else if (y <= 0 && x == -r) {
                textX -= sumTextWidth + textMargin;
                textY += sumTextHeight / 3;
            } else if (y >= 0 && x == r) {
                textX += textMargin;
                textY += sumTextHeight / 3;
            }

            //总文字减去分数文字，就是种类文字的长度最后一个字的角标
            int index = sumStr.length() - scoreStr.length();
            //画种类，先用黑色画种类
            canvas.drawText(categoryTextArr[i - 1], 0, index, textX, textY, textPaint);
            //设置对应种类的分数的颜色
            textPaint.setColor(scoreValueColorArr[i - 1]);
            //画分数，加3dp作为间隔,textX + scoreTextWidth的意思是起点移动到分数文字第一个字，LastY就是总文字的长度最后的位置
            canvas.drawText(scoreStr, 0, scoreStr.length(), textX + scoreTextWidth + categoryScoreTextMargin, textY, textPaint);
            //设置回种类文字的黑色颜色
            textPaint.setColor(textColor);
        }
    }

    /**
     * 设置测试模式
     *
     * @param debug true为是测试模式，false为不是
     */
    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 当前是否是测试模式
     *
     * @return true为是测试模式，false为不是
     */
    public boolean isDebug() {
        return isDebug;
    }

    /**
     * 设置种类文字数组
     *
     * @param categoryTextArr 种类文字数组
     */
    public void setCategoryTextArr(String[] categoryTextArr) {
        this.categoryTextArr = categoryTextArr;
        postInvalidate();
    }

    /**
     * 设置分值文字的颜色数组
     *
     * @param scoreValueColorArr 分值文字颜色数组
     */
    public void setScoreValueColorArr(int[] scoreValueColorArr) {
        this.scoreValueColorArr = scoreValueColorArr;
        postInvalidate();
    }

    /**
     * 设置数值数组
     *
     * @param categoryTextArr    种类文字数组
     * @param scoreValueArr      种类数值数组
     * @param scoreValueColorArr 种类分值对应的颜色数组
     */
    public void config(String[] categoryTextArr, int[] scoreValueArr, int[] scoreValueColorArr) {
        this.categoryTextArr = categoryTextArr;
        this.scoreValueArr = scoreValueArr;
        this.scoreValueColorArr = scoreValueColorArr;
        //按传入的分值数组设定多边形边数
        this.num = scoreValueArr.length;
        //重新设置多边形中心角的角度
        this.centerAngle = (float) (piDouble / num);
        //通知重绘
        postInvalidate();
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected float dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected float sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }
}
