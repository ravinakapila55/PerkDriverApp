package com.app.perk99driver.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class CustomTextViewMedium extends TextView
{
    public CustomTextViewMedium(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextViewMedium(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CustomTextViewMedium(Context context)
    {
        super(context);
        init();
    }

    public void init()
    {
        if (!isInEditMode())
        {
            Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Raleway-Medium.ttf");
            setTypeface(normalTypeface);
        }
    }
}