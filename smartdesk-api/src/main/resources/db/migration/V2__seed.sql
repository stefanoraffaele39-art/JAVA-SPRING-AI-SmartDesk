-- =====================================================================
-- SmartDesk - V2: dati di esempio
-- Servono per provare subito le API (Swagger/Postman) dopo l'avvio.
-- Gli id NON sono scritti a mano: li genera IDENTITY. I collegamenti
-- usano sottoquery su campi unici (email, nome categoria, titolo),
-- cosi' la sequenza degli id resta allineata.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Utenti: 3 clienti e 2 agenti
-- ---------------------------------------------------------------------
INSERT INTO users (full_name, email, role, created_at) VALUES
    ('Mario Rossi',     'mario.rossi@example.com',     'CUSTOMER', now() - INTERVAL '30 days'),
    ('Giulia Bianchi',  'giulia.bianchi@example.com',  'CUSTOMER', now() - INTERVAL '25 days'),
    ('Luca Verdi',      'luca.verdi@example.com',      'CUSTOMER', now() - INTERVAL '20 days'),
    ('Anna Neri',       'anna.neri@smartdesk.it',      'AGENT',    now() - INTERVAL '60 days'),
    ('Paolo Gialli',    'paolo.gialli@smartdesk.it',   'AGENT',    now() - INTERVAL '60 days');

-- ---------------------------------------------------------------------
-- Categorie
-- ---------------------------------------------------------------------
INSERT INTO categories (name, description) VALUES
    ('Account',      'Accesso, password, dati del profilo'),
    ('Fatturazione', 'Pagamenti, fatture, rimborsi'),
    ('Tecnico',      'Malfunzionamenti ed errori del servizio'),
    ('Altro',        'Richieste che non rientrano nelle altre categorie');

-- ---------------------------------------------------------------------
-- Ticket: uno per ogni stato, con priorita' diverse
-- ---------------------------------------------------------------------
INSERT INTO tickets (title, description, status, priority, author_id, assignee_id, category_id, created_at, updated_at, closed_at) VALUES
    ('Non riesco ad accedere',
     'Da ieri il login restituisce "credenziali non valide" anche dopo il reset della password.',
     'OPEN', 'HIGH',
     (SELECT id FROM users WHERE email = 'mario.rossi@example.com'),
     NULL,
     (SELECT id FROM categories WHERE name = 'Account'),
     now() - INTERVAL '2 days', now() - INTERVAL '2 days', NULL),

    ('Addebito doppio sulla carta',
     'Il pagamento dell''abbonamento di novembre risulta addebitato due volte.',
     'IN_PROGRESS', 'URGENT',
     (SELECT id FROM users WHERE email = 'giulia.bianchi@example.com'),
     (SELECT id FROM users WHERE email = 'anna.neri@smartdesk.it'),
     (SELECT id FROM categories WHERE name = 'Fatturazione'),
     now() - INTERVAL '5 days', now() - INTERVAL '1 day', NULL),

    ('Errore 500 durante l''esportazione',
     'Esportando i report in CSV compare una pagina di errore 500.',
     'IN_PROGRESS', 'MEDIUM',
     (SELECT id FROM users WHERE email = 'luca.verdi@example.com'),
     (SELECT id FROM users WHERE email = 'paolo.gialli@smartdesk.it'),
     (SELECT id FROM categories WHERE name = 'Tecnico'),
     now() - INTERVAL '4 days', now() - INTERVAL '3 days', NULL),

    ('Cambio indirizzo email',
     'Vorrei associare all''account un nuovo indirizzo email.',
     'RESOLVED', 'LOW',
     (SELECT id FROM users WHERE email = 'mario.rossi@example.com'),
     (SELECT id FROM users WHERE email = 'anna.neri@smartdesk.it'),
     (SELECT id FROM categories WHERE name = 'Account'),
     now() - INTERVAL '10 days', now() - INTERVAL '8 days', NULL),

    ('Richiesta fattura elettronica',
     'Mi serve la fattura di ottobre in formato elettronico.',
     'CLOSED', 'LOW',
     (SELECT id FROM users WHERE email = 'giulia.bianchi@example.com'),
     (SELECT id FROM users WHERE email = 'paolo.gialli@smartdesk.it'),
     (SELECT id FROM categories WHERE name = 'Fatturazione'),
     now() - INTERVAL '15 days', now() - INTERVAL '12 days', now() - INTERVAL '12 days'),

    ('Suggerimento: tema scuro',
     'Sarebbe utile avere un tema scuro nell''interfaccia.',
     'OPEN', 'LOW',
     (SELECT id FROM users WHERE email = 'luca.verdi@example.com'),
     NULL,
     (SELECT id FROM categories WHERE name = 'Altro'),
     now() - INTERVAL '1 day', now() - INTERVAL '1 day', NULL);

-- ---------------------------------------------------------------------
-- Commenti: scambi tra cliente e agente sui ticket gia' presi in carico
-- ---------------------------------------------------------------------
INSERT INTO comments (content, ticket_id, author_id, created_at) VALUES
    ('Ciao Giulia, abbiamo verificato il doppio addebito e avviato il rimborso.',
     (SELECT id FROM tickets WHERE title = 'Addebito doppio sulla carta'),
     (SELECT id FROM users WHERE email = 'anna.neri@smartdesk.it'),
     now() - INTERVAL '1 day'),

    ('Grazie! In quanti giorni arriva il rimborso?',
     (SELECT id FROM tickets WHERE title = 'Addebito doppio sulla carta'),
     (SELECT id FROM users WHERE email = 'giulia.bianchi@example.com'),
     now() - INTERVAL '20 hours'),

    ('Stiamo riproducendo il problema: puoi indicarci il browser che usi?',
     (SELECT id FROM tickets WHERE title = 'Errore 500 durante l''esportazione'),
     (SELECT id FROM users WHERE email = 'paolo.gialli@smartdesk.it'),
     now() - INTERVAL '3 days'),

    ('Fatto: ora puoi accedere con il nuovo indirizzo email.',
     (SELECT id FROM tickets WHERE title = 'Cambio indirizzo email'),
     (SELECT id FROM users WHERE email = 'anna.neri@smartdesk.it'),
     now() - INTERVAL '8 days'),

    ('Ti abbiamo inviato la fattura via PEC. Chiudo il ticket.',
     (SELECT id FROM tickets WHERE title = 'Richiesta fattura elettronica'),
     (SELECT id FROM users WHERE email = 'paolo.gialli@smartdesk.it'),
     now() - INTERVAL '12 days');
