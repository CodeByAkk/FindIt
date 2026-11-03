package com.campus.lostfound.controller;

import com.campus.lostfound.entity.*;
import com.campus.lostfound.repository.*;
import com.campus.lostfound.service.ItemService;
import com.campus.lostfound.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository users;
    private final ItemRepository items;
    private final ClaimRepository claims;
    private final NotificationService notifications;
    private final ItemService itemService;

    public AdminController(UserRepository u, ItemRepository i, ClaimRepository c,
                           NotificationService n, ItemService itemService) {
        this.users = u;
        this.items = i;
        this.claims = c;
        this.notifications = n;
        this.itemService = itemService;
    }

    @GetMapping
    public String dashboard(Model m) {
        m.addAttribute("students", users.findByRole("ROLE_STUDENT"));
        m.addAttribute("items", items.findAll());
        m.addAttribute("claims", claims.findAllByOrderByCreatedAtDesc());
        m.addAttribute("pendingItems", items.findByStatusOrderByCreatedAtDesc(ItemStatus.PENDING));
        m.addAttribute("pendingClaims", claims.countByStatus(ClaimStatus.PENDING));
        m.addAttribute("returned", items.countByStatus(ItemStatus.RETURNED));
        m.addAttribute("lost", items.findAll().stream().filter(i -> "LOST".equals(i.getType())).count());
        m.addAttribute("found", items.findAll().stream().filter(i -> "FOUND".equals(i.getType())).count());
        return "admin";
    }

    @PostMapping("/items/{id}/status")
    public String itemStatus(@PathVariable("id") Long id,
                             @RequestParam("status") ItemStatus status,
                             RedirectAttributes ra) {
        Item item = items.findById(id).orElseThrow();
        itemService.review(item, status);
        if (status == ItemStatus.APPROVED) {
            notifications.notifyUser(item.getReporter(), "Your " + item.getType().toLowerCase() + " report was approved.");
        }
        ra.addFlashAttribute("success", "Item status updated.");
        return "redirect:/admin";
    }

    @PostMapping("/claims/{id}/status")
    public String claimStatus(@PathVariable("id") Long id,
                              @RequestParam("status") ClaimStatus status,
                              RedirectAttributes ra) {
        Claim c = claims.findById(id).orElseThrow();
        c.setStatus(status);
        claims.save(c);
        if (status == ClaimStatus.APPROVED) {
            c.getItem().setStatus(ItemStatus.CLAIMED);
            items.save(c.getItem());
        }
        notifications.notifyUser(c.getClaimant(), "Your claim for " + c.getItem().getName() + " was " + status.name().toLowerCase() + ".");
        ra.addFlashAttribute("success", "Claim status updated.");
        return "redirect:/admin";
    }

    @PostMapping("/students/{id}/toggle")
    public String toggle(@PathVariable("id") Long id, RedirectAttributes ra) {
        User u = users.findById(id).orElseThrow();
        if (!"ROLE_ADMIN".equals(u.getRole())) {
            u.setEnabled(!u.isEnabled());
            users.save(u);
        }
        ra.addFlashAttribute("success", "Student access updated.");
        return "redirect:/admin";
    }
}
