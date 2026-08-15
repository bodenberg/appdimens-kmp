# Security Policy

## Supported Versions

| Version | Supported |
| ------- | ------------------ |
| 1.0.x | :white_check_mark: |
| < 1.0 | :x: |

Security fixes for **1.0.0+** apply to the full published set (`appdimens-dynamic`, strategy modules, and `appdimens-dynamic-bom`) at the shared version line.

> **Note:** the Android-only predecessor (`appdimens-dynamic` 3.x, Android-only packaging) is superseded by the Kotlin Multiplatform **1.0.0** line, which carries the same math, cache and R8 contract forward. 3.x is no longer actively patched — migrate to 1.0.0 (same packages and imports).

## Reporting a Vulnerability

Please report security issues privately via GitHub Security Advisories on [bodenberg/appdimens-dynamic](https://github.com/bodenberg/appdimens-dynamic), or email the maintainer listed in the Maven POM (`jean.bodenberg2@outlook.com`).

You can expect an initial acknowledgement within a few days. If the report is accepted, a fix will be prepared for the supported 1.0.x line and credited as appropriate. If declined, we will explain why.
