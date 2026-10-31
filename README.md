# SLOVIGO — Učimo pisati kroz igru

Izvorna nativna Android aplikacija za pisanje 30 slova hrvatske abecede.
Bez oglasa, prijave, analitike ili pristupa internetu. Minimalna verzija Android 7 (API 24).

## Tehnologije
- Kotlin / Jetpack Compose / Material 3
- Android Gradle Plugin 9.4.1, Gradle 9.6.0, JDK 17
- Compose BOM 2026.09.00
- compileSdk / targetSdk 36; minSdk 24
- Adaptivni raspored: mali mobiteli, veliki mobiteli, tableti, portret i pejzaž

## Izgradnja
Otvoriti u Android Studiju s JDK 17 i instaliranim SDK 36, ili u terminalu s instaliranim Gradle 9.6:

```bash
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleRelease :app:bundleRelease
```

GitHub Actions automatski testira svaki PR i za svaki commit na `main` izrađuje novo izdanje.
Bez pohranjenog certifikata izdanje je **testno** i uključuje instalabilni debug APK te nepotpisani AAB.
Za distribuciju putem Google Playa treba postaviti četiri GitHub Actions tajne:
`SLOVIGO_KEYSTORE_BASE64` (base64 originalne JKS datoteke),
`SLOVIGO_STORE_PASSWORD`, `SLOVIGO_KEY_ALIAS`, `SLOVIGO_KEY_PASSWORD`.
Ni lozinke ni ključeve nemojte spremati u Git. Obavezno čuvajte trajnu privatnu sigurnosnu kopiju ključa.

## Privatnost
Aplikacija čuva napredak lokalno na uređaju. Ne prikuplja osobne podatke, ne traži INTERNET dopuštenje i nema vanjsku telemetriju. Zvuk koristi sustavnu uslugu TextToSpeech ako je instalirana.

## Napomena o dizajnu
Dizajn se temelji na originalnom SLOVIGO konceptu. Za stvarnu provjeru poklapanja 1:1 potrebno je testiranje na fizičkim uređajima, usporedba screenshotova i završni dizajnerski pregled.
Vidi [PRIVACY.md](PRIVACY.md) za objašnjenje obrade podataka.
