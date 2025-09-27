# SWIFT XML PDF Processor

Transforme les messages SWIFT au format XML en fichiers PDF structurés et **faciles à lire**.

## Présentation
Ce projet propose une solution backend pour traiter les messages SWIFT au format XML et générer des **PDF user-friendly**.  
Il gère le **parsing**, le **mapping** et le traitement hiérarchique des nœuds XML, incluant sections, sous-sections et sous-sous-sections, tout en conservant une mise en page propre et professionnelle.

Le système est développé en **Java** avec **Spring Boot** pour la structure backend et **Apache PDFBox** pour la génération des PDF.
...

---

## Fonctionnalités
- **Parsing XML** : lecture et traitement efficace des messages SWIFT.  
- **Mapping des données** : conversion des éléments XML en labels compréhensibles grâce à un fichier de configuration `.properties`.  
- **Gestion hiérarchique** : support des sections, sous-sections et sous-sous-sections.  
- **PDF lisible** : génération de PDF bien formatés avec indentation, retour à la ligne automatique et structuration.  
- **Layout personnalisable** : marges, tailles de police, espacements et indentation configurables pour un rendu professionnel.

---

## Technologies utilisées
- **Backend** : Java 11+, Spring Boot  
- **XML Processing** : DOM Parser (`org.w3c.dom`), XPath  
- **Génération PDF** : Apache PDFBox  
- **Configuration** : JSON pour sections/sous-sections, fichier `.properties` pour le mapping XML → labels

---

## Exemple

**  XML SWIFT PACS.008 :**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<saa:DataPDU xmlns:saa="urn:swift:saa:xsd:saa.2.0">
    <saa:Header>
        <saa:Message>
            <saa:SenderReference>MSGID-0001</saa:SenderReference>
            <saa:MessageIdentifier>pacs.008.001.08</saa:MessageIdentifier>
            <saa:Format>MX</saa:Format>
            <saa:SubFormat>Output</saa:SubFormat>
            <saa:Sender>
                <saa:DN>ou=xxx,o=AAAAUS33XXX,o=swift</saa:DN>
                <saa:FullName>
                    <saa:X1>AAAAUS33XXX</saa:X1>
                </saa:FullName>
            </saa:Sender>
            <saa:Receiver>
                <saa:DN>ou=xxx,o=BBBBGB22XXX,o=swift</saa:DN>
                <saa:FullName>
                    <saa:X1>BBBBGB22XXX</saa:X1>
                </saa:FullName>
            </saa:Receiver>
        </saa:Message>
    </saa:Header>
</saa:DataPDU>

## PDF généré (exemple simplifié) :
```text
PACS.008.001.08
Sender Reference : MSGID-0001
Format : MX
SubFormat : Output

Sender : AAAAUS33XXX
Receiver : BBBBGB22XXX

...



## Structure du projet :
src/
 └── main/
     ├── java/com/hiba/        # Packages Java
     └── resources/            # XML, JSON et fichiers properties
output/                        # PDF généré

## Installation et utilisation:

1. Cloner le dépôt :

git clone git@github.com:hibachaabani/swift-xml-pdf-processor.git

2. Importer le projet dans IntelliJ comme projet Maven.

3. Vérifier que les fichiers de configuration sont présents dans resources/ :

   data.xml → données XML SWIFT

   Label-mapping.properties → mapping XML → labels

   Sections.json → structure des sections/sous-sections

4. Lancer le projet (main class : XmlToPdfGenerator) pour générer le PDF.

5. Le PDF sera créé dans le dossier output/ du projet avec le nom user_friendly.pdf.

## Licence

Ce projet est sous licence MIT, ce qui permet une utilisation libre tout en conservant la mention de l’auteur.
