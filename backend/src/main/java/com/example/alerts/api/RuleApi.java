package com.example.alerts.api;

import com.example.alerts.dto.RuleResponse;
import com.example.alerts.dto.RuleSaveRequest;
import com.example.alerts.dto.RuleUpdateRequest;
import com.example.alerts.service.RuleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleApi {

    private final RuleService ruleService;

    @GetMapping
    public List<RuleResponse> list() {
        return ruleService.list();
    }

    @GetMapping("/all")
    public List<RuleResponse> listAll() {
        return ruleService.listAll();
    }

    @GetMapping("/{id}")
    public RuleResponse load(@PathVariable Long id) {
        return ruleService.load(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RuleResponse save(@Valid @RequestBody RuleSaveRequest request) {
        return ruleService.save(request);
    }

    @PutMapping("/{id}")
    public RuleResponse update(@PathVariable Long id, @Valid @RequestBody RuleUpdateRequest request) {
        return ruleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ruleService.delete(id);
    }
}
