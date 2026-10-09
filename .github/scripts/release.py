"""Validate Boom release inputs and the exact shaded artifact; stdlib only."""

import argparse
from datetime import date
from pathlib import Path
import re
import sys
from zipfile import BadZipFile, ZipFile


def validate_tag(tag: str) -> None:
    if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+", tag):
        raise ValueError(f"Tag {tag!r} must be a numeric version such as 2.12.1 (no v prefix).")


def prepare(tag: str, notes: Path) -> None:
    validate_tag(tag)
    build = Path("build.gradle.kts").read_text(encoding="utf-8")
    versions = re.findall(r'^version\s*=\s*"([^"\r\n]+)"[ \t]*$', build, re.MULTILINE)
    if len(versions) != 1:
        raise ValueError('build.gradle.kts must contain exactly one version = "<version>" assignment.')
    if versions[0] != tag:
        raise ValueError(f"Gradle plugin version {versions[0]!r} does not match tag {tag!r}.")

    wrapper = Path("gradle/wrapper/gradle-wrapper.properties").read_text(encoding="utf-8")
    urls = re.findall(r"^distributionUrl=(.+)$", wrapper, re.MULTILINE)
    if len(urls) != 1 or not re.search(r"/gradle-8\.10-(?:bin|all)\.zip$", urls[0]):
        raise ValueError("The release build requires the Gradle 8.10 wrapper distribution.")

    changelog = Path("CHANGELOG.md").read_text(encoding="utf-8")
    headers = list(re.finditer(
        rf"^## \[{re.escape(tag)}\] - (\d{{4}}-\d{{2}}-\d{{2}})[ \t]*$",
        changelog,
        re.MULTILINE,
    ))
    if len(headers) != 1:
        raise ValueError(f"CHANGELOG.md must contain exactly one '## [{tag}] - YYYY-MM-DD' section.")
    header = headers[0]
    try:
        date.fromisoformat(header.group(1))
    except ValueError as error:
        raise ValueError(f"Invalid release date in CHANGELOG.md for {tag}: {header.group(1)}.") from error
    next_heading = re.search(r"^#{1,2}[ \t]+", changelog[header.end():], re.MULTILINE)
    end = header.end() + next_heading.start() if next_heading else len(changelog)
    if not changelog[header.end():end].strip():
        raise ValueError(f"CHANGELOG.md release section for {tag} is empty.")
    notes.parent.mkdir(parents=True, exist_ok=True)
    notes.write_text(changelog[header.start():end].strip() + "\n", encoding="utf-8")
    print(f"Validated release {tag}; extracted its notes to {notes}.")


def verify_jar(tag: str) -> None:
    validate_tag(tag)
    jar = Path("build/libs") / f"Boom-{tag}.jar"
    if not jar.is_file():
        raise ValueError(f"Exact shaded artifact is missing: {jar}. Run clean shadowJar; do not publish a plain JAR.")
    with ZipFile(jar) as archive:
        names = archive.namelist()
        if names.count("plugin.yml") != 1:
            raise ValueError(f"{jar} must contain exactly one root plugin.yml.")
        plugin = archive.read("plugin.yml").decode("utf-8")
        versions = re.findall(
            r'''^version:[ \t]*(?:"([^"\r\n]+)"|'([^'\r\n]+)'|([^#\s]+))[ \t]*(?:#.*)?$''',
            plugin,
            re.MULTILINE,
        )
        if len(versions) != 1:
            raise ValueError(f"{jar}: plugin.yml must contain exactly one scalar version.")
        version = next(value for value in versions[0] if value)
        if version != tag:
            raise ValueError(f"{jar}: built plugin.yml version {version!r} does not match tag {tag!r}.")
        if not any(name.startswith("top/e404/boom/relocate/") and name.endswith(".class") for name in names):
            raise ValueError(f"{jar} has no relocated dependency classes; refusing to publish a plain JAR.")
    print(f"Validated shaded artifact {jar} and built plugin.yml version {tag}.")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    prepare_command = commands.add_parser("prepare", help="Validate tag, Gradle version, wrapper and release notes.")
    prepare_command.add_argument("tag")
    prepare_command.add_argument("notes", type=Path)
    jar_command = commands.add_parser("verify-jar", help="Validate build/libs/Boom-<tag>.jar after shadowJar.")
    jar_command.add_argument("tag")
    args = parser.parse_args()
    try:
        if args.command == "prepare":
            prepare(args.tag, args.notes)
        else:
            verify_jar(args.tag)
    except (ValueError, OSError, UnicodeError, BadZipFile) as error:
        print(f"Release validation failed: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
