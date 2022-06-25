# Auto replanter
Auto replanter is a small modification that aims to improve farming experience in Minecraft,
by automatically replanting crops when you harvest them.

[![Super-Linter check](https://github.com/cichu/minecraft-auto-replanter/actions/workflows/lint-main-branch.yml/badge.svg)](https://github.com/cichu/minecraft-auto-replanter/actions/workflows/lint-main-branch.yml)

## Table of contents
1. [About the mod](#about-the-mod)
2. [Getting started](#getting-started)
   1. [Prerequisites](#prerequisites)
   2. [Installation](#installation)
3. [Roadmap](#roadmap)
4. [How to report a bug or request new feature](#reporting-bugs-or-requesting-features)

## About the mod
For auto replanting to work you need to use a tool when harvesting.

Here is a list of tools with crops they can harvest:
- Shears
  - Wheat
- Hoe
  - Beetroots
  - Carrots
  - Potatoes

Once you have a proper tool in hand, you can simply right-click respective, fully grown crop.
This will harvest it and plant the same type of crop in its place.

Harvesting crop this way drops one less seed item than by gathering it in the regular way.
For crops that don't have a separate seed item (i.e. carrot or potato) the number of harvested crop items is decreased by one instead.
This means that regardless if you harvest using a tool or in the regular way - by punching and planting them manually - the number of gathered crops and seeds should be the same.

## Getting started

### Prerequisites
To use this mod you need to have Minecraft with Fabric loader installed.  
Instructions how to install Fabric loader can be found [here](https://fabricmc.net/wiki/install).

### Installation
1. Download the latest version from the [GitHub releases](https://github.com/cichu/minecraft-auto-replanter/releases) for your version of Minecraft.
   > Each release has 2 JAR files attached.  
   > Download the one **WITHOUT** the word "sources" in its name.
2. Move the downloaded file into mods folder inside your Minecraft installation folder.

## Roadmap
- [x] Use different harvesting tools for different crops
- [ ] Allow harvesting more crops
  - [ ] Cocoa beans
  - [ ] Nether warts
- [ ] Add ability to put looting enchantment on the harvesting tools

## Reporting bugs or requesting features
Both bug reports and feature requests are handled through [issues tab](https://github.com/cichu/minecraft-auto-replanter/issues).
Before opening new issue check if there isn't one similar to yours.  
If you find one that matches your needs comment on it or add a reaction to let me know that there is interest in it.  
Otherwise create new issue providing at least a title, a short description and selecting a proper label:
- `bug` - for reporting a bug or an error
- `enchancement` - when proposing an idea for improvement