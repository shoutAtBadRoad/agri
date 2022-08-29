package com.agri.filter.unfilter;

import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import lombok.Data;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPatternParser;


import java.util.Set;
import java.util.regex.Pattern;

@Data
@Component
public class WhiteList {
    //TODO 白名单实体类

    private Set<String> set = new ConcurrentHashSet<>();

    public boolean match(String uri) {
        for (String pattern : set) {
            boolean matched = new PathPatternParser().parse(pattern).matches(PathContainer.parsePath(uri));
            if (matched)
                return true;
        }
        return false;
    }

}
