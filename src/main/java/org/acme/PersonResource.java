package org.acme;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.dao.PersonDao;
import org.acme.entity.Person;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("/person")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
public class PersonResource {

    private static final Logger LOGGER = Logger.getLogger(PersonResource.class.getName());

    @Inject
    PersonDao personDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Person> getPersons() {
        LOGGER.info("Listar todas las personas ...");
        return Person.listAll();
    }

    @GET
    @Path("{id}")
    public Person getPersons(@PathParam("id") Long id) {
        LOGGER.info("Listar la persona con id ... [" + id + "]");
        return Person.findById(id);
    }

    @POST
    @Transactional
    public Response setPersons(Person person) {
        LOGGER.info("Añadir la persona con id ... [" + person.getId() + "] ... " + person.getName() + " ... " + person.getBirth());
        if (person == null || person.getId() != null) {
            throw new WebApplicationException("El identificador no es válido.", 422);
        }
        person.persist();
        return Response.ok(person).status(CREATED).build();
    }

    @PUT
    @Transactional
    @Path("{id}")
    public Response update(@PathParam(value = "id") Long id, Person person) {
        if (person == null || person.getName() == null) {
            throw new WebApplicationException("El nombre no se recibe en la llamada.", 422);
        }

        Person newPerson = Person.findById(id);

        //updatePerso
        if(newPerson == null) {
            throw new WebApplicationException("Persona no encontrada con el id [" + id + "]", 404);
        }
        newPerson.setName(person.getName());
        newPerson.setBirth(person.getBirth());

        return Response.ok(newPerson).build();
                //.onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public Response delete(@PathParam(value = "id")Long id) {
        Person.deleteById(id);
        return Response.ok().status(NO_CONTENT).build();
        //return Panache.withTransaction(() -> Person.deleteById(id)).map(deleted -> deleted? Response.ok().status(NO_CONTENT).build()                     : Response.ok().status(NOT_FOUND).build());
    }
    // Querys
    @GET
    @Path("/by-name/{name}")
    public List<Person> findByName(@PathParam(value = "name") String name) {
        return Person.findByName(name);
    }

    @GET
    @Path("/born-after/{date}")
    public List<Person> findBornAfter(@PathParam(value = "date") Date date) {
        return Person.findBornAfter(date);
    }

    @GET
    @Path("/by-name-query/{name}")
    public List<Person> findByNameQuery(@PathParam(value = "name") String name) {
        return personDao.findByName(name);
    }

    @GET
    @Path("/by-name-query-criteria/{name}")
    public List<Person> findByNameQuery2(@PathParam(value = "name") String name) {
        return personDao.findByNameWithQuery(name);
    }

}