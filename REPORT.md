# App report description

In de Pathless app kunnen reizigers hun favorieten locaties toevoegen aan de Pathless map.</br>
Vervolgens kunnen zij foto's en een aantal regels tekst bijvoegen. Hiermee maken de gebruikers een visueel archief aan die laat zien welke onvergetelijke plekken zij gezien hebben.</br>

![Screenshot](doc/Screenshot_1x.jpeg)

# Technical design

![Screenshot](doc/DesignDocFinal.png)
</br>

### InputActivity
De 'MapActivity' is de homescreen van de app. Door op de floating action button te klikken komt de gebruiker in de 'InputActivity' om een nieuwe locatie toe te voegen. De edit text bovenin dit scherm is geconnect aan de 'PlaceAutocompleteAdapter' (OnCreate), deze adapter komt van een project uit de github van Google Samples. Als er op de 'add images button' wordt geklikt, komt de gebruiker in de gallerij. Nadat er een foto is gekozen wordt deze toegevoegd aan de 'storage' in Firebase met als file naam: de current time en het laatste deel van de uri. De foto heeft op dat moment een eigen url gekregen via firebase. Dit proces geldt voor elke toegevoegde foto. Op de 'add images button' is "Loading..." te zien totdat de foto succesvol is gepusht naar de Firebase storage. De Uri van de foto's worden daarnaast lokaal toegevoegd aan een array. Deze wordt gebruikt om de toegevoegde foto's in de 'InputActivity' te laten zien (method: showImage). De 'ImageSliderAdapter' wordt dan geconnect aan die array met foto's, waardor de gebruiker door de foto's kan swipen. Ook wordt de viewPager geconnect aan de 'CirclePagerIndicator'. Dit zorgt voor de bolletjes onder de foto die aangeven hoeveel er te zien zijn. Dan kan er nog een stuk tekst tegoevoegd worden. Als er vervolgens op de 'check button' geklikt is, wordt de locatienaam, description, coordinaten en foto's (als URL in een array) samengevoegd in een 'Post object' (Post.java) en daarna toegevoegd in de Firebase database onder de user id van de telefoon.
</br>

### MapActivity
</br>

### DetailActivity
</br>
