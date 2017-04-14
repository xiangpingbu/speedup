# Maas
## maven部分
maas的java模块由maven构建,因此在项目工程上可以将其分为四个模块

* maas-common 这个模块提供工具类和静态变量等,是最底层的模块
* maas-model 这个模块包含各个模型的实现,并存有相关模型的配置文件
* maas-schedule 这个模块只涉及到和定时任务相关的业务
* maas-service 这个模块暂时还没有提供服务
* maas-web 最顶级的模块,它通过http对外提供maas的服务

### 依赖关系
* maas-web 依賴 maas-schedule和maas-service  
* maas-schedule 依賴 maas-common
* maas-service 依賴maas-model 和 maa-common
* maas-model 依賴 maa-common

依赖可以传递,因此maas-web依赖所有模块

### 打包配置
依靠maven的配置实现动态配置项目依赖的配置信息

分别配置了五种profile:local,dev,test,stable,product.
默认为local,即cur.env = local

这样我们就能找到config-local.properties文件.
然后resource标签就起作用了.filtering = true的一项, includes标签内指定的所有.properties和logback.xml文件就会被执行过滤操作,
含有\${}的位置就会被替换为config-${cur.env}.properties内配置好的变量.

filtering = false的一项,指定除了.properties和logback.xml文件外的所有资源文件都会被打包,但是不会被过滤.



```xml
<filters>
   <filter>
     ../config-${cur.env}.properties
   </filter>
</filters>


<profiles>
        <profile>
            <id>local</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <cur.env>local</cur.env>
            </properties>
        </profile>
        <profile>
            <id>dev</id>
            <properties>
                <cur.env>dev</cur.env>
            </properties>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <cur.env>test</cur.env>
            </properties>
        </profile>
        <profile>
            <id>stable</id>
            <properties>
                <cur.env>stable</cur.env>
            </properties>
        </profile>
        <profile>
            <id>online</id>
            <properties>
                <cur.env>online</cur.env>
            </properties>
        </profile>
    </profiles>
    
    <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <includes>
                    <include>**/*.properties</include>
                    <include>logback.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>false</filtering>
                <excludes>
                    <exclude>**/*.properties</exclude>
                    <exclude>logback.xml</exclude>
                </excludes>
            </resource>
        </resources>

```

#### 多模块打包
这段配置存在于含有资源的模块,它能将各个模块的资源都加载到根目录的config-dir中,为上线做准备

```
<plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-resources</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-resources</goal>
                    </goals>
                    <configuration>
                        <encoding>UTF-8</encoding>
                        <outputDirectory>
                            ../config-dir
                        </outputDirectory>   
                        <resources>
                            <resource>
                                <directory>src/main/resources</directory>
                                <filtering>true</filtering>
                                <includes>
                                    <include>**/*.properties</include>
                                    <include>logback.xml</include>
                                </includes>
                            </resource>
                            <resource>
                                <directory>src/main/resources</directory>
                                <filtering>false</filtering>
                                <excludes>
                                    <exclude>**/*.properties</exclude>
                                    <exclude>logback.xml</exclude>
                                    <exclude>logback-test.xml</exclude>
                                </excludes>
                            </resource>
                        </resources>
                    </configuration>
                </execution>
            </executions>
        </plugin>
```


## web模块部分
web模块由下面几部分组成
#### jersey
jersey是一个web框架.在它的帮助下,可以使用注解和java方法描述一次Http请求接收和响应的过程.jersey自己维护一个容器,利用扫描包中的注解信息,将URL Mapping加入容器管理.

#### swagger
将Rest接口图形界面化,方便调试接口.配置和初始化过程和jersey相似.

#### spring
spring也有一个容器,用于维护项目内部的对象,可以方便整合其他工具,例如Redis,mysql等.


#### 图示
![maas](http://oagjvfn8h.bkt.clouddn.com/maas.png)

	黑色箭头代表依次请求过程
	白色箭头指向代表被依赖的关系


### python web
因为建模和分析过程需要依赖python的第三方类库,因此再搭建一个由python实现的web项目.通过http接口对外提供数据.

#### 项目结构
flask作为web框架,部署在gunicorn服务器上,结合gevent提高服务端的并发能力.


## 工程变量依赖配置
### 初始化
在工程中也会用到appliacation.properties的配置,因为要和ecreditpal保持一致,且mvc框架用到的也不是spring mvc,因此放弃使用spring注入形式的变量配置,转而使用apache的configuration来管理.这样在变量使用上面会显得更自由,不会被依赖注入所束缚.

在ConfigurationManager的静态块中做了初始化处理,首先加载了SystemConfiguration,这样我们就可以得到linux服务器上的系统启动时提供的参数,这样本地和远程的配置加载就可以区分开来.一旦得到application.properties文件,就可以依赖配置做很多东西.  

接下去就可读取了,maas的各个模块的信息,进而获得每个模块下的所有配置文件,这样就可以直接通过文件名获取配置文件的绝对路径

```
static {
        try {
            logger.info("loading system properties ...");
            cc.addConfiguration(new SystemConfiguration());
            //判断是否为本地
            String productConfigDir = cc.getString("config.dir");
            String applicationProp;
            if (productConfigDir == null) {
                //从本地获取配置文件
                applicationProp = "maas-web/target/classes/application.properties";
            } else {
                //从服务器的目录获取配置文件
                applicationProp = productConfigDir + "/application.properties";
            }

            logger.info("loading  property in directory {}.",
                    applicationProp);
            PropertiesConfiguration conf = new PropertiesConfiguration(
                    applicationProp);

            /*
                递归获得配置目录下的所有文件的路径
             */
            if (productConfigDir != null) {
                File file = new File(productConfigDir);
                listFile(file, conf);
            } else{
                List subModels = conf.getList("maven.submodel");
                for (Object subModel : subModels) {
                    File file = new File(subModel.toString()+"/src/main/resources");
                    listFile(file,conf);
                }
            }

            conf.addProperty("defaultKafkaConfig",new MaasKafkaConfig());

            cc.addConfiguration(conf);
        } catch (Exception e) {
            logger.error("Failed to load configuration files", e);
        }
    }
```



### 使用方法

```java
ConfigurationManager.getConfiguration().
getString(key:"mgc.txt",default:"/xx/xx/xx/mgc.txt")
```

如果没有得到mgc.txt的相关配置,那么默认使用/xx/xx/xx/mgc.txt


## 模型服务
### 模型结构
模型的运行依赖于输入的参数,我们将参数分别交由多个变量类来执行(Variable),执行成功后再交由模型进行整合并输出.

为了Variable能够复用,且能够灵活地更改,我们选择使用xml进行配置,下面为信用宝模型的部分变量配置:  
    <Model>标签用作模型的介绍,<Variable>标签代表了一个变量所有的域,以及Variable类的具体实现.

*xyb_model_variables.xml*

```
<VariableConfiguration>
    <Model>XYBModel</Model>
    <Variable name="CreditUtilizationVariable" class="com.ecreditpal.maas.model.variables.SectionVariable">
            <property name="key" value="creditUtilization"/>
            <property name="paramType" value="numerical"/>
            <property name="paramValue" value="(0-max)"/>
            <property name="description" value="return one's credit usage rate"/>
            <property name="returnType" value="Double"/>
        </Variable>

        <Variable name="PersonalEducation" class="com.ecreditpal.maas.model.variables.SectionVariable">
            <property name="key" value="personalEducation"/>
            <property name="paramType" value="categorical"/>
            <property name="paramValue" value="1|2|3|4"/>
            <property name="paramMapping" value="masterOrAbove|undergraduate|junior|others"/>
            <property name="description" value="return one's education"/>
            <property name="returnType" value="String"/>
        </Variable>
</VariableConfiguration>
```

该配置形式借鉴了spring,只要variable的子类存在相应的域,且实现了set方法,那么通过解析产生的对象都会得到这些值.

SAX解析xml.

```
public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        sb = new StringBuilder();

        if ("Variables".equals(name)) {
            variableList = Lists.newArrayList();
            v.setVariables(variableList);
        } else if ("Variable".equals(name)) {
            String className = attributes.getValue(1); //Variable的子类的全限定名
            try {
                variable = (Variable) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            variable.setName(attributes.getValue(0)); //指定的variable的名称
            variableList.add(variable);
        } else if ("property".equals(name)) {
            String fieldName = attributes.getValue(0); //variable的值域
            String value = attributes.getValue(1);  //值域的值
            /*通过反射的方式注入对应的值*/
            Field[] allFields = variable.getClass().getDeclaredFields();
            for (Field field : allFields) {
                if (field.getName().equals(fieldName)) {
                    String firstLetter = field.getName().substring(0, 1)
                            .toUpperCase();
                    String setter = "set" + firstLetter
                            + field.getName().substring(1);
                    try {
                        Method method = Variable.class.getMethod(setter,field.getType());
                        method.invoke(variable, value);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
```


### 模型计算流程

![maas-model -1-](http://oagjvfn8h.bkt.clouddn.com/maas-model%20-1-.png)

1. 请求到达,提供各种参数
2. 将参数存入inputMap中
3. 生成一个模型实例,模型第一次启动的时候将会解析配置文件(例如**xyb_model_variables.xml**),这个配置文件包含了模型依赖的变量,以及变量依赖的各项配置.解析成功后,xml将会被转化为**List< Variable >**
4. 这个List每次在Model启动的时候将会被复用,产生一套相同的variable list,并交由线程池来执行这一组variable,运行依赖的参数来自于inputMap.
5. 执行完毕后,model会通过回调的方式获得variable中的计算值,最后根据该值输出结果

###模型结果记录
在模型的运作过程中,有三部分的数据非常重要,外部传入的原始变量、variable计算产生的衍生变量、模型根据衍生变量计算得出的最终结果.将这部分数据记录下来后,日后就可以很方便地针对这些数据进行分析  

maas一次web请求的前和后都加入了filter,前置的filter可以生成LookUpEventMassage对象,该对象包含了以下对象:
    
* EventIds 记录随机产生的id,请求到来时的时间戳,发起请求的ip
* UserInfo 发起请求的用户信息
* RequestInfo 请求包含的各种信息,参数,路径等等
* ResponseInfo 请求返回的数据
* ModelLog 模型的变量产生的数据和最终结果,以及模型的名称.

在前置的filter中可以加入EventIds和RequestInfo.因为有ecreditpal拦在前头,因此可能得不到UserInfo.  

在每一个模型计算完毕的最后都会将结果封装为ModelLog对象,并加入到LookUpEventMassage中.

在后置的filter中会记录ResponseInfo.

LookUpEventMassage存在于请求的上下文中,并贯穿整个请求的周期,随着requestContext的消亡而消失.在最后的comitFilter中,将会从requestContext中获取LookUpEventMassage,并将其由kafka发往日志存储系统中.在调试的过程中发现kafka在发送消息前会从leader获取metadata,默认超时时间为60s,重复次数为0,且而且是一个同步的过程.因此我将这个方法交由线程池执行,使得这部分操作完全异步.



