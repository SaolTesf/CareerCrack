/* Since the ProblemTags table has a composite primary key, this composite key class is necessary */
package com.careercrack.careercrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ProblemTagId implements Serializable { // will be serialized and sent across the network
}
