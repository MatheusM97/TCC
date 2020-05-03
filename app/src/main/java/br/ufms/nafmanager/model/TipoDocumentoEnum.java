package br.ufms.nafmanager.model;

public enum TipoDocumentoEnum {
    CPF(0L, "CPF"),
    CNPJ(1L, "CNPJ");

    private Long valor;
    private String label;

    TipoDocumentoEnum(Long valor){this.valor = valor;}

    TipoDocumentoEnum(Long valor, String label){
        this.valor = valor;
        this.label = label;
    }

    public Long getValor(){return this.valor;}

    public String getLabel(){return this.label;}

    @Override
    public String toString(){
        return label;
    }
}