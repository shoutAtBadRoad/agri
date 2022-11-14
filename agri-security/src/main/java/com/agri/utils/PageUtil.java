package com.agri.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;

import java.util.Map;

public class PageUtil<T> {

    private static final String pagesize = "pagesize";

    private static final String pagenum = "pagenum";

    public IPage<T> page(Map<String, Object> params, String... p) {
        long pageSize = 0, pageNum = 0;
        if(p.length == 0) {
            Integer o = (Integer) params.get(pagesize);
            pageSize = o.longValue();
            o  = (Integer) params.get(pagenum);
            pageNum = o.longValue();
        }else {
            Integer o = (Integer) params.get(p[0]);
            pageSize = o.longValue();
            o = (Integer) params.get(p[1]);
            pageNum = o.longValue();
        }

        return new Page<>(pageNum, pageSize);
    }
}
