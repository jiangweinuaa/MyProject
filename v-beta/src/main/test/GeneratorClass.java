import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeneratorClass {
    private String name;

    private List<GeneratorField> fieldList;
}
