package br.ufms.nafmanager.model;

public enum AcessoTipoEnum {
    UNIDADE(0L, "Unidade"),
    UNIVERSIDADE(1L, "Universidade"),
    REGIAO(2L, "Região");

    private Long valor;
    private String label;

    AcessoTipoEnum(Long valor){this.valor = valor;}

    AcessoTipoEnum(Long valor, String label){
        this.valor = valor;
        this.label = label;
    }

    public Long getValor(){return this.valor;}

    public String getLabel(){return this.label;}

    public static AcessoTipoEnum getEnumByLabel(String label){
        switch (label){
            case ("Unidade"):
                return AcessoTipoEnum.UNIDADE;
            case ("Universidade"):
                return AcessoTipoEnum.UNIVERSIDADE;
            case ("Região"):
                return AcessoTipoEnum.REGIAO;
        }
        return null;
    }

    @Override
    public String toString(){
        return label;
    }
}