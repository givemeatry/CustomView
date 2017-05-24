package com.zefengsysu.lib.ripplecirclebutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wangzf on 2017/5/22.
 */

public class RippleCircleButton extends RelativeLayout {

    private static final int SCALE_TYPE_ORIGINAL = 1;
    private static final int SCALE_TYPE_FIT = 2;

    /**
     * To ensure usability for people with disabilities, give buttons a height of 36dp and give touchable targets a minimum height of 48dp.
     */
    private static final float DEFAULT_RADIUS = 24.0f;
    /**
    * the default colorPrimary value
    */
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#3F51B5");
    /**
     * TextAppearance.Medium, 18sp
     */
    private static final float DEFAULT_TEXT_SIZE = 18.0f;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    /**
     * Keep the original size of src in default.
     */
    private static final int DEFAULT_SCALE_TYPE = SCALE_TYPE_ORIGINAL;

    private static final float RIPPLE_AMPLIFY_RATE = 0.4f;
    private static final int RIPPLE_AMPLIFY_DURATION = 5 * 100;
    private static final int RIPPLE_SHRINK_DURATION = 0;

    private int mRadius;
    private  int mBackgroundColor;
    private String mText;
    private float mTextSize;
    private int mTextColor;
    private Drawable mSrc;
    private int mScaleType;

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private OnClickListener mOnClickListener;

    public RippleCircleButton(Context context) {
        this(context, null);
    }

    public RippleCircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleCircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs);
        initLayout(context);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public boolean hasOnClickListeners() {
        return mOnClickListener != null;
    }

    private void initFromAttributes(
            Context context, AttributeSet attrs) {
        int defaultRadius = dip2px(context, DEFAULT_RADIUS);
        int defaultTextSize = sp2px(context, DEFAULT_TEXT_SIZE);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RippleCircleButton);
        mRadius = (int) a.getDimension(R.styleable.RippleCircleButton_radius, defaultRadius);
        mBackgroundColor = a.getColor(R.styleable.RippleCircleButton_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        mText = a.getString(R.styleable.RippleCircleButton_text);
        mTextSize = a.getDimension(R.styleable.RippleCircleButton_textSize, defaultTextSize);
        mTextColor = a.getColor(R.styleable.RippleCircleButton_textColor, DEFAULT_TEXT_COLOR);
        mSrc = a.getDrawable(R.styleable.RippleCircleButton_src);
        mScaleType = a.getInt(R.styleable.RippleCircleButton_scaleType, DEFAULT_SCALE_TYPE);
        a.recycle();
    }

    private void initLayout(Context context) {
        computeExactRadius();
        int rippleRadius = (int)(mRadius * (1 + RIPPLE_AMPLIFY_RATE));

        LayoutParams rippleViewParams = new LayoutParams(rippleRadius * 2, rippleRadius * 2);
        rippleViewParams.addRule(CENTER_IN_PARENT, TRUE);
        RippleView rippleView = new RippleView(context);
        addView(rippleView, rippleViewParams);
        initRippleAnimation(rippleView);

        LayoutParams buttonParams = new LayoutParams(mRadius * 2, mRadius * 2);
        buttonParams.addRule(CENTER_IN_PARENT, TRUE);
        Button button = new Button(context);
        addView(button, buttonParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnimatorSet.start();
                if (hasOnClickListeners()) {
                    mOnClickListener.onClick(RippleCircleButton.this);
                }
            }
        });
    }

    private void computeExactRadius() {
        if (mSrc != null) {
            if (mScaleType == SCALE_TYPE_ORIGINAL) {
                int sideLength = Math.max(mSrc.getIntrinsicWidth(), mSrc.getIntrinsicHeight());
                int srcBoundsRadius = (int) (sideLength / Math.sqrt(2));
                mRadius = mRadius > srcBoundsRadius ? mRadius : srcBoundsRadius;
            }
        } else if (mText != null) {
            Paint paint = new Paint();
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            Rect textBounds = new Rect();

            paint.setColor(mTextColor);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setTextSize(mTextSize);
            paint.setTypeface(font);
            paint.getTextBounds(mText, 0, mText.length(), textBounds);

            int sideLength = Math.max(textBounds.width(), textBounds.height());
            int textBoundsRadius = (int) (sideLength / Math.sqrt(2));
            mRadius = mRadius > textBoundsRadius ? mRadius : textBoundsRadius;
        }
    }

    private void initRippleAnimation(View rippleView) {
        float amplifyRate = 1.0f + RIPPLE_AMPLIFY_RATE;
        List<Animator> animatorList = new ArrayList<>();

        ObjectAnimator amplifyXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, amplifyRate);
        amplifyXAnimator.setDuration(RIPPLE_AMPLIFY_DURATION);
        animatorList.add(amplifyXAnimator);

        ObjectAnimator amplifyYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, amplifyRate);
        amplifyYAnimator.setDuration(RIPPLE_AMPLIFY_DURATION);
        animatorList.add(amplifyYAnimator);

        ObjectAnimator shrinkXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", amplifyRate, 1.0f);
        shrinkXAnimator.setStartDelay(RIPPLE_AMPLIFY_DURATION);
        amplifyXAnimator.setDuration(RIPPLE_SHRINK_DURATION);
        animatorList.add(shrinkXAnimator);

        ObjectAnimator shrinkYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", amplifyRate, 1.0f);
        shrinkYAnimator.setStartDelay(RIPPLE_AMPLIFY_DURATION);
        amplifyYAnimator.setDuration(RIPPLE_SHRINK_DURATION);
        animatorList.add(shrinkYAnimator);

        mAnimatorSet.playTogether(animatorList);
    }

    private static int dip2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
    }

    private static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    private class Button extends View {

        public Button(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            drawCircle(canvas);
            if (mSrc != null) {
                drawDrawable(canvas);
            } else if (mText != null) {
                drawText(canvas);
            }
        }

        private void drawCircle(Canvas canvas) {
            Paint paint = new Paint();

            paint.setColor(mBackgroundColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            canvas.drawCircle(mRadius, mRadius, mRadius, paint);
        }

        private void drawText(Canvas canvas) {
            Paint paint = new Paint();
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            Rect textBounds = new Rect();

            paint.setColor(mTextColor);
            paint.setTextSize(mTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setTypeface(font);
            paint.getTextBounds(mText, 0, mText.length(), textBounds);

            canvas.drawText(mText, (float) mRadius, mRadius + textBounds.height() / 2.0f, paint);
        }

        private void drawDrawable(Canvas canvas) {
            int left;
            int top;
            int width = mSrc.getIntrinsicWidth();
            int height = mSrc.getIntrinsicHeight();

            if (mScaleType == SCALE_TYPE_FIT) {
                if (mSrc.getIntrinsicWidth() > mSrc.getIntrinsicHeight()) {
                    width = (int)(mRadius * Math.sqrt(2));
                    height = width * mSrc.getIntrinsicHeight() / mSrc.getIntrinsicWidth();
                } else {
                    height = (int)(mRadius * Math.sqrt(2));
                    width = height * mSrc.getIntrinsicWidth() / mSrc.getIntrinsicHeight();
                }
            }

            left = (getWidth() - width) / 2;
            top = (getHeight() - height) / 2;
            mSrc.setBounds(left, top, left + width, top + height);
            mSrc.draw(canvas);
        }
    }

    private class RippleView extends View {

        private final int ALPHA = 4 * 16 + 15; // 4F

        public RippleView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // the position of the centre of the original circle
            int centreX = getWidth() / 2;
            int centreY = getHeight() / 2;

            Paint paint = new Paint();
            paint.setColor(mBackgroundColor);
            paint.setAlpha(ALPHA);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            canvas.drawCircle(centreX, centreY, mRadius, paint);
        }
    }
}
