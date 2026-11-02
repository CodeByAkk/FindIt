package com.campus.lostfound.service;

import com.campus.lostfound.entity.*;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.text.Normalizer;
import java.util.*;

@Service
public class MatchingService {
    public record Match(Item item, int score, String reasons) {}
    private final ItemRepository items;
    public MatchingService(ItemRepository items) { this.items = items; }
    public List<Match> matches(Item source) {
        List<Item> candidates = items.findByStatusOrderByCreatedAtDesc(ItemStatus.APPROVED).stream()
            .filter(i -> !i.getId().equals(source.getId()) && !i.getType().equals(source.getType())).toList();
        return candidates.stream().map(i -> score(source, i)).filter(m -> m.score() >= 25)
            .sorted(Comparator.comparingInt(Match::score).reversed()).limit(5).toList();
    }
    private Match score(Item a, Item b) {
        int score = 0; List<String> reasons = new ArrayList<>();
        double name = nameSimilarity(a.getName(), b.getName());
        int ns = (int) Math.round(name * 45); score += ns;
        if (ns >= 20) reasons.add("similar item name");
        if (a.getCategory().equalsIgnoreCase(b.getCategory())) { score += 25; reasons.add("same category"); }
        double loc = nameSimilarity(a.getLocation(), b.getLocation());
        int ls = (int) Math.round(loc * 20); score += ls;
        if (ls >= 8) reasons.add("nearby location");
        long days = Math.abs(ChronoUnit.DAYS.between(a.getItemDate(), b.getItemDate()));
        int ds = days == 0 ? 10 : days <= 2 ? 8 : days <= 7 ? 5 : days <= 14 ? 2 : 0;
        score += ds; if (ds >= 5) reasons.add("close date");
        return new Match(b, Math.min(score, 100), String.join(", ", reasons));
    }
    private double nameSimilarity(String a, String b) {
        Set<String> x = tokens(a), y = tokens(b);
        if (x.isEmpty() || y.isEmpty()) return 0;
        Set<String> both = new HashSet<>(x); both.retainAll(y);
        Set<String> all = new HashSet<>(x); all.addAll(y);
        double j = (double) both.size() / all.size();
        String na = normalize(a), nb = normalize(b);
        int lev = levenshtein(na, nb);
        double sim = 1.0 - (double) lev / Math.max(na.length(), nb.length());
        return Math.max(j, sim);
    }
    private Set<String> tokens(String s) {
        Set<String> r = new HashSet<>(Arrays.asList(normalize(s).split("\\s+")));
        r.removeIf(String::isBlank); return r;
    }
    private String normalize(String s) {
        return Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "").replaceAll("[^a-z0-9 ]", " ").trim().replaceAll("\\s+", " ");
    }
    private int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int[] cur = new int[b.length() + 1]; cur[0] = i;
            for (int j = 1; j <= b.length(); j++)
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1),
                    prev[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
            prev = cur;
        }
        return prev[b.length()];
    }
}
