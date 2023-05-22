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

## Downloads

Available on [Modrinth](https://modrinth.com/mod/refinedcrafterproxy)
and [Curseforge](https://www.curseforge.com/minecraft/mc-mods/refined-crafter-proxy).

## Redistribution

This project is licensed under the AGPLv3. See LICENSE.

## Acknowledgements

Special thanks to Refined Mods for [Refined Storage](https://refinedmods.com/refined-storage/), and

- Edivad99 for [Extra Storage](https://github.com/Edivad99/ExtraStorage/)
- SuperMartijn642 for [Entangled](https://github.com/SuperMartijn642/Entangled),

which I referenced while coding the crafter proxy block and the crafter proxy card respectively. 
