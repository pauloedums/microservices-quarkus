package br.com.impacta.quarkus.interfaces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.impacta.quarkus.domain.CEP;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@RegisterRestClient
public interface BuscaCEPRestClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CEP getNumeroCEP();
}