package world.bentobox.bentobox.managers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import io.github.TheBusyBiscuit.GitHubWebAPI4Java.GitHubWebAPI;
import io.github.TheBusyBiscuit.GitHubWebAPI4Java.objects.repositories.GitHubContributor;
import io.github.TheBusyBiscuit.GitHubWebAPI4Java.objects.repositories.GitHubRepository;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.Settings;
import world.bentobox.bentobox.web.catalog.CatalogEntry;
import world.bentobox.bentobox.web.credits.Contributor;

/**
 * Handles web-related stuff.
 * Should be instantiated after all addons are loaded.
 * @author Poslovitch
 * @since 1.3.0
 */
public class WebManager {

    private @NonNull BentoBox plugin;
    private @Nullable GitHubWebAPI gitHub;
    private @NonNull List<CatalogEntry> addonsCatalog;
    private @NonNull List<CatalogEntry> gamemodesCatalog;
    private @NonNull Map<String, List<Contributor>> contributors;

    public WebManager(@NonNull BentoBox plugin) {
        this.plugin = plugin;
        this.addonsCatalog = new ArrayList<>();
        this.gamemodesCatalog = new ArrayList<>();
        this.contributors = new HashMap<>();

        // Setup the GitHub connection
        if (plugin.getSettings().isGithubDownloadData()) {
            this.gitHub = new GitHubWebAPI();

            long connectionInterval = plugin.getSettings().getGithubConnectionInterval() * 20L * 60L;
            if (connectionInterval <= 0) {
                // If below 0, it means we shouldn't run this as a repeating task.
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, this::requestGitHubData, 20L);
            } else {
                // Set connection interval to be at least 60 minutes.
                connectionInterval = Math.max(connectionInterval, 60 * 20 * 60L);
                plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::requestGitHubData, 20L, connectionInterval);
            }
        }
    }

    public void requestGitHubData() {
        getGitHub().ifPresent(gh -> {
            if (plugin.getSettings().isLogGithubDownloadData()) {
                plugin.log("正在从 GitHub 下载数据...");
                plugin.log("更新目录中...");
            }
            GitHubRepository weblinkRepo;
            try {
                weblinkRepo = new GitHubRepository(gh, "BentoBoxWorld/weblink");
            } catch (Exception e) {
                if (plugin.getSettings().isLogGithubDownloadData()) {
                    plugin.logError("连接到 GitHub 时发生未知错误...");
                    plugin.logStacktrace(e);
                }
                weblinkRepo = null;
            }

            if (weblinkRepo != null) {
                // Downloading the data
                String tagsContent = getContent(weblinkRepo, "catalog/tags.json");
                String topicsContent = getContent(weblinkRepo, "catalog/topics.json");
                String catalogContent = getContent(weblinkRepo, "catalog/catalog.json");

                /* Parsing the data */
                parseCatalogContent(tagsContent, topicsContent, catalogContent);
            }

            if (plugin.getSettings().isLogGithubDownloadData()) {
                plugin.log("更新贡献者信息中...");
            }

            List<String> repositories = new ArrayList<>();
            // Gather all the repositories of installed addons and or catalog entries.
            repositories.add("BentoBoxWorld/BentoBox");
            repositories.addAll(plugin.getAddonsManager().getEnabledAddons()
                    .stream().map(addon -> addon.getDescription().getRepository())
                    .filter(repo -> !repo.isEmpty())
                    .collect(Collectors.toList()));
            repositories.addAll(addonsCatalog.stream().map(CatalogEntry::getRepository)
                    .filter(repo -> !repositories.contains(repo))
                    .collect(Collectors.toList()));
            repositories.addAll(gamemodesCatalog.stream().map(CatalogEntry::getRepository)
                    .filter(repo -> !repositories.contains(repo))
                    .collect(Collectors.toList()));

            /* Download the contributors */
            if (plugin.getSettings().isLogGithubDownloadData()) {
                plugin.log("正在为: " + String.join(", ", repositories) + " 收集贡献者信息");
            }

            for (String repository : repositories) {
                GitHubRepository addonRepo;
                try {
                    addonRepo = new GitHubRepository(gh, repository);
                } catch (Exception e) {
                    if (plugin.getSettings().isLogGithubDownloadData()) {
                        plugin.logError("为仓库 '" + repository + "' 收集贡献者信息时出错...");
                        plugin.logStacktrace(e);
                    }
                    addonRepo = null;
                }
                if (addonRepo != null) {
                    gatherContributors(addonRepo);
                }
            }

            // People were concerned that the download took ages, so we need to tell them it's over now.
            if (plugin.getSettings().isLogGithubDownloadData()) {
                plugin.log("从 GitHub 下载数据成功.");
            }
        });
    }

    private void parseCatalogContent(String tagsContent, String topicsContent, String catalogContent) {
        // Register the tags translations in the locales
        if (!tagsContent.isEmpty()) {
            try {
                JsonObject tags = new JsonParser().parse(tagsContent).getAsJsonObject();
                tags.entrySet().forEach(entry -> plugin.getLocalesManager().getLanguages().values().forEach(locale -> {
                    JsonElement translation = entry.getValue().getAsJsonObject().get(locale.toLanguageTag());
                    if (translation != null) {
                        locale.set("catalog.tags." + entry.getKey(), translation.getAsString());
                    }
                }));
            } catch (JsonParseException e) {
                if (plugin.getSettings().isLogGithubDownloadData()) {
                    plugin.log("无法更新目录标签: JSON 数据格式有误.");
                }
            }
        }

        // Register the topics translations in the locales
        if (!topicsContent.isEmpty()) {
            try {
                JsonObject topics = new JsonParser().parse(topicsContent).getAsJsonObject();
                topics.entrySet().forEach(entry -> plugin.getLocalesManager().getLanguages().values().forEach(locale -> {
                    JsonElement translation = entry.getValue().getAsJsonObject().get(locale.toLanguageTag());
                    if (translation != null) {
                        locale.set("catalog.topics." + entry.getKey(), translation.getAsString());
                    }
                }));
            } catch (JsonParseException e) {
                if (plugin.getSettings().isLogGithubDownloadData()) {
                    plugin.log("无法更新目录主题: JSON 数据格式有误.");
                }
            }
        }

        // Register the catalog data
        if (!catalogContent.isEmpty()) {
            try {
                JsonObject catalog = new JsonParser().parse(catalogContent).getAsJsonObject();

                this.addonsCatalog.clear();
                this.gamemodesCatalog.clear();

                catalog.getAsJsonArray("gamemodes").forEach(gamemode -> gamemodesCatalog.add(new CatalogEntry(gamemode.getAsJsonObject())));
                catalog.getAsJsonArray("addons").forEach(addon -> addonsCatalog.add(new CatalogEntry(addon.getAsJsonObject())));
            } catch (JsonParseException e) {
                if (plugin.getSettings().isLogGithubDownloadData()) {
                    plugin.log("无法更新目录内容: JSON 数据格式有误.");
                }
            }
        }
    }

    /**
     *
     * @param repo - Github repository
     * @param fileName - file name
     * @return content or nothing
     * @since 1.8.0
     */
    @NonNull
    private String getContent(@NonNull GitHubRepository repo, String fileName) {
        try {
            String content = repo.getContent(fileName).getContent().replaceAll("\\n", "");
            return new String(Base64.getDecoder().decode(content), StandardCharsets.UTF_8);
        } catch (IllegalAccessException e) {
            // Fail silently
        } catch (Exception e) {
            if (plugin.getSettings().isLogGithubDownloadData()) {
                plugin.logError("从 GitHub 下载 '" + fileName + "' 时出错...");
                plugin.logStacktrace(e);
            }
        }
        return "";
    }

    private void gatherContributors(@NonNull GitHubRepository repo) {
        try {
            List<Contributor> addonContributors = new LinkedList<>();
            for (GitHubContributor gitHubContributor : repo.getContributors()) {
                addonContributors.add(new Contributor(gitHubContributor.getUsername(), gitHubContributor.getContributionsAmount()));
            }
            contributors.put(repo.getFullName(), addonContributors);
        } catch (IllegalAccessException e) {
            // Silently fail
        }
    }

    /**
     * Returns the contents of the addons catalog (may be an empty list).
     * @return the contents of the addons catalog.
     * @since 1.5.0
     */
    @NonNull
    public List<CatalogEntry> getAddonsCatalog() {
        return addonsCatalog;
    }

    /**
     * Returns the contents of the gamemodes catalog (may be an empty list).
     * @return the contents of the gamemodes catalog.
     * @since 1.5.0
     */
    @NonNull
    public List<CatalogEntry> getGamemodesCatalog() {
        return gamemodesCatalog;
    }

    /**
     *
     * @param repository
     * @return list of contributors
     * @since 1.9.0
     */
    @NonNull
    public List<Contributor> getContributors(String repository) {
        return contributors.getOrDefault(repository, new ArrayList<>());
    }

    /**
     * Returns an optional that may contain the {@link GitHubWebAPI} instance only and only if {@link Settings#isGithubDownloadData()} is {@code true}.
     * @return the GitHub instance.
     * @since 1.5.0
     */
    @NonNull
    public Optional<GitHubWebAPI> getGitHub() {
        return Optional.ofNullable(gitHub);
    }
}
