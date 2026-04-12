package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DcpUnitconvertId implements java.io.Serializable {
    private static final long serialVersionUID = 2716083024088063131L;
    @Column(name = "EID", nullable = false, length = 32)
    private String eid;

    @Column(name = "OUNIT", nullable = false, length = 32)
    private String ounit;

    @Column(name = "UNIT", nullable = false, length = 32)
    private String unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DcpUnitconvertId entity = (DcpUnitconvertId) o;
        return Objects.equals(this.ounit, entity.ounit) &&
                Objects.equals(this.eid, entity.eid) &&
                Objects.equals(this.unit, entity.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ounit, eid, unit);
    }

}