Requirements:
ProtocolLib

On a standard server loading 16 chunks of radius (1089 chunks) is standard practice. However, when a player descends underground, the server is forced to hold those chunks in RAM, calculate cave mob AI, handle lighting updates, and manage I/O pressure for areas the player cannot even see at all while the player stares at a stone wall.

**Aggressive Memory Reclamation**: By dynamically shifting View Distance, we drastically reduce the heap size required by the server, allowing your RAM to be used for more important processes, effectively increasing your server's capacity without upgrading your hardware.

**CPU Bottleneck Mitigation**: Simulation Distance scaling stops the "hidden" CPU tax. By limiting simulation to a set amount of chunk radius underground, we stop the server from ticking cave entities, flowing water, and complex redstone circuitry in distant, dark, and irrelevant cave systems.

**I/O Pressure Reduction**: Fewer chunks loaded means significantly lower read/write operations on your storage. This is critical for servers running on standard HDDs or budget-tier SSDs, preventing disk-induced stutter.

Instant Load Mitigation: Prevents massive CPU and I/O spikes when players log in, respawn, or teleport directly underground. The plugin instantly applies optimized distances before the server attempts to load the massive surface-level chunk radius.

---------------
summary
chunk optimizer in a way for optimization purposes for those who want max performance from their hardware

Removes Unnecessary high distance chunk processing for players who are in underground.

Customizable by allowing you to change view distance and simulation distance independently

Flicker-Free with zero visual disruption when changing view distance

Buffer zones to not worry about players unloading and loading chunks rapidly

Instantly adapts to fast travels and joins (join/teleport/respawn) to prevent temporary chunk-loading lag spikes
