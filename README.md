# About TestFlyway #

软件开发时，随着项目的进展，数据库表结构等不可避免的会一直在变化。不断变化必然导致繁琐的工作，如果只需要支持一种数据库，最简单的就是直接改Sql脚本。但是如果要同时兼容多种数据库时，全靠手工去改的话，一没效率，二不能保证正确；

只需要维护一种数据库的时候，一般都是编写数据库ER图，然后导出Sql脚本；

以[Mysql][Mysql]为例，可以用[Mysql WorkBench][MysqlW]设计数据库，然后导出Mysql脚本，最后导入数据库；

![Mysql创建表](http://i.imgur.com/spXTjCE.jpg)

![使用EER Diagram可以很方便的管理各表的关系](http://i.imgur.com/fIUPTMj.jpg)

**当然这些还远远不够！**

为了实现只需要维护一份数据库，必须有各数据库之间脚本转换工具*（主要是建表语句,不是数据迁移）*。

	// TODO 已经在写，完善之后迁移到Github


# About Flyway

*官网的介绍*  
Evolve your Database Schema easily and reliably across all your instances  
大概意思是，可以在任何地方简单可靠地实现你的数据库版本演变

## 如何使用 ##

具体怎么使用，不是本文重点，感兴趣可以去[Flyway官网][Flyway]查看。

# 测试内容

主要测试Flyway **Migrate**功能对数据库脚本的转化/兼容能力；

[Migrate功能介绍](http://flywaydb.org/documentation/command/migrate.html "Migrate功能介绍")

## Sqlite sql to Sqlite Db ##

|版本            |主键|自增长|结果|
|----------------|----|---|-----|
|V1__COMMON.sql| 无 | 无 |√|
|V2__KEY.sql   | 有 | 无 |√|
|V3__KEYAndAI.sql| 有 | 有 |√|

Sqlite脚本详细：

````sql
-- V1__COMMON.sql
CREATE TABLE TB_COMMON
(	
	ID INTEGER NOT NULL ,
	NAME VARCHAR(32) NOT NULL 

);

-- V2__KEY.sql
CREATE TABLE TB_KEY
(	
	ID INTEGER NOT NULL ,
	NAME VARCHAR(255) 
);

-- V3__KEYAndAI.sql
CREATE TABLE TB_KEY_AI
(	
	ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,
	NAME VARCHAR(255) 
);

````

* 测试结果详细

> 脚本=sqlite,版本=v1,结果=true  
> 脚本=sqlite,版本=v2,结果=true  
> 脚本=sqlite,版本=v3,结果=true  


### Mysql sql to Sqlite Db ###

|版本|主键|自增长|结果|
|----------------|----|---|-|
|V1__COMMON.sql| 无 | 无 |×|
|V2__KEY.sql   | 有 | 无 |×|
|V3__KEYAndAI.sql| 有 | 有 |×|

* MySql脚本详细

````sql
-- V1__COMMON.sql
CREATE TABLE TB_COMMON
(	
	ID INTEGER NOT NULL ,
	NAME VARCHAR(32) NOT NULL 

)
ENGINE = InnoDB
DEFAULT CHARACTER SET = gbk
COLLATE = gbk_chinese_ci;

-- V2__KEY.sql
CREATE TABLE TB_KEY
(	
	ID INTEGER NOT NULL ,
	NAME VARCHAR(255) ,
    PRIMARY KEY (ID)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = gbk
COLLATE = gbk_chinese_ci;

-- V3__KEYAndAI.sql
CREATE TABLE TB_KEY_AI
(	
	ID INTEGER NOT NULL AUTO_INCREMENT ,
	NAME VARCHAR(255) ,
    PRIMARY KEY (ID)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = gbk
COLLATE = gbk_chinese_ci;

````

* 测试结果详细

> Migration V1__COMMON.sql failed
> -------------------------------
> SQL State  : null
> Error Code : 1
> Message    : [SQLITE_ERROR] SQL error or missing database (near "ENGINE": syntax error)
> Location   : db/migration/mysql/v1/V1__COMMON.sql (D:\Git_github_corningsun\test\flyway\source\target\classes\db\migration\mysql\v1\V1__COMMON.sql)
> Line       : 1
> Statement  : CREATE TABLE TB_COMMON
> (	
> 	ID INTEGER NOT NULL ,
> 	NAME VARCHAR(32) NOT NULL 
> 
> )
> ENGINE = InnoDB
> DEFAULT CHARACTER SET = gbk
> COLLATE = gbk_chinese_ci
> 
> 脚本=mysql,版本=v1,结果=false
> 
> Migration V2__KEY.sql failed
> ----------------------------
> SQL State  : null
> Error Code : 1
> Message    : [SQLITE_ERROR] SQL error or missing database (near "ENGINE": syntax error)
> Location   : db/migration/mysql/v2/V2__KEY.sql (D:\Git_github_corningsun\test\flyway\source\target\classes\db\migration\mysql\v2\V2__KEY.sql)
> Line       : 1
> Statement  : CREATE TABLE TB_KEY
> (	
> 	ID INTEGER NOT NULL ,
> 	NAME VARCHAR(255) ,
>     PRIMARY KEY (ID)
> )
> ENGINE = InnoDB
> DEFAULT CHARACTER SET = gbk
> COLLATE = gbk_chinese_ci
> 
> 脚本=mysql,版本=v2,结果=false
> 
> Migration V3__KEYAndAI.sql failed
> ---------------------------------
> SQL State  : null
> Error Code : 1
> Message    : [SQLITE_ERROR] SQL error or missing database (near "AUTO_INCREMENT": syntax error)
> Location   : db/migration/mysql/v3/V3__KEYAndAI.sql (D:\Git_github_corningsun\test\flyway\source\target\classes\db\migration\mysql\v3\V3__KEYAndAI.sql)
> Line       : 1
> Statement  : CREATE TABLE TB_KEY_AI
> (	
> 	ID INTEGER NOT NULL AUTO_INCREMENT ,
> 	NAME VARCHAR(255) ,
>     PRIMARY KEY (ID)
> )
> ENGINE = InnoDB
> DEFAULT CHARACTER SET = gbk
> COLLATE = gbk_chinese_ci
> 
> 脚本=mysql,版本=v3,结果=false


## 测试总结 ##

* 数据源只能是脚本文件/Java类，不支持连接数据库
* 使用脚本初始化数据库功能很好用
* Sqlite数据库脚本可以migrate到Sqlite数据库
* Mysql的数据库脚本无法migrate到Sqlite数据库
* 没有脚本转换的功能

对简单的建表语句支持还可以。但是主键、自增长、特殊字段等，兼容性不好；






[Mysql]: http://www.mysql.com/ "http://www.mysql.com/"
[MysqlW]: http://dev.mysql.com/downloads/workbench/ "http://dev.mysql.com/downloads/workbench/"
[Flyway]: http://flywaydb.org/ "http://flywaydb.org/"
