package org.delcom.app.views;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.dto.CashFlowForm;
import org.delcom.app.dto.CashFlowStats;
import org.delcom.app.entities.CashFlow;
import org.delcom.app.entities.User;
import org.delcom.app.services.CashFlowService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/cash-flows")
public class CashFlowView {

    private final CashFlowService cashFlowService;

    public CashFlowView(CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    @PostMapping("/add")
    public String postAddCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm cashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        // Validasi form
        if (cashFlowForm.getType().isBlank() || cashFlowForm.getSource().isBlank() || cashFlowForm.getLabel().isBlank()
                || cashFlowForm.getAmount() <= 0 || cashFlowForm.getDescription().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Data tidak valid");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/";
        }

        // Simpan cash flow
        var entity = cashFlowService.createCashFlow(
                authUser.getId(),
                cashFlowForm.getType(),
                cashFlowForm.getSource(),
                cashFlowForm.getLabel(),
                cashFlowForm.getAmount(),
                cashFlowForm.getDescription());

        if (entity == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan cash flow");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/";
        }

        // Redirect dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "Cash flow berhasil ditambahkan.");
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String postEditCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm cashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {
        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        // Validasi form
        if (cashFlowForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID cash flow tidak valid");
            redirectAttributes.addFlashAttribute("editCashFlowModalOpen", true);
            return "redirect:/";
        }

        if (cashFlowForm.getType().isBlank() || cashFlowForm.getSource().isBlank() || cashFlowForm.getLabel().isBlank()
                || cashFlowForm.getAmount() <= 0 || cashFlowForm.getDescription().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Data tidak valid");
            redirectAttributes.addFlashAttribute("addCashFlowModalOpen", true);
            return "redirect:/";
        }

        // Update cashFlow
        var updated = cashFlowService.updateCashFlow(
                cashFlowForm.getId(),
                authUser.getId(),
                cashFlowForm.getType(),
                cashFlowForm.getSource(),
                cashFlowForm.getLabel(),
                cashFlowForm.getAmount(),
                cashFlowForm.getDescription());

        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui cash flow");
            redirectAttributes.addFlashAttribute("editCashFlowModalOpen", true);
            redirectAttributes.addFlashAttribute("editCashFlowModalId", cashFlowForm.getId());
            return "redirect:/";
        }

        // Redirect dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "Cash flow berhasil diperbarui.");
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String postDeleteCashFlow(@Valid @ModelAttribute("cashFlowForm") CashFlowForm cashFlowForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        // Validasi form
        if (cashFlowForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID cashFlow tidak valid");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            return "redirect:/";
        }

        if (cashFlowForm.getConfirmId() == null || cashFlowForm.getConfirmId().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi ID cash flow tidak boleh kosong");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteCashFlowModalId", cashFlowForm.getId());
            return "redirect:/";
        }

        // Periksa apakah cashFlow tersedia
        CashFlow existingCashFlow = cashFlowService.getCashFlowById(cashFlowForm.getId(), authUser.getId());
        if (existingCashFlow == null) {
            redirectAttributes.addFlashAttribute("error", "CashFlow tidak ditemukan");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteCashFlowModalId", cashFlowForm.getId());
            return "redirect:/";
        }

        if (!existingCashFlow.getId().toString().equals(cashFlowForm.getConfirmId().toString())) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi ID cash flow tidak sesuai");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteCashFlowModalId", cashFlowForm.getId());
            return "redirect:/";
        }

        // Hapus cashFlow
        boolean deleted = cashFlowService.deleteCashFlow(
                cashFlowForm.getId(),
                authUser.getId());
        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus cash flow");
            redirectAttributes.addFlashAttribute("deleteCashFlowModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteCashFlowModalId", cashFlowForm.getId());
            return "redirect:/";
        }

        // Redirect dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "CashFlow berhasil dihapus.");
        return "redirect:/";
    }

    @GetMapping("/{cashFlowId}")
    public String getDetailCashFlow(@PathVariable UUID cashFlowId, Model model) {
        // Autentikasi user
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

        // Ambil cashFlow
        CashFlow cashFlow = cashFlowService.getCashFlowById(cashFlowId, authUser.getId());
        if (cashFlow == null) {
            return "redirect:/";
        }
        model.addAttribute("cashFlow", cashFlow);

        return ConstUtil.TEMPLATE_PAGES_CASH_FLOWS_DETAIL;
    }

    @GetMapping("/info/stats")
    public String getDetailCashFlowInfoStats(Model model) {
        // Autentikasi user
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

        // Instance dto utuk menampung data stats
        CashFlowStats cfs = new CashFlowStats();

        // Ambil semua cash flows
        List<CashFlow> cashFlowList = cashFlowService.getAllCashFlows(authUser.getId(), "");

        Map<String, Integer> mapIncomes = new HashMap<String, Integer>();
        Map<String, Integer> mapOutcomes = new HashMap<String, Integer>();

        var formatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.of("id", "ID"));

        for (CashFlow cashFlow : cashFlowList) {
            // Label adalah bulan dan tahun lihat formatter
            String label = cashFlow.getCreatedAt().format(formatter);

            boolean isExistLabel = cfs.getStatLabels().contains(label);
            if (!isExistLabel) {
                cfs.addStatLabels(label);
            }

            if (cashFlow.getType().equals("Income")) {
                cfs.addTotalIncome(cashFlow.getAmount());
                mapIncomes.merge(label, cashFlow.getAmount(), Integer::sum);
            } else {
                cfs.addTotalOutcome(cashFlow.getAmount());
                mapOutcomes.merge(label, cashFlow.getAmount(), Integer::sum);
            }
        }

        // Ambil list untuk masing-masing tipe cash flows agar sesuai labels
        List<Integer> listIncomes = new ArrayList<>();
        List<Integer> listOutcomes = new ArrayList<>();

        for (String label : cfs.getStatLabels()) {
            Integer totalCurrentIncomes = mapIncomes.getOrDefault(label, 0);
            Integer totalCurrentOutcomes = mapOutcomes.getOrDefault(label, 0);

            listIncomes.add(totalCurrentIncomes);
            listOutcomes.add(totalCurrentOutcomes);
        }

        cfs.setStatIncomes(listIncomes);
        cfs.setStatOutcomes(listOutcomes);

        model.addAttribute("cashFlowStat", cfs);

        return ConstUtil.TEMPLATE_PAGES_CASH_FLOWS_STATS;
    }

}
