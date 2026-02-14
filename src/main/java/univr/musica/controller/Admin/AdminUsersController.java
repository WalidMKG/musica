package univr.musica.controller.Admin;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import univr.musica.model.Model;
import univr.musica.model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminUsersController implements Initializable {
    private final Model model;

    // Tabella Pendenti
    @FXML public TableView<User> pendingTable;
    @FXML public TableColumn<User, String> col_pending_user;
    @FXML public TableColumn<User, Void> col_pending_actions;

    // Tabella Attivi
    @FXML public TableView<User> activeTable;
    @FXML public TableColumn<User, String> col_active_user;
    @FXML public TableColumn<User, Void> col_active_actions;

    public AdminUsersController(Model model) {
        this.model = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup colonne nomi
        col_pending_user.setCellValueFactory(new PropertyValueFactory<>("username"));
        col_active_user.setCellValueFactory(new PropertyValueFactory<>("username"));

        // Setup pulsanti per le due tabelle
        setupPendingActions();
        setupActiveActions();

        refreshTables();
    }

    private void refreshTables() {

        ObservableList<User> pending = model.getUserRepository().getPendingUsers();
        ObservableList<User> active = model.getUserRepository().getAllUsers();


        pendingTable.setItems(pending);
        activeTable.setItems(active);


        pendingTable.refresh();
        activeTable.refresh();
        System.out.println("Pending size: " + pending.size() + " | Active size: " + active.size());
    }

    private void setupPendingActions() {
        col_pending_actions.setCellFactory(param -> new TableCell<>() {
            private final Button btnApprove = new Button("Approva");
            private final Button btnReject = new Button("Rifiuta");
            private final HBox box = new HBox(10, btnApprove, btnReject);

            {
                btnApprove.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnApprove.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    model.getUserRepository().approveUser(u.getUsername());
                    refreshTables();
                });

                btnReject.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    model.getUserRepository().deleteUser(u.getUsername());
                    refreshTables();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupActiveActions() {
        col_active_actions.setCellFactory(param -> new TableCell<>() {
            private final Button btnRemove = new Button("Rimuovi Utente");
            {
                btnRemove.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
                btnRemove.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    // Dialogo di conferma opzionale
                    model.getUserRepository().deleteUser(u.getUsername());
                    refreshTables();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRemove);
            }
        });
    }
}
