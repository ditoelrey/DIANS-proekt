import requests
from bs4 import BeautifulSoup
import pandas as pd
from datetime import datetime, timedelta
import os
import time
from concurrent.futures import ThreadPoolExecutor

start_time = time.time()


def convert_date_format(date_str):
    date_obj = datetime.strptime(date_str, '%m/%d/%Y')
    return date_obj.strftime('%d.%m.%Y')


def get_issuer_codes():
    url = "https://www.mse.mk/en/stats/symbolhistory/kmb"
    with requests.Session() as session:
        response = session.get(url)
    soup = BeautifulSoup(response.content, "html.parser")
    dropdown = soup.find("select", id="Code")

    if not dropdown:
        print("No issuer codes found.")
        return []

    issuer_codes = [
        option.text.strip()
        for option in dropdown.find_all("option")
        if not any(char.isdigit() for char in option.text)
    ]
    print(f"Issuer codes: {issuer_codes}")
    return issuer_codes


def get_last_available_date(issuer_code):
    file_path = f"csv/{issuer_code}.csv"
    try:
        df = pd.read_csv(file_path)
        last_date = pd.to_datetime(df['Date'], format='%d.%m.%Y').max()
        print(f"Last available date for {issuer_code}: {last_date.strftime('%d.%m.%Y')}")
        return last_date
    except (FileNotFoundError, pd.errors.EmptyDataError):
        print(f"No existing data found for {issuer_code}.")
        return None


def fetch_issuer_data(issuer_code, start_date, end_date):
    url = (
        f"https://www.mse.mk/en/stats/symbolhistory/{issuer_code}"
        f"?FromDate={start_date.strftime('%m/%d/%Y')}"
        f"&ToDate={end_date.strftime('%m/%d/%Y')}"
    )

    with requests.Session() as session:
        response = session.get(url)
    print(f"Fetching data for {issuer_code} from {start_date} to {end_date}. Status: {response.status_code}")
    soup = BeautifulSoup(response.content, 'html.parser')
    tbody = soup.select_one('tbody')

    if not tbody:
        print(f"No data found for {issuer_code} from {start_date} to {end_date}.")
        return []

    data = [
        [cell.get_text(strip=True) for cell in row.find_all('td')]
        for row in tbody.find_all('tr')
    ]
    return data


def format_price(value):
    try:
        formatted_value = "{:,.2f}".format(float(value.replace(',', '')))
        return formatted_value.replace(',', 'X').replace('.', ',').replace('X', '.')
    except ValueError:
        return value


def format_turnover(value):
    try:
        formatted_value = "{:,.0f}".format(int(value.replace(',', '')))
        return formatted_value.replace(',', '.')
    except ValueError:
        return value


def save_data_to_csv(issuer_code, data):
    columns = ['Date', 'Last trade price', 'Max', 'Min', 'Avg. Price', '%chg.', 'Volume', 'Turnover in BEST',
               'Total turnover']
    df = pd.DataFrame(data, columns=columns)
    df['Date'] = df['Date'].apply(convert_date_format)

    for col in ['Last trade price', 'Max', 'Min', 'Avg. Price']:
        df[col] = df[col].apply(format_price)
    for col in ['Turnover in BEST', 'Total turnover']:
        df[col] = df[col].apply(format_turnover)

    file_path = f"csv/{issuer_code}.csv"
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    df.to_csv(file_path, mode='w', index=False)
    print(f"Data saved to {file_path}")


def update_issuer_data(issuer_code):
    last_date = get_last_available_date(issuer_code)
    current_date = datetime.now()
    start_date = last_date + timedelta(days=1) if last_date else current_date - timedelta(days=3650)
    all_data = []

    while start_date <= current_date:
        if (current_date.year - start_date.year) >= 1:
            year_end_date = datetime(start_date.year, 12, 31)
            end_date = min(year_end_date, current_date)
        else:
            end_date = current_date

        data = fetch_issuer_data(issuer_code, start_date, end_date)
        if data:
            all_data.extend(data)

        start_date = end_date + timedelta(days=1)
        if end_date == current_date:
            break

    if all_data:
        save_data_to_csv(issuer_code, all_data)


def main():
    issuer_codes = get_issuer_codes()
    with ThreadPoolExecutor(max_workers=10) as executor:
        executor.map(update_issuer_data, issuer_codes)

    print("Data retrieval and storage complete.")
    end_time = time.time()
    execution_time = (end_time - start_time) / 60
    print(f"Execution Time: {execution_time:.2f} minutes")


if __name__ == "__main__":
    main()