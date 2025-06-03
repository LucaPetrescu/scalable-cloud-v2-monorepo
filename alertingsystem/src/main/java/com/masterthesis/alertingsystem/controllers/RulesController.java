package com.masterthesis.alertingsystem.controllers;

import com.masterthesis.alertingsystem.dtos.NewRuleDto;
import com.masterthesis.alertingsystem.dtos.RuleDto;
import com.masterthesis.alertingsystem.exceptions.NothingToUpdateException;
import com.masterthesis.alertingsystem.exceptions.ThresholdsLoadingException;
import com.masterthesis.alertingsystem.rules.DroolsRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
public class RulesController {

    private final DroolsRuleService droolsRuleService;

    public RulesController(DroolsRuleService droolsRuleService){
        this.droolsRuleService = droolsRuleService;
    }

    @PostMapping("/changeRules")
    public ResponseEntity<List<NewRuleDto>> changeRules(@RequestParam(name="serviceName", required = true) String serviceName, @RequestBody List<NewRuleDto> newRulesDtoList) throws ThresholdsLoadingException, NothingToUpdateException {
        System.out.println(newRulesDtoList.toString());
        List<NewRuleDto> response = droolsRuleService.changeMetricsRules(serviceName, newRulesDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getRulesForService")
    public ResponseEntity<List<RuleDto>> getRulesForService(@RequestParam(name="serviceName", required = true) String serviceName) throws ThresholdsLoadingException {
        List<RuleDto> response = droolsRuleService.getRulesForService(serviceName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
