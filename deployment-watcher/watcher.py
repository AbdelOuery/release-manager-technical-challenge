import os
import time

import docker
import requests

RELEASE_MANAGER_URL = os.environ["RELEASE_MANAGER_URL"]
PROJECT = os.environ["PROJECT"]

client = docker.from_env()


def report(name: str, version: int) -> None:
    # Retry, because the release manager may still be starting
    for attempt in range(30):
        try:
            response = requests.post(
                f"{RELEASE_MANAGER_URL}/deploy",
                json={"name": name, "version": version},
                timeout=5,
            )
            response.raise_for_status()
            print(f"{name} v{version} -> system version {response.json()}", flush=True)
            return
        except requests.RequestException as e:
            print(f"retry {attempt + 1}: {e}", flush=True)
            time.sleep(2)


def handle(labels: dict) -> None:
    # Ignore other environments sharing the same Docker engine
    if labels.get("com.docker.compose.project") != PROJECT:
        return
    # Ignore containers that aren't services (postgres, release-manager, ...)
    if "release.version" not in labels:
        return
    report(labels["release.service"], int(labels["release.version"]))


# 1. Containers already running when the watcher starts emit no event, so scan them first
for container in client.containers.list(filters={"label": "release.version"}):
    handle(container.labels)

# 2. Then react to every container start
for event in client.events(decode=True, filters={"type": "container", "event": "start"}):
    handle(event["Actor"]["Attributes"])