# reclamation-util
Utilities and mixins for the Reclamation modpack.

## Current Features

* Custom particles
  * Colored leaves
  * Colored dripping liquid
  * Two-tone colored dripping liquid
* Complicated Bees
  * Natural Aura effect
  * Crimsine, Ochurate, Asuric, Veridine effect (convert stone to Create orestones)
  * Poison Frame (0.75x lifespan, 0.9x productivity)
  * Permafrost Frame (-2 temperature, 0.4x lifespan)
* Area of effect tools
  * Hammer, excavator, and broadaxe
  * Nature's aura and botania compat
* Mekanism paxels
  * Skyseeker, botanists, and manasteel paxels
* Biome Essence and Attuned Biome Essence
* Biome Globes
* Flimsy door
* Agricraft patches 
  * Fertility patch (thanks @Piotr015!)
  * Complexity patch (thanks @Piotr015!)
  * Enchanted Mandrake plant spawn modifier
  * Makes fluid condition nonlethal
* Enchanted patches
  * Cauldron and kettle only need one bucket of water and accept fluid automation
* Nature's Aura
  * Fixes mobs without spawn eggs recipes breaking in a Birthing Altar recipe
  * Adds reclaim effect (turns blocks with a special tag into dirt)
  * Removes snowballs from projectile list
* Sculk Awakener
  * Turns sculk shriekers into spawning versions
* Frame Remover
  * A way to break end portal frames and end portal blocks
* Gravestone mod gravestones generate on clay, not dirt
* Watering cans
* Camel pack
  * Restore thirst automatically, respecting water purity - 3 tiers
* Theurgy patches
  * All machines now only allow extraction of outputs
  * Digestion and fermentation vats output comparator signal when there's a valid recipe in them - signal strength scales with how full the output inventory is
  * Accumulator recipe json supports nbt when checking inputs (for Thirst is Taken compat)
  * Liquefaction cauldron infinite filling from partially draining bucket bug patched
  * Digestion and fermentation vats ignore fluid nbt when checking for recipes
* Thirst was Taken patches
  * Compatibility with the Create pump to respect purity when pulling water from a cauldron
  * Cauldrons filled with rainwater have purity of Dirty
  * Cauldrons filled with dripstone have purity of Acceptable
  * Cauldrons filled with water display purity on their jade tooltip
  * Botania's rod of the seas produces slightly dirty water instead of untagged
  * Ars Elemental's Everfull Urn produces acceptable water instead of untagged
  * Macaw's Furniture sinks can no longer produce water
  * Stop Botania Petal Apothecary from laundering water
  * Bottles pick up whole source blocks 4 at a time
  * No ability to pick up water from flowing blocks
  * Embers boiler ignores fluid purity
* Removes bonemealing water to generate plants
