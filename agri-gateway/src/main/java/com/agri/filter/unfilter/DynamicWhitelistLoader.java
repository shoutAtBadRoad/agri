package com.agri.filter.unfilter;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@Log4j
public class DynamicWhitelistLoader implements IWhitelistLoad{


    @Override
    public void load(WhiteList list) {
        log.info("动态加载白名单完成");
    }
}
