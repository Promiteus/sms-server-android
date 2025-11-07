package com.romanm.smsserver.model.viewHolder;

import android.widget.Button;
import android.widget.TextView;

public class ViewHolder {
    private TextView textView;
    private Button btnStart;

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public Button getBtnStart() {
        return btnStart;
    }

    public void setBtnStart(Button btnStart) {
        this.btnStart = btnStart;
    }

    public void addLogText(String msg, StringBuilder stringBuilder) {
       if (msg != null) {
           if (textView != null) {
               textView.append(msg);
           }
           if (stringBuilder != null) {
               stringBuilder.append(msg);
           }
       }
    }
}
