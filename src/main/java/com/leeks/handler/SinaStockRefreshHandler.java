package com.leeks.handler;

import com.leeks.bean.StockBean;
import com.leeks.utils.HttpClientPool;
import com.leeks.utils.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.*;

public class SinaStockRefreshHandler extends StockRefreshHandler {

    private JLabel label;
    private final Map<String, String> costMapping = new HashMap<>();

    public SinaStockRefreshHandler(JTable table, JLabel label) {
        super(table);
        this.label = label;
    }

    @Override
    public void handle(List<String> codes) {
        packingCode(codes);
        LogUtil.info("Leeks update stock data.");
        handle(costMapping.keySet(), this::stepAction, 5);
    }

    private void stepAction(Collection<String> codes) {
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

    private void packingCode(List<String> originCodes) {
        if (CollectionUtils.isEmpty(originCodes)) {
            return;
        }
        costMapping.clear();
        originCodes.stream()
                .filter(StringUtils::isNotBlank)
                .forEach(code -> {
                    String[] split = code.split("[:：]");
                    costMapping.put(split[0], split.length > 1 ? split[1] : null);
                });
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
            if (values.length < 4) {
                continue;
            }
            String code = split[0].substring(split[0].lastIndexOf("_") + 1);
            double now = Double.parseDouble(values[3]);
            double yesterday = Double.parseDouble(values[2]);
            StockBean bean = new StockBean(code);
            bean.setCode(code);
            bean.setName(values[0]);
            String cost = costMapping.get(code);
            if (StringUtils.isNotBlank(cost)) {
                bean.setCost(costMapping.get(code));
                double cost_d = Double.parseDouble(cost);
                bean.setG_or_l(String.format("%.2f", (now - cost_d) / cost_d * 100) + "%");
            } else {
                bean.setCost("--");
                bean.setG_or_l("--");
            }
            bean.setNow(String.format("%.2f", now));
            bean.setLow(String.format("%.2f", Double.parseDouble(values[5])));
            bean.setHigh(String.format("%.2f", Double.parseDouble(values[4])));
            bean.setChange(String.format("%.2f", now - yesterday));
            bean.setChangePercent(String.format("%.2f", (now - yesterday) / yesterday * 100) + "%");
            bean.setTime(values[31]);
            beans.add(bean);
        }
        return beans;
    }
}
