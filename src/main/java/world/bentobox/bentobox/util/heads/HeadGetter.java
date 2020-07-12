package world.bentobox.bentobox.util.heads;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.panels.PanelItem;

public class HeadGetter {
    private static Map<String,HeadCache> cachedHeads = new HashMap<>();
    private static final Map<String, PanelItem> names = new HashMap<>();
    private static final long TOO_LONG = 360000; // 3 minutes
    private BentoBox plugin;
    private static Map<String,Set<HeadRequester>> headRequesters = new HashMap<>();

    /**
     * @param plugin - plugin
     */
    public HeadGetter(BentoBox plugin) {
        super();
        this.plugin = plugin;
        runPlayerHeadGetter();
    }

    @SuppressWarnings("deprecation")
    private void runPlayerHeadGetter() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            synchronized(names) {
                Iterator<Entry<String, PanelItem>> it = names.entrySet().iterator();
                if (it.hasNext()) {
                    Entry<String, PanelItem> en = it.next();
                    ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, en.getValue().getItem().getAmount());
                    SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
                    meta.setOwner(en.getKey());
                    playerSkull.setItemMeta(meta);
                    // Save in cache
                    cachedHeads.put(en.getKey(), new HeadCache(playerSkull, System.currentTimeMillis()));
                    // Tell requesters the head came in
                    if (headRequesters.containsKey(en.getKey())) {
                        for (HeadRequester req : headRequesters.get(en.getKey())) {
                            en.getValue().setHead(playerSkull.clone());
                            Bukkit.getServer().getScheduler().runTask(plugin, () -> req.setHead(en.getValue()));
                        }
                    }
                    it.remove();
                }
            }
        }, 0L, 20L);
    }

    /**
     * @param panelItem - head to update
     * @param requester - callback class
     */
    public static void getHead(PanelItem panelItem, HeadRequester requester) {
        // Freshen cache
        cachedHeads.values().removeIf(c -> System.currentTimeMillis() - c.getTimestamp() > TOO_LONG);
        // Check if in cache
        if (cachedHeads.containsKey(panelItem.getPlayerHeadName())) {
            panelItem.setHead(cachedHeads.get(panelItem.getPlayerHeadName()).getHead().clone());
            requester.setHead(panelItem);
        } else {
            // Get the name
            headRequesters.computeIfAbsent(panelItem.getPlayerHeadName(), k -> new HashSet<>()).add(requester);
            names.put(panelItem.getPlayerHeadName(), panelItem);
        }
    }

}
