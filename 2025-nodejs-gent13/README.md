# 2025-nodejs-gent13

# _teamleden_

| Name             | GitHub username |
| ---------------- | --------------- |
| Ali Aydinlioglu  | AliAydinlioglu  |
| Oguz Aydinlioglu | OguzAydinlioglu |
| Kamil Urtnowski  | kamilehh        |
| Seppe Dornon     | seppedornon     |
| Andrej Bianco    | ABianco03       |

## Vereisten

-   [NodeJS](https://nodejs.org)
-   [Yarn](https://yarnpkg.com)
-   [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)

## Opstarten

### Dependencies installeren

-   Kloon de repository en installeer de afhankelijkheden.

-   Schakel dev dependcies niet uit als u gemakkelijk met TypeScript wilt werken en testen wilt uitvoeren.

```sh
git clone https://github.com/HoGentProjectenII/2025-nodejs-gent13.git
cd  2025-nodejs-gent13
yarn install
```

### MySQL

Maak een MySQL-server aan met een wachtwoord en start de server.

### Maak `.env` bestanden

Maak in de _root map_ een bestand aan met de naam `.env`, met de volgende inhoud:

```sh
# Genereer gewoon een lang en sterk wachtwoord aan.
JWTSECRET=
# Prisma
DATABASE_URL=PINNED IN DISCORD #DEVELOPMENT
```

Als je de testsuite ook wilt gebruiken, maak dan een ander bestand aan met de naam `.env.test`:

```sh
# Genereer gewoon een lang en sterk wachtwoord aan.
JWTSECRET=
# Prisma
DATABASE_URL=PINNED IN DISCORD #DEVELOPMENT
```

### Startup

#### Normale startup

Compileer TS naar JS en voer het uit met de aangepaste opdracht:

```sh
yarn tsc
yarn startJS
```

#### Developer start-up

Als u de code met TypeScript wilt uitvoeren, gebruik dan de volgende commando:

```
yarn start
```

#### Reset

Als u hetzelfde DB-schema gebruikt voor TypeScript en JavaScript, zal het migratieproces mislukken. Reset of verwijder de DB of stel verschillende schema namen in in de configuratiebestanden van de gecompileerde JS. Dit wordt veroorzaakt door knextables in DB.

Je kunt de database resetten door "reset" in te voeren aan het einde van de startopdrachten:

-   TS:

    ```
    yarn start reset
    ```

    Of

-   JS:
    ```
    yarn startJS reset
    ```

Vergeet niet dat, tenzij je wijzigingen aanbrengt, beide talen standaard hetzelfde schema proberen te verbinden (en dus ook te resetten).

## Testen

Voer de tests uit met het volgende commando:

```
yarn test
```

De tests moeten worden uitgevoerd op een lege DB (sommige tests zijn zoektests die afhankelijk zijn van het aantal items). Om deze reden zal het testcommando een nieuw schema aanmaken voor testdoeleinden.

## API Calls

### Authenticatie

- `POST /api/auth/register`: Registreer een nieuwe gebruiker
- `POST /api/auth/login`: Login een gebruiker

### Health

- `GET /api/health/ping`: Haalt een bericht op dat "pong" zegt
- `GET /api/health/version`: Haalt de huidige versie van de API op

### KPI's

- `GET /api/kpis`: Haalt de Key Performance Indicators op

### Machines

- `POST /api/machines`: Maak een nieuwe machine aan
- `GET /api/machines`: Haal alle machines op
- `GET /api/machines/:id`: Haal details van een specifieke machine op
- `PUT /api/machines/:id`: Werk de gegevens van een machine bij
- `DELETE /api/machines/:id`: Verwijder een specifieke machine

### Meldingen

- `POST /api/meldingen`: Maak een nieuwe melding aan
- `GET /api/meldingen`: Haal alle meldingen op
- `GET /api/meldingen/:id`: Haal details van een specifieke melding op
- `PUT /api/meldingen/:id`: Werk de gegevens van een melding bij
- `DELETE /api/meldingen/:id`: Verwijder een specifieke melding

### Onderhoud

- `POST /api/onderhoud`: Maak een nieuwe onderhoudsregistratie aan
- `GET /api/onderhoud`: Haal alle onderhoudsregistraties op
- `GET /api/onderhoud/:id`: Haal details van een specifieke onderhoudsregistratie op
- `PUT /api/onderhoud/:id`: Werk de gegevens van een onderhoudsregistratie bij
- `DELETE /api/onderhoud/:id`: Verwijder een specifieke onderhoudsregistratie

### Sites

- `POST /api/sites`: Maak een nieuwe site aan
- `GET /api/sites`: Haal alle sites op
- `GET /api/sites/:id`: Haal details van een specifieke site op
- `PUT /api/sites/:id`: Werk de gegevens van een site bij
- `DELETE /api/sites/:id`: Verwijder een specifieke site

### Gebruikers

- `GET /api/users/me`: Haal je accountinformatie op
- `GET /api/users`: Haal alle gebruikers op
- `GET /api/users/:id`: Haal de accountinformatie van een specifieke gebruiker op
- `PUT /api/users/me`: Werk je eigen accountinformatie bij
- `PUT /api/users/:id`: Werk de accountinformatie van een specifieke gebruiker bij
- `DELETE /api/users/:id`: Verwijder een specifieke gebruikersaccount
