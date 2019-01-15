# Android App Studio Style Guide
### By Corné, Daan, Govert, Jeffrey

## Introductie
Deze style guide kan onderverdeeld worden in stijl met betrekking tot de code en de UI.
Hiervoor zijn aparte uitgangspunten die beide behandeld worden.

## Code
Er zijn in de industrie twee belangrijke bronnen voor de style Guide ([Google](https://google.github.io/styleguide/javaguide.html) en [Oracle](https://www.oracle.com/technetwork/java/codeconvtoc-136057.html)). Een samenvatting en samenvoeging van beide wordt gegeven op een [Github](https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/styleguide.md)-pagina, die als basis voor onze afspraken is genomen.

## User Interface
Design voor de User Interface wordt beschreven op de website van [Android](https://developer.android.com/design/). Hierop zijn diverse links te vinden naar [materials.io](https://material.io/), ,de huidig geldende standaarden met betrekking to de User Interface. Dit type design wordt op de website onderverdeeld in een zestal componenten:
* [Layout](https://material.io/design/layout/understanding-layout.html)
* [Style](https://material.io/guidelines/style/color.html)
* [Animation](https://material.io/guidelines/motion/material-motion.html)
* [Components](https://material.io/guidelines/components/bottom-navigation.html)
* [Patterns](https://material.io/guidelines/patterns/confirmation-acknowledgement.html)
* [Usability](https://material.io/guidelines/usability/accessibility.html)

Daarnaast staat er nog een soort checklist met de naam [Core app quality](https://developer.android.com/docs/quality-guidelines/core-app-quality). Dit is een soort laatste controle om te kijken of de app voldoet aan de standaarden die vanuit Android gewenst zijn.

## Afspraken
* Boven iedere comment een witregel
* Use of proper scope
* Variable declareren waar het echt nodig is, dus zo laat mogelijk
* Iedere functie heeft een comment die het doel van de functie beschrijft
* Alle namen van variabelen zijn in camelCase.
* DRY code, dus herhalende stukken code incapsuleren in een functie
* Indentatie is 4 spaties
* De app is goed werkzaam op ieder formaat telefoon
* Landscape modus moet op ieder scherm van de app te gebruiken zijn
* De materials.io standaarden worden zo veel mogelijk als basis genomen voor het ontwerp van de app.
* De back button moet je altijd terug sturen naar een relevante pagina, dus zo nodig een activity finishen.
