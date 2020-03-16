package com.lyl.pageturning.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lyl.pageturning.R;
import com.lyl.pageturning.util.SDCardUtils;

import java.util.Optional;


public class PageTurningView extends View  {
    private static final String TAG = "lym123";
    private Paint mAPaint, mBPaint, mCPaint, mTextPaint;
    private Path mAPath, mBPath, mCPath, mPath;
    private float aX, aY, bX, bY, cX, cY, dX, dY, eX, eY, fX, fY,
            hX, hY, iX, iY, jX, jY, kX, kY;
    private float mViewH, mViewW;
    private float valid1X, valid1Y, valid2X, valid2Y;//有效值
    private Canvas bitmapCanvas, mBitmapCanvasA, mBitmapCanvasB, mBitmapCanvasC;
    private Bitmap mBitmap, mBitmapA, mBitmapB, mBitmapC;
    private Matrix mMatrix;
    private boolean bRCornerStart = true;//是否从右下角开始翻页
    private final boolean showContent = true; //显示文本内容
    private StaticLayout layout;

    private TextPaint textPaint;


    private final int A_BG_COLOR = Color.GREEN;
    private final int B_BG_COLOR = Color.WHITE;
    private final int C_BG_COLOR = Color.YELLOW;
    private final int TEXT_COLOR = Color.BLACK;
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
        mAPaint.setColor(A_BG_COLOR);
        mAPaint.setAntiAlias(true);
        //mAPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        mBPaint = new Paint();
        mBPaint.setColor(B_BG_COLOR);
        mBPaint.setAntiAlias(true);


        mCPaint = new Paint();
        mCPaint.setColor(C_BG_COLOR);
        //mCPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));//SRC_OVER/SRC_ATOP/DST_OVER
        mCPaint.setAntiAlias(true);
        mBPaint.setStrokeWidth(10);

        mAPath = new Path();
        mBPath = new Path();
        mCPath = new Path();
        mPath = new Path();

        mTextPaint = new Paint();
        mTextPaint.setColor(TEXT_COLOR);
        mTextPaint.setTextSize(35);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setSubpixelText(true);//设置自像素。如果该项为true，将有助于文本在LCD屏幕上的显示效果。

        mMatrix = new Matrix();

        valid2X = valid1X = cX = 0;
        valid2Y = valid1Y = jY = 0;
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //final float v = mAPaint.measureText(string);
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
//        String sb = SDCardUtils.handleStr(string, mAPaint, mViewW).toString();
//        Log.d(TAG, "string: " + string + ", \n sb = " + sb);
//        String sb = string.replace("\n", "\\n");//读取sd卡数据后，换行
        textPaint = new TextPaint();
        textPaint.setColor(TEXT_COLOR);/*setARGB(0xFF, 0, 0, 0);*/
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);
        layout = new StaticLayout(string, textPaint, (int)mViewW,
                Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        createCanvas(100, 25);
    }
    private void createCanvas(float x, float y){
        if (null == mBitmapA) {
            //mBitmapA = BitmapFactory.decodeResource(getResources(), R.drawable.bg).copy(Bitmap.Config.RGB_565, true);
            //mBitmapA = Bitmap.createScaledBitmap(mBitmapA, (int) mViewW, (int) mViewH, true);
            mBitmapA = Bitmap.createBitmap((int) mViewW, (int) mViewH, Bitmap.Config.RGB_565);
            mBitmapCanvasA = new Canvas(mBitmapA);
            mBitmapCanvasA.drawPath(getPathDefault(), mAPaint);

            //mBitmapCanvasA.drawText(strA, x, y, mTextPaint)
            mBitmapCanvasA.save();
            mBitmapCanvasA.translate(x, y);//从x,y开始画
            layout.draw(mBitmapCanvasA);
            mBitmapCanvasA.restore();//别忘了restore

            //mBitmapCanvasA.drawText(strA, w, h, mTextPaint);
        }
        if (null == mBitmapB) {
            mBitmapB = Bitmap.createBitmap((int) mViewW, (int) mViewH, Bitmap.Config.RGB_565);
            mBitmapCanvasB = new Canvas(mBitmapB);
            mBitmapCanvasB.drawPath(getPathDefault(), mBPaint);

            //mBitmapCanvasB.drawText(strB, x, y, mTextPaint);
            mBitmapCanvasB.save();
            mBitmapCanvasB.translate(x, y);
            layout.draw(mBitmapCanvasB);
            mBitmapCanvasB.restore();
        }
        if (null == mBitmapC) {
            mBitmapC = Bitmap.createBitmap((int)mViewW, (int)mViewH, Bitmap.Config.RGB_565);
            mBitmapCanvasC = new Canvas(mBitmapC);
            mBitmapCanvasC.drawPath(getPathDefault(), mCPaint);

            mBitmapCanvasC.save();
            mBitmapCanvasC.translate(x, y);
            layout.draw(mBitmapCanvasC);
            mBitmapCanvasC.restore();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        long l = System.currentTimeMillis();
        getEveryPoint();
        getAPath();
        getBPath();
        getCPath();

        if (showContent) {
            setCMatrix();

            // C : 此处经过矩阵变换，C得到的是一个三角形，无法填满原本C的区域
            // 要先画出C的背景色，再画C的path
            canvas.drawColor(C_BG_COLOR);

            canvas.save();
            canvas.clipPath(mCPath);
            canvas.drawBitmap(mBitmapC, mMatrix, null);
            canvas.restore();

            // A
            canvas.save();
            canvas.clipPath(mAPath);
            canvas.drawBitmap(mBitmapA, 0, 0, null);
            canvas.restore();

            // B
            canvas.save();
            canvas.clipPath(mCPath);
            canvas.clipPath(mBPath, Region.Op.REVERSE_DIFFERENCE);//裁剪出pathB不同于pathC区域的部分
            canvas.drawBitmap(mBitmapB, 0, 0, null);
            canvas.restore();
        } else {
            //显示顺序B-C-A，C在B上，使用SRC_OVER模式，此时C会把B的贝塞尔曲线部分覆盖，再画A，A把C多出来的部分覆盖。
            //AB分割是akij,abdc这个两条贝塞尔曲线，C是abdika这个闭合的直线组成
            bitmapCanvas.drawPath(mBPath, mBPaint);
            bitmapCanvas.drawPath(mCPath, mCPaint);
            bitmapCanvas.drawPath(mAPath, mAPaint);
            canvas.drawBitmap(mBitmap,0,0,null);
            Log.d(TAG, "onDraw: " + (System.currentTimeMillis() - l));
            Log.d(TAG, "onDraw: " + canvas.isHardwareAccelerated());
        }
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

    private void getCPath(){
        mCPath.reset();
        mCPath.moveTo(aX, aY);
        mCPath.lineTo(bX, bY);
        mCPath.lineTo(dX, dY);
        mCPath.lineTo(iX, iY);
        mCPath.lineTo(kX, kY);
        mCPath.close();
    }

    private Path getPathDefault(){ //绘制默认的界面
        mPath.reset();
        mPath.lineTo(0, mViewH);
        mPath.lineTo(mViewW, mViewH);
        mPath.lineTo(mViewW, 0);
        mPath.close();
        return mPath;
    }

    private void setCMatrix(){//从A 变换 到C
        float eh = (float) Math.hypot(fX-eX, hY-fY);
        float sin0 = (fX-eX) / eh;
        float cos0 = (hY-fY) / eh;
        //设置翻转和旋转矩阵
        float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };
        mMatrixArray[0] = -(1-2f * sin0 * sin0);
        mMatrixArray[1] = 2f * sin0 * cos0;
        mMatrixArray[3] = 2f * sin0 * cos0;
        mMatrixArray[4] = 1 - 2f * sin0 * sin0;

        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);//翻转和旋转
        mMatrix.preTranslate(-eX, -eY);//沿当前XY轴负方向位移得到 矩形A₃B₃C₃D₃
        mMatrix.postTranslate(eX, eY);//沿原XY轴方向位移得到 矩形A4 B4 C4 D4
    }

    private String string;
    public void updateContent(String str){
        string = str;
//        aX = fX = mViewW = getWidth();
//        aY = fY = mViewH = getHeight();
//        createCanvas(str, "bbbbbbb", 0, 0);
////        mBitmapCanvasA.drawText(str, 0, 0, mTextPaint);
////        mBitmapCanvasB.drawText("this is screen B...right!", mViewW - 200, mViewH - 100, mTextPaint);
////        mBitmapCanvasC.drawText(str, 0, 0, mTextPaint);
//        invalidate();
    }
}
