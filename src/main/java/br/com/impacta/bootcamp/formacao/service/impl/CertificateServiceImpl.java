package br.com.impacta.bootcamp.formacao.service.impl;

import br.com.impacta.bootcamp.admin.dto.BodyListDTO;
import br.com.impacta.bootcamp.commons.dto.SearchCriteriaDTO;
import br.com.impacta.bootcamp.commons.enums.SearchOperation;
import br.com.impacta.bootcamp.commons.exception.BusinessRuleException;
import br.com.impacta.bootcamp.commons.util.Beans;
import br.com.impacta.bootcamp.commons.util.Validador;
import br.com.impacta.bootcamp.formacao.dto.CertificateDTO;
import br.com.impacta.bootcamp.formacao.model.Certificate;
import br.com.impacta.bootcamp.formacao.repository.CertificateRepository;
import br.com.impacta.bootcamp.formacao.service.CertificateService;
import br.com.impacta.bootcamp.formacao.specification.CertificateSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private Beans beans;

    @Value("${page.filter.offset}")
    private Integer offset;

    public BodyListDTO getAll(List<SearchCriteriaDTO> lista, int pagina) {
        Pageable pages = beans.montarPageable(pagina, offset, lista);
        BodyListDTO bodyListDTO = new BodyListDTO();
        CertificateSpecification msCategoria = new CertificateSpecification();

        for (SearchCriteriaDTO sc: lista) {
            if (beans.validaSearchCriteriaDTO(sc)) {
                msCategoria.add(beans.instanciar(sc));
            }
        }

        Page<Certificate> msPage = certificateRepository.findAll(msCategoria, pages);
        bodyListDTO.setLista(msPage
                .stream()
                .map(this::montarDTO)
                .collect(Collectors.toList()));
        bodyListDTO.setTotal(msPage.getTotalPages());
        bodyListDTO.setPagina(pagina);
        return bodyListDTO;
    }

    @Override
    public void save(CertificateDTO dto) {
        isRepetido(dto);
        certificateRepository.save(montarEntity(dto));
    }

    @Override
    public Certificate findByIdInterno(UUID id) {
        return certificateRepository.findById(id).orElseThrow( () ->
                new BusinessRuleException("Certificado não encontrado"));
    }

    @Override
    public CertificateDTO montarDTO(Certificate entity) {
        CertificateDTO dto = new CertificateDTO();
        beans.updateObjectos(dto, entity);

        return dto;
    }

    private Certificate montarEntity(CertificateDTO dto) {
        Certificate entity = new Certificate();
        beans.updateObjectos(entity, dto);

        Date data = beans.converterStringToDate(dto.getData());
        entity.setData(data);

        return entity;
    }

    @Override
    public Certificate findByToken(UUID token) {
        return certificateRepository.findByToken(token);
    }


    private void isValido(CertificateDTO dto) {
        Validador.validador(dto);
    }

    private void isRepetido(CertificateDTO dto) {
        isValido(dto);

        List<SearchCriteriaDTO> lista = new ArrayList<>();

        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setKey("userName");
        criteria.setValue(dto.getUserName());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        criteria = new SearchCriteriaDTO();
        criteria.setKey("token");
        criteria.setValue(dto.getToken());
        criteria.setOperation(SearchOperation.EQUAL.name());
        lista.add(criteria);

        int pagina = 1;
        BodyListDTO bodyListDTO = getAll(lista, pagina);
        if (!bodyListDTO.getLista().isEmpty()) {
            boolean existe = false;
            while (pagina <= bodyListDTO.getTotal() && !existe) {
                pagina ++;

                for (int i = 0 ; i < bodyListDTO.getLista().size() ; i ++) {
                    CertificateDTO dto1 = (CertificateDTO) bodyListDTO.getLista().get(i);
                    if (!Objects.equals(dto1.getId(), dto.getId())
                    ) {
                        existe = true;
                        break;
                    }
                }

                bodyListDTO = getAll(lista, pagina);
            }

            if (existe) {
                throw new BusinessRuleException("Já existe este Certificado cadastrado");
            }
        }
    }
}
