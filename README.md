# PolygonView

### 文章地址
- [简书](https://www.jianshu.com/p/0963fc9e2f14)

### 需求：多边形属性图，后台返回例如爱情、财运、工作等属性的百分比，客户端画出UI的设计图中的效果。

### 最终效果图
![最终效果图.png](http://upload-images.jianshu.io/upload_images/1641428-5767d640500aa058.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 声明
- 该文章部分图片采用以下博客链接，还有一些概念解释，读者可以先看看该文章，容易理解。
- [Canvas学习：绘制正多边形](https://www.w3cplus.com/canvas/drawing-regular-polygons.html)

### 分析

- 多边形是正多边形（什么是：正多边形？下面概念有讲哈，大家打起精神！）
- 多边形的边是灰色的。
- 多边形总共5层，每一层的多边形的半径叠加。
- 多边形的顶点，旁边都有对应的属性种类名称和分值的百分比，百分比数值是有颜色的。
- 分值在多边形上，有对应的比值，并且有着重点，并且有颜色。
- 全部分值构成一个多边形，并且有颜色填充，颜色比着重点的颜色浅。

### 解析

- 边是灰色，则画边的paint的颜色是灰色。
- 五层多边形，先画中间的多边形，用for循环，循环5次，每次叠加一次半径。
- 找出最外层的多边形的4个顶点的坐标，偏移一定距离后，canvas.drawText()分别画种类文字和有颜色的百分比分值。
- 着重点，分值和图形有个比例关系，换算后，画点，半径要大一点。
- 最后将所有的点连线，设置画笔的风格为填充，就成为了我们的闭合图形。

# 一些概念
- View的坐标系，从View的左上角为（0，0）点，右、下为正，和数学的坐标轴不同（这里要清楚，数学的坐标轴是右为正，下为负）

- 正多边形，什么是正多边形呢？（像我们设计图上的就是一个正四边形）

```
维基百科，上是这样描述的：正多边形是所有角都相等、并且所有边都相等的简单多边形，简单多边形是指在任何位置都不与自身相交的多边形。
```
- 正多边形的所有顶点都在同一个外接圆上，每个正多边形都有一个外接圆，这也称为圆内接正多边形。

- 多边形的内角和为360度。

- 2个相邻的多边形从顶点到外接圆的圆心构成的角为中心角，并且中心角个数为多边形的边数，每个角的角度相等。
![中心角.png](http://upload-images.jianshu.io/upload_images/1641428-178de407c4ff6c47.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 假设我们求出了中心角的角度，其实就可以求出每个顶点的坐标了，怎么算？三角函数。

### 初始化

- 首先是新建类，继承我们的View，在构造方法中，调用我们的init方法，初始化一些画笔。

```java
public class PolygonView extends View {
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
        //初始化画笔
        initPaint();
    }
}
```

- 定义我们一些需要的常量参数

```java
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
```

- 尺寸转换方法

```java
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
```

- 初始化画笔

```java
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
```

- 这里我们为了好看，画多边形的外接圆和中心线，方便我们写时用，在init方法加入我们设置测试方法。

```java
/**
     * 初始化
     */
    private void init() {
    	//设置画外接圆和中心线
        setDebug(true);
        //初始化画笔
        initPaint();
    }
```

- 重写onSizeChanged()获取我们View的宽高

```java
	//View的宽
    private int mWidth;
    //View的高
    private int mHeight;

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //记录整个View的宽高
        mWidth = w;
        mHeight = h;
    }
```

- 重写我们的onDraw方法，绘制我们的图形

```java
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
    }
```

- 一开始我们用 canvas.translate(mWidth / 2, mHeight / 2);方法将View的原点坐标设置到了我们的View的中心。画测试的外接圆和坐标轴。

![外接圆和坐标轴.png](http://upload-images.jianshu.io/upload_images/1641428-5061aa6ffd347fab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 外接圆和坐标轴代码

```java
	 //多边形层数
    private int PolygonLayerCount = 5;

    /**
     * 画坐标轴
     */
    private void drawCoordinateAxis(Canvas canvas, Paint paint) {
    	 //一半的宽和高
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
```
- 画5层多边形，在onDraw方法上加上一个drawPolygon(canvas)方法

```java
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
```
![多边形外框.png](http://upload-images.jianshu.io/upload_images/1641428-23265c266a061c49.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 画每个多边形的对角线

```java
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
```

- 画区域和着重点（按分数在轴上画点，连线，填充颜色）

![画区域和着重点.png](http://upload-images.jianshu.io/upload_images/1641428-eead24bdeb3e1f8f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 画文字

```java
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
```

![画文字.png](http://upload-images.jianshu.io/upload_images/1641428-0dd78c8eaa7bcbf0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 最后是一些暴露出来的设置方法，记得设置完后要调用重绘方法

```java
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
```

### github地址

- [github地址](https://github.com/hezihaog/PolygonView)