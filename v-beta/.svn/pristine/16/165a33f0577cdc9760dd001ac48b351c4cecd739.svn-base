package ${basePackage};

import ${baseClassPackage};
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author ${classAuthor}
 * @date ${.now?string("yyyy/MM/dd")}
 */
@Getter
@Setter
public class ${className} extends ${baseClazz}{

   <#list fieldList as field>
        <#if field.require>
   @JSONFieldRequired(display="${field.desc}")
        </#if>
   private ${field.type} ${field.name};
   </#list>

  <#list clazzList as clazz>
    @Getter
    @Setter
    public class ${clazz.name}{
      <#list clazz.fieldList as field>
          <#if field.require>
          @JSONFieldRequired(display="${field.desc}")
          </#if>
          private ${field.type} ${field.name};
      </#list>
    }
  </#list>

}