package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_AccountingSubjectQueryRes extends JsonRes
{
    private level1Elm datas;

    @Data
    public class level1Elm
    {
        private List<level2Elm> subjectList;
    }

    @Data
    public class level2Elm
    {
        private String subjectId;
        private String subjectName;
        private String direction;
        private String auxiliaryType;
        private String memo;
    }

}
