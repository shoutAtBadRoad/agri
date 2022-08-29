package com.agri.filter.unfilter;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Data
@Log4j
@Component
@ConfigurationProperties(prefix = "ignore")
@Order(0)
public class YamlWhitelistLoader implements IWhitelistLoad{

    public List<String> urls;


    @Override
    public void load(WhiteList list) {
        Set<String> set = list.getSet();
        set.addAll(urls);
        log.info("yml白名单加载");
    }

}
