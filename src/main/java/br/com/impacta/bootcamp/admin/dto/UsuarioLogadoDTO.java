package br.com.impacta.bootcamp.admin.dto;

import br.com.impacta.bootcamp.commons.dto.PermissionsDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
public class UsuarioLogadoDTO {

    private UserDTO userDTO;

    private List<PermissionsDTO> permissionsDTOS = new ArrayList<>();

    private TokenDTO tokenDTO;

    private String token;

    private String logo;

    public boolean isValid() {
        return tokenDTO != null && tokenDTO.getExpiraEm() > Calendar.getInstance().getTimeInMillis();
    }

}
