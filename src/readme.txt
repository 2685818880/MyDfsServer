在spring配置文件中配置
 <?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	 http://www.springframework.org/schema/aop  http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">
	<bean id="mydfsStorageServer" class="mydfs.storage.server.MydfsStorageServer" 
		lazy-init="false" init-method="startServer" 
		destroy-method="stopServer">
		<property name="host" value="127.0.0.1"/>
		<property name="port" value="9999"/>
		<property name="workers" value="10"/>
		<property name="basepath" value="D:/data/mydfs/store"/>
	</bean>
	<bean id="mydfsTrackerServer" class="mydfs.storage.server.MydfsTrackerServer" scope="prototype">
		<property name="host" value="127.0.0.1"/>
		<property name="port" value="9999"/>
		<property name="mydfsStorageServer" ref="mydfsStorageServer"/>
	</bean>
</beans>
 
在web.xml中配置
	<!-- Begin Author:wuqiwei:Data:2014-06-05 AddReason:内嵌式小型分布式文件系统集成 -->
	 <servlet>
		<servlet-name>storageClient</servlet-name>
		<servlet-class>mydfs.storage.server.MydfsHttpClient</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>storageClient</servlet-name>
		<url-pattern>/group/M00/*</url-pattern>
	</servlet-mapping>
	<!-- End Author:wuqiwei:Data:2014-06-05 AddReason:内嵌式小型分布式文件系统集成 -->