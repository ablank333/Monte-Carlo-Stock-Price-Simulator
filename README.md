# Monte Carlo Stock Price Simulator

A full-stack Java web app that simulates a stock's future price using real historical market data and Monte Carlo methods.

Type in a ticker, and the app:
1. Pulls real historical daily closing prices from the Alpha Vantage API
2. Calculates the stock's annualized expected return (μ) and volatility (σ) from that data, using log returns
3. Runs 10,000 simulations of the stock's price one year forward using Geometric Brownian Motion (GBM)
4. Returns key statistics — mean, median, 5th/95th percentile, probability of gain, and 95% Value at Risk
5. Displays the results and a histogram of all 10,000 simulated outcomes in the browser

## Tech Stack

- **Backend:** Java, Javalin 6.3.0 (web framework), Gson 2.11.0 (JSON parsing), Jackson-databind 2.17.2 (required by Javalin's ctx.json())
- **Frontend:** Plain HTML/CSS/JavaScript, Chart.js (via CDN)
- **Data source:** Alpha Vantage API (free tier)

## How It Works

The core math is Geometric Brownian Motion:

S(t+dt) = S(t) * exp[(mu - 0.5*sigma^2)*dt + sigma*sqrt(dt) * Z]

where Z is a random draw from a standard normal distribution. The app estimates mu and sigma from real historical log returns (not simple returns, since log returns are additive across time and match GBM's assumptions), then runs 10,000 independent one-year paths forward to build a distribution of possible future prices.

## Running Locally

### Prerequisites
- Java (JDK 17+ recommended)
- IntelliJ IDEA (or any Java IDE / manual javac/java setup)
- A free Alpha Vantage API key (https://www.alphavantage.co/support/#api-key)

### Setup

1. Clone the repo:
   git clone https://github.com/ablank333/Monte-Carlo-Stock-Price-Simulator.git
   cd Monte-Carlo-Stock-Price-Simulator

2. Add the required dependencies (if not already resolved via your build tool):
   - io.javalin:javalin:6.3.0
   - com.google.code.gson:gson:2.11.0
   - com.fasterxml.jackson.core:jackson-databind:2.17.2

3. Set your Alpha Vantage API key as an environment variable named ALPHA_VANTAGE_API_KEY.
   - In IntelliJ: Run -> Edit Configurations -> add ALPHA_VANTAGE_API_KEY under Environment Variables
   - In a terminal: export ALPHA_VANTAGE_API_KEY=your_key_here

4. Run Server.java. The app starts on port 8080.

5. Open your browser to:
   http://localhost:8080

6. Enter a stock ticker (e.g. AAPL) and click Simulate.

### Notes on the free API tier

Alpha Vantage's free tier is limited to 25 requests/day, 5/min, and caps historical lookback at 100 days (outputsize=compact). If you see a rate-limit-related error, wait and try again later - Alpha Vantage doesn't officially document the reset window, but it behaves like a rolling ~24 hour window.

## Project Structure

Monte-Carlo-Stock-Price-Simulator/
├── src/
│   ├── Main.java                  (console version - still works, standalone)
│   ├── Server.java                (Javalin web server - primary entry point)
│   ├── Stock.java
│   ├── RandomGenerator.java
│   ├── Simulator.java
│   ├── Statistics.java
│   ├── HistoricalDataLoader.java
│   ├── SimulationResponse.java
│   ├── ErrorResponse.java
│   └── public/
│       └── index.html             (frontend UI)
├── out/                           (build output, gitignored)

Note: the public/ folder must live inside src/, not at the project root, for Javalin's static file serving to find it.

## Screenshots

Add a screenshot of the running app here once available.

## Status

Core functionality is complete and working end-to-end. Remaining work is polish, deployment, and documentation - see the project's technical handoff notes for full details on outstanding items.

## License

Add a license if you plan to make this public (MIT is a common choice for portfolio projects).
