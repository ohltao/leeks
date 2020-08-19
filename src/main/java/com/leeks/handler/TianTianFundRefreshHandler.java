package com.leeks.handler;

import com.google.gson.Gson;
import com.leeks.bean.FundBean;
import com.leeks.utils.HttpClientPool;
import com.leeks.utils.LogUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TianTianFundRefreshHandler extends FundRefreshHandler {
    private static final Gson gson = new Gson();

    public TianTianFundRefreshHandler(JTable table) {
        super(table);
    }

    @Override
    public void handle(List<String> codes) {
        LogUtil.info("Leeks update fund data.");
        handle(codes, this::stepAction, 30);
    }

    private void stepAction(Collection<String> codes) {
        List<FundBean> beans = codes.parallelStream()
                .map(String::trim)
                .map(code -> {
                    try {
                        String result = HttpClientPool.getHttpClient().get("http://fundgz.1234567.com.cn/js/" + code + ".js?rt=" + System.currentTimeMillis());
                        if (StringUtils.isNotBlank(result) && result.length() > 8) {
                            return gson.fromJson(result.substring(8, result.length() - 2), FundBean.class);
                        }
                    } catch (Exception e) {
                        LogUtil.info(e.getMessage());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        updateUI(beans);
    }
}
