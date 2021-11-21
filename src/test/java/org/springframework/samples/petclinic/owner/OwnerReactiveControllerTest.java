package org.springframework.samples.petclinic.owner;

import static org.mockito.ArgumentMatchers.any;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;

import com.datastax.workshop.petclinic.db.VisitReactiveDao;
import com.datastax.workshop.petclinic.owner.Owner;
import com.datastax.workshop.petclinic.owner.OwnerReactiveController;
import com.datastax.workshop.petclinic.owner.OwnerReactiveServices;
import com.datastax.workshop.petclinic.owner.db.OwnerReactiveDao;
import com.datastax.workshop.petclinic.pet.Pet;
import com.datastax.workshop.petclinic.pet.db.PetReactiveDao;

import reactor.core.publisher.Mono;

/**
 * Testing the {@link OwnerReactiveController} layer.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = OwnerReactiveController.class)
@Import({OwnerReactiveServices.class})
@TestPropertySource(locations = "/application-test.properties")
public class OwnerReactiveControllerTest {
    
    @MockBean
    PetReactiveDao petDao;
    
    @MockBean
    VisitReactiveDao visitDao;

    @MockBean
    OwnerReactiveDao ownerDao;
    
    @MockBean
    OwnerReactiveServices ownerServices;
 
    @Autowired
    private WebTestClient webClient;
   
    @Test
    void testCreateOwner() throws UnsupportedEncodingException {
        Owner o1 = new Owner(UUID.fromString("11111111-1111-1111-1111-111111111111"), "John", 
                                "Connor", "T800 street", "Detroit", "0123456789", new HashSet<Pet>());
        Mockito.when(ownerServices.createOwner(any())).thenReturn(Mono.just(o1));
        
        webClient.post()
                 .uri("/petclinic/api/owners")
                 .contentType(MediaType.APPLICATION_JSON)
                 .header("Authorization", "Basic " + Base64Utils
                         .encodeToString(("user:user").getBytes("UTF-8")))
                 .body(BodyInserters.fromValue(o1))
                 .exchange()
                 .expectStatus().isCreated()
                 .expectBody(Owner.class);
        
        // KO as the UID is generated at controller side, not same object
        // Mockito.verify(ownerDao, times(1)).save(dto);
        // <--
    }
    

}
