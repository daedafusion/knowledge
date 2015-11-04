package com.daedafusion.knowledge.trinity.impl;

import org.apache.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Created by mphilpot on 11/2/15.
 */
@Entity
@Table(name = "triples", indexes = {
        @Index(name="spo_p", columnList="subject,predicate,object,partition"),
        @Index(name="sp_p", columnList="subject,predicate,partition"),
        @Index(name="s_p", columnList="subject,partition"),
        @Index(name="pos_p", columnList="predicate,object,subject,partition"),
        @Index(name="po_p", columnList="predicate,object,partition"),
        @Index(name="p_p", columnList="predicate,partition"),
        @Index(name="osp_p", columnList="object,subject,predicate,partition"),
        @Index(name="os_p", columnList = "object,subject,partition"),
        @Index(name="o_p", columnList = "object,partition")
})
public class HibernateTriple
{
    private static final Logger log = Logger.getLogger(HibernateTriple.class);

    // Key

    @Column(name = "partition", nullable = false)
    private Long partitionHash;

    @Column(name = "subject", nullable = false)
    private Long subjectHash;

    @Column(name = "predicate", nullable = false)
    private Long predicateHash;

    @Column(name = "object", nullable = false)
    private Long objectHash;

    // Info Family

    @Column
    private Long epoch;

    @Column(name = "external_source")
    private String externalSource;

    @Column(name = "ingest_id")
    private String ingestId;

    // Resource Family
    public static final int RESOURCE_LENGTH = 256;

    @Column(name = "subject_resource", length = RESOURCE_LENGTH)
    private String subject;

    @Column(name = "predicate_resource", length = RESOURCE_LENGTH)
    private String predicate;

    @Column(name = "object_resource", length = RESOURCE_LENGTH)
    private String object;
}
