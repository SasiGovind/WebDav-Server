README

Notre projet consiste  à implémenter des commandes http et webdav. 
Un client Webdav sera nécessaire pour faire des tests. 

I- Le client Webdav : 

	1) POSTMAN
Vous pouvez procéder aux tests avec POSTMAN mais uniquement pour les méthodes (GET, HEAD, PUT, DELETE, OPTIONS, COPY, PROPFIND). 
Pour l’avoir, télécharger le ici : https://www.getpostman.com/ puis installer le.

OU/ET

	2) RESTLET CLIENT (Recommandé)
Par contre toutes les méthodes (GET, HEAD, PUT, DELETE, OPTIONS, COPY, MKCOL, MOVE, PROPFIND) pourrons être testé avec Restlet Client (c'est une extension de Chrome)
Vous pouvez l’avoir ici https://chrome.google.com/webstore/detail/restlet-client-rest-api-t/aejoelaoggembcahagimdiliamlcdmfm puis en cliquant sur ajouter à Chrome.
Certaines méthodes comme PROPFIND n’y existent pas par défaut.
Il faudra donc les ajouter en allant dans Settings, puis dans http, puis en cliquant sur New et en donnant le nom de la méthode.
Il sera inutile de coché « request body » sauf pour la méthode PROPFIND.


II- Les tests

Avant le début de test, il est nécessaire de lancer le server WebDav avec le fichier ServerWebDav.jar qui vous ai fourni.
IMPORTANT : pour arrêter le Serveur WEBDAV, il est obligatoire de fermer la fênetre du serveur.

Remarque :
---------------------------------------------------------------------------------------------------------
Il est important de savoir qu’une requête peut se diviser en 2 parties (par ailleurs, la réponse aussi) : 
  - l’entête (HEADER) 
  - le corps (BODY)

Certaines méthodes n’auront pas de BODY et d’autres devront en avoir. 
Il sera souvent nécessaire que vous ajoutiez des éléments du HEADER par vous-même.
Le bouton SEND enverra la requête à notre serveur. 
L’URI que l’on souhaite envoyer devra toujours commencer par ceci : http://localhost:1234/ 
---------------------------------------------------------------------------------------------------------

IMPORTANT : Il est impératif que le serveur soit lancé (fenêtre ouverte) pendant toutes les rêquetes (à travers POSTMAN ou RESTLET CLIENT)

Voici donc comment vous allez pouvoir tester chaque méthode (avec Postman et/ou Restlet Client) : 
On aura un dossier "test" à la racine du projet qui contiendra des dossiers et des fichiers avec lesquels on pourra faire les tests

•	GET :

Cette méthode est utilisée pour demander une ressource. 
Il faut ajouter l’URI : ce sera le chemin vers la ressource que l’on souhaitera récupérer.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx

Si c’est un dossier elle renvoie le fichier « index.html » qui s’y trouve si elle existe et un fichier se trouvant dans le dossier sinon.
S’il ne trouve aucun fichier il ne renvoie rien. 
Si c’est un fichier, c’est celui-ci qui sera envoyer. 

Voici les codes réponses qu’elle peut renvoyer : 
200 OK : si la ressource demandée existe
404 File Not Found : si elle n’existe pas

 
•	HEAD :

Cette méthode demande des informations sur la ressource (sans demander la ressource elle-même). 
Elle renvoie uniquement un entête semblable à celui de la méthode GET.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx

Voici les codes réponses qu’elle peut renvoyer : 
200 OK : si la ressource demandée existe
404 File Not Found : si elle n’existe pas


•	DELETE :

Cette méthode permet de supprimer une ressource du serveur. 

exemple URI : http://localhost:1234/test/ -> le dossier test sera supprimer avec son contenu
	      http://localhost:1234/test/test2/test3/hallo.docx -> hallo.docx sera supprimer

Voici les codes réponses qu’elle peut renvoyer : 
200 OK : si la ressource existe et que la suppression a été faite avec succès 
404 File Not Found : si elle n’existe pas
500 Internal Server Error : si une autre erreur se produit


•	PUT :

Cette méthode ajoute une ressource sur le serveur. 
L'URI fourni est celui vers lequel on doit ajouter la ressource.
Il faudra ajouter la ressource que l'on souhaitera ajouter dans le BODY de la requête.
(Pour POSTMAN ::= cliquer sur : body -> binary -> Sélect. fichier )
(Pour RESTLET CLIENT ::= body -> chager "text" par "file" -> choose file )

exemple URI : http://localhost:1234/test/test2/test3/hallo.docx -> hallo.docx sera créer dans "test/test2/test3" et son contenu sera le contenu du fichier que vous aurez donnez.

Voici les codes réponses qu’elle peut renvoyer :
204 No Content : si la ressource existe déjà ou si c’est un répertoire (exemple : http://localhost:1234/test/test2/test3 )
201 Created : si la ressource a bien été créer 
400 Bad Request : problème de type (par exemple si vous envoyer une image (hallo.png) avec cet URI http://localhost:1234/test/test2/test3/hallo.docx) 


•	OPTIONS :

Cette méthode est utilisée pour décrire les options de communications avec la ressource visée.
Elle envoie 200 OK comme réponse et donne dans l’entête toutes les méthodes que nous avons implémenté et que l’on peut donc utiliser.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx


•	COPY :

Cette méthode copie une ressource d’un URI à un autre.
Il faut ajouter une "Destination" , un "Depth" et un "Overwrite" au HEADER. 
IMPORTANT : l'orthographe des clés (ex: Destination) est à respecter
(depth et overwrite sont facultatifs car ils ont des valeurs par défaut si on ne les trouve pas dans la requête)

Pour ajouter ces entêtes : 
Dans POSTMAN ::= cliquer sur Headers -> New key
Dans RESTLET CLIENT ::= add header

- la valeur de Destination sera l'URI de destination (en commançant par http://localhost:1234/)
- la valeur de Depth est soit "0" soit "infinity" (par defaut on a infinity)
	ce sera la profondeur de la copie
- la valeur de Overwrite est soit "F" soit "T" (par défaut on a "F")
	Si une ressource existe déjà à l'URI de destination et que l'entête Overwrite est "T", un DELETE est effectué sur la ressource de destination avant l'exécution de la méthode COPY. 
	Mais si l'entête Overwrite est réglé sur "F", la méthode COPY échouera.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx

Voici les codes réponses qu’elle peut renvoyer :
204 No Content : la copie a réussit
409 Conflict : si les URI sont incorrects
412 Precondition Failed : si la méthode échoue
403 Forbidden : si la source est égale à la destination


•	MKCOL :

Cette méthode crée des dossiers vides

exemple : http://localhost:1234/test/test2/test3/test4/ -> un dossier test4 sera créer dans /test/test2/test3/

Voici les codes réponses qu’elle peut renvoyer :
405 Method Not Allowed : si le dossier qu’on souhaite créer existe déjà
409 Conflict : si les URI sont incorrects
201 Created : si le dossier a été correctement créer


•	MOVE :

Cette méthode permet de déplacer une ressource d’un URI à un autre. 
Elle doit avoir une Destination et facultativement un Overwrite comme la méthode COPY.

Voici les codes réponses qu’elle peut renvoyer :
204 No Content : le déplacement de la ressource a réussit
409 Conflict : si les URI sont incorrects
412 Precondition Failed : si la méthode échoue
403 Forbidden : si la source est égale à la destination


•	PROPFIND :

Cette méthode reccupère en XML les propriétés d'une ressource. 
Voici les propriétés que nous avons utilisé : "displayname", "lastmodified", "contenttype" et "contentlength" 

On pourra ajouter un "Deph" dont la valeur sera "0", "1" ou "infinity" (par défaut infinity)

-------------------------------------------------------------------------------------------------------------
Dans POSTMAN ::= body -> raw
Dans RESTLET CLIENT ::= body -> text

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx
-------------------------------------------------------------------------------------------------------------

Elle devra donc contenir un Body de cette forme (si on ajoute pas de body ce sera "allprop" par défaut) : 
	* si on veut avoir toutes les propriétés 
<?xml version="1.0"?>
<a:propfind xmlns:a="DAV:">
<a:prop><a:allprop/></a:prop>
</a:propfind>
	* si on veut avoir juste le name
<?xml version="1.0"?>
<a:propfind xmlns:a="DAV:">
<a:prop><a:displayname/></a:prop>
</a:propfind>
	* si on veut avoir le name et la date de dernière modification de la ressource par exemple
<?xml version="1.0"?>
<a:propfind xmlns:a="DAV:">
<a:prop><a:displayname/></a:prop>
<a:prop><a:lastmodified/></a:prop>
</a:propfind>




























