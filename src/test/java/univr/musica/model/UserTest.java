package univr.musica.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User standardUser;
    private User adminUser;

    @BeforeEach
    public void setUp() {
        // Inizializza un utente standard in attesa di approvazione e un admin
        standardUser = new User("walid", "pass123", false, false, 0);
        adminUser = new User("admin", "admin", true, true, 0);
    }

    @Test
    public void testPasswordValidation() {
        assertTrue(standardUser.checkPassword("pass123"), "La password corretta deve restituire true");
        assertFalse(standardUser.checkPassword("errata"), "Una password errata deve restituire false");
    }

    @Test
    public void testUserApprovalWorkflow() {
        // All'iscrizione lo stato deve essere non approvato (false / status = 0)
        assertFalse(standardUser.getStatus(), "L'utente appena registrato non deve risultare attivo");

        // Simula l'approvazione da parte dell'admin
        standardUser.setStatus(true);
        assertTrue(standardUser.getStatus(), "Dopo l'approvazione lo stato deve risultare true");
    }

    @Test
    public void testAdminPrivileges() {
        assertTrue(adminUser.isAdmin(), "L'account admin deve avere il flag is_admin impostato a true");
        assertTrue(adminUser.getStatus(), "L'account admin deve risultare sempre approvato");
    }
}