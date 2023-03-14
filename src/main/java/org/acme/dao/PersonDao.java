package org.acme.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.acme.entity.Person;

@Singleton
public class PersonDao {

    @Inject
    private EntityManager entityManager;

    public List<Person> findAll() {
        return entityManager.createQuery("FROM Person", Person.class).getResultList();
    }

    public List<Person> findByName(String name) {
        return entityManager.createQuery("FROM Person WHERE name = :name", Person.class).setParameter("name", name).getResultList();
    }

    public List<Person> findByNameWithQuery(String name) {
        return entityManager.createQuery("Select person from Person person", Person.class).getResultList();            
    }
    
}