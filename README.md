Copyright: Mihai Cherechesu

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
IntelliTherm ##################################################################
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Table of contents:
- Idee generala
- Prezentarea claselor si metodele aferente

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
###############################################################################
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1) Idee generala
	Nucleul a constat in colectarea unor date intr-o maniera 
organizata si structurarea lor cu ajutorul conceptului de statistica Time Series pentru a facilita accesul si interpretarea.	

	Pipeline:
	a) Citirea inputului din therm.in
	b) Configurarea camerelor cu device-uri si setarea parametrilor globali
	c) Inregistrarea temperaturii / umiditatii prin device-urile instalate
	   si stocarea lor in structuri de date din JFC.
	d) Pornirea secventiala a centralei (daca este permis)
	e) Setarea secventiala a temperaturii globale
	f) Afisarea inregistrarilor (LIST) dintr-un interval specific

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
###############################################################################
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

2) Prezentarea claselor
	Clasele proiectului IntelliTherm:
	a) DataLoader
	b) Room
	c) Device
	d) ValueComparator
	e) HeatSystem
	f) StandardFunctions
	g) StandardTokenIO


a) DataLoader.main ~~~~~~~~~~~~~~~~~~~~~~~~~
	- main(): In file reading.
	- loader(): Incarca parametrii globali si numarul de camere ce vor avea 
 device-uri pentru inregistrare.
	- runner(): Ruleaza functiile sistemului de incalzire.


b) Room ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	- Salveaza datele reprezentative unei camere


c) Device ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	- Salveaza datele reprezentative unui device dintr-o camera
	
	Aici se salveaza datele inregistrate prin senzorii de temperatura si 
umiditate: records, series, humiditySeries si humidityRecords.
	
	- records: Am decis sa nu ma folosesc de niciun algoritm de sortare
comun, precum BubbleSort sau QuickSort pentru a sorta datele tinute in buckets
si am plecat din preambul cu ideea de a folosi puterea interfetei SortedMap, pe
care o implementeaza structura din JCF, TreeMap. Records, in ambele situatii
(humidity sau temperature) a avut rolul unui intermediar suport.
		   El a stocat, la fiecare pas, orice inregistrare din sistem,
iar in acelasi timp a transferat in buckets, secvential, fiecare inregistrare
pentru a pastra cerinta valida (stocarea in buckets de 1h, in ordine crescatoa-
re). 
		   Transferul a fost realizat cu functia specifica TreeMap, 
subMap(low, high).
		   Series a avut pre-definite 24 buckets cu key-urile calculate in functie de timestamp-ul de referinta (am scazut de 24 de ori 3600 de sec).
		   La fiecare citire a unei intrari de temperatura (OBSERVE) sau de umiditate (OBSERVEH), s-a refacut transferul datelor in buckets. Dupa folo-
 sirea functiei subMap() pe keys calculate conform timestamp-ului citit (i.e.
 cele care marginesc timestamp-ul citit), mapa se updata cu intrarea nou-stocata in records.
	
		   Pana la pasul curent, am reusit sa obtinem doar temperaturiledintr-o ora specifica, ordonate aleatoriu, insa crescator dupa timestamp. Pentrua stoca temperaturile in buckets de o ora, crescator dupa value (valoarea temperaturii), am folosit tot un TreeMap, care foloseste un comparator ValueComparator. El primeste keys (dupa care ar sorta natural TreeMap-ul), insa mapeaza keys lavalorile lor din map si le compara pe acestea. Valoarea acestuia de return e -13, in caz ca o cheie mapeaza la null.
		   Dupa folosirea putAll in noul TreeMap ce sorteaza dupa values, stocam mapa in buckets (time series) la key-ul lowerBound.
		
	

e) HeatSystem ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


	- Sunt implementate metodele principale ale sistemului de incalzire:
		list(), temperature(), trigger(), observe()


	- Singura implementare care poate avea detalii interesante este list():
implementarea temei a stocat permanent stamp-urile de referinta (key-urile) 
buckets in ordine crescatoare si datele inregistrate stocate in buckets 
(temp, humid) tot in ordine crescatoare, insa afisarea lor se face de la 
ora cea mai tarzie (invers). La LIST am folosit tot un TreeMap care foloseste
Comparator.reverseOrder() pentru a afisa corect datele.

	In linia: 
	reversed.putAll(rooms.get(key).getDevice().getSeries().subMap(lower,
upper) ,
	se face mai intai subMapping la TreeMap-ul referentiat de key-ul din
series, iar apoi se face punerea in ordine inversa dupa timestamp-uri in 
map-ul reversed.
