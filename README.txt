README

Notre projet consiste  � impl�menter des commandes http et webdav. 
Un client Webdav sera n�cessaire pour faire des tests. 

I- Le client Webdav : 

	1) POSTMAN
Vous pouvez proc�der aux tests avec POSTMAN mais uniquement pour les m�thodes (GET, HEAD, PUT, DELETE, OPTIONS, COPY, PROPFIND). 
Pour l�avoir, t�l�charger le ici : https://www.getpostman.com/ puis installer le.

OU/ET

	2) RESTLET CLIENT (Recommand�)
Par contre toutes les m�thodes (GET, HEAD, PUT, DELETE, OPTIONS, COPY, MKCOL, MOVE, PROPFIND) pourrons �tre test� avec Restlet Client (c'est une extension de Chrome)
Vous pouvez l�avoir ici https://chrome.google.com/webstore/detail/restlet-client-rest-api-t/aejoelaoggembcahagimdiliamlcdmfm puis en cliquant sur ajouter � Chrome.
Certaines m�thodes comme PROPFIND n�y existent pas par d�faut.
Il faudra donc les ajouter en allant dans Settings, puis dans http, puis en cliquant sur New et en donnant le nom de la m�thode.
Il sera inutile de coch� � request body � sauf pour la m�thode PROPFIND.


II- Les tests

Avant le d�but de test, il est n�cessaire de lancer le server WebDav avec le fichier ServerWebDav.jar qui vous ai fourni.
IMPORTANT : pour arr�ter le Serveur WEBDAV, il est obligatoire de fermer la f�netre du serveur.

Remarque :
---------------------------------------------------------------------------------------------------------
Il est important de savoir qu�une requ�te peut se diviser en 2 parties (par ailleurs, la r�ponse aussi) : 
  - l�ent�te (HEADER) 
  - le corps (BODY)

Certaines m�thodes n�auront pas de BODY et d�autres devront en avoir. 
Il sera souvent n�cessaire que vous ajoutiez des �l�ments du HEADER par vous-m�me.
Le bouton SEND enverra la requ�te � notre serveur. 
L�URI que l�on souhaite envoyer devra toujours commencer par ceci : http://localhost:1234/ 
---------------------------------------------------------------------------------------------------------

IMPORTANT : Il est imp�ratif que le serveur soit lanc� (fen�tre ouverte) pendant toutes les r�quetes (� travers POSTMAN ou RESTLET CLIENT)

Voici donc comment vous allez pouvoir tester chaque m�thode (avec Postman et/ou Restlet Client) : 
On aura un dossier "test" � la racine du projet qui contiendra des dossiers et des fichiers avec lesquels on pourra faire les tests

�	GET :

Cette m�thode est utilis�e pour demander une ressource. 
Il faut ajouter l�URI : ce sera le chemin vers la ressource que l�on souhaitera r�cup�rer.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx

Si c�est un dossier elle renvoie le fichier � index.html � qui s�y trouve si elle existe et un fichier se trouvant dans le dossier sinon.
S�il ne trouve aucun fichier il ne renvoie rien. 
Si c�est un fichier, c�est celui-ci qui sera envoyer. 

Voici les codes r�ponses qu�elle peut renvoyer : 
200 OK : si la ressource demand�e existe
404 File Not Found : si elle n�existe pas

 
�	HEAD :

Cette m�thode demande des informations sur la ressource (sans demander la ressource elle-m�me). 
Elle renvoie uniquement un ent�te semblable � celui de la m�thode GET.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx

Voici les codes r�ponses qu�elle peut renvoyer : 
200 OK : si la ressource demand�e existe
404 File Not Found : si elle n�existe pas


�	DELETE :

Cette m�thode permet de supprimer une ressource du serveur. 

exemple URI : http://localhost:1234/test/ -> le dossier test sera supprimer avec son contenu
	      http://localhost:1234/test/test2/test3/hallo.docx -> hallo.docx sera supprimer

Voici les codes r�ponses qu�elle peut renvoyer : 
200 OK : si la ressource existe et que la suppression a �t� faite avec succ�s 
404 File Not Found : si elle n�existe pas
500 Internal Server Error : si une autre erreur se produit


�	PUT :

Cette m�thode ajoute une ressource sur le serveur. 
L'URI fourni est celui vers lequel on doit ajouter la ressource.
Il faudra ajouter la ressource que l'on souhaitera ajouter dans le BODY de la requ�te.
(Pour POSTMAN ::= cliquer sur : body -> binary -> S�lect. fichier )
(Pour RESTLET CLIENT ::= body -> chager "text" par "file" -> choose file )

exemple URI : http://localhost:1234/test/test2/test3/hallo.docx -> hallo.docx sera cr�er dans "test/test2/test3" et son contenu sera le contenu du fichier que vous aurez donnez.

Voici les codes r�ponses qu�elle peut renvoyer :
204 No Content : si la ressource existe d�j� ou si c�est un r�pertoire (exemple : http://localhost:1234/test/test2/test3 )
201 Created : si la ressource a bien �t� cr�er 
400 Bad Request : probl�me de type (par exemple si vous envoyer une image (hallo.png) avec cet URI http://localhost:1234/test/test2/test3/hallo.docx) 


�	OPTIONS :

Cette m�thode est utilis�e pour d�crire les options de communications avec la ressource vis�e.
Elle envoie 200 OK comme r�ponse et donne dans l�ent�te toutes les m�thodes que nous avons impl�ment� et que l�on peut donc utiliser.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx


�	COPY :

Cette m�thode copie une ressource d�un URI � un autre.
Il faut ajouter une "Destination" , un "Depth" et un "Overwrite" au HEADER. 
IMPORTANT : l'orthographe des cl�s (ex: Destination) est � respecter
(depth et overwrite sont facultatifs car ils ont des valeurs par d�faut si on ne les trouve pas dans la requ�te)

Pour ajouter ces ent�tes : 
Dans POSTMAN ::= cliquer sur Headers -> New key
Dans RESTLET CLIENT ::= add header

- la valeur de Destination sera l'URI de destination (en comman�ant par http://localhost:1234/)
- la valeur de Depth est soit "0" soit "infinity" (par defaut on a infinity)
	ce sera la profondeur de la copie
- la valeur de Overwrite est soit "F" soit "T" (par d�faut on a "F")
	Si une ressource existe d�j� � l'URI de destination et que l'ent�te Overwrite est "T", un DELETE est effectu� sur la ressource de destination avant l'ex�cution de la m�thode COPY. 
	Mais si l'ent�te Overwrite est r�gl� sur "F", la m�thode COPY �chouera.

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx

Voici les codes r�ponses qu�elle peut renvoyer :
204 No Content : la copie a r�ussit
409 Conflict : si les URI sont incorrects
412 Precondition Failed : si la m�thode �choue
403 Forbidden : si la source est �gale � la destination


�	MKCOL :

Cette m�thode cr�e des dossiers vides

exemple : http://localhost:1234/test/test2/test3/test4/ -> un dossier test4 sera cr�er dans /test/test2/test3/

Voici les codes r�ponses qu�elle peut renvoyer :
405 Method Not Allowed : si le dossier qu�on souhaite cr�er existe d�j�
409 Conflict : si les URI sont incorrects
201 Created : si le dossier a �t� correctement cr�er


�	MOVE :

Cette m�thode permet de d�placer une ressource d�un URI � un autre. 
Elle doit avoir une Destination et facultativement un Overwrite comme la m�thode COPY.

Voici les codes r�ponses qu�elle peut renvoyer :
204 No Content : le d�placement de la ressource a r�ussit
409 Conflict : si les URI sont incorrects
412 Precondition Failed : si la m�thode �choue
403 Forbidden : si la source est �gale � la destination


�	PROPFIND :

Cette m�thode reccup�re en XML les propri�t�s d'une ressource. 
Voici les propri�t�s que nous avons utilis� : "displayname", "lastmodified", "contenttype" et "contentlength" 

On pourra ajouter un "Deph" dont la valeur sera "0", "1" ou "infinity" (par d�faut infinity)

-------------------------------------------------------------------------------------------------------------
Dans POSTMAN ::= body -> raw
Dans RESTLET CLIENT ::= body -> text

exemple URI : http://localhost:1234/test/
	      http://localhost:1234/test/test2/test3/hallo.docx
-------------------------------------------------------------------------------------------------------------

Elle devra donc contenir un Body de cette forme (si on ajoute pas de body ce sera "allprop" par d�faut) : 
	* si on veut avoir toutes les propri�t�s 
<?xml version="1.0"?>
<a:propfind xmlns:a="DAV:">
<a:prop><a:allprop/></a:prop>
</a:propfind>
	* si on veut avoir juste le name
<?xml version="1.0"?>
<a:propfind xmlns:a="DAV:">
<a:prop><a:displayname/></a:prop>
</a:propfind>
	* si on veut avoir le name et la date de derni�re modification de la ressource par exemple
<?xml version="1.0"?>
<a:propfind xmlns:a="DAV:">
<a:prop><a:displayname/></a:prop>
<a:prop><a:lastmodified/></a:prop>
</a:propfind>




























