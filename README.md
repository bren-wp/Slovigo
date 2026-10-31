<div align="center">

<img src="docs/branding/slovigo-mark.svg" alt="SLOVIGO — ikona aplikacije" width="120" height="120" />

# SLOVIGO
### Učimo pisati kroz igru

**Vesela Android učionica u kojoj svako slovo postaje mala pustolovina.**

[![Android](https://img.shields.io/badge/Android-7.0%2B-168D99?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-6C84E8?logo=kotlin&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Offline](https://img.shields.io/badge/Radi-bez%20interneta-168D99)](#privatnost-i-sigurnost)
[![GitHub Release](https://img.shields.io/github/v/release/bren-wp/Slovigo?include_prereleases&label=izdanje)](https://github.com/bren-wp/Slovigo/releases)
[![Android CI](https://github.com/bren-wp/Slovigo/actions/workflows/android.yml/badge.svg)](https://github.com/bren-wp/Slovigo/actions/workflows/android.yml)

**[Preuzmi testnu verziju](https://github.com/bren-wp/Slovigo/releases)** · **[Pregledaj izvorni kod](https://github.com/bren-wp/Slovigo)** · **[Privatnost](PRIVACY.md)**

</div>

---

## ✨ Učenje koje izgleda kao igra

**SLOVIGO** je hrvatska nativna Android aplikacija za vježbanje pravilnog pisanja velikih i malih tiskanih slova. Dijete prati označene poteze prstom, uči redoslijed crtanja i vidi vlastiti napredak — bez oglasa i bez korisničkog računa.

| 🔤 Hrvatska abeceda | ✍️ Pisanje prstom | ⭐ Pohvale i napredak |
|:--:|:--:|:--:|
| Svih 30 slova, uključujući Č, Ć, Đ, Š, Ž, DŽ, LJ i NJ | Smjerovi i početne točke poteza | Lokalno praćenje naučenih slova |

## 📱 Stvarni prikazi i kvaliteta

Splash (01), Početna (02), Velika tiskana slova (03) i Mala tiskana slova (04) imaju zasebne responzivne Compose implementacije za mobitele i tablete. **Podudarnost 1:1 još nije izmjerena ni potvrđena usporedbom sa stvarnim prikazom.**

Prethodne PNG snimke emulatora bile su **nevaljane** jer su prikazivale Androidov ANR (`Process system isn't responding`) umjesto SLOVIGO aplikacije. Iz marketinške prezentacije uklonjene su njihove poveznice. Valjane snimke mogu se objaviti tek kada prođu stvarnu UI provjeru; stari PNG-ovi u repozitoriju nisu dokaz uspješnog prikaza.

[Pokreni opcionalnu provjeru emulatora](https://github.com/bren-wp/Slovigo/actions/workflows/screenshots.yml) · [Android CI](https://github.com/bren-wp/Slovigo/actions/workflows/android.yml)

## 🎨 Vizualni identitet

| Boja | HEX | Namjena |
|:--|:--|:--|
| 🟦 Tirkizna | `#168D99` | Primarne akcije i napredak |
| 🟨 Žuta | `#FFC857` | Aktivni potezi, poticaji |
| 🟥 Koraljna | `#FF7477` | Istaknuti detalji i maskota |
| 🟪 Plava | `#6C84E8` | Obrazovne kartice |
| ⬜ Krem | `#FFF9EF` | Pozadine i prostor za pisanje |
| ⬛ Tamnoplava | `#243449` | Naslovi i čitljiv tekst |

Ilustracije osnovnog zaslona implementirane su kao **Compose vektorski crteži** kako bi bile oštre na ekranima različitih gustoća. Ne koriste vanjske CDN-ove, mrežno učitavanje ili ugrađene slike cijelog zaslona.

## 🧩 Zasloni i razvojni redoslijed

| # | Zaslon | Stanje |
|--:|:--|:--|
| 01 | Splash / Dobrodošlica | ✅ Zaseban responzivni dizajn |
| 02 | Početni izbornik | ✅ Zaseban responzivni Compose dizajn; vizualna usporedba još nije potvrđena |
| 03 | Velika tiskana slova | ✅ Samostalna responzivna mreža s 30 slova; vizualnu usporedbu tek treba provesti |
| 04 | Mala tiskana slova | ✅ Poseban responzivni Compose zaslon, uključuje digrafe i odvojeni lokalni napredak; vizualna usporedba još nije završena |
| 05 | Vođeno pisanje | Funkcionalan, potrebna pedagoška provjera putanja |
| 06 | Uspješno završena vježba | Funkcionalan, vizualna dorada slijedi |
| 07 | Napredak | Funkcionalan, vizualna dorada slijedi |
| 08 | Igre | Osnovna igra, za proširenje |
| 09 | Postavke | Osnovne funkcije, vizualna dorada slijedi |

## 🛠️ Tehnologija i uređaji

- **Kotlin + Jetpack Compose + Material 3**
- **Gradle 9.6 / AGP 9.4.1 / JDK 17**, AndroidX Compose BOM 2026.09.00
- **compileSdk 37**, **targetSdk 36**, **minSdk 24**
- Android 7.0+; mobiteli, veliki telefoni, tableti i vodoravni prikaz
- Komponente su u `dp/sp`; na širokim zaslonima Splash koristi **dva stupca**
- Lokalne postavke i spremanje napretka; izgovor slova preko sustavne TTS usluge

## 🚀 Pokretanje i izdanja

Preuzmi **APK** iz odjeljka [Releases](https://github.com/bren-wp/Slovigo/releases). **Debug APK** namijenjen je testiranju i instalaciji izvan Trgovine Play. Ako izdanje sadrži `UNSIGNED.aab`, taj Android App Bundle **nije spreman za Google Play**.

Za razvoj otvori projekt u Android Studiju s JDK 17 i instaliranim Android SDK 37.0. CLI build:

```bash
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleRelease :app:bundleRelease
```

Automatsko izdanje generira se nakon uspješnog builda na `main`. Za stvarno potpisane release APK/AAB pakete spremi sljedeće tajne u **GitHub Actions Secrets**, bez objavljivanja privatnog ključa:

`SLOVIGO_KEYSTORE_BASE64`, `SLOVIGO_STORE_PASSWORD`, `SLOVIGO_KEY_ALIAS`, `SLOVIGO_KEY_PASSWORD`.

## 🔐 Privatnost i sigurnost

SLOVIGO ne traži račun, ne prikazuje oglase, nema pristup internetu i ne koristi analitiku. Podaci o napretku spremaju se **lokalno** i ne uključuju se u Android backup. Ako je uključen zvuk, Android TextToSpeech koristi sustavnog pružatelja, čija vlastita obrada ovisi o njegovim pravilima.

> Usklađenost s Google Play pravilima za dječje aplikacije nije automatski potvrđena uspješnom kompilacijom. Prije objave potrebno je pregledati sadržaj, pristupačnost, izjave Data Safety i Families pravila te konfigurirati potpisivanje.

## 📋 Licenca, kvaliteta i doprinosi

[Izvorni kod](https://github.com/bren-wp/Slovigo) · [Automatizirani testovi](https://github.com/bren-wp/Slovigo/actions/workflows/android.yml) · [Politika privatnosti](PRIVACY.md) · [Licenca](LICENSE)

Dizajnerske reference su smjernice, a **stvarne snimke aplikacije** izrađuju se automatizirano. Pritom se ne predstavljaju koncepti kao gotovi proizvodi.

## Dijagnostika rušenja

Ako se aplikacija ruši na fizičkom uređaju, provjeriti **Android Studio Logcat**, filtrirati po `hr.brendigo.slovigo.debug` i oznakama `FATAL EXCEPTION` / `AndroidRuntime`. Ukloniti osobne identifikatore prije slanja. Sama uspješna Gradle kompilacija nije dokaz ispravnosti na stvarnom uređaju.

### Zaslon 04 — mala tiskana slova

Zasebni `SlovigoLowercaseScreen` prikazuje 30 malih tiskanih slova abecede (`a, b, c, č, ć, d, dž, đ, …, lj, nj, š, ž`). Uspješno naučena slova prikazana su zeleno sa zvjezdicom, dok je prvo nenaučeno vizualno istaknuto. Napredak za mala slova odvojen je od velikih. Kartice imaju dodirne radnje i opis za čitače zaslona, a mreža zadržava pet stupaca na tipičnom telefonu te se prilagođava uskim i širokim prikazima. Dodavanje screenshotova dopušteno je samo nakon uspješnog snimanja stvarne aplikacije.
