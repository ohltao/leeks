package com.leeks.handler;

import com.leeks.bean.StockBean;
import com.leeks.utils.HttpClientPool;
import com.leeks.utils.LogUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SinaStockRefreshHandler extends StockRefreshHandler {

    private JLabel label;

    public SinaStockRefreshHandler(JTable table, JLabel label) {
        super(table);
        this.label = label;
    }

    @Override
    public void handle(List<String> codes) {
        LogUtil.info("Leeks update stock data.");
        handle(codes, this::stepAction, 5);
    }

    private void stepAction(List<String> codes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (String code : codes) {
            stringBuffer.append(code.trim()).append(",");
        }
        if (stringBuffer.length() == 0) {
            return;
        }
        try {
            String result = HttpClientPool.getHttpClient().get("http://hq.sinajs.cn/list=" + stringBuffer.substring(0, stringBuffer.length() - 1));
            updateUI(parse(result));
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
        }
    }

    private List<StockBean> parse(String result) {
        if (result == null) {
            return Collections.emptyList();
        }
        String[] lines = result.split("\n");
        List<StockBean> beans = new ArrayList<>(lines.length);
        for (String line : lines) {
            String[] split = line.replaceAll("\"", "").split("=");
            if (split.length != 2) {
                continue;
            }
            String[] values = split[1].split(",");
            String code = split[0].substring(split[0].lastIndexOf("_"));
            StockBean bean = new StockBean(code);
            bean.setCode(code);
            bean.setName(values[0]);
            bean.setNow(String.format("%.2f", Double.parseDouble(values[3])));
            bean.setLow(String.format("%.2f", Double.parseDouble(values[5])));
            bean.setHigh(String.format("%.2f", Double.parseDouble(values[4])));
            bean.setChange(String.format("%.2f", Double.parseDouble(values[3]) - Double.parseDouble(values[2])));
            bean.setChangePercent(String.format("%.2f", (Double.parseDouble(values[3]) - Double.parseDouble(values[2])) / Double.parseDouble(values[2]) * 100) + "%");
            bean.setTime(values[31]);
            beans.add(bean);
        }

        LogUtil.info("stock size " + beans.size());
        return beans;
    }
}
