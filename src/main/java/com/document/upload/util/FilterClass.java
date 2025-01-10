package com.document.upload.util;

import com.document.upload.entity.FileEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FilterClass {

    @PersistenceContext
    private EntityManager entityManager;

    public List<FileEntity> findAllFiles() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FileEntity> query = cb.createQuery(FileEntity.class);
        Root<FileEntity> root = query.from(FileEntity.class);
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }

}
