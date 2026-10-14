# Oil Price Checker

A Spring Boot web app that fetches home heating oil prices from local suppliers (BT47/BT48, Northern Ireland area), tracks historical price trends, and displays them on a dashboard (with Highcharts) so you can compare prices and spot the cheapest supplier.

## Tech Stack

- Java 17 bytecode baseline (runs on Java 25)
- Spring Boot 3.3.4 (Web, Thymeleaf, Scheduling, Async)
- Maven (via the included Maven Wrapper — no local Maven install required)
- Vanilla JS / Highcharts for the dashboard UI
- Flat text files for storage (no database) — either on local disk or in AWS S3

## Prerequisites

- **Java 17 or later** — including [Eclipse Temurin 25](https://adoptium.net/). The app compiles to Java 17-compatible bytecode even when built with Java 25; a separate Java 17 installation is not required.
  Check your version:
  ```bash
  java -version
  ```
- **Git**, to clone the repository.

That's it — you do **not** need to install Maven separately; this project ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`), which will download the correct Maven version automatically.

> ℹ️ This app is intended to be run directly on your own machine (not in Docker/a container).

## Getting Started

### Install the prerequisites

Only a **JDK (Java Development Kit) 17 or later** and **Git** need to be installed. If you already have Java 25, keep using it — you do not need a separate Java 17 installation.

**macOS (the supported local storage setup)**

If you use [Homebrew](https://brew.sh/), install Java 25 and Git with:

```bash
brew install --cask temurin@25
brew install git
```

Without Homebrew, download the macOS JDK installer from [Eclipse Temurin](https://adoptium.net/temurin/releases/) (choose JDK 25 and the architecture matching your Mac). Install Git using the Xcode Command Line Tools:

```bash
xcode-select --install
```

**Windows**

Install a JDK from [Eclipse Temurin](https://adoptium.net/temurin/releases/) and [Git for Windows](https://git-scm.com/downloads/win). Enable the JDK installer's `PATH` and `JAVA_HOME` options, then open a new terminal.

**Ubuntu / Debian**

```bash
sudo apt update
sudo apt install openjdk-17-jdk git
```

The current local storage path is macOS-specific; Windows and Linux users may need to adapt `LocalFileHandler` before running the app.

Verify the tools are available:

```bash
java -version
javac -version
git --version
```

Both Java commands should report version 17 or later. No separate installation of **Maven, Spring Boot, Node.js, or a database** is needed: the Maven Wrapper downloads Maven and the project's dependencies, and the dashboard uses browser-side JavaScript.

### Build and run

1. Clone the repository:
   ```bash
   git clone https://github.com/mlynch147/web-oil-price-checker.git
   cd web-oil-price-checker
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```
   On Windows, use `mvnw.cmd clean install` instead.

3. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows, use `mvnw.cmd spring-boot:run` instead.

   On Java 24 or later, explicitly enable native access to avoid Tomcat's startup warnings:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED"
   ```
   In IntelliJ, add `--enable-native-access=ALL-UNNAMED` under **Run → Edit Configurations → Modify options → Add VM options**, not under Environment variables or Program arguments.

4. Once it's started, open your browser at:
   ```
   http://localhost:8080
   ```
   (there's also a legacy UI at `http://localhost:8080/v1`)

### Alternative: run the packaged jar

```bash
./mvnw clean package
java -jar target/oilpricechecker-0.0.1-SNAPSHOT.jar
```

On Java 24 or later, use `java --enable-native-access=ALL-UNNAMED -jar target/oilpricechecker-0.0.1-SNAPSHOT.jar` to avoid the native-access warnings.

## Configuration

- `src/main/resources/application.properties` is minimal by default — no setup is required to run locally.
- **Storage backend**: by default the app uses `LocalFileHandler`, which stores its data files under:
  ```
  ~/Library/Application Support/oilpricechecker/   (macOS)
  ```
  On first run it automatically copies the bundled sample data files (`src/main/resources/data/*.txt`) into that folder if they don't already exist there.
- **Optional AWS S3 backend**: an `S3FileHandler` implementation also exists (bucket name via the `s3.bucketName` property, default `oil-price-checker-data-files`, region `eu-west-2`). It's not used unless you explicitly wire it in over the default `LocalFileHandler` — you'd also need valid AWS credentials configured locally (e.g. via `~/.aws/credentials` or environment variables) for it to work.
- **Supplier configuration** lives in `src/main/resources/price_requests_config.json` — this drives which suppliers are fetched, their URLs, and how prices are scraped, without needing any code changes.

## Running Tests

```bash
./mvnw test
```

## Project Structure (high level)

```
src/main/java/com/ml/oilpricechecker/
├── controllers/   REST + MVC endpoints (dashboard, prices, charts)
├── fetcher/       Strategy classes that fetch/scrape each supplier's price
├── service/       PriceService, ChartService, FileWriterService, etc.
├── file/          IFileHandler (Local + S3 implementations) for persistence
├── schedule/       Cron jobs that periodically fetch & store prices
├── models/        DTOs used across the app
└── config/        Spring bean configuration (RestTemplate, ExecutorService, S3Client)
```

## Notes

- Scheduled jobs automatically fetch and store prices 3x/day, plus a weekly Friday job — no manual intervention is needed once the app is running, but it must stay running for these to fire.
- SSL certificate verification is intentionally relaxed for some supplier requests due to certificate issues on their end — this is a deliberate workaround, not an oversight.
