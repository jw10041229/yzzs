package com.huimv.yzzs.db.entity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DA_MC.
 */
public class Da_mc {

    private Long id;
    private String mcid;
    private String mcmc;
    private String zsid;
    private String zsmc;
    private String jqid;

    public Da_mc() {
    }

    public Da_mc(Long id) {
        this.id = id;
    }

    public Da_mc(Long id, String mcid, String mcmc, String zsid, String zsmc, String jqid) {
        this.id = id;
        this.mcid = mcid;
        this.mcmc = mcmc;
        this.zsid = zsid;
        this.zsmc = zsmc;
        this.jqid = jqid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMcid() {
        return mcid;
    }

    public void setMcid(String mcid) {
        this.mcid = mcid;
    }

    public String getMcmc() {
        return mcmc;
    }

    public void setMcmc(String mcmc) {
        this.mcmc = mcmc;
    }

    public String getZsid() {
        return zsid;
    }

    public void setZsid(String zsid) {
        this.zsid = zsid;
    }

    public String getZsmc() {
        return zsmc;
    }

    public void setZsmc(String zsmc) {
        this.zsmc = zsmc;
    }

    public String getJqid() {
        return jqid;
    }

    public void setJqid(String jqid) {
        this.jqid = jqid;
    }

}
