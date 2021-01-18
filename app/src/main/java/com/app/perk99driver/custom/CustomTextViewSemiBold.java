package com.app.perk99driver.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewSemiBold extends TextView
{
    public CustomTextViewSemiBold(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextViewSemiBold(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CustomTextViewSemiBold(Context context)
    {
        super(context);
        init();
    }

    public void init()
    {
        if (!isInEditMode())
        {
            Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "Raleway-SemiBold.ttf");
            setTypeface(normalTypeface);
        }
    }
}