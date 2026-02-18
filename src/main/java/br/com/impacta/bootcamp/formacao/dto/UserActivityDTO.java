package br.com.impacta.bootcamp.formacao.dto;

import java.util.UUID;

import br.com.impacta.bootcamp.commons.util.Validation;
import lombok.Data;

@Data
public class UserActivityDTO {

    private UUID id;

    private Long attemptNumber;

    private String answerJson;

    private Long score;

    private String submittedAtS;

    @Validation(required = true, lengthMax = 50, lengthMin = 2)
    private String status;

    private UUID userId;

    private String userName;

    private UUID activityId;

    private String activityType;

    private UUID moduleId;

    private String moduleDescription;

}
