package org.acme.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class Person extends PanacheEntity {
    private Long id;
    private String name;
    private LocalDate birth;

    public static List<Person> findByName(String name) {
        return find("name", name.trim()).list();
    }

    public static List<Person> findBornAfter(Date date) {
        return find("birth > :date", Parameters.with("date", date)).list();
    }
}