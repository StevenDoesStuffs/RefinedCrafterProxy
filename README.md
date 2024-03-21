# Refined Crafter Proxy

This mod adds the **crafter proxy** to Refined Storage,
which acts like a crafter but gets its patterns from another linked crafter.

This allows you to distribute the same pattern across multiple crafters
(which will automatically get load balanced by Refined Storage)
without needing to duplicate the pattern.
While using a single crafter and doing round-robin on the ingredients is an alternative,
it's often very difficult to do when also needing to do some form of blocking.

To link a crafter proxy, craft a **crafter proxy card**,
and shift right-click on a block to bind that block to the card.
Then place the card inside the crafter proxy.

Notes: 
- The proxy will not show up on the crafting manager.
- Refined Storage's load balancing algorithm will feed more ingredients to a crafter with more speed upgrades. 
  If your items aren't being distributed evenly, this may be why. 
- For modpack creators: if you have a mod like [Extra Storage](https://github.com/Edivad99/ExtraStorage/) which adds crafters with custom performance characteristics,
  you can add proxy variants of those crafters in the RefinedCrafterProxy config in order to match the speed for proper load balancing. 
  These will not have crafting recipes by default.

## Downloads

Available on [Modrinth](https://modrinth.com/mod/refinedcrafterproxy)
and [Curseforge](https://www.curseforge.com/minecraft/mc-mods/refined-crafter-proxy).

## Screenshots

![2024-03-21_00 07 29](https://github.com/StevenDoesStuffs/RefinedCrafterProxy/assets/13265529/2d326981-1069-4f1b-ae4a-15dd588b50ff)
![2024-03-21_00 09 37](https://github.com/StevenDoesStuffs/RefinedCrafterProxy/assets/13265529/fb1b55b6-23bc-4ae9-b9da-73d15c7f2e71)

## Redistribution

This project is licensed under the AGPLv3. See LICENSE.

## Acknowledgements

Special thanks to Refined Mods for [Refined Storage](https://refinedmods.com/refined-storage/), and

- Edivad99 for [Extra Storage](https://github.com/Edivad99/ExtraStorage/),
- SuperMartijn642 for [Entangled](https://github.com/SuperMartijn642/Entangled),

which I referenced while coding the crafter proxy block and the crafter proxy card respectively. 
