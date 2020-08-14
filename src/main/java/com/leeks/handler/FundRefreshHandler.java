package com.leeks.handler;

import com.leeks.bean.FundBean;
import com.leeks.utils.PinYinUtils;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class FundRefreshHandler extends RefreshHandler {

    public FundRefreshHandler(JTable table) {
        super(table);
    }

    /**
     * 从网络更新数据
     */
    public abstract void handle(List<String> code);

    /**
     * 更新全部数据
     */
    public void updateUI(List<FundBean> datas) {
        String[] columnNames = {"基金名称", "估算净值", "估算涨跌", "更新时间"};
        updateUI(columnNames, 2, convertData(datas));
    }

    private Object[][] convertData(List<FundBean> data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Object[][] temp = new Object[data.size()][4];
        for (int i = 0; i < data.size(); i++) {
            FundBean fundBean = data.get(i);
            String timeStr = fundBean.getGztime();

            String today = dateFormat.format(new Date());
            if (timeStr != null && timeStr.startsWith(today)) {
                timeStr = timeStr.substring(timeStr.indexOf(" "));
            }
            String gszzlStr = "--";
            if (fundBean.getGszzl() != null) {
                gszzlStr = fundBean.getGszzl().startsWith("-") ? fundBean.getGszzl() : "+" + fundBean.getGszzl();
            }
            temp[i] = new Object[]{colorful ? fundBean.getFundName() : PinYinUtils.toPinYin(fundBean.getFundName()), fundBean.getGsz(), gszzlStr + "%", timeStr};
        }
        return temp;
    }
}
