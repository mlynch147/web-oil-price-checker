# Oil Price Checker

A Spring Boot web app that fetches home heating oil prices from local suppliers (BT47/BT48, Northern Ireland area), tracks historical price trends, and displays them on a dashboard (with Highcharts) so you can compare prices and spot the cheapest supplier.

## Tech Stack

- Java 17
- Spring Boot 3.3.4 (Web, Thymeleaf, Scheduling, Async)
- Maven (via the included Maven Wrapper — no local Maven install required)
- Vanilla JS / Highcharts for the dashboard UI
- Flat text files for storage (no database) — either on local disk or in AWS S3

## Prerequisites

- **Java 17** (or later) — [Eclipse Temurin](https://adoptium.net/) or [Amazon Corretto](https://aws.amazon.com/corretto/) both work.
  Check your version:
  ```bash
  java -version
  ```
- **Git**, to clone the repository.

That's it — you do **not** need to install Maven separately; this project ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`), which will download the correct Maven version automatically.

> ℹ️ This app is intended to be run directly on your own machine (not in Docker/a container).

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/<your-org-or-user>/oilpricechecker.git
   cd oilpricechecker
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

