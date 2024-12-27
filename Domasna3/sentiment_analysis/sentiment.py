import asyncio
import aiohttp
import re
import csv
from datetime import datetime
from bs4 import BeautifulSoup
from transformers import AutoTokenizer, AutoModelForSequenceClassification


class NewsScraper:
    def __init__(self, session, max_concurrent_requests=10):
        self.base_url = "https://www.mse.mk/en/symbol/{issuer}"
        self.text_url = "https://api.seinet.com.mk/public/documents/single/{news_id}"
        self.session = session
        self.semaphore = asyncio.Semaphore(max_concurrent_requests)

    async def fetch_news_links(self, issuer):
        url = self.base_url.format(issuer=issuer)
        print(f"Fetching news links for issuer: {issuer} from {url}")
        async with self.semaphore:
            try:
                async with self.session.get(url, timeout=15) as response:
                    print(f"Response status for {issuer}: {response.status}")
                    response.raise_for_status()
                    page_content = await response.text()
                    soup = BeautifulSoup(page_content, "html.parser")
                    news_links = soup.select('#seiNetIssuerLatestNews a')
                    print(f"Found {len(news_links)} news links for issuer {issuer}.")
                    return [
                        {
                            "news_id": self.extract_news_id(link["href"]),
                            "date": self.extract_date(
                                link.select_one("ul li:nth-child(2) h4").text
                            ) if link.select_one("ul li:nth-child(2) h4") else None,
                        }
                        for link in news_links if "href" in link.attrs
                    ]
            except Exception as e:
                print(f"Error fetching news links for {issuer}: {e}")
                return []

    def extract_news_id(self, url):
        news_id = url.split("/")[-1]
        print(f"Extracted news ID: {news_id} from URL: {url}")
        return news_id

    def extract_date(self, date_str):
        try:
            match = re.search(r"\d{1,2}/\d{1,2}/\d{4}", date_str)
            if match:
                date = datetime.strptime(match.group(), "%m/%d/%Y").date()
                print(f"Extracted date: {date} from string: {date_str}")
                return date
        except Exception as e:
            print(f"Error extracting date from {date_str}: {e}")
        return None

    async def fetch_news_content(self, news_id):
        url = self.text_url.format(news_id=news_id)
        print(f"Fetching news content for ID: {news_id} from {url}")
        async with self.semaphore:
            try:
                async with self.session.get(url, timeout=15) as response:
                    print(f"Response status for news ID {news_id}: {response.status}")
                    if response.status == 200:
                        data = await response.json()
                        content = data.get("data", {}).get("content")
                        clean_content = re.sub(r"<[^>]*>", "", content) if content else None
                        print(f"Fetched content for news ID {news_id}: {clean_content[:100]}...")  # Truncate for readability
                        return clean_content
            except Exception as e:
                print(f"Error fetching content for news ID {news_id}: {e}")
                return None


class SentimentAnalyzer:
    def __init__(self):
        print("Initializing Sentiment Analyzer...")
        self.tokenizer = AutoTokenizer.from_pretrained("ProsusAI/finbert")
        self.model = AutoModelForSequenceClassification.from_pretrained("ProsusAI/finbert")
        print("Sentiment Analyzer ready.")

    def analyze(self, text):
        print(f"Analyzing text: {text[:100]}...")  # Truncate for readability
        inputs = self.tokenizer(text, return_tensors="pt", truncation=True, padding=True)
        outputs = self.model(**inputs)
        label = outputs.logits.argmax().item()
        sentiment = ["negative", "neutral", "positive"][label]
        print(f"Sentiment: {sentiment}")
        return sentiment


class Pipeline:
    def __init__(self, issuers, output_file):
        self.issuers = issuers
        self.output_file = output_file
        self.analyzer = SentimentAnalyzer()

    async def process_issuer(self, issuer, scraper):
        print(f"Processing issuer: {issuer}")
        news_links = await scraper.fetch_news_links(issuer)
        results = []
        for news in news_links:
            print(f"Processing news ID: {news['news_id']} for issuer {issuer}")
            content = await scraper.fetch_news_content(news["news_id"])
            if content:
                sentiment = self.analyzer.analyze(content)
                recommendation = "buy" if sentiment == "positive" else "sell" if sentiment == "negative" else "hold"
                print(f"Issuer: {issuer}, News ID: {news['news_id']}, Recommendation: {recommendation}")
                results.append({
                    "issuer_name": issuer,
                    "recommendation": recommendation,
                    "scraped_date": news["date"].strftime('%Y-%m-%d') if news["date"] else "N/A",
                })
        return results

    async def run(self):
        print("Starting pipeline...")
        async with aiohttp.ClientSession() as session:
            scraper = NewsScraper(session)
            tasks = [self.process_issuer(issuer, scraper) for issuer in self.issuers]


            batch_size = 50
            results = []
            for i in range(0, len(tasks), batch_size):
                print(f"Processing batch {i // batch_size + 1}...")
                batch = tasks[i:i+batch_size]
                batch_results = await asyncio.gather(*batch, return_exceptions=True)
                for result in batch_results:
                    if isinstance(result, list):
                        results.extend(result)


        print(f"Writing results to {self.output_file}...")
        with open(self.output_file, "w", newline="", encoding="utf-8") as csvfile:
            fieldnames = ["issuer_name", "recommendation", "scraped_date"]
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for result in results:
                writer.writerow(result)
        print("Pipeline complete.")



if __name__ == "__main__":
    issuers_list = ['ADIN', 'ALK', 'ALKB', 'AMBR', 'AMEH', 'APTK', 'ATPP', 'AUMK', 'BANA', 'BGOR', 'BIKF', 'BIM', 'BLTU', 'CBNG', 'CDHV', 'CEVI', 'CKB', 'CKBKO', 'DEBA', 'DIMI', 'EDST', 'ELMA', 'ELNC', 'ENER', 'ENSA', 'EUHA', 'EUMK', 'EVRO', 'FAKM', 'FERS', 'FKTL', 'FROT', 'FUBT', 'GALE', 'GDKM', 'GECK', 'GECT', 'GIMS', 'GRDN', 'GRNT', 'GRSN', 'GRZD', 'GTC', 'GTRG', 'IJUG', 'INB', 'INDI', 'INEK', 'INHO', 'INOV', 'INPR', 'INTP', 'JAKO', 'JULI', 'JUSK', 'KARO', 'KDFO', 'KJUBI', 'KKFI', 'KKST', 'KLST', 'KMB', 'KMPR', 'KOMU', 'KONF', 'KONZ', 'KORZ', 'KPSS', 'KULT', 'KVAS', 'LAJO', 'LHND', 'LOTO', 'LOZP', 'MAGP', 'MAKP', 'MAKS', 'MB', 'MERM', 'MKSD', 'MLKR', 'MODA', 'MPOL', 'MPT', 'MPTE', 'MTUR', 'MZHE', 'MZPU', 'NEME', 'NOSK', 'OBPP', 'OILK', 'OKTA', 'OMOS', 'OPFO', 'OPTK', 'ORAN', 'OSPO', 'OTEK', 'PELK', 'PGGV', 'PKB', 'POPK', 'PPIV', 'PROD', 'PROT', 'PTRS', 'RADE', 'REPL', 'RIMI', 'RINS', 'RZEK', 'RZIT', 'RZIZ', 'RZLE', 'RZLV', 'RZTK', 'RZUG', 'RZUS', 'SBT', 'SDOM', 'SIL', 'SKON', 'SKP', 'SLAV', 'SNBT', 'SNBTO', 'SOLN', 'SPAZ', 'SPAZP', 'SPOL', 'SSPR', 'STB', 'STBP', 'STIL', 'STOK', 'TAJM', 'TASK', 'TBKO', 'TEAL', 'TEHN', 'TEL', 'TETE', 'TIGA', 'TIKV', 'TKPR', 'TKVS', 'TNB', 'TRDB', 'TRPS', 'TRUB', 'TSMP', 'TSZS', 'TTK', 'UNI', 'USJE', 'VARG', 'VFPM', 'VITA', 'VROS', 'VSC', 'VTKS', 'ZAS', 'ZILU', 'ZILUP', 'ZIMS', 'ZKAR', 'ZPKO', 'ZPOG', 'ZSIL', 'ZUAS']
    output_csv = "news_sentiment.csv"
    pipeline = Pipeline(issuers_list, output_csv)
    asyncio.run(pipeline.run())
