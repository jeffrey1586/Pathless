# Design document - Pathless
De volgende sketch geeft de uitgebreide UI weer van de Pathless app.</br>
De achterliggende connecties tussen de activities en functionaliteiten zijn hier weergegeven.</br>
![Screenshot](doc/DesignDoc.png)

| Class LocationEntry | EntryDatabase (SQLite) |
| ------------------- | ---------------------  |
| coordinates         | float,float            |
| pictures            | Bitmap                 |
| description         | text                   |

### API, Classes.
- Google maps API (homescreen)
- ViewPager Class voor de swipe functionaliteit van de foto's (detailscreen)