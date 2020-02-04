package org.smartregister.giz.repository;

import java.util.List;

interface RegisterTypeDao {
    List<RegisterType> findAll(String baseEntityId);
    boolean remove(String registerType, String baseEntityId);
    boolean removeAll(String baseEntityId);
    boolean add(String registerType, String baseEntityId);
}
