package com.sawatruck.loader.view.design;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sawatruck.loader.R;


public class NumberButton extends RelativeLayout {
    private Context context;
    private AttributeSet attrs;
    private int styleAttr;
    private OnClickListener mListener;
    private int initialNumber;
    private int currentNumber;
    private int finalNumber;
    public TextView textView;
    private View view;

    public NumberButton(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public NumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public NumberButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    private void initView() {
        this.view = this;
        inflate(context, R.layout.layout_number_button, this);
        final Resources res = getResources();
        final int defaultColor = res.getColor(R.color.colorPrimary);
        final int defaultTextColor = res.getColor(R.color.colorWhite);
        final Drawable defaultDrawable = res.getDrawable(R.drawable.background_number_button);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberButton, styleAttr, 0);

        initialNumber = a.getInt(R.styleable.NumberButton_initialNumber, 0);
        finalNumber = a.getInt(R.styleable.NumberButton_finalNumber, Integer.MAX_VALUE);
        float textSize = a.getDimension(R.styleable.NumberButton_textSize, 13);
        int color = a.getColor(R.styleable.NumberButton_backGroundColor, defaultColor);
        int textColor = a.getColor(R.styleable.NumberButton_textColor, defaultTextColor);
        Drawable drawable = a.getDrawable(R.styleable.NumberButton_backgroundDrawable);

        Button button1 = (Button) findViewById(R.id.subtract_btn);
        Button button2 = (Button) findViewById(R.id.add_btn);
        textView = (TextView) findViewById(R.id.number_counter);
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.layout);

        button1.setTextColor(textColor);
        button2.setTextColor(textColor);
        textView.setTextColor(textColor);
        button1.setTextSize(textSize);
        button2.setTextSize(textSize);
        textView.setTextSize(textSize);

        if (drawable == null) {
            drawable = defaultDrawable;
        }
        assert drawable != null;
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC));
        if (Build.VERSION.SDK_INT > 16)
            mLayout.setBackground(drawable);
        else
            mLayout.setBackgroundDrawable(drawable);

        textView.setText(String.valueOf(initialNumber));

        currentNumber = initialNumber;

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                int num = Integer.valueOf(textView.getText().toString());
                if (num != 0)
                    num -= 1;
                currentNumber = num;
                textView.setText(String.valueOf(num));
                callListener(view);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                int num = Integer.valueOf(textView.getText().toString());
                currentNumber = num + 1;
                textView.setText(String.valueOf(num + 1));
                callListener(view);
            }
        });
        a.recycle();
    }

    private void callListener(View view) {
        if (mListener != null) {
            mListener.onClick(view);
        }
    }

    public int getNumber() {
        return currentNumber;
    }

    public void setNumber(String number) {
        setNumber(Integer.parseInt(number));
    }

    public void setNumber(int number) {
        this.currentNumber = number;
        if (this.currentNumber > finalNumber) {
            this.currentNumber = finalNumber;
        }
        if (this.currentNumber < initialNumber) {
            this.currentNumber = initialNumber;
        }
        textView.setText(String.valueOf(currentNumber));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    public interface OnClickListener {

        void onClick(View view);

    }

    public void setRange(Integer startingNumber, Integer endingNumber) {
        this.initialNumber = startingNumber;
        this.finalNumber = endingNumber;
    }
}
