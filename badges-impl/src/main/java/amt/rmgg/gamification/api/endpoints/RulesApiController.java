package amt.rmgg.gamification.api.endpoints;

import amt.rmgg.gamification.api.RulesApi;
import amt.rmgg.gamification.api.model.Rule;
import amt.rmgg.gamification.api.util.ApiKeyManager;
import amt.rmgg.gamification.entities.ApplicationEntity;
import amt.rmgg.gamification.entities.BadgeEntity;
import amt.rmgg.gamification.entities.RuleEntity;
import amt.rmgg.gamification.repositories.AppRepository;
import amt.rmgg.gamification.repositories.BadgeRepository;
import amt.rmgg.gamification.repositories.RuleRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Controller
public class RulesApiController implements RulesApi {
    @Autowired
    private ApiKeyManager apiKeyManager;
    @Autowired
    RuleRepository ruleRepository;
    @Autowired
    AppRepository appRepository;
    @Autowired
    BadgeRepository badgeRepository;
    @Autowired
    HttpServletRequest httpServletRequest;

    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Rule> createRule(@ApiParam(value = "", required = true) @Valid @RequestBody Rule rule ) {

        String apikey = httpServletRequest.getHeader("x-api-key");
        ApplicationEntity applicationEntity = apiKeyManager.getApplicationEntityFromApiKey(apikey);


        if(applicationEntity == null){
            return ResponseEntity.notFound().build();
        }

        Optional<BadgeEntity> badgeEntity = badgeRepository.findById((long)rule.getBadgeId());

        if(badgeEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        RuleEntity newRuleEntity = toRuleEntity(rule, badgeEntity.get());
        applicationEntity.getRules().add(newRuleEntity);

        ruleRepository.save(newRuleEntity);

        try {
            return ResponseEntity.created(new URI("/rules/" + newRuleEntity.getId())).body(rule);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    public static RuleEntity toRuleEntity(Rule rule, BadgeEntity badge) {
        RuleEntity entity = new RuleEntity();
        entity.setType(rule.getEventType());
        entity.setThreshold(rule.getThreshold());
        entity.setBadge(badge);
        return entity;
    }


    public static Rule toRule(RuleEntity entity){
        Rule rule = new Rule();
        rule.setEventType(entity.getType());
        rule.setThreshold(entity.getThreshold());
        rule.setBadgeId((int) entity.getBadge().getId());
        return rule;
    }

}
