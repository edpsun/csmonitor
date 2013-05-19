package com.gmail.edpsun.hystock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "HOLDER_STAT")
public class HolderStat {
    @Id
    private String id;

    @Column(name = "STOCK_ID")
    private String stockId;

    @Column(name = "FISCAL_YEAR")
    private int year;

    @Column(name = "QUARTER")
    private int quarter;

    @Column(name = "TOTAL_SHARE")
    private long totalShare;

    @Column(name = "CIRCULATING_SHARE")
    private long circulatingShare;

    @Column(name = "HOLDER_NUM")
    private long holderNum;

    @Column(name = "AVERAGE_HOLDING")
    private long averageHolding;

    @Column(name = "DELTA")
    private String delta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public long getTotalShare() {
        return totalShare;
    }

    public void setTotalShare(long totalShare) {
        this.totalShare = totalShare;
    }

    public long getCirculatingShare() {
        return circulatingShare;
    }

    public void setCirculatingShare(long circulatingShare) {
        this.circulatingShare = circulatingShare;
    }

    public long getHolderNum() {
        return holderNum;
    }

    public void setHolderNum(long holderNum) {
        this.holderNum = holderNum;
    }

    public long getAverageHolding() {
        return averageHolding;
    }

    public void setAverageHolding(long averageHolding) {
        this.averageHolding = averageHolding;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(this.getStockId()).append(this.getYear())
                .append(this.getQuarter()).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HolderStat)) {
            return false;
        }

        HolderStat another = (HolderStat) object;
        return new EqualsBuilder().append(this.getStockId(), another.getStockId())
                .append(this.getYear(), another.getYear()).append(this.getQuarter(), another.getQuarter()).isEquals();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }
}
