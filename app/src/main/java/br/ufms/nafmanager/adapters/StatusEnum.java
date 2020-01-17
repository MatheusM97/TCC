package br.ufms.nafmanager.adapters;

public enum StatusEnum {
    INATIVO(0L),
    ATIVO(1L),
    RASCUNHO(2l),
    BLOQUEADO(3l);

    public Long valor;
    StatusEnum(Long valor){
        this.valor = valor;
    }
}
