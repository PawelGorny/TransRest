package com.pawelgorny.transrest.model;

import org.codehaus.jackson.annotate.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ENTITY_EXAMPLE_CHILD")
public class EntityExampleChild implements Serializable {

    private static final long serialVersionUID = -4132038506270736011L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;
    @Column(name = "VALUE")
    private String value;

    @JsonBackReference
    @ManyToOne
    private EntityExample entityExample;

    public EntityExampleChild(){};

    public EntityExampleChild( String value){
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EntityExample getEntityExample() {
        return entityExample;
    }

    public void setEntityExample(EntityExample entityExample) {
        this.entityExample = entityExample;
    }

    @Override
    public String toString() {
        return "EntityExampleChild{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", entityExample=" + entityExample +
                '}';
    }
}
