package com.developez.config;

import com.developez.model.Customer;
import com.developez.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EazyBankUserDetails implements UserDetailsService {

    // L'annotazione @Autowired viene utilizzata per iniettare una dipendenza del tipo CustomerRepository. Questo
    // significa che
    //  Spring si occuperà di creare un'istanza di CustomerRepository e di assegnarla alla variabile customerRepository
    //  presente nel costruttore della classe.
    private final CustomerRepository customerRepository;

    @Autowired
    public EazyBankUserDetails(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Questa classe è un'implementazione dell'interfaccia UserDetailsService fornita da Spring Security.
    // Viene utilizzata per caricare le informazioni dell'utente in base al suo nome utente durante
    // il processo di autenticazione.

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userName, password = null;
        List<GrantedAuthority> authorities = null;

        // Cerca nel repository dei clienti un cliente con l'email corrispondente al nome utente fornito.
        List<Customer> customer = customerRepository.findByEmail(username);

        // Se non viene trovato alcun cliente corrispondente, solleva un'eccezione UsernameNotFoundException.
        if (customer.size() == 0) {
            throw new UsernameNotFoundException("User " + username + " not found");
        } else {
            // Se viene trovato un cliente corrispondente, ottieni le informazioni di autenticazione dal primo cliente trovato.
            userName = customer.get(0).getEmail();
            password = customer.get(0).getPwd();

            // Crea una lista vuota di autorizzazioni.
            authorities = new ArrayList<>();

            // Aggiungi una nuova SimpleGrantedAuthority basata sul ruolo del cliente trovato.
            authorities.add(new SimpleGrantedAuthority(customer.get(0).getRole()));
        }

        // Crea e restituisce un nuovo oggetto User con il nome utente, la password e le autorizzazioni ottenute dal cliente trovato.
        return new User(userName, password, authorities);
    }
}
