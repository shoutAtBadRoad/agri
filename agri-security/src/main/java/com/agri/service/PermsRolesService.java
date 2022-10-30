package com.agri.service;

import java.util.List;
import java.util.Map;

public interface PermsRolesService {

    Map<String, List<String>> getPermsOfRoles();

    List<String> getRoles(String uri);

    Boolean checkPerms(String uri, List<String> permissions);

    void  deletePermsOfRolesInRedis();

}
