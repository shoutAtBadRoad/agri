package com.agri.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PermsMapper{

    List<Map<String ,String>> getPermsOfRole();
}
