package com.gmail.edpsun.hystock.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "STOCK")
public class Stock {
    @Id
    private String id;
    private String name;
    private String comment;

    // @OneToMany(mappedBy = "stockId", fetch = FetchType.EAGER)
    // @Cascade(value = { CascadeType.ALL })
    @Transient
    private List<HolderStat> holderStats = new ArrayList<HolderStat>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<HolderStat> getHolderStats() {
        return holderStats;
    }

    public void setHolderStats(List<HolderStat> holderStats) {
        this.holderStats = holderStats;
    }
}
