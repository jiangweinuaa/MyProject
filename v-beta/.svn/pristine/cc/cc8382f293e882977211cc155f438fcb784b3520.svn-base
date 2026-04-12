package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DcpOrgId implements java.io.Serializable {
    private static final long serialVersionUID = 6441373358169178060L;
    @Column(name = "ORGANIZATIONNO", nullable = false, length = 32)
    private String organizationno;

    @Column(name = "EID", nullable = false, length = 32)
    private String eid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DcpOrgId entity = (DcpOrgId) o;
        return Objects.equals(this.eid, entity.eid) &&
                Objects.equals(this.organizationno, entity.organizationno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eid, organizationno);
    }

}