
package com.project.tour.service.chat;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.dialogflow.v2.AgentName;
import com.google.cloud.dialogflow.v2.Intent;
import com.google.cloud.dialogflow.v2.IntentView;
import com.google.cloud.dialogflow.v2.IntentsClient;
import com.google.cloud.dialogflow.v2.ListIntentsRequest;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DialogflowSuggestionService {

    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    private static final List<String> REQUIRED_INTENTS = List.of(
            "LienHeHotline",
            "HuongDanDatTour",
            "ThongTinDoanhNghiep");

    private final IntentsClient intentsClient;
    private final String projectId;

    private volatile Map<String, List<String>> cachedPhrases = Map.of();
    private volatile long cacheLoadedAt = 0L;

    public DialogflowSuggestionService(
            @Value("${google.cloud.dialogflow.project-id}") String projectId) throws IOException {
        this.projectId = projectId;
        this.intentsClient = IntentsClient.create();
    }

    public List<String> getRandomSuggestions() {
        Map<String, List<String>> phrasesByIntent = getTrainingPhrases();

        List<String> suggestions = new ArrayList<>();

        for (String intentName : REQUIRED_INTENTS) {
            List<String> phrases = phrasesByIntent.get(intentName);

            if (phrases == null || phrases.isEmpty()) {
                throw new IllegalStateException(
                        "Intent chưa có training phrases: " + intentName);
            }

            int index = ThreadLocalRandom.current().nextInt(phrases.size());
            suggestions.add(phrases.get(index));
        }

        return List.copyOf(suggestions);
    }

    private Map<String, List<String>> getTrainingPhrases() {
        long now = System.currentTimeMillis();

        if (!cachedPhrases.isEmpty()
                && now - cacheLoadedAt < CACHE_TTL_MS) {
            return cachedPhrases;
        }

        synchronized (this) {
            now = System.currentTimeMillis();

            if (!cachedPhrases.isEmpty()
                    && now - cacheLoadedAt < CACHE_TTL_MS) {
                return cachedPhrases;
            }

            try {
                ListIntentsRequest request = ListIntentsRequest.newBuilder()
                        .setParent(AgentName.of(projectId).toString())
                        .setIntentView(IntentView.INTENT_VIEW_FULL)
                        .setLanguageCode("vi")
                        .setPageSize(1000)
                        .build();

                Map<String, List<String>> loaded = new java.util.HashMap<>();

                for (Intent intent : intentsClient.listIntents(request).iterateAll()) {
                    String name = intent.getDisplayName();

                    if (!REQUIRED_INTENTS.contains(name)) {
                        continue;
                    }

                    List<String> phrases = new ArrayList<>();

                    for (Intent.TrainingPhrase phrase : intent.getTrainingPhrasesList()) {
                        String text = phrase.getPartsList().stream()
                                .map(part -> part.getText())
                                .reduce("", String::concat)
                                .trim();

                        if (!text.isBlank()) {
                            phrases.add(text);
                        }
                    }

                    loaded.put(name, List.copyOf(phrases));
                }

                for (String required : REQUIRED_INTENTS) {
                    if (!loaded.containsKey(required)
                            || loaded.get(required).isEmpty()) {
                        throw new IllegalStateException(
                                "Không tìm thấy training phrases cho intent: "
                                        + required);
                    }
                }

                cachedPhrases = Map.copyOf(loaded);
                cacheLoadedAt = System.currentTimeMillis();

                return cachedPhrases;

            } catch (ApiException ex) {
                throw ex;
            }
        }
    }

    @PreDestroy
    public void close() {
        intentsClient.close();
    }
}
