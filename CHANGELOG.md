# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.2] - 2019-05-08
Updated for Forge 191. Crop support is currently broken, I'm looking into it.
### Added
- Armor value to entity info (can be tweaked in the config)

## [1.1.0] - 2019-01-24
Mostly working 1.13 port. Some configs do not work yet. Built against Forge 97.
### Added
- A "sticky time" to HUD info. When no longer looking at an object, the previous is shown for a short time (currently 1 second for blocks and 4 seconds for entities).
### Changed
- Improved block replacements, no reload required (for devs: it maps to a supplier now)
