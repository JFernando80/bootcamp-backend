package br.com.impacta.bootcamp.formacao.dto;

import br.com.impacta.bootcamp.commons.util.Validation;
import br.com.impacta.bootcamp.commons.util.ClassePersonal;
import java.util.UUID;
import lombok.Data;

@Data
public class UserCourseDTO {

    private UUID id;

    @Validation(dateMax = "+0d")
    private String enrolledAtS;

    private String status;

    @Validation(required = true)
    private Long progressPercent;

    private String certificateIssuedAtS;

    private String certificateToken;

    private String certificateUrl;

    @Validation(dateMax = "+0d")
    private String lastActivityAtS;

    @ClassePersonal (exibe = "name")
    private UUID userId;

    private String userName;


    @ClassePersonal (exibe = "description")
    private UUID courseId;

    @Validation(required = true, lengthMax = 300, lengthMin = 10)
    private String courseDescription;


}
