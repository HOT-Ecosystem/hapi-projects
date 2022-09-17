"""Tests for FHIR server

TODO's
 1. Figure out fhirpath execution in HAPI, and use it:
 https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r5/org/hl7/fhir/r5/hapi/fhirpath/FhirPathR5.html
"""
import os
from pathlib import Path

import requests

from config import TEST_SCRIPT_DIR, TEST_SERVER_URL


def run(input_dir: Path = TEST_SCRIPT_DIR):
    """Run tests"""
    headers = {'Content-Type': 'application/xml'}
    post_url = TEST_SERVER_URL + 'TestScript'  # TODO:
    for script_path in os.listdir(input_dir):
        with open(Path(input_dir, str(script_path)), 'r') as f:
            script_txt = f.read()
        response = requests.post(post_url, headers=headers, data=script_txt)
        # TODO: response.text is xml. how to get in json? else parse xml
        result = response.json()  # err
        print()


if __name__ == '__main__':
    run()
