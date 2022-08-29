package com.agri.filter.unfilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class WhitelistLoadChain{

    @Autowired
    private final List<IWhitelistLoad> loaders = new ArrayList<>();

    @Autowired
    private WhiteList whiteList;

    public void load(WhiteList list) {
        if(loaders.size() > 0) {
            for(IWhitelistLoad loader : loaders) {
                loader.load(list);
            }
        }
        list.getSet().forEach(System.out::println);
    }

    public void addLoader(IWhitelistLoad loader) {
        loaders.add(loader);
    }

    @PostConstruct
    public void onApplicationEvent() {
        this.load(whiteList);
    }
}
