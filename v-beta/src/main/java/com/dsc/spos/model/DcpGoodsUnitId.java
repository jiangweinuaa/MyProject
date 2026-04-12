package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DcpGoodsUnitId implements java.io.Serializable {
    private static final long serialVersionUID = 1416056052292945324L;
    @Column(name = "EID", nullable = false, length = 48)
    private String eid;

    @Column(name = "PLUNO", nullable = false, length = 48)
    private String pluno;

    @Column(name = "OUNIT", nullable = false, length = 48)
    private String ounit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DcpGoodsUnitId entity = (DcpGoodsUnitId) o;
        return Objects.equals(this.ounit, entity.ounit) &&
                Objects.equals(this.eid, entity.eid) &&
                Objects.equals(this.pluno, entity.pluno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ounit, eid, pluno);
    }

}