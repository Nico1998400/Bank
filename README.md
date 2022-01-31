# BANKING ASSIGNMENT

Uppgift

Skapa en ny bank, med hjälp av de kraven som finns via automatiserade tester.
Skapa klasser med metoder som uppfyller den logik som behövs för att testerna ska bli
godkända.

Genomförande

Uppgiften genomförs med TDD (test driven utveckling). Det vill säga, fallerande tester först
och sedan implementation av egenskaperna, så att testerna blir gröna. Alla tester är röda
och “trasiga” till en början. Genom att skriva rätt logik i metoderna som behöver skapas ska
testerna istället bli gröna och “hela”.
Forka projekt
https://gitlab.com/sensera-education-public/banking


Tekniker som skall användas
● Java 17
● Streams
● Objektorientering
● Funktionell programmering
● Metoder med inte mer än 7 rader kod
● Korrekt namngivning av metoder, fält, parametrar och klasser
● Minst ett pattern (namngivet med kommentar)


Kod

● Följ kodstandard https://google.github.io/styleguide/javaguide.html
● All kod skall vara lättläst och lätt att följa och lätt att förstå.
● Alla tester skall ha 100& code coverage

Vad du behöver skapa

En mapp under “se.sensera.banking” som ska ha service klasser i sig. Du ser på Interfaces
vilka klasser som behöver skapas. Om den heter Service ska den ha metoder i sig. Och du
vet vilka metoder som behöver skapas samt vilken logik de behöver innehålla genom att
lösa testen. Du kan läsa mer om hur du går tillväga i nästa del.

Test består av tre delar med rubriker för vad koden i de olika delarna kollar.

//Given
Koden på raderna under står för vad som finns.

//When
Koden under visar vad det är som behöver skapas.

//Then
Koden under visar vilken logik som behöver finnas i metoden från When.
