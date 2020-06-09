package org.smartregister.giz.repository;

import java.util.List;

interface ClientRegisterTypeDao {
    List<ClientRegisterType> findAll(String baseEntityId);

    boolean remove(String registerType, String baseEntityId);

    boolean softDelete(String registerType, String baseEntityId, String dateRemoved);

    boolean removeAll(String baseEntityId);

    boolean add(String registerType, String baseEntityId);

    boolean findByRegisterType(String baseEntityId, String registerType);
}
