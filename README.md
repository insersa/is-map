# IS-MAP: Java Mapping Features
![Java](https://img.shields.io/badge/Java-11-blue)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-orange)
![Tomcat](https://img.shields.io/badge/Tomcat-10-yellow)
![Version](https://img.shields.io/badge/Stable%20Version-6.0.4-brightgreen)

## 📌 Overview
The IS-MAP library provides REST services to execute actions related to geographic objects. The principle is to compose a URL with:

- The word "map"

- The service name

- The URL structure becomes:

	 http://urlDuProjet/services/map/nomservice

***

## Configuration

### Tomcat Web.xml Configuration

To allow the front-end in development mode (or Swagger) to communicate with the backend REST services, you need to configure the CORS filter in Tomcat's web.xml.

```
	<!-- ================== Built In Filter Definitions ===================== -->
	...
	<filter>
	    <filter-name>CorsFilter</filter-name>
	    <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
	    <init-param>
	        <param-name>cors.allowed.origins</param-name>
	        <param-value>*</param-value>
	    </init-param>
	    <init-param>
	        	<param-name>cors.allowed.headers</param-name>
	        	<param-value>token,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers</param-value>
	    </init-param>
	    <init-param>
	        <param-name>cors.allowed.methods</param-name>
	        <param-value>GET,POST,HEAD,OPTIONS,PUT,DELETE,PATCH</param-value>
	    </init-param>
	</filter>
	<filter-mapping>
	    <filter-name>CorsFilter</filter-name>
	    <url-pattern>/*</url-pattern>
	</filter-mapping>
```

## 📜 License
This library is licensed under the **GNU Lesser General Public License v3 (LGPL-3.0)**, as published by the **Free Software Foundation**. You are free to use, modify, and redistribute this library under the terms of the LGPL-3.0 license, either version 3 of the License, or (at your option) any later version.

## 📢 Contact
INSER SA
🔗 **Website**: [www.inser.ch](https://www.inser.ch)
📍 **Address**: INSER SA, Chem. de Maillefer 36, 1052 Le Mont-sur-Lausanne, Vaud, Switzerland

## 👥 Contributing
Contributions are welcome! Please submit pull requests with:
- Clear descriptions
- Unit tests
- Documentation updates
