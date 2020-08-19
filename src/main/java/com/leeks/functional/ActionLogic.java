package com.leeks.functional;

import java.util.Collection;

@FunctionalInterface
public interface ActionLogic {
    void action(Collection<String> datas);
}
