package com.javawy.jordanpharmacyguide.adapters;

/**
 * Created by anupamchugh on 09/02/16.
 */
public class DataModel {

    /** Pharmacy Id */
    private Integer id;

    /** Name */
    private String name;

    /** Type */
    private String type;

    /** Vrsion Number */
    private String version_number;

    /** Feature */
    private String feature;

    /** Pharmacy Name */
    private String pharmacyName;

    /**
     * Data Model Constructor
     *
     * @param name : name
     * @param type : type
     * @param version_number : version_number
     * @param feature feature
     */
    public DataModel(Integer id,String pharmacyName,String name, String type, String version_number, String feature ) {
        this.id=id;
        this.name=name;
        this.type=type;
        this.version_number=version_number;
        this.pharmacyName=pharmacyName;
        this.feature=feature;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public String getFeature() {
        return feature;
    }

    /**
     * Getter for Id
     *
     * @return Id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for Id
     *
     * @param id : Id
     */
    public void setId(Integer id) {
        this.id = id;
    }
}