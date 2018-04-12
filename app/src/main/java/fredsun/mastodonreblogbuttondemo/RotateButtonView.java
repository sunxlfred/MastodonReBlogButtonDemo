package fredsun.mastodonreblogbuttondemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class RotateButtonView extends View  {
    final String DEBUG_TAG = "DEBUG_TAG";
    private PathMeasure mPathMeasure;
    Path path, pathTriangle, pathTriangleRight, pathTrans, pathTransRight;
    Paint paint, paintTriangle, paintTrans;
    private float[] pos = new float[2];
    private float[] tan = new float[2];
    private int mWidth, mHeight;
    float mAnimatorValue;
    float rectWidth, rectHeight;
    float triangleWidth, triangleHeight;
    float offset, offsetTrans;
    Xfermode xfermode;
    float strokeWidth, roundCornerHeight, sweepAngle;
    boolean FLAG_SELECTED;
    ValueAnimator valueAnimator;
    private SparkEventListener mListener;
    public void setEventListener(SparkEventListener listener){
        this.mListener = listener;
    }

    public RotateButtonView(Context context) {
        super(context);
    }

    public RotateButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        pathTriangle = new Path();
        pathTriangleRight = new Path();
        pathTrans = new Path();
        pathTransRight = new Path();
        paint = new Paint();
        paintTriangle = new Paint();
        paintTrans = new Paint();
        mPathMeasure = new PathMeasure();

        Drawable background = getBackground();
        if (background instanceof ColorDrawable){
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            paintTrans.setColor(color);
        }else if (background instanceof BitmapDrawable){
            throw new AssertionError("you can't set a bitmap as background ");
        }else {
            paintTrans.setColor(getResources().getColor(R.color.colorWhite));
        }
        //创建一个值从0到xxx的动画
        valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(1200);
        //每过10毫秒 调用一次
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i("animator", "start");
                paint.setColor(getResources().getColor(R.color.colorBlue));
                paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("animator", "end");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i("animator", "cancel   ");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public RotateButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        FLAG_SELECTED = false;
        mWidth = w;
        mHeight = h;
        rectWidth = mWidth * 12 / 42;
        rectHeight =  mHeight* 9 / 36;
        triangleWidth = mWidth * 18 / 42;
        triangleHeight = mHeight * 12 / 36;
        offset = mHeight   / 36 ;
        offsetTrans = mHeight * 3 / 36;
        strokeWidth = mWidth * 6 / 42;
        roundCornerHeight = strokeWidth/2;//矩形的圆角促使矩形缩短的距离
        sweepAngle = 45;//矩形的圆角划过的角度

        //绘制圆角矩形
        path.moveTo(-rectWidth, -offset);//左侧中间点
        path.lineTo(-rectWidth, -(rectHeight-strokeWidth));
        RectF rectF = new RectF(-rectWidth, -rectHeight, -(rectWidth - 2 * roundCornerHeight), -(rectHeight - 2 * roundCornerHeight));
        path.arcTo(rectF, -180 + sweepAngle/2, sweepAngle, false);
        path.lineTo(-(rectWidth-roundCornerHeight), -rectHeight );
        path.lineTo(rectWidth - roundCornerHeight, -rectHeight);
        RectF rectRightTop = new RectF(rectWidth - 2 * roundCornerHeight, -rectHeight, rectWidth, -(rectHeight - 2 * roundCornerHeight));
        path.arcTo(rectRightTop, -90 + sweepAngle/2, sweepAngle);
        path.lineTo(rectWidth, rectHeight - roundCornerHeight);
        RectF rectRightBottom = new RectF(rectWidth - 2 * roundCornerHeight, rectHeight - 2 * roundCornerHeight, rectWidth, rectHeight);
        path.arcTo(rectRightBottom, sweepAngle/2,sweepAngle,false);
        path.lineTo(-(rectWidth-roundCornerHeight), rectHeight);
        RectF rectLeftBottom = new RectF(-rectWidth, rectHeight - 2 * roundCornerHeight, -(rectWidth - 2 * roundCornerHeight), rectHeight);
        path.arcTo(rectLeftBottom, 90 + sweepAngle/2+20, sweepAngle, false);
        path.lineTo(-rectWidth, -offset);

        mPathMeasure.setPath(path, true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(getResources().getColor(R.color.colorGray));

//        //绘制左侧背景色条
        pathTrans.moveTo(0,-triangleWidth/2*1.2f);
        pathTrans.lineTo(triangleHeight*1.2f,0);
        pathTrans.lineTo(0,triangleWidth/2*1.2f);
        pathTrans.lineTo(0,-triangleWidth/2*1.2f);
        paintTrans.setStyle(Paint.Style.FILL);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
        paintTrans.setXfermode(xfermode);

//        //绘制左侧三角形
        paintTriangle.setStyle(Paint.Style.FILL);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(offsetTrans);//圆角
        paintTriangle.setPathEffect(cornerPathEffect);
        paintTriangle.setColor(getResources().getColor(R.color.colorGray));
        pathTriangle.lineTo(0,-triangleWidth / 2);
        pathTriangle.lineTo(triangleHeight,0);
        pathTriangle.lineTo(0,triangleWidth / 2);
        pathTriangle.close();

        //绘制右侧三角形
        pathTriangleRight.lineTo(0,-triangleWidth/2);
        pathTriangleRight.lineTo(-triangleHeight,0);
        pathTriangleRight.lineTo(0,triangleWidth/2);
        pathTriangleRight.close();

        //绘制右侧背景色条
        pathTransRight.moveTo(0,-triangleWidth/2*1.2f);
        pathTransRight.lineTo(-triangleHeight*1.2f,0);
        pathTransRight.lineTo(0,triangleWidth/2*1.2f);
        pathTransRight.lineTo(0,-triangleWidth/2*1.2f);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(240,200);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(240, heightSpecSize);
        }else if(heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, 200);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("view", "value"+mAnimatorValue);
        mPathMeasure.getPosTan(mAnimatorValue * mPathMeasure.getLength()/2, pos, tan);

        canvas.save();
        canvas.translate(mWidth/2, mHeight/2);//坐标系原点切到控件1/2处
        canvas.drawPath(path, paint);

        float degree = (float) (Math.atan2(tan[1], tan[0])*180.0/ Math.PI);
        //坐标系移动到左侧的path起点
        canvas.translate(pos[0],pos[1]);
        //画布旋转趋势与x轴的夹角
        canvas.rotate(degree);
        int i = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(pathTriangle, paintTriangle);
        canvas.drawPath(pathTrans, paintTrans);
        canvas.restoreToCount(i);
        //画布画布转回原来的夹角
        canvas.rotate(-degree);
        //坐标系移动到原点
        canvas.translate(-pos[0], -pos[1]);
        //坐标系移动到右侧的path起点
        canvas.translate(-pos[0], -pos[1]);
        //画布旋转趋势与x轴的夹角
        canvas.rotate(degree);
        int j = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(pathTriangleRight, paintTriangle);
        canvas.drawPath(pathTransRight, paintTrans);
        canvas.restoreToCount(j);
        canvas.restore();
    }

    void setSelected(){
        paint.setColor(getResources().getColor(R.color.colorBlue));
        paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
        postInvalidate();
    }
    void setUnSelected(){
        paint.setColor(getResources().getColor(R.color.colorGray));
        paintTriangle.setColor(getResources().getColor(R.color.colorGray));
        postInvalidate();
    }
    void startMove(){
        valueAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action){
            case (MotionEvent.ACTION_DOWN) :
                //防止连点
                if (valueAnimator.isRunning()){
                    return false;
                }
                Log.d(DEBUG_TAG,"Action was DOWN");
                paint.setColor(getResources().getColor(R.color.colorAccent));
                paintTriangle.setColor(getResources().getColor(R.color.colorAccent));
                postInvalidate();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(DEBUG_TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(DEBUG_TAG,"Action was UP");
                //对抬起时的区域判断
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    if (FLAG_SELECTED) {
                        paint.setColor(getResources().getColor(R.color.colorGray));
                        paintTriangle.setColor(getResources().getColor(R.color.colorGray));
                        postInvalidate();
                        FLAG_SELECTED = false;

                    } else {
                        paint.setColor(getResources().getColor(R.color.colorBlue));
                        paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
                        FLAG_SELECTED = true;
                        startMove();
                    }
                }else {
                   if (FLAG_SELECTED){
                       paint.setColor(getResources().getColor(R.color.colorBlue));
                       paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
                       postInvalidate();
                   }else {
                       paint.setColor(getResources().getColor(R.color.colorGray));
                       paintTriangle.setColor(getResources().getColor(R.color.colorGray));
                       postInvalidate();
                   }
                }
                mListener.onFingerUp(FLAG_SELECTED);
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(DEBUG_TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
    public interface SparkEventListener{
        void onFingerUp(boolean flag);
    }
}
