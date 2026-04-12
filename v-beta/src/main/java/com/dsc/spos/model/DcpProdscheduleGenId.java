package com.dsc.spos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DcpProdscheduleGenId implements java.io.Serializable {
    private static final long serialVersionUID = 8766288902646181456L;
    @Column(name = "EID", nullable = false, length = 64)
    private String eid;

    @Column(name = "BILLNO", nullable = false, length = 32)
    private String billno;

    @Column(name = "ITEM", nullable = false)
    private Long item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DcpProdscheduleGenId entity = (DcpProdscheduleGenId) o;
        return Objects.equals(this.eid, entity.eid) &&
                Objects.equals(this.item, entity.item) &&
                Objects.equals(this.billno, entity.billno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eid, item, billno);
    }

}