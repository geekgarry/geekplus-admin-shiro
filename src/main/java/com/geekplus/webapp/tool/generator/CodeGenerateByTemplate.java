package com.geekplus.webapp.tool.generator;

import com.geekplus.common.constant.ProjectConstant;
import com.geekplus.webapp.tool.generator.entity.TableInfo;
import com.geekplus.webapp.tool.generator.utils.GenUtil;
import com.geekplus.webapp.tool.generator.utils.MybatisUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: spring-boot-project-mybatis
 * @description: 根据模板生成代码，不用mybatis提供的生成方法
 * @author: GarryChan
 * @create: 2020-11-27 11:30
 **/
public class CodeGenerateByTemplate {
    private static final String PROJECT_PATH = System.getProperty("user.dir");//项目在硬盘上的基础路径
    private static final String TEMPLATE_FILE_JAVA_PATH = PROJECT_PATH + "/src/main/resources/generator/template/java/";//模板位置
    private static final String TEMPLATE_FILE_XML_PATH = PROJECT_PATH + "/src/main/resources/generator/template/xml/";//模板位置
    private static final String TEMPLATE_FILE_VUE_PATH = PROJECT_PATH + "/src/main/resources/generator/template/vue/";//模板位置
    private static final String TEMPLATE_FILE_JS_PATH = PROJECT_PATH + "/src/main/resources/generator/template/js/";//模板位置
    private static final String JAVA_PATH = "/src/main/java"; //java文件路径
    private static final String RESOURCES_PATH = "/src/main/resources";//资源文件路径

    private static final String PACKAGE_PATH_MODEL = GenUtil.packageConvertPath(ProjectConstant.MODEL_PACKAGE);//生成的model存放路径
    private static final String PACKAGE_PATH_MAPPER = GenUtil.packageConvertPath(ProjectConstant.MAPPER_PACKAGE);//生成的mapper存放路径
    private static final String PACKAGE_PATH_SERVICE = GenUtil.packageConvertPath(ProjectConstant.SERVICE_PACKAGE);//生成的Service存放路径
    private static final String PACKAGE_PATH_SERVICE_IMPL = GenUtil.packageConvertPath(ProjectConstant.SERVICE_IMPL_PACKAGE);//生成的Service实现存放路径
    private static final String PACKAGE_PATH_CONTROLLER = GenUtil.packageConvertPath(ProjectConstant.CONTROLLER_PACKAGE);//生成的Controller存放路径

    private static final String AUTHOR = "CodeGenerator";//@author
    private static final String CURRENT_DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());//@date

    /**
     * 生成Java文件
     * @param fileName
     * @param templateName
     * @param map
     */
    public static void generateJavaFile(String fileName, String templateName, Map<String, Object> map){
        try {
            Configuration config = getJavaConfiguration();
            //config.setTemplateLoader(new ClassTemplateLoader(CodeGenerateByTemplate.class, "/"));
            try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
                Template template = config.getTemplate(templateName);
                template.process(map, out);
                out.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成XML文件
     * @param fileName
     * @param templateName
     * @param map
     */
    public static void generateXmlFile(String fileName, String templateName, Map<String, Object> map){
        try {
            Configuration config = getMapperXmlConfiguration();
            //config.setTemplateLoader(new ClassTemplateLoader(CodeGenerateByTemplate.class, "/"));
            try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
                Template template = config.getTemplate(templateName);
                template.process(map, out);
                out.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成VUE文件
     * @param fileName
     * @param templateName
     * @param map
     */
    public static void generateVueFile(String fileName, String templateName, Map<String, Object> map){
        try {
            Configuration config = getVueConfiguration();
            //config.setTemplateLoader(new ClassTemplateLoader(CodeGenerateByTemplate.class, "/"));
            try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
                Template template = config.getTemplate(templateName);
                template.process(map, out);
                out.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成VUE文件
     * @param fileName
     * @param templateName
     * @param map
     */
    public static void generateJsFile(String fileName, String templateName, Map<String, Object> map){
        try {
            Configuration config = getJsConfiguration();
            //config.setTemplateLoader(new ClassTemplateLoader(CodeGenerateByTemplate.class, "/"));
            try{
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
                Template template = config.getTemplate(templateName);
                template.process(map, out);
                out.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件目录
     * @param path
     */
    public static void createDir(String path){
        if(null != path && !"".equals(path)){
            File file = new File(path);
            file.mkdirs();
        }
    }
//    public static void initDirName(){
//        //1.mapper
//        //String poDir = params.getOsdir() + File.separatorChar + "po";
//        String mapperPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_MAPPER;
//        createDir(mapperPath);
//        //2.service
//        String servicePath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_SERVICE;
//        createDir(servicePath);
//        //3.serviceImpl
//        String serviceImplPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_SERVICE_IMPL;
//        createDir(serviceImplPath);
//        //4.model
//        String modelPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_MODEL;
//        createDir(modelPath);
//        //5.controller
//        String controllerPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_CONTROLLER;
//        createDir(controllerPath);
//        //6.xml
//        String mapperXMLPath=PROJECT_PATH+RESOURCES_PATH+"/mapper";
//        createDir(mapperXMLPath);
//    }
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        // String result=null;
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
            scanner.close();
            // return result;
        }
        try {
            throw new Exception("请输入正确的" + tip + "！");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 生成Java代码
     * @param table
     */
    public static void genJavaByTemplate(TableInfo table) {
        String javaClassName = GenUtil.capitaFirstLetter(GenUtil
                .mapTableNameToVarName(table.getTableName()));
        Map<String, Object> map = new HashMap<>();
        String tableComment=null;
        if(table.getTableComment()!=null||table.getTableComment()!=""){
            tableComment=table.getTableComment();
        } else if(table.getTableComment()==""||table.getTableComment()!=null){
            tableComment="【请填写功能的名称或完成的事项】";
        }
        map.put("tableName",table.getTableName());
        map.put("title", tableComment);
        map.put("author", AUTHOR);
        map.put("date", CURRENT_DATE);
        map.put("basePackage", ProjectConstant.BASE_PACKAGE);
        map.put("baseRequestMapping", GenUtil.getRequestMapping(table.getTableName()));
        map.put("modelNameUpperCamel", javaClassName);
        map.put("modelNameLowerCamel", GenUtil
                .mapTableNameToVarName(table.getTableName()));
        map.put("functionName", tableComment);
        map.put("pkColumn",table.getPkColumn());
        map.put("allColumn",table.getAllColumns());
        map.put("allColumnCount",table.getAllColumns().size());
        map.put("importList",GenUtil.getImportList(table.getAllColumns()));
        //1.mapper 这些是创建生成文件的目录
        String mapperPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_MAPPER;
        createDir(mapperPath);
        //2.service
        String servicePath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_SERVICE;
        createDir(servicePath);
        //3.serviceImpl
        String serviceImplPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_SERVICE_IMPL;
        createDir(serviceImplPath);
        //4.model
        String modelPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_MODEL;
        createDir(modelPath);
        //5.controller
        String controllerPath=PROJECT_PATH+JAVA_PATH+PACKAGE_PATH_CONTROLLER;
        createDir(controllerPath);
        //6.mapperXml
        //String mapperXMLPath=PROJECT_PATH+RESOURCES_PATH+"/mybatis/mapper";
        //createDir(mapperXMLPath);

        // 1.mapper 这些是生成代码文件
        String mapperName = mapperPath + File.separatorChar + javaClassName + "Mapper.java";
        generateJavaFile(mapperName, "mapper.ftl", map);
        // 2.service
        String serviceName = servicePath + File.separatorChar + javaClassName + "Service.java";
        generateJavaFile(serviceName, "service.ftl", map);
        // 3.serviceImpl
        String serviceImplName = serviceImplPath + File.separatorChar + javaClassName + "ServiceImpl.java";
        generateJavaFile(serviceImplName, "service-impl.ftl", map);
        // 4.model
        String modelName = modelPath + File.separatorChar + javaClassName + ".java";
        generateJavaFile(modelName, "entity.ftl", map);
        // 5.controller
        String controllerName = controllerPath + File.separatorChar + javaClassName + "Controller.java";
        generateJavaFile(controllerName, "controller.ftl", map);
        // 6.mapperXml
        //String mapperXMLName = mapperXMLPath + File.separatorChar + javaClassName + "Mapper.xml";
        //generateMapperXmlFile(mapperXMLName, "mapper-xml.ftl", map);
    }

    /**
     * 生成Java代码
     * @param table
     */
    public static void genXmlByTemplate(TableInfo table) {
        String javaClassName = GenUtil.capitaFirstLetter(GenUtil
                .mapTableNameToVarName(table.getTableName()));
        Map<String, Object> map = new HashMap<>();
        String tableComment=null;
        if(table.getTableComment()!=""){
            tableComment=table.getTableComment();
        } else if(table.getTableComment()==""){
            tableComment="【请填写功能的名称或完成的事项】";
        }
        map.put("tableName",table.getTableName());
        map.put("tableAlias", GenUtil.getFirstChar(table.getTableName()));
        map.put("basePackage", ProjectConstant.BASE_PACKAGE);
        map.put("modelNameUpperCamel", javaClassName);
        map.put("modelNameLowerCamel", GenUtil
                .mapTableNameToVarName(table.getTableName()));
        map.put("functionName", tableComment);
        map.put("pkColumn",table.getPkColumn());
        map.put("allColumn",table.getAllColumns());
        map.put("allColumnCount",table.getAllColumns().size());
        //1.mapperXml
        String mapperXMLPath=PROJECT_PATH+RESOURCES_PATH+ "/mybatis/system";
        createDir(mapperXMLPath);
        // 1.mapperXml
        String mapperXMLName = mapperXMLPath + File.separatorChar + javaClassName + "Mapper.xml";
        generateXmlFile(mapperXMLName, "mapper-xml.ftl", map);
    }

    /**
     * 生成代码
     */
    public static void generateCode() {
        // 生成全部文件
        List<TableInfo> tables = MybatisUtil.getTableInfoList();
        for (TableInfo table : tables) {
            genJavaByTemplate(table);
            genXmlByTemplate(table);
        }
    }

    /**
     * 生成代码
     */
    public static void generateCode(String tableName) {
        TableInfo table=MybatisUtil.getTableByName(tableName);
        genJavaByTemplate(table);
        genXmlByTemplate(table);
    }
    /**
     * 生成代码
     */
    public static void generateCode(String[] tableNames) {
        for(String tableName:tableNames) {
            TableInfo table = MybatisUtil.getTableByName(tableName);
            genJavaByTemplate(table);
            genXmlByTemplate(table);
        }
    }

    public static void main(String[] args) {
        generateCode(scanner("要操作的表名"));
        //generateCode();
    }

    private static Configuration getJavaConfiguration() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //基于文件系统
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_JAVA_PATH));
        //类路径的方法其实这个方法是根据类加载路径来判断的，最终会执行以下代码：CodeGenerateByTemplate.class.getClassLoader().getResource("/generator/template/java/");
        //cfg.setClassForTemplateLoading(CodeGenerateByTemplate.class,"/generator/template/java/");
        cfg.setDefaultEncoding("UTF-8");
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
        return cfg;
    }

    private static Configuration getMapperXmlConfiguration() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //基于文件系统
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_XML_PATH));
        //类路径的方法其实这个方法是根据类加载路径来判断的，最终会执行以下代码：CodeGenerateByTemplate.class.getClassLoader().getResource("/generator/template/xml/");
        //cfg.setClassForTemplateLoading(CodeGenerateByTemplate.class,"/generator/template/xml/");
        cfg.setDefaultEncoding("UTF-8");
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
        return cfg;
    }

    private static Configuration getVueConfiguration() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //基于文件系统
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_VUE_PATH));
        //类路径的方法其实这个方法是根据类加载路径来判断的，最终会执行以下代码：CodeGenerateByTemplate.class.getClassLoader().getResource("/generator/template/xml/");
        //cfg.setClassForTemplateLoading(CodeGenerateByTemplate.class,"/generator/template/xml/");
        cfg.setDefaultEncoding("UTF-8");
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
        return cfg;
    }

    private static Configuration getJsConfiguration() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        //基于文件系统
        cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_JS_PATH));
        //类路径的方法其实这个方法是根据类加载路径来判断的，最终会执行以下代码：CodeGenerateByTemplate.class.getClassLoader().getResource("/generator/template/xml/");
        //cfg.setClassForTemplateLoading(CodeGenerateByTemplate.class,"/generator/template/xml/");
        cfg.setDefaultEncoding("UTF-8");
        //cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
        return cfg;
    }
}
