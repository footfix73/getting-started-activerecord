# Persistencia de datos con Quarkus y Hibernate Panache

Vamos a simplificar la **Persistencia de Datos** con **Quarkus**, utilizando **Hibernate ORM Panache** una librería que se sitúa encima de Hibernate.


# Crea un proyecto Quarkus con Panache

```mermaid
mvn io.quarkus:quarkus-maven-plugin:2.3.0.Final:create \
     -DprojectGroupId=com.mastertheboss \
     -DprojectArtifactId=panache-demo \
     -DclassName="com.mastertheboss.MyService" \
     -Dpath="/tickets" \
     -Dextensions="quarkus-hibernate-orm-panache,quarkus-jdbc-postgresql,quarkus-resteasy-jsonb"
```
Como puede ver, hemos incluido un conjunto de extensiones adicionales que serán necesarias para crear nuestra aplicación
```mermaid
Adding extension io.quarkus:quarkus-jdbc-postgresql
Adding extension io.quarkus:quarkus-hibernate-orm-panache
Adding extension io.quarkus:quarkus-resteasy-jsonb
## Create files and folders
```
Por lo tanto, debe tener las siguientes dependencias en su proyecto.

```xml
 <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-bom</artifactId>
        <version>${quarkus.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-junit5</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>io.rest-assured</groupId>
      <artifactId>rest-assured</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-postgresql</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-jsonb</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-orm-panache</artifactId>
    </dependency>
```
**Hibernate Panache** es un complemento de Hibernate que agiliza la escritura de la capa ORM para sus aplicaciones. Hay dos estrategias para conectar Panache en tu Entidad:  
  
* Extendiendo la clase **io.quarkus.hibernate.orm.panache.PanacheEntity** : Esta es la opción más simple ya que obtendrás un campo ID que es auto-generado.  
* Extendiendo **io.quarkus.hibernate.orm.panache.PanacheEntityBase** : Esta opción se puede utilizar si se requiere una estrategia de ID personalizada.  

En la siguiente Entidad utilizaremos la estrategia anterior, es decir, extender la clase **io.quarkus.hibernate.orm.panache.PanacheEntity**:

```java
package  org.acme.entity;

import  java.time.LocalDate;
import  javax.persistence.Entity;
import  io.quarkus.hibernate.orm.panache.PanacheEntity;
import  lombok.AllArgsConstructor;
import  lombok.Builder;
import  lombok.Getter;
import  lombok.RequiredArgsConstructor;
import  lombok.Setter;
 
@Getter @Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public  class  Person  extends  PanacheEntity {
	private  Long id;
	private  String name;
	private  LocalDate birth;
}
```
Al extender la clase **io.quarkus.hibernate.orm.panache.PanacheEntity** podrás utilizar algunas ventajas como un campo ID autogenerado. También puedes elegir una estrategia de ID personalizada extendiendo PanacheEntityBase y gestionar el ID tú mismo.  
  
*Además, Panache utiliza variables de clase públicas para sus campos, por lo que no hay necesidad de escribir getters y setters. Puedes referirte directamente al nombre del campo. Construyamos ahora una aplicación de ejemplo que utilice la clase Entity anterior.*

A continuación, editar la clase org.acme.PersonResource para incluir los métodos necesarios para realizar operaciones CRUD en nuestra Entidad:

```java
package  org.acme;

import  java.util.List;
import  java.util.logging.Logger;
import  javax.enterprise.context.ApplicationScoped;
import  javax.transaction.Transactional;
import  javax.ws.rs.Consumes;
import  javax.ws.rs.DELETE;
import  javax.ws.rs.GET;
import  javax.ws.rs.POST;
import  javax.ws.rs.PUT;
import  javax.ws.rs.Path;
import  javax.ws.rs.PathParam;
import  javax.ws.rs.Produces;
import  javax.ws.rs.WebApplicationException;
import  javax.ws.rs.core.MediaType;
import  javax.ws.rs.core.Response
import  org.acme.entity.Person;
import  static  javax.ws.rs.core.Response.Status.CREATED;
import  static  javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("/person")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
public  class  PersonResource {

private  static  final  Logger LOGGER = Logger.getLogger(PersonResource.class.getName());
  
@GET
@Produces(MediaType.APPLICATION_JSON)
public  List<Person> getPersons() {
	LOGGER.info("Listar todas las personas ...");
	return Person.listAll();
}

@GET
@Path("{id}")
public  Person  getPersons(@PathParam("id") Long id) {
	LOGGER.info("Listar la persona con id ... ["  + id +  "]");
	return Person.findById(id);
}

@POST
@Transactional
public  Response  setPersons(Person person) {
LOGGER.info("Añadir la persona con id ... ["  + person.getId() +  "] ... "  + person.getName() +  " ... "  + person.getBirth());

	if (person ==  null  || person.getId() !=  null) {
		throw  new  WebApplicationException("El identificador no es 		válido.", 422);
	}

	person.persist();
	return Response.ok(person).status(CREATED).build();
}

@PUT
@Transactional
@Path("{id}")
public  Response  update(@PathParam(value =  "id") Long id, Person person) {

	if (person ==  null  || person.getName() ==  null) {
		throw  new  WebApplicationException("El nombre no se recibe en 		la llamada.", 422);
	}

	Person newPerson = Person.findById(id);
	//updatePerson

	if(newPerson ==  null) {
		throw  new  WebApplicationException("Persona no encontrada con el id ["  + id +  "]", 404);
	}

	newPerson.setName(person.getName());
	newPerson.setBirth(person.getBirth());
	return Response.ok(newPerson).build();
}

@DELETE
@Transactional
@Path("{id}")
public  Response  delete(@PathParam(value =  "id")Long id) {
	Person.deleteById(id);

	return Response.ok().status(NO_CONTENT).build();
	}
}
```
## Configurar el application.properties

A continuación, para conectarnos a MariaDB, configuraremos el fichero **src/main/resources/application.properties** para incluir el Connection Pool para llegar a la Base de Datos,

```yml
quarkus.datasource.db-kind=mariadb
quarkus.datasource.jdbc.url=jdbc:mariadb://localhost:3306/developer
quarkus.datasource.jdbc.driver=org.mariadb.jdbc.Driver
quarkus.datasource.username=developer
quarkus.datasource.password=developer
quarkus.datasource.devservices.enabled=false

quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql: true
quarkus.hibernate-orm.sql-load-script: import.sql
```
Para tener algunas Entidades por defecto en la Base de Datos, podemos incluir un fichero **src/main/resources/import.sql**

```sql
insert into Person(id, name, birth) values (99, 'Jose', '2001-01-01');
insert into Person(id, name, birth) values (100, 'Maria', '2002-01-01');
insert into Person(id, name, birth) values (99, 'Jesus', '2003-01-01');
insert into Person(id, name, birth) values (100, 'Daniella', '2004-01-01');
insert into Person(id, name, birth) values (99, 'Manuel', '2005-01-01');
insert into Person(id, name, birth) values (100, 'Carola', '2006-01-01');
insert into Person(id, name, birth) values (99, 'Lucia', '2007-01-01');
insert into Person(id, name, birth) values (100, 'Marcos', '2008-01-01');
insert into Person(id, name, birth) values (99, 'Pedro', '2009-01-01');
insert into Person(id, name, birth) values (100, 'Lidia', '2010-01-01');
insert into Person(id, name, birth) values (99, 'Benito', '20011-01-01');
insert into Person(id, name, birth) values (100, 'Benita', '2012-01-01');
```

## Docker compose con la BBD

Se necesita tener el Docker Hub instalado en maquina para poder ejecutar el archivo a continuación, si ya tenemos instalado uno no es necesario 

```yml
version: "3.3"

services:
  db:
    image: mysql
    container_name: MariaDB_Developer
    restart: always
    environment:
      MYSQL_DATABASE: developer
      MYSQL_USER: developer
      MYSQL_PASSWORD: developer
      MYSQL_ROOT_PASSWORD: developer
    ports:
      - "3306:3306"
    expose:
      - "3306"
    volumes:
      - mydb-db:/var/lib/mysql
volumes:
  mydb-db:
```
