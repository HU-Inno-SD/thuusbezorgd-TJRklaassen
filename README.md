# Thuusbezorgd

## Inleiding

Welkom bij de Thuusbezorgd Opdracht. In deze opdracht gaan jullie een basic applicatie in stukken verdelen zodat verschillende
teams van developers hier onafhankelijk aan door kunnen ontwikkelen.

Deze repository bestaat uit 3 branches:

1: monoliet-geen-security: Hier staat een more-or-less werkende applicatie als 1 geheel. Dat is het ding dat je in stukken moet
hakken
2: leeg: Hier staat een bijna-lege repository met wat Maven en Docker gefrutsel klaar
3: monoliet (U BENT NU HIER): hier is ook Spring Security gebruikt. Dat levert zo veel meer technisch geneuzel op dat het optioneel
, maar wel interessant kan zijn om mee te nemen.

Het is redelijk goed te doen om met deze opdracht ook de SQL doelen van eerder te behalen. Dus als je de SQL opdracht hebt
gedaan, gebruik bijv. MongoDB en Neo4J als databases, en als je juist de NoSQL opdracht hebt gedaan dan kun je wat extra
liefde in de PostGres mapping stoppen.

Je mag helemaal zelf weten wat je prettig vindt werken natuurlijk, maar als ik zelf deze opdracht zou maken, dan zou ik
2 checkouts van de code naast elkaar zetten. Ik zou in de ene op de 'leeg' branch gaan zitten, en in de andere op een van
de monoliet branches (start zonder security zou mijn tip zijn). Vervolgens zou ik langzaam code migreren van de ene naar de andere.

Het (ook goede) alternatief is om juist een branch te gaan herschrijven. Dat zou in een professionele context het voordeel
hebben dat je een beter versie-beheer log hebt van hoe de transformatie is gebeurd. Ik zou het persoonljk lastiger vinden
om te doen.

## Opstarten

Het is handig om een `application-dev.properties` bestand inrichten aan de hand van het example bestand. Voor de security versie
moet je een OAuth koppeling met Github hebben. Het `application-dev.properties` bevat de details hoe dit precies moet.

Verder heb je een Postgres instantie nodig, of moet je het H2 spring-profile aanzetten (kan in de run-settings van IntelliJ).
Voor PostGres en RabbitMQ staan een Docker-Compose klaar.

Bij het opstarten zou ik aanraden om het 'dev' profile te gebruiken. Dat doet 3 dingen:

1) Je start elke keer met een verse database
2) Je krijgt Dummy data
3) Er start een DummyClient die elke seconde een bestelling doet

## Achtergrond

Deze applicatie is niet bedoeld als 'goed voorbeeld', maar gewoon een basis naieve opzet van hoe je met wat
tweedejaars-skills en wat haast toch best een eindje kan komen.
Basically heb ik gewoon altijd 'het eerste idee dat in me op kwam' geïmplementeerd en als ik me later bedacht dat het
onhandig was, heb ik toch koppig doorgedrukt (want het handiger maken is juist jullie taak).

Slechts op een paar plekken heb ik het willens en wetens de eerste keer 'verkeerd' gedaan. Uiteraard heb ik qua modules
en scheiding alles zoveel mogelijk 'op één hoop gegooid', anders is er zo weinig uit elkaar te trekken. DDD heb ik niet
intensief gebruikt, zodat je evt. deze opdracht ook daarvoor kan gebruiken.

Unit Tests waren gebruikt tijdens het maken van de applicatie, maar deze heb ik uit de start-repo verwijderd. Unit-tests
zijn essentieel als je een werkende applicatie wil migreren. Ze leveren een beetje wrijving als je een applicatie herschrijft,
maar maken dat ruimschoots goed als je uiteindelijk wilt checken of alle features goed zijn doorgekomen. Aangezien deze
schoolopdracht geen werkende productie-applicatie is ligt die verhouding wat mij betreft anders.
