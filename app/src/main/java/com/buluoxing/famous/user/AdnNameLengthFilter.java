package com.buluoxing.famous.user;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/6 0006.
 */
public class AdnNameLengthFilter implements InputFilter {
    Context context;
    int MAX_EN;// 最大英文/数字长度 一个汉字算两个字母
    //String regEx = "[\\u4e00-\\u9fa5]"; // unicode编码，判断是否为汉字
    //匹配非表情符号的正则表达式 ^[\u4e00-\u9fa5]{0,}$
    private final String regEx ="(\\w|[\\u4e00-\\u9fa5])";

    public AdnNameLengthFilter(Context mContext,int mAX_EN) {
        super();
        MAX_EN = mAX_EN;
        context=mContext;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        int destCount = dest.toString().length()
                + getChineseCount(dest.toString());
        Log.i("TAG", "filter: destCount = " + destCount);
        int sourceCount = source.toString().length()
                + getChineseCount(source.toString());
        Log.i("TAG", "filter: sourceCount = " + sourceCount);
        if (destCount + sourceCount > MAX_EN) {
            Toast.makeText(context, "最多输入"+ MAX_EN/3 +"个字", Toast.LENGTH_SHORT).show();
            return "";

        } else {
            Log.i("TAG", "filter: source = " + source);
            Log.i("TAG", "filter: dest = " + dest);
            return source; //source
        }
    }

    private int getChineseCount(String str) {
        int count = 0;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }
}

