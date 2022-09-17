"""Test config"""
import os
from pathlib import Path

TEST_DIR = os.path.dirname(__file__)
TEST_INPUTS_DIR = Path(TEST_DIR, 'input')
TEST_SCRIPT_DIR = Path(TEST_INPUTS_DIR, 'TestScript')
# TEST_SERVER_URL = 'http://20.119.216.32:8000/tester/'  # our swagger UI
TEST_SERVER_URL = 'http://20.119.216.32:8000/r4/'
