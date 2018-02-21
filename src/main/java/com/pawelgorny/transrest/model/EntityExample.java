package com.pawelgorny.transrest.model;

import org.codehaus.jackson.annotate.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ENTITY_EXAMPLE")
public class EntityExample implements Serializable {

    private static final long serialVersionUID = -4945035237437398363L;

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;
    @Column(name = "VALUE")
    private String value;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name="ENTITY_EXAMPLE_ID")
    @JsonManagedReference
    private List<EntityExampleChild> children = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EntityExampleChild> getChildren() {
        return children;
    }

    public void setChildren(List<EntityExampleChild> children) {
        this.children = children;
    }

    public void addToChildren(EntityExampleChild child) {
        child.setEntityExample(this);
        this.children.add(child);
    }

    @Override
    public String toString() {
        return "EntityExample{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
