package com.github.sawied.microservice.trade.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * code from oneport chinasoft team.
 */

@Entity
@Table(name = "case_activity")
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(targetEntity = CaseEntity.class)
    @JoinColumn(name="case_id_ref")
    public CaseEntity caseEntity;

    @Column(name="name")
    private String name;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CaseEntity getCaseEntity() {
        return this.caseEntity;
    }

    public void setCaseEntity(CaseEntity caseEntity) {
        this.caseEntity = caseEntity;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityEntity that = (ActivityEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
