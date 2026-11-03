package com.campus.lostfound.service;

import com.campus.lostfound.entity.*;
import com.campus.lostfound.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@Service
public class ItemService {
    private final ItemRepository items;
    private final UserRepository users;
    private final NotificationService notifications;
    private final MatchingService matching;
    private final Path uploadDir;

    public ItemService(ItemRepository i, UserRepository u, NotificationService n,
                       MatchingService matching, @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.items = i;
        this.users = u;
        this.notifications = n;
        this.matching = matching;
        this.uploadDir = Path.of(uploadDir);
    }

    @Transactional
    public Item report(String type, String name, String category, String location,
                       LocalDate date, String description, MultipartFile image, User reporter) throws IOException {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Item date cannot be in the future.");
        }
        LocalDate itemDate = date != null ? date : LocalDate.now();
        Item item = "LOST".equalsIgnoreCase(type)
                ? new LostItem(name, category, location, itemDate, description, reporter)
                : new FoundItem(name, category, location, itemDate, description, reporter);

        if (image != null && !image.isEmpty()) {
            if (image.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Image must be smaller than 5 MB.");
            }
            String ct = image.getContentType();
            if (ct == null || !ct.startsWith("image/")) {
                throw new IllegalArgumentException("Please upload an image file.");
            }
            Files.createDirectories(uploadDir);
            String originalName = image.getOriginalFilename();
            String safeName = (originalName != null && !originalName.isBlank())
                    ? Path.of(originalName).getFileName().toString()
                    : "upload.png";
            String fn = UUID.randomUUID() + "-" + safeName;
            Files.copy(image.getInputStream(), uploadDir.resolve(fn), StandardCopyOption.REPLACE_EXISTING);
            item.setImagePath("/uploads/" + fn);
        }

        return items.save(item);
    }

    @Transactional
    public void review(Item item, ItemStatus status) {
        item.setStatus(status);
        items.save(item);

        if (status == ItemStatus.APPROVED) {
            for (User candidate : users.findByRole("ROLE_STUDENT")) {
                if (candidate.getId().equals(item.getReporter().getId())) {
                    continue;
                }
                for (Item report : items.findByReporterIdOrderByCreatedAtDesc(candidate.getId())) {
                    if (report.getStatus() == ItemStatus.APPROVED
                            && matching.matches(report).stream().anyMatch(match -> match.item().getId().equals(item.getId()))) {
                        notifications.notifyUser(candidate, "A possible match was found for your report: " + report.getName() + " ↔ " + item.getName());
                        break;
                    }
                }
            }
        } else if (status == ItemStatus.REJECTED) {
            notifications.notifyUser(item.getReporter(), "Your item report was rejected by an administrator.");
        } else if (status == ItemStatus.RETURNED) {
            notifications.notifyUser(item.getReporter(), "Your item has been marked as successfully returned.");
        }
    }
}
