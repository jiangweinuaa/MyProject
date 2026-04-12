
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 20250218 add by 11217
 * 根据yapi接口自动生成对应的请求实体json类
 */
public class YapiGenerator {

    public static void main(String[] args) {

        String url = "http://172.16.70.153:3000/api/interface/get?id="; //请求地址

        //登陆信息
        LoginInfo loginInfo = new LoginInfo(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjcyMSwiaWF0IjoxNzQ3MTE0ODYwLCJleHAiOjE3NDc3MTk2NjB9.nnLWgiW8cOtxbgkIXWmbg2MAARR3QMswkj7CpDh1yhM",
                "721"
        );

        //接口id
        String id = "16585";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + id);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Cookie", loginInfo.toString());
        CloseableHttpResponse response = null;

        JSONObject req_body_other = null;
        JSONObject res_body = null;

        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            JSONObject jsonObject = JSONObject.parseObject(result);

            jsonObject = jsonObject.getJSONObject("data");

            String className = jsonObject.getJSONObject("query_path").getString("path").substring(1);
            System.out.println(className);

            req_body_other = JSONObject.parseObject(jsonObject.getString("req_body_other"));
            res_body = JSONObject.parseObject(jsonObject.getString("res_body"));

            System.out.println(req_body_other);
            System.out.println(res_body);


            //请求中需要生成的json节点
            String JSONReqField = "request";
            //返回中需要生成的json节点
            String JSONResField = "datas";

            //请求类的生成地址
            String reqJSONDir = "src/main/java/com/dsc/spos/json/cust/req";

            //返回类的生成地址
            String resJSONDir = "src/main/java/com/dsc/spos/json/cust/res";

            if (null != req_body_other) {
                JSONObject req = req_body_other.getJSONObject("properties");
                List<String> requireList = null;
                if (null != req.getJSONArray("required")) {
                    requireList = req.getJSONArray("required").toJavaList(String.class);
                }

                req = req.getJSONObject(JSONReqField);

                List<GeneratorField> fieldList = Lists.newArrayList();
                List<GeneratorClass> classList = Lists.newArrayList();
                GeneratorField generatorField = generatorField(JSONReqField,req,requireList);
                fieldList.add(generatorField);
                classList.add(generatorClass(JSONReqField,req));
                if ("array".equals(req.getString("type"))) {
                    addClassForClassList(req.getJSONObject("items").getJSONObject("properties"),classList);
                }else {
                    addClassForClassList(req.getJSONObject("properties"),classList);
                }

                JsonClassModel reqModel = new JsonClassModel(
                        className +"Req",
                        "com.dsc.spos.json.cust.req",
                        "JsonBasicReq",
                        " com.dsc.spos.json.JsonBasicReq",
                        "YapiGenerator自动生成",
                        fieldList,
                        classList );

                GeneratorUtils generatorUtils = new GeneratorUtils();
                generatorUtils.setOutputDir(reqJSONDir);
                generatorUtils.Generator(reqModel);

            }
            if (null != res_body) {
                JSONObject res = res_body.getJSONObject("properties");
//                List<String> requireList = null;
//                if (null != res.getJSONArray("required")) {
//                    requireList = res.getJSONArray("required").toJavaList(String.class);
//                }

                res = res.getJSONObject(JSONResField);

                List<GeneratorField> fieldList = Lists.newArrayList();
                List<GeneratorClass> classList = Lists.newArrayList();
                GeneratorField generatorField = generatorField(JSONResField,res,null);
                fieldList.add(generatorField);
                classList.add(generatorClass(JSONResField,res));
                if ("array".equals(res.getString("type"))) {
                    addClassForClassList(res.getJSONObject("items").getJSONObject("properties"),classList);
                }else {
                    addClassForClassList(res.getJSONObject("properties"),classList);
                }

                JsonClassModel reqModel = new JsonClassModel(
                        className + "Res" ,
                        "com.dsc.spos.json.cust.res",
                        "JsonRes",
                        "com.dsc.spos.json.cust.JsonRes",
                        "YapiGenerator自动生成",
                        fieldList,
                        classList );

                GeneratorUtils generatorUtils = new GeneratorUtils();
                generatorUtils.setOutputDir(resJSONDir);
                generatorUtils.Generator(reqModel);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private static GeneratorField generatorField(String name,JSONObject object,List<String> requireList){
        GeneratorField generatorField = new GeneratorField();
        generatorField.setName(name);
        String desc = object.getString("description");
        generatorField.setDesc(desc == null ? "" : desc);
        String type = object.getString("type");
        if ("object".equals(type)){
            generatorField.setType(name.substring(0,1).toUpperCase()+name.substring(1));
        }else if ("array".equals(type)){
            generatorField.setType(String.format("List<%s>",name.substring(0,1).toUpperCase()+name.substring(1)));
        }else {
            FieldType fieldType = FieldType.getFieldType(type);
            generatorField.setType(fieldType.getTarget());
        }
        generatorField.setRequire(false);
        if (null!=requireList && requireList.contains(name)){
            generatorField.setRequire(true);
        }

        return generatorField;
    }

    private static void addClassForClassList(JSONObject jsonObject, List<GeneratorClass> classList){
        if (null == jsonObject){return;}
        if (null == classList){return;}

        for (String key : jsonObject.keySet()) {
            JSONObject obj = null;
            if (jsonObject.get(key) instanceof JSONObject){
                obj  = jsonObject.getJSONObject(key);
            }
            if (obj == null){
                continue;
            }
            if ("object".equals(obj.getString("type")) || "array".equals(obj.getString("type")) ) {
                GeneratorClass generatorClass = generatorClass(key,jsonObject.getJSONObject(key));
                if (null != generatorClass){
                    classList.add(generatorClass);
                }
            }
            addClassForClassList(obj,classList);
        }
    }

    private static GeneratorClass generatorClass(String name,JSONObject object){

        GeneratorClass generatorClass = new GeneratorClass();
        generatorClass.setName(name.substring(0,1).toUpperCase()+name.substring(1));
        generatorClass.setFieldList(new ArrayList<>());
        JSONObject properties = null;

        if (object.getString("type") == null) {
          return null;
        }
        if ("array".equals(object.getString("type"))) {
            properties = object.getJSONObject("items").getJSONObject("properties");
        } else {
            properties = object.getJSONObject("properties");
        }

        List<String> requireList = null;
        if (null != object.getJSONArray("required")) {
            requireList = object.getJSONArray("required").toJavaList(String.class);
        }
        for (String key : properties.keySet()){
            generatorClass.getFieldList().add(generatorField(key,properties.getJSONObject(key),requireList));
        }
        return generatorClass;
    }

    private String dirToClassPath(String dir){
        return  dir.replaceAll("src/main/java/","").replaceAll("/",".");
    }

}




@Getter
@Setter
class GeneratorUtils{

    private String dirTemplate = "src/main/test";
    private String templateName = "jsonclassTemplete.ftl";
    private String outputDir = "src/main/java/com/dsc/spos/json/cust/req";


    public void Generator(JsonClassModel model) throws Exception {

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setDirectoryForTemplateLoading(new File(dirTemplate));

        FileWriter writer = null;

        try {

            writer  = new FileWriter(outputDir +"/"+ model.getClassName() + ".java");
            Template template = configuration.getTemplate(templateName);
            Map<String,Object> dataMap = Maps.newHashMap();
            dataMap.put("basePackage",model.getBasePackage());
            dataMap.put("baseClazz",model.getBaseClazz());
            dataMap.put("baseClassPackage",model.getBaseClassPackage());
            dataMap.put("classAuthor",model.getClassAuthor());
            dataMap.put("className",model.getClassName());
            dataMap.put("fieldList",model.getFieldList());
            dataMap.put("clazzList",model.getClazzList());

            template.process(dataMap, writer);

        }finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}


@Getter
@Setter
@AllArgsConstructor
class LoginInfo {
    String _yapi_token = "";
    String _yapi_uid = "";

    @Override
    public String toString() {
        return "_yapi_token=" + _yapi_token + "; _yapi_uid=" + _yapi_uid;
    }
}

@Getter
@Setter
@AllArgsConstructor
class JsonClassModel{

    private String className;
    private String basePackage;
    private String baseClazz;
    private String baseClassPackage;
    private String classAuthor;

    private List<GeneratorField> fieldList;
    private List<GeneratorClass> clazzList;
}

enum FieldType {

    STRING("string", "String"),
    NUMBER("number", "double"),
    ARRAY("array", "List"),
    OBJECT("object", ""),
    BOOLEAN("boolean", "boolean"),
    INTEGER("integer", "int")
    ;

    /**
     * yapi的字段类型
     */
    private String source;

    /**
     * java的字段类型
     */
    private String target;

    FieldType(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public static FieldType getFieldType(String source) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }
        for (FieldType type : FieldType.values()) {
            if (type.getSource().equals(source)) {
                return type;
            }
        }
        return null;
    }

}


