# Programmeerproject - Pathless

Name: Jeffrey Chong</br>
Student nmr.: 11304669</br>
Course: Programmeerproject (mprog), UvA</br>

### De Pathless app.
Het ontdekken en vastleggen van nieuwe plekken op de wereld spreekt veel mensen aan.</br>
Men wilt vaak niet meer alleen naar een resort of pretpark. Maar juist het zoeken naar het mooiste uitzicht,</br>
het ontmoeten van de lokale bevolking en het leren van nieuwe culturen wordt bijvoorbeeld steeds belangrijker voor reizigers.</br>

In de Pathless app kunnen reizigers tijdens de reis hun favorieten locaties toevoegen aan de map.</br>
Vervolgens kunnen zij foto's en een aantal regels tekst bijvoegen die voor hun een verhaal verteld over die plek</br>
of simpelweg laat zien hoe mooi die plek is.</br>
Hiermee maken de gebruikers een visueel archief aan die laat zien welke onvergetelijke plekken zij gezien hebben.</br>

### Visual sketch.
In het bestand 'docs' is de visual sketch van de Pathless app opgeslagen in een PNG bestand.</br>
Hierin zijn drie activities te zien die in ieder geval in de app komen te staan.</br>
Onder elke activity is een korte beschrijving van de basale functionaliteiten.<br>

### Benodigdheden.
- Google maps activity (homescreen):</br>
  Hierin is het in ieder geval belangrijk dat de vlaggen mee veranderen qua grootte als er in- of uitgezoomd wordt in de map.</br>
  Daarnaast moet er een nieuwe vlag toegevoegd worden op de map als de gebruiker een nieuwe locatie heeft toegevoegd.
  
- Swipable pictures:</br>
  Er moet op de foto's, die te zien zijn in 'the detailscreen', geswiped kunnen worden. Daarmee kunnen de gebruikers alle foto's bekijken.
  De drie bolletjes onder de foto geven aan of je bij de eerste foto (alleen eerste bolletje wit), de laatste foto (alleen laatste
  bolletje wit) of op een foto ergens daartussen bent (alleen middelste booletje wit).</br>

- Add pictures (pop up, SQL):</br>
  In 'the inputscreen' moet het mogelijk zijn om foto's toe te voegen die uit de galerij van de gebruiker komt.</br>
  Als er een foto gekozen wordt moet dit samen met de loactie gegevens en tekst opgeslagen worden in een database (SQL).</br>

- Add location:</br>
  Er zijn in dit concept twee opties om een locatie toe tevoegen.</br>
  Afhankelijk van de moeilijkheid wordt 1 van de twee gekozen.</br>

  De eerste optie is een textbox in 'the inputscreen' waarbij de gebruiker zelf de locatie typt en ondertussen worden er suggesties
  laten zien. De gebruiker kan dan èèn van de suggesties kiezen. Dit concept is te zien in de visual sketch.</br>

  De tweede optie is dat de gebruiker niet zelf een locatie mag kiezen, maar dat altijd de huidige locatie gebruikt moet worden. Als de
  gebruiker niet de GPS aan heeft staan, komt er een pop up waarop staat dat eerst de gps aan moet zijn om door te gaan.
