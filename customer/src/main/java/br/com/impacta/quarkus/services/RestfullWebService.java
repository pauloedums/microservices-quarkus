package br.com.impacta.quarkus.services;

import br.com.impacta.quarkus.domain.Customer;

public class RestfullWebService {
    public Customer hello() {
        Customer customer = new Customer();
        customer.setPrimeiroNome("nome1");
        customer.setSobreNome("sobreNome1");
        customer.setRg(101010);
        return customer;
    } 
}
