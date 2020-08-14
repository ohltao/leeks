package com.leeks.handler;

import com.intellij.ui.JBColor;
import com.leeks.bean.StockBean;
import com.leeks.utils.PinYinUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public abstract class StockRefreshHandler extends RefreshHandler {
    private int[] sizes = new int[]{0, 0, 0, 0, 0, 0, 0};

    public StockRefreshHandler(JTable table) {
        super(table);
    }

    /**
     * 从网络更新数据
     *
     * @param code
     */
    public abstract void handle(List<String> code);

    /**
     * 更新全部数据
     */
    public void updateUI(List<StockBean> beans) {
        String[] columnNames = {"股票名称", "当前价", "涨跌", "涨跌幅", "最低价", "最高价", "更新时间"};
        updateUI(columnNames, 3, convertData(beans));
    }

    private Object[][] convertData(List<StockBean> data) {
        Object[][] temp = new Object[data.size()][5];
        for (int i = 0; i < data.size(); i++) {
            StockBean fundBean = data.get(i);
            temp[i] = new Object[]{
                    colorful ? fundBean.getName() : PinYinUtils.toPinYin(fundBean.getName()) + " (" + fundBean.getCode() + ")",
                    fundBean.getNow(),
                    fundBean.getChange(),
                    fundBean.getChangePercent(),
                    fundBean.getLow(),
                    fundBean.getHigh(),
                    fundBean.getTime()};
        }
        return temp;
    }
}
