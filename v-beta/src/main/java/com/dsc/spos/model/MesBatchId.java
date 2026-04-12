package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class MesBatchId implements java.io.Serializable {
    private static final long serialVersionUID = -3838972523810345334L;
    @Column(name = "EID", nullable = false, length = 32)
    private String eid;

    @Column(name = "ORGANIZATIONNO", nullable = false, length = 32)
    private String organizationno;

    @Column(name = "PLUNO", nullable = false, length = 40)
    private String pluno;

    @Column(name = "FEATURENO", nullable = false, length = 40)
    private String featureno;

    @Column(name = "BATCHNO", nullable = false, length = 20)
    private String batchno;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MesBatchId entity = (MesBatchId) o;
        return Objects.equals(this.eid, entity.eid) &&
                Objects.equals(this.batchno, entity.batchno) &&
                Objects.equals(this.pluno, entity.pluno) &&
                Objects.equals(this.featureno, entity.featureno) &&
                Objects.equals(this.organizationno, entity.organizationno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eid, batchno, pluno, featureno, organizationno);
    }

}