package br.com.impacta.quarkus.resources;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.impacta.quarkus.domain.Customer;
import br.com.impacta.quarkus.interfaces.BuscaCEPRestClient;
import br.com.impacta.quarkus.services.CustomerService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import org.jboss.logmanager.Level;
import java.util.logging.Logger;


@Path("/customers")
public class CustomerResource {

    private static final Logger LOGGER = Logger.getLogger(CustomerResource.class.toString());

    @ConfigProperty(name = "isTestingFault")
    boolean isTestingFault;

    @ConfigProperty(name = "isRetry")
    boolean isRetry;
    final int quantidadeRetry = 5;
    int exceptionCount = quantidadeRetry - 1;
    int count = 0;

    @ConfigProperty(name = "isFallBack")
    boolean isFallBack;

    @Inject
    CustomerService customerService;

    @Inject
    @RestClient
    BuscaCEPRestClient buscaCepRestClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@Retry(maxRetries = quantidadeRetry, delay = 1, delayUnit = ChronoUnit.SECONDS)
    //@Fallback(fallbackMethod = "fallbackCustomers")
    public List<Customer> listCustomer(){
        if (isTestingFault){
            executeFault();
        }
        return customerService.listCustomer();
    }

    private void executeFault(){
        if (isRetry){
            while ( count < exceptionCount){
                count++;
                String message = "Simulando Erro: " + count;
                LOGGER.log(Level.ERROR, message);
                throw new RuntimeException(message);
            }
            count = 0;
        }
        if (isFallBack){
            throw new RuntimeException("Simulando Erro: ");
        }
    }
    
    private List<Customer> fallbackCustomers(){
        return new ArrayList<Customer>(0);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Customer getCustomerById(@PathParam("id") Long id){
        Customer customerEntity = new Customer();
        customerEntity.id = id;
        customerEntity = customerService.getCustomerById(customerEntity);
        return customerEntity;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rg/{rg}")
    public Customer getCustomer(@PathParam("rg") Integer rg){
        Customer customerEntity = new Customer();
        customerEntity.setRg(rg);
        customerEntity = customerService.getCustomerByRg(customerEntity);
        return customerEntity;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/primeiroNome/{primeiroNome}")
    public List<Customer> getCustomerByName(@PathParam("primeiroNome") String name){
        Customer customerEntity = new Customer();
        customerEntity.setPrimeiroNome(name);
        List<Customer> customers = customerService.getByPrimeiroNome(customerEntity);
        return customers;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/primeiroNome/{primeiroNome}/sobreNome/{sobreNome}")
    public List<Customer> getCustomerByNameOrLastName(@PathParam("primeiroNome") String primeiroNome,
                                                    @PathParam("sobreNome") String sobreNome){
        Customer customerEntity = new Customer();
        customerEntity.setPrimeiroNome(primeiroNome);
        customerEntity.setSobreNome(sobreNome);
        List<Customer> customers = customerService.getByPrimeiroNomeOrSobreNome(customerEntity);
        return customers;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer addCustomer(Customer customer){
        customer.setNumeroCep(buscaCepRestClient.getNumeroCEP().getNumeroCep());
        Customer customerEntity = customerService.addCustomer(customer);
        return customerEntity;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer updatCustomer(Customer customer){
        Customer customerEntity = customerService.updateCustomer(customer);
        return customerEntity;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rg/{rg}")
    @RolesAllowed("admin")
    public Customer deleteCustomerByRg(@PathParam("rg") Integer rg){
    Customer customerEntity = new Customer();
    customerEntity.setRg(rg);
    customerEntity = customerService.getCustomerByRg(customerEntity);
    customerEntity = customerService.deleteCustomer(customerEntity);
    return customerEntity;
    }

}
