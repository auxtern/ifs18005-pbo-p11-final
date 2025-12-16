package org.delcom.app.views;

import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.dto.TodoForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.services.TodoService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeView {

    private final TodoService todoService;
    private final CashFlowService cashFlowService;

    public HomeView(TodoService todoService, CashFlowService cashFlowService) {
        this.todoService = todoService;
        this.cashFlowService = cashFlowService;
    }

    @GetMapping
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        // Todos
        var todos = todoService.getAllTodos(authUser.getId(), "");
        model.addAttribute("todos", todos);

        // Todo Form
        model.addAttribute("todoForm", new TodoForm());

        // Cash flows
        var cashFlows = cashFlowService.getAllCashFlows(authUser.getId(), "");
        model.addAttribute("cashFlows", cashFlows);

        // Cash flows form
        model.addAttribute("cashFlowForm", new CashFlowForm());

        return ConstUtil.TEMPLATE_PAGES_HOME;
    }
}
