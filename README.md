# Piattaforma di Gestione e Streaming Musicale

Applicazione desktop per la gestione, riproduzione e condivisione di brani musicali, testi e spartiti in formato PDF[cite: 3].
Progetto realizzato per il corso di Ingegneria del Software (Università degli Studi di Verona)[cite: 3].

## Funzionalità

### User
- Registrazione account con controllo dello stato di approvazione al login[cite: 3].
- Caricamento brani: inserimento metadati (titolo, autore, genere, anno), file audio MP3 obbligatorio, copertina e file PDF facoltativi[cite: 3].
- Riproduzione audio tramite player integrato e apertura dei documenti PDF associati[cite: 3].
- Ricerca in tempo reale dei brani filtrata per titolo e autore[cite: 3].
- Inserimento e gestione commenti (limite massimo di 200 caratteri)[cite: 3].
- Eliminazione dei propri brani e rimozione dei relativi commenti[cite: 3].
- Ripristino automatico dell'ultima traccia in ascolto alla riapertura dell'applicazione[cite: 3].

### Admin
- Account predefinito per la gestione generale della piattaforma[cite: 3].
- Pannello dedicato per approvare o rifiutare le registrazioni degli utenti[cite: 3].
- Moderazione globale con possibilità di eliminare qualsiasi brano (con rimozione fisica dei file da disco) e commento[cite: 3].

## Architettura e Design Pattern

Il software è sviluppato secondo il pattern Model-View-Controller (MVC) ed è organizzato tramite moduli Java (module-info.java)[cite: 3]:

- **Singleton:** impiegato in `Model` per lo stato globale e l'accesso ai repository, in `PlaybackManager` per la gestione dell'unico MediaPlayer attivo e in `DatabaseManager` per la connessione JDBC[cite: 3].
- **Factory Method:** implementato nella classe `ViewFactory` per centralizzare il caricamento dei file FXML, l'iniezione del controller e l'applicazione dello stile CSS globale[cite: 3].
- **Repository:** `UserRepository`, `SongRepository` e `CommentsRepository` incapsulano le operazioni sul database SQLite gestendo una cache in memoria per ridurre gli accessi a disco[cite: 3].
- **Observer:** utilizzo delle proprietà osservabili di JavaFX in `PlaybackManager` per aggiornare reattivamente la barra multimediale al cambio di stato o di traccia[cite: 3].

## Gestione Dati

- **Database:** SQLite (`data/database/musica.db`) interfacciato con driver JDBC e vincoli di chiave esterna `ON DELETE CASCADE`[cite: 3].
- **File System:** i file multimediali (.mp3, .pdf, .jpg) vengono salvati nelle sottocartelle di `data/` rinominandoli con l'ID numerico generato da database[cite: 3].

## Requisiti

- Java JDK 21 o superiore[cite: 3]
- JavaFX 21 (moduli controls, fxml, graphics, media)[cite: 3]
- SQLite JDBC[cite: 3]
- Apache Maven[cite: 3]

## Compilazione ed Esecuzione

Avvio da terminale tramite Maven Wrapper:

- Windows:
  .\mvnw.cmd clean javafx:run

- Linux / macOS:
  ./mvnw clean javafx:run

Avvio da IDE (IntelliJ IDEA / Eclipse):
1. Importare il progetto tramite il file pom.xml[cite: 3].
2. Eseguire la classe principale `univr.musica.Main` oppure lanciare il goal Maven `javafx:run`[cite: 3].

## Testing

Test di unità implementati con JUnit 5 nel package `univr.musica.model` per verificare la logica di dominio (UserTest, SongTest, CommentsTest)[cite: 3].

Esecuzione dei test:
./mvnw test
