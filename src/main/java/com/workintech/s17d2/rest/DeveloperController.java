package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeveloperController {
    public Map<Integer, Developer> developers;

    private Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable){
        this.taxable = taxable;
    }

    @PostConstruct
    public void init(){
        this.developers = new HashMap<>();
    }

    @GetMapping("/developers")
    public List<Developer> getAll(){
        return developers.values().stream().toList();
    }

    @GetMapping("/developers/{id}")
    public Developer getById(@PathVariable int id){
        if(developers.containsKey(id)){
            return developers.get(id);
        }
        return null;
    }

    @PostMapping("/developers")
    @ResponseStatus(HttpStatus.CREATED)
    public Developer create(@RequestBody Developer developer){
        Developer createdDeveloper;
        double currentSalary = developer.getSalary();

        if (developer.getExperience() == null) {
            return null;
        }

        switch (developer.getExperience()){
            case JUNIOR:
                double juniorSalary = currentSalary - (currentSalary * taxable.getSimpleTaxRate() / 100);
                createdDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), juniorSalary);
                break;
            case MID:
                double midSalary = currentSalary - (currentSalary * taxable.getMiddleTaxRate() / 100);
                createdDeveloper = new MidDeveloper(developer.getId(), developer.getName(), midSalary);
                break;
            case SENIOR:
                double seniorSalary = currentSalary - (currentSalary * taxable.getUpperTaxRate() / 100);
                createdDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), seniorSalary);
                break;
            default:
                createdDeveloper = developer;
                break;
        }
        developers.put(createdDeveloper.getId(), createdDeveloper);
        return createdDeveloper;
    }

    @PutMapping("/developers/{id}")
    public Developer update(@PathVariable int id, @RequestBody Developer developer){
        if(!developers.containsKey(id)){
            return null;
        }
        developer.setId(id);
        developers.put(id, developer);
        return developer;
    }

    @DeleteMapping("/developers/{id}")
    public Developer delete(@PathVariable int id){
        return developers.remove(id);
    }
}
