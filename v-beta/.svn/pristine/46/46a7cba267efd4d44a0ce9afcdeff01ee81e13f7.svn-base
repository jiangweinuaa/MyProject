package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DcpGoodId implements java.io.Serializable {
    private static final long serialVersionUID = -3562542553103151646L;
    @Column(name = "EID", nullable = false, length = 48)
    private String eid;

    @Column(name = "PLUNO", nullable = false, length = 48)
    private String pluno;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DcpGoodId entity = (DcpGoodId) o;
        return Objects.equals(this.eid, entity.eid) &&
                Objects.equals(this.pluno, entity.pluno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eid, pluno);
    }

}