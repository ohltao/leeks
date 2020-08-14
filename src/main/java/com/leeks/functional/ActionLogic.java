package com.leeks.functional;

import java.util.List;

@FunctionalInterface
public interface ActionLogic {
    void action(List<String> datas);
}
