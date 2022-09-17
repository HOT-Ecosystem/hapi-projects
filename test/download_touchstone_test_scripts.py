"""Download touchstone test scripts"""
from pathlib import Path

import requests
from bs4 import BeautifulSoup

from config import TEST_SCRIPT_DIR


TOUCHSTONE_BASE_URL = 'https://touchstone.aegis.net'
TOUCHSTONE_SCRIPTS_URL = (
    TOUCHSTONE_BASE_URL + '/touchstone/testdefinitions?selectedTestGrp=%2FFHIR4-0-1-Connectathon31%2FTerminology-'
    'Formal%2FXML%20Format&activeOnly=false&includeInactive=false&ps=200&sb=qualifiedName&sd=' 
    'ASC&allSelected=false&contentEntry=TEST_SCRIPTS')


def download_test_scripts(outdir: Path = TEST_SCRIPT_DIR):
    """Download test scripts

    script_path -> script_name:
      - The script_name appears at the end
      - script_path: /FHIR4-0-1-Connectathon31/Terminology-Formal/JSON Format/Expand/terminology-expand-filter-json
      - script_name: terminology-expand-filter-json

    TODO's
     1. Handle pagination: Right now, assumes that there are max of 200 scripts. This is stipulated by the url query
     param `ps=200`. I don't think query param can go higher, so need to figure out pagination if want to DL more.
    """
    response = requests.get(TOUCHSTONE_SCRIPTS_URL)
    parser = BeautifulSoup(response.text, 'html.parser')
    script_table = parser.find_all("table", class_="table table-striped")[0]
    rows = script_table.findChildren(['tr'])
    rows = rows[2:]  # first 2 rows on touchstone are not data; 1st is a selector, and 2nd is a button
    scriptname_url_map = {}
    for row in rows:
        cells = row.findChildren('td')
        if cells:
            script_path = cells[1].text.replace('\n', '')
            script_name = script_path.split('/')[-1]
            url = TOUCHSTONE_BASE_URL + cells[1].contents[1].attrs['href']
            scriptname_url_map[script_name] = url

    for script_name, url in scriptname_url_map.items():
        outpath = Path(TEST_SCRIPT_DIR, script_name + '.xml')
        script_response = requests.get(url)
        script_parser = BeautifulSoup(script_response.text, 'html.parser')
        xml = script_parser.find_all('pre')[0].text
        with open(outpath, 'w') as f:
            f.write(xml)


if __name__ == '__main__':
    download_test_scripts()
