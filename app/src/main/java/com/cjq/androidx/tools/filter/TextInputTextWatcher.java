package com.cjq.androidx.tools.filter;

import android.text.Editable;
import android.text.TextWatcher;

import com.blankj.utilcode.util.ObjectUtils;

/**
 * 禁止输入表情等特殊符号
 */
public class TextInputTextWatcher implements TextWatcher {
    public static final String COMMON_TEXT_REGEX = "^[\\u4e00-\\u9fa5_a-zA-Z0-9\\s·~！@#￥%…&*（）—\\-+=【】{}、|；‘’：“”《》？，。`!$^()\\[\\]\\\\;':\",./<>?]*$";

    private String regex = COMMON_TEXT_REGEX;
    private String originText;
    private boolean disableEnterInput;
    private int maxLength = Integer.MAX_VALUE;

    public TextInputTextWatcher() {
    }

    public void setAllowedRegex(String regex) {
        this.regex = regex;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setDisableEnterInput(boolean disableEnterInput) {
        this.disableEnterInput = disableEnterInput;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        originText = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        boolean invalid = !text.matches(regex) || (disableEnterInput && text.contains("\n"));
        if (invalid) {
            text = originText;
        } else if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
        }
        if (!ObjectUtils.equals(text, s.toString())) {
            s.clear();
            s.append(text);
        }
    }
}
