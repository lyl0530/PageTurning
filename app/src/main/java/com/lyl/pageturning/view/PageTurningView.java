package com.lyl.pageturning.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Optional;


public class PageTurningView extends View  {
    private static final String TAG = "lym123";
    private Paint mAPaint, mBPaint, mCPaint;
    private Path mAPath, mBPath, mCPath;
    private float aX, aY, bX, bY, cX, cY, dX, dY, eX, eY, fX, fY,
            hX, hY, iX, iY, jX, jY, kX, kY;
    private float mViewH, mViewW;
    private float valid1X, valid1Y, valid2X, valid2Y;//有效值
    private Canvas bitmapCanvas;
    private Bitmap mBitmap;
    private boolean bRCornerStart = true;//是否从右下角开始翻页

    public PageTurningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageTurningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mAPaint = new Paint();
        mAPaint.setColor(Color.GREEN);
        mAPaint.setAntiAlias(true);
        //mAPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        mBPaint = new Paint();
        mBPaint.setColor(Color.WHITE);
        mBPaint.setAntiAlias(true);

        mCPaint = new Paint();
        mCPaint.setColor(Color.YELLOW);
        //mCPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));//SRC_OVER/SRC_ATOP/DST_OVER
        mCPaint.setAntiAlias(true);

        mAPath = new Path();
        mBPath = new Path();
        mCPath = new Path();

        valid2X = valid1X = cX = 0;
        valid2Y = valid1Y = jY = 0;
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        aX = fX = mViewW = getWidth();
        aY = fY = mViewH = getHeight();
        Log.d(TAG, "onLayout: mViewH = " + mViewH + ", mViewW = " + mViewW);
        if (null == mBitmap) {
            //获取bitmap，用该bitmap构造一个bitmapCanvas，然后用bitmapCanvas来drawPath。
            //最后，用canvas显示该bitmap
            mBitmap = Bitmap.createBitmap((int)mViewW, (int)mViewH, Bitmap.Config.RGB_565);
            bitmapCanvas = new Canvas(mBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long l = System.currentTimeMillis();
        getEveryPoint();
        getAPath();
        getBPath();
        getCPath();
        //显示顺序B-C-A，C在B上，使用SRC_OVER模式，此时C会把B的贝塞尔曲线部分覆盖，再画A，A把C多出来的部分覆盖。
        //AB分割是akij,abdc这个两条贝塞尔曲线，C是abdika这个闭合的直线组成
        bitmapCanvas.drawPath(mBPath, mBPaint);
        bitmapCanvas.drawPath(mCPath, mCPaint);
        bitmapCanvas.drawPath(mAPath, mAPaint);
        canvas.drawBitmap(mBitmap,0,0,null);
        Log.d(TAG, "onDraw: " + (System.currentTimeMillis() - l));
        Log.d(TAG, "onDraw: " + canvas.isHardwareAccelerated());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                bRCornerStart = event.getY() >= mViewH/2f;
                fX = mViewW;
                fY = bRCornerStart ? mViewH : 0;
                aX = event.getX();
                aY = event.getY();
                //Log.d(TAG, "[" + fX + "," + fY + "], [" + aX + "," + aY +"]");
                break;
            case MotionEvent.ACTION_MOVE:
                aX = event.getX();
                aY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private void getEveryPoint(){
        //处理边界问题
        float tempY = 3f*(fY-aY)/4f;
        float tempX = 3f*(fX-aX)/4f;
        cX = 0 == tempX ? fX : fX - (tempY*tempY/tempX+tempX);
//        Log.d(TAG, "cX = " + cX + "[" + fX + "," + fY
//                + "], [" + aX + "," + aY +"]"
//                + ", fX-aX = " + (fX-aX)
//                + ", fY-aY = " + (fY-aY));
        if (cX < 0) {
            aX = fX - valid1X;
            aY = fY - valid1Y;
        } else {
            valid1X = fX-aX;
            valid1Y = fY-aY;
        }
        //Log.d(TAG, "[aX, aY] = [" + aX + "," + aY +"]");

        jY = 0 == tempY ? fY : fY - (tempX*tempX/tempY + tempY);
        //Log.d(TAG, "jY = " + jY);
        if (jY < 0 || jY > mViewH) {//当从右上侧开始翻页时，jY不能超过屏幕宽度
            aX = fX - valid2X;
            aY = fY - valid2Y;
        } else {
            valid2X = fX-aX;
            valid2Y = fY-aY;
        }

        float gX = aX + (fX - aX)/2f;
        float gY = aY + (fY - aY)/2f;

        //gm/mf = em/mg -> e
        float mg = (fY - gY);
        float fm = fX - gX;
        eX = 0 == fm ? gX :gX - mg*mg/fm;
        eY = fY;

        // h
        float of = mg;
        float go = fm;
        hX = fX;
        hY = 0 == of ? gY : gY - go*go/of;
        // n
        float nX = gX + (aX-gX)/2f;
        float nY = gY + (aY-gY)/2f;

        // c : g为af中点 n为ag中点 fe/fc = fg/fn = 3/2 => fe = 2ec
        cX = eX - (fX - eX)/2f;
        cY = fY;
        // j  : fh = 2hj : fY-hY=2(hY-jY)
        jX = fX;
        jY = hY - (fY-hY)/2f;

        // b为 ae中点, k 为ah的中点
        bX = eX+(aX-eX)/2f;
        bY = eY+(aY-eY)/2f;
        kX = hX+(aX-hX)/2f;
        kY = hY+(aY-hY)/2f;
        // p     ep=gn
        float pX = eX - (gX - nX);
        float pY = eY - (gY - nY);

        //q    hq=gn
        float qX = hX - (gX - nX);
        float qY = hY - (gY - nY);

        //d为pe中点
        dX = eX + (pX - eX)/2f;
        dY = eY + (pY - eY)/2f;
        //i为pe中点
        iX = hX + (qX - hX)/2f;
        iY = hY + (qY - hY)/2f;
    }

    private void getAPath(){
        mAPath.reset();
        if (bRCornerStart) {//从右下侧开始翻页
            mAPath.lineTo(0, mViewH);
            mAPath.lineTo(cX, cY);
            mAPath.quadTo(eX, eY, bX, bY);
            mAPath.lineTo(aX, aY);
            mAPath.lineTo(kX, kY);
            mAPath.quadTo(hX, hY, jX, jY);
            mAPath.lineTo(mViewW, 0);
        } else {
            mAPath.lineTo(cX, cY);
            mAPath.quadTo(eX, eY, bX, bY);
            mAPath.lineTo(aX, aY);
            mAPath.lineTo(kX, kY);
            mAPath.quadTo(hX, hY, jX, jY);
            mAPath.lineTo(mViewW, mViewH);
            mAPath.lineTo(0, mViewH);
        }
        mAPath.close();
    }

    private void getBPath(){
        mBPath.reset();
        mBPath.moveTo(fX, fY);
        mBPath.lineTo(cX, cY);
        mBPath.quadTo(eX, eY, bX, bY);
        mBPath.lineTo(aX, aY);
        mBPath.lineTo(kX, kY);
        mBPath.quadTo(hX, hY, jX, jY);
        mBPath.close();
    }

    private void  getCPath(){
        mCPath.reset();
        mCPath.moveTo(aX, aY);
        mCPath.lineTo(bX, bY);
        mCPath.lineTo(dX, dY);
        mCPath.lineTo(iX, iY);
        mCPath.lineTo(kX, kY);
        mCPath.close();
    }
}
