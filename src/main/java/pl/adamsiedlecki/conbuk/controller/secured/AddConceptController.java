package pl.adamsiedlecki.conbuk.controller.secured;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.adamsiedlecki.conbuk.db.concept.Concept;
import pl.adamsiedlecki.conbuk.db.concept.ConceptService;
import pl.adamsiedlecki.conbuk.db.user.MyUserPrincipal;
import pl.adamsiedlecki.conbuk.db.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AddConceptController {

    private final ConceptService conceptService;

    @Autowired
    public AddConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @GetMapping("/add-concept")
    public String addConcept(Model model) {
        model.addAttribute(new Concept());
        return "addConcept";
    }

    @PostMapping("/add-concept")
    public String addConcept(@ModelAttribute Concept concept, Model model) {
        concept.setSaveTime(LocalDateTime.now());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((MyUserPrincipal) principal).getUser().getUsername();
        } else {
            username = principal.toString();
        }

        if (username == null) {
            model.addAttribute("added", false);
        } else {
            User user = ((MyUserPrincipal) principal).getUser();
            concept.setAuthor(user);

            Optional<Concept> conceptByName = conceptService.getConceptByName(concept.getName());
            if (!conceptByName.isPresent()) {
                conceptService.saveNewConcept(concept);
                model.addAttribute("added", true);

            } else {
                model.addAttribute("nameIsNotUnique", true);
            }


        }

        model.addAttribute(new Concept());
        return "addConcept";
    }
}
